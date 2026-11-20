package cl.irondisk.backend.dto;

// Un producto del ranking de mas vendidos con las unidades que se vendieron
public record ProductoVendidoResponse(Long productoId, String codigo, String nombre, Long unidades) {
}
