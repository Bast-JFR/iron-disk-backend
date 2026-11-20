package cl.irondisk.backend.repository;

import cl.irondisk.backend.dto.ProductoVendidoResponse;
import cl.irondisk.backend.model.DetalleBoleta;
import cl.irondisk.backend.model.EstadoBoleta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Consultas del detalle de las boletas
public interface DetalleBoletaRepository extends JpaRepository<DetalleBoleta, Long> {

    boolean existsByProductoId(Long productoId);

    // Productos ordenados por unidades vendidas, sin contar las boletas con el estado indicado
    @Query("select new cl.irondisk.backend.dto.ProductoVendidoResponse(d.producto.id, d.producto.codigo, d.nombreProducto, sum(d.cantidad)) "
            + "from DetalleBoleta d where d.boleta.estado <> :estado "
            + "group by d.producto.id, d.producto.codigo, d.nombreProducto "
            + "order by sum(d.cantidad) desc")
    List<ProductoVendidoResponse> masVendidos(@Param("estado") EstadoBoleta estado, Pageable pageable);
}
