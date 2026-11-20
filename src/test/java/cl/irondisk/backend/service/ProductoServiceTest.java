package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.ProductoRequest;
import cl.irondisk.backend.exception.ReglaNegocioException;
import cl.irondisk.backend.model.Categoria;
import cl.irondisk.backend.model.Producto;
import cl.irondisk.backend.repository.CategoriaRepository;
import cl.irondisk.backend.repository.DetalleBoletaRepository;
import cl.irondisk.backend.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas de la logica de productos, los repositorios se reemplazan por mocks
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepo;
    @Mock
    private CategoriaRepository categoriaRepo;
    @Mock
    private DetalleBoletaRepository detalleRepo;
    @InjectMocks
    private ProductoService service;

    private Categoria metal;
    private ProductoRequest request;

    @BeforeEach
    void preparar() {
        metal = Categoria.builder().id(1L).nombre("Metal").build();
        request = new ProductoRequest("im-001", "The Number of the Beast", "Iron Maiden", "Vinilo", "Clásico",
                32990, 15, 12, 3, "img/maiden-beast.png", 1L);
    }

    private Producto existente(Long id, String codigo) {
        return Producto.builder().id(id).codigo(codigo).nombre("Disco").artista("Banda").formato("CD")
                .precio(10000).ofertaPorcentaje(0).stock(5).stockCritico(2).categoria(metal).build();
    }

    @Test
    void noPermiteUnCodigoRepetido() {
        when(productoRepo.findByCodigoIgnoreCase("im-001")).thenReturn(Optional.of(existente(9L, "IM-001")));

        assertThatThrownBy(() -> service.crear(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("IM-001");
        verify(productoRepo, never()).save(any());
    }

    @Test
    void noEliminaUnProductoQueApareceEnBoletas() {
        when(productoRepo.findById(5L)).thenReturn(Optional.of(existente(5L, "IM-001")));
        when(detalleRepo.existsByProductoId(5L)).thenReturn(true);

        assertThatThrownBy(() -> service.eliminar(5L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("boletas");
        verify(productoRepo, never()).delete(any());
    }

}
