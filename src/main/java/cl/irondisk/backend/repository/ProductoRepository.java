package cl.irondisk.backend.repository;

import cl.irondisk.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Consultas de productos
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoIgnoreCase(String codigo);

    List<Producto> findAllByOrderByIdAsc();

    boolean existsByCategoriaId(Long categoriaId);
}
