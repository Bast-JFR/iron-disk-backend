package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.BoletaResponse;
import cl.irondisk.backend.dto.CompraRequest;
import cl.irondisk.backend.dto.ItemCompraRequest;
import cl.irondisk.backend.exception.DatosInvalidosException;
import cl.irondisk.backend.exception.RecursoNoEncontradoException;
import cl.irondisk.backend.exception.ReglaNegocioException;
import cl.irondisk.backend.model.Boleta;
import cl.irondisk.backend.model.DetalleBoleta;
import cl.irondisk.backend.model.EstadoBoleta;
import cl.irondisk.backend.model.Producto;
import cl.irondisk.backend.model.Usuario;
import cl.irondisk.backend.repository.BoletaRepository;
import cl.irondisk.backend.repository.ProductoRepository;
import cl.irondisk.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Logica de negocio de las compras y las boletas
// Reglas: el precio siempre sale de la base de datos y nunca del frontend, el total lo calcula el backend,
// la compra descuenta el stock todo o nada, anular una boleta devuelve el stock y un cliente solo ve sus boletas
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoletaService {

    private final BoletaRepository boletaRepo;
    private final ProductoRepository productoRepo;
    private final UsuarioRepository usuarioRepo;

    // Registra una compra, correoUsuario es el del token o null si compra sin cuenta
    @Transactional
    public BoletaResponse crearCompra(CompraRequest req, String correoUsuario) {
        String correo = req.correo().trim().toLowerCase();
        if (!ValidadorUsuario.dominioPermitido(correo)) {
            throw new DatosInvalidosException("Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com");
        }

        Boleta boleta = new Boleta();
        boleta.setFecha(LocalDateTime.now());
        boleta.setEstado(EstadoBoleta.PAGADA);
        boleta.setClienteNombre(req.nombre().trim());
        boleta.setClienteApellidos(req.apellidos().trim());
        boleta.setClienteCorreo(correo);
        boleta.setCalle(req.calle().trim());
        boleta.setRegion(req.region());
        boleta.setComuna(req.comuna());

        // Si quien compra inicio sesion, la boleta queda asociada a su cuenta
        if (correoUsuario != null) {
            usuarioRepo.findByCorreoIgnoreCase(correoUsuario).ifPresent(boleta::setUsuario);
        }

        int total = 0;
        for (Map.Entry<Long, Integer> linea : agruparPorProducto(req).entrySet()) {
            Producto producto = productoRepo.findById(linea.getKey())
                    .orElseThrow(() -> new RecursoNoEncontradoException("El producto " + linea.getKey() + " no existe"));

            // Si falta stock de un producto no se compra nada, la transaccion deshace lo ya descontado
            if (producto.getStock() < linea.getValue()) {
                throw new ReglaNegocioException("Stock insuficiente para " + producto.getNombre()
                        + " (disponible: " + producto.getStock() + ", solicitado: " + linea.getValue() + ")");
            }
            producto.setStock(producto.getStock() - linea.getValue());

            DetalleBoleta detalle = new DetalleBoleta(producto, linea.getValue(), producto.precioFinal());
            boleta.agregarDetalle(detalle);
            total += detalle.getSubtotal();
        }
        boleta.setTotal(total);

        // Al guardar la boleta se guardan tambien sus detalles
        return BoletaResponse.from(boletaRepo.save(boleta));
    }

    public List<BoletaResponse> listar() {
        return boletaRepo.findAllByOrderByFechaDesc().stream().map(BoletaResponse::from).toList();
    }

    // El administrador y el vendedor ven cualquier boleta, un cliente solo las suyas
    public BoletaResponse obtener(Long id, String correoUsuario, boolean esPersonal) {
        Boleta boleta = buscar(id);
        boolean esDueno = boleta.getUsuario() != null && boleta.getUsuario().getCorreo().equalsIgnoreCase(correoUsuario);
        if (!esPersonal && !esDueno) {
            throw new AccessDeniedException("La boleta no pertenece al usuario");
        }
        return BoletaResponse.from(boleta);
    }

    public List<BoletaResponse> porUsuario(Long usuarioId) {
        return boletaRepo.findByUsuarioIdOrderByFechaDesc(usuarioId).stream().map(BoletaResponse::from).toList();
    }

    public List<BoletaResponse> misCompras(String correoUsuario) {
        Usuario usuario = usuarioRepo.findByCorreoIgnoreCase(correoUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return porUsuario(usuario.getId());
    }

    // Una boleta anulada no se puede modificar y al anular una boleta el stock vuelve al inventario
    @Transactional
    public BoletaResponse cambiarEstado(Long id, EstadoBoleta nuevo) {
        Boleta boleta = buscar(id);
        if (boleta.getEstado() == EstadoBoleta.ANULADA) {
            throw new ReglaNegocioException("La boleta " + id + " está anulada y no se puede modificar");
        }
        if (nuevo == EstadoBoleta.ANULADA) {
            boleta.getDetalles().forEach(d -> d.getProducto().setStock(d.getProducto().getStock() + d.getCantidad()));
        }
        boleta.setEstado(nuevo);
        return BoletaResponse.from(boleta);
    }

    private Boleta buscar(Long id) {
        return boletaRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La boleta " + id + " no existe"));
    }

    // Si el mismo producto viene dos veces en el carrito, se suman sus cantidades
    private Map<Long, Integer> agruparPorProducto(CompraRequest req) {
        Map<Long, Integer> lineas = new LinkedHashMap<>();
        for (ItemCompraRequest item : req.items()) {
            lineas.merge(item.productoId(), item.cantidad(), Integer::sum);
        }
        return lineas;
    }
}
