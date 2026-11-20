package cl.irondisk.backend.service;

import cl.irondisk.backend.exception.ReglaNegocioException;
import cl.irondisk.backend.model.Categoria;
import cl.irondisk.backend.repository.CategoriaRepository;
import cl.irondisk.backend.repository.ProductoRepository;
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

// Pruebas de la logica de categorias, los repositorios se reemplazan por mocks
@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repo;
    @Mock
    private ProductoRepository productoRepo;
    @InjectMocks
    private CategoriaService service;

    private Categoria categoria(Long id, String nombre) {
        return Categoria.builder().id(id).nombre(nombre).build();
    }

    @Test
    void noEliminaUnaCategoriaQueTieneProductos() {
        when(repo.findById(1L)).thenReturn(Optional.of(categoria(1L, "Metal")));
        when(productoRepo.existsByCategoriaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.eliminar(1L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("tiene productos");
        verify(repo, never()).delete(any());
    }

}
