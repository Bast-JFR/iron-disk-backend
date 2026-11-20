package cl.irondisk.backend.repository;

import cl.irondisk.backend.model.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Consultas de boletas
public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    List<Boleta> findAllByOrderByFechaDesc();

    List<Boleta> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}
