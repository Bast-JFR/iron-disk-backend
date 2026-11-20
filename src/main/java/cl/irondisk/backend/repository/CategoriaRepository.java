package cl.irondisk.backend.repository;

import cl.irondisk.backend.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Consultas de categorias
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    List<Categoria> findAllByOrderByIdAsc();
}
