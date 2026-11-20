package cl.irondisk.backend.repository;

import cl.irondisk.backend.model.DetalleBoleta;
import org.springframework.data.jpa.repository.JpaRepository;

// Consultas del detalle de las boletas
public interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta, Long> {

    boolean existsByProductoId(Long productoId);
}
