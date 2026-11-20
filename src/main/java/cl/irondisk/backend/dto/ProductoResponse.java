package cl.irondisk.backend.dto;

import cl.irondisk.backend.model.Producto;

// Producto que sale por la API, incluye el precio final y datos calculados
public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        String artista,
        String formato,
        String descripcion,
        Integer precio,
        Integer ofertaPorcentaje,
        Integer precioFinal,
        boolean enOferta,
        Integer stock,
        Integer stockCritico,
        boolean critico,
        String imagen,
        Long categoriaId,
        String categoriaNombre
) {

    // Se debe llamar dentro de una transaccion porque lee la categoria
    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(p.getId(), p.getCodigo(), p.getNombre(), p.getArtista(), p.getFormato(),
                p.getDescripcion(), p.getPrecio(), p.getOfertaPorcentaje(), p.precioFinal(), p.enOferta(),
                p.getStock(), p.getStockCritico(), p.critico(), p.getImagen(),
                p.getCategoria().getId(), p.getCategoria().getNombre());
    }
}
