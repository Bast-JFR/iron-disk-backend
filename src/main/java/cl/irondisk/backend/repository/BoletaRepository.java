package cl.irondisk.backend.repository;

import cl.irondisk.backend.model.Boleta;
import cl.irondisk.backend.model.EstadoBoleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Consultas de boletas
public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    List<Boleta> findAllByOrderByFechaDesc();

    List<Boleta> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);

    // Cantidad de boletas que no tienen el estado indicado, se usa para no contar las anuladas
    long countByEstadoNot(EstadoBoleta estado);

    // Suma de los totales de las boletas que no tienen el estado indicado
    @Query("select coalesce(sum(b.total), 0) from Boleta b where b.estado <> :estado")
    long sumarTotalSinEstado(@Param("estado") EstadoBoleta estado);
}
