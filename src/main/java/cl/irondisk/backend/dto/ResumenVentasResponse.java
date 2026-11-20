package cl.irondisk.backend.dto;

import java.util.List;

// Resumen de ventas para el administrador, no cuenta las boletas anuladas
public record ResumenVentasResponse(
        long totalVendido,
        long cantidadBoletas,
        long ticketPromedio,
        List<ProductoVendidoResponse> masVendidos
) {
}
