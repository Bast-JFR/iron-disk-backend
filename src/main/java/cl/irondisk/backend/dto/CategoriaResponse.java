package cl.irondisk.backend.dto;

import cl.irondisk.backend.model.Categoria;

// Categoria que sale por la API, incluye cuantos productos tiene
public record CategoriaResponse(Long id, String nombre, String descripcion, String imagen, long totalProductos) {

    public static CategoriaResponse from(Categoria c, long totalProductos) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion(), c.getImagen(), totalProductos);
    }
}
