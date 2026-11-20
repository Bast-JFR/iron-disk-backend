package cl.irondisk.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Tabla producto del modelo de datos, muchos discos pertenecen a una categoria
@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String artista;

    // Vinilo, CD o Cassette
    @Column(nullable = false, length = 20)
    private String formato;

    @Column(length = 500)
    private String descripcion;

    // Precio normal en pesos chilenos
    @Column(nullable = false)
    private Integer precio;

    // Descuento sobre el precio, cero significa sin oferta
    @Column(nullable = false)
    private Integer ofertaPorcentaje;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Integer stockCritico;

    // Ruta de la imagen que muestra el frontend
    @Column(length = 300)
    private String imagen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public boolean enOferta() {
        return ofertaPorcentaje != null && ofertaPorcentaje > 0;
    }

    // Precio que realmente se cobra, con el descuento aplicado
    public int precioFinal() {
        return enOferta() ? Math.round(precio * (100 - ofertaPorcentaje) / 100f) : precio;
    }

    // Un producto es critico cuando su stock llega al nivel critico
    public boolean critico() {
        return stock <= stockCritico;
    }
}
