package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.BoletaResponse;
import cl.irondisk.backend.dto.CompraRequest;
import cl.irondisk.backend.dto.ItemCompraRequest;
import cl.irondisk.backend.exception.ReglaNegocioException;
import cl.irondisk.backend.model.Boleta;
import cl.irondisk.backend.model.Categoria;
import cl.irondisk.backend.model.DetalleBoleta;
import cl.irondisk.backend.model.EstadoBoleta;
import cl.irondisk.backend.model.Producto;
import cl.irondisk.backend.model.Rol;
import cl.irondisk.backend.model.Usuario;
import cl.irondisk.backend.repository.BoletaRepository;
import cl.irondisk.backend.repository.ProductoRepository;
import cl.irondisk.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas de la logica de compras y boletas, los repositorios se reemplazan por mocks
@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepo;
    @Mock
    private ProductoRepository productoRepo;
    @Mock
    private UsuarioRepository usuarioRepo;
    @InjectMocks
    private BoletaService service;

    private Producto disco;

    @BeforeEach
    void preparar() {
        Categoria metal = Categoria.builder().id(1L).nombre("Metal").build();
        // Con 15% de oferta el precio final es 28042
        disco = Producto.builder().id(1L).codigo("IM-001").nombre("The Number of the Beast").artista("Iron Maiden")
                .formato("Vinilo").precio(32990).ofertaPorcentaje(15).stock(12).stockCritico(3).categoria(metal).build();
    }

    private CompraRequest compra(int cantidad) {
        return new CompraRequest("Camila", "Soto Rojas", "camila.soto@gmail.com", "Calle Valparaiso 567",
                "Valparaiso", "Vina del Mar", List.of(new ItemCompraRequest(1L, cantidad)));
    }

    private Boleta boletaConDetalle(EstadoBoleta estado, int cantidad, Usuario dueno) {
        Boleta boleta = new Boleta();
        boleta.setId(7L);
        boleta.setFecha(LocalDateTime.now());
        boleta.setEstado(estado);
        boleta.setUsuario(dueno);
        boleta.setClienteNombre("Camila");
        boleta.setClienteApellidos("Soto Rojas");
        boleta.setClienteCorreo("camila.soto@gmail.com");
        boleta.setCalle("Calle Valparaiso 567");
        boleta.setRegion("Valparaiso");
        boleta.setComuna("Vina del Mar");
        boleta.agregarDetalle(new DetalleBoleta(disco, cantidad, disco.precioFinal()));
        boleta.setTotal(cantidad * disco.precioFinal());
        return boleta;
    }

    @Test
    void calculaElTotalConElPrecioConOferta() {
        when(productoRepo.findById(1L)).thenReturn(Optional.of(disco));
        when(boletaRepo.save(any(Boleta.class))).thenAnswer(i -> i.getArgument(0));

        BoletaResponse respuesta = service.crearCompra(compra(2), null);

        assertThat(respuesta.total()).isEqualTo(2 * 28042);
        assertThat(respuesta.items().get(0).precio()).isEqualTo(28042);
    }

    @Test
    void descuentaElStockDeLoComprado() {
        when(productoRepo.findById(1L)).thenReturn(Optional.of(disco));
        when(boletaRepo.save(any(Boleta.class))).thenAnswer(i -> i.getArgument(0));

        service.crearCompra(compra(2), null);

        assertThat(disco.getStock()).isEqualTo(10);
    }

    @Test
    void sinStockNoCreaLaBoletaNiDescuenta() {
        disco.setStock(2);
        when(productoRepo.findById(1L)).thenReturn(Optional.of(disco));

        assertThatThrownBy(() -> service.crearCompra(compra(3), null))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Stock insuficiente");
        verify(boletaRepo, never()).save(any());
        assertThat(disco.getStock()).isEqualTo(2);
    }

    @Test
    void anularUnaBoletaDevuelveElStock() {
        disco.setStock(10);
        Boleta boleta = boletaConDetalle(EstadoBoleta.PAGADA, 2, null);
        when(boletaRepo.findById(7L)).thenReturn(Optional.of(boleta));

        service.cambiarEstado(7L, EstadoBoleta.ANULADA);

        assertThat(disco.getStock()).isEqualTo(12);
        assertThat(boleta.getEstado()).isEqualTo(EstadoBoleta.ANULADA);
    }

    @Test
    void unaBoletaAnuladaNoSePuedeModificar() {
        Boleta boleta = boletaConDetalle(EstadoBoleta.ANULADA, 1, null);
        when(boletaRepo.findById(7L)).thenReturn(Optional.of(boleta));

        assertThatThrownBy(() -> service.cambiarEstado(7L, EstadoBoleta.ENVIADA))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("anulada");
    }

    @Test
    void unClienteNoVeLaBoletaDeOtro() {
        Usuario dueno = Usuario.builder().id(4L).correo("camila.soto@gmail.com").rol(Rol.CLIENTE).build();
        Boleta boleta = boletaConDetalle(EstadoBoleta.PAGADA, 1, dueno);
        when(boletaRepo.findById(7L)).thenReturn(Optional.of(boleta));

        assertThatThrownBy(() -> service.obtener(7L, "otro.cliente@gmail.com", false))
                .isInstanceOf(AccessDeniedException.class);
    }
}
