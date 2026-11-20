package cl.irondisk.backend.repository;

import cl.irondisk.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Consultas de usuarios, Spring arma el SQL a partir del nombre de cada metodo
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    Optional<Usuario> findByRun(String run);

    List<Usuario> findAllByOrderByIdAsc();
}
