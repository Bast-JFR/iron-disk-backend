package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.BoletaResponse;
import cl.irondisk.backend.dto.CompraRequest;
import cl.irondisk.backend.dto.ItemCompraRequest;
import cl.irondisk.backend.exception.DatosInvalidosException;
import cl.irondisk.backend.exception.RecursoNoEncontradoException;
import cl.irondisk.backend.model.Boleta;
import cl.irondisk.backend.model.DetalleBoleta;
import cl.irondisk.backend.model.EstadoBoleta;
import cl.irondisk.backend.model.Producto;
import cl.irondisk.backend.repository.BoletaRepository;
import cl.irondisk.backend.repository.ProductoRepository;
import cl.irondisk.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// Logica de negocio de las compras y las boletas
// Reglas: el precio siempre sale de la base de datos y nunca del frontend, y el total lo calcula el backend
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
            DetalleBoleta detalle = new DetalleBoleta(producto, linea.getValue(), producto.precioFinal());
            boleta.agregarDetalle(detalle);
            total += detalle.getSubtotal();
        }
        boleta.setTotal(total);

        // Al guardar la boleta se guardan tambien sus detalles
        return BoletaResponse.from(boletaRepo.save(boleta));
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
