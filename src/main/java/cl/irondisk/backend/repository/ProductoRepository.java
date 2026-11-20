package cl.irondisk.backend.repository;

import cl.irondisk.backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// Consultas de productos
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoIgnoreCase(String codigo);

    List<Producto> findAllByOrderByIdAsc();

    boolean existsByCategoriaId(Long categoriaId);

    long countByCategoriaId(Long categoriaId);

    // Catalogo con filtros opcionales, un filtro en null no se aplica
    @Query("select p from Producto p "
            + "where (:categoriaId is null or p.categoria.id = :categoriaId) "
            + "and (:formato is null or p.formato = :formato) "
            + "and (:texto is null or lower(p.nombre) like :texto or lower(p.artista) like :texto) "
            + "order by p.id")
    List<Producto> buscar(@Param("categoriaId") Long categoriaId,
                          @Param("formato") String formato,
                          @Param("texto") String texto);

    // Productos con descuento
    List<Producto> findByOfertaPorcentajeGreaterThanOrderByIdAsc(Integer porcentaje);

    // Productos cuyo stock llego al nivel critico
    @Query("select p from Producto p where p.stock <= p.stockCritico order by p.id")
    List<Producto> findCriticos();
}
