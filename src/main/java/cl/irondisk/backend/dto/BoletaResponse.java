package cl.irondisk.backend.dto;

import cl.irondisk.backend.model.Boleta;

import java.time.LocalDateTime;
import java.util.List;

// Boleta con su detalle, es la forma que usa el frontend
public record BoletaResponse(
        Long id,
        LocalDateTime fecha,
        String estado,
        Integer total,
        Long usuarioId,
        Cliente cliente,
        Envio envio,
        List<Item> items
) {

    public record Cliente(String nombre, String apellidos, String correo) {
    }

    public record Envio(String calle, String region, String comuna) {
    }

    public record Item(Long productoId, String codigo, String nombre, String artista, String imagen,
                       Integer precio, Integer cantidad, Integer subtotal) {
    }

    // Se debe llamar dentro de una transaccion porque lee el usuario, los detalles y los productos
    public static BoletaResponse from(Boleta b) {
        return new BoletaResponse(
                b.getId(), b.getFecha(), b.getEstado().name(), b.getTotal(),
                b.getUsuario() != null ? b.getUsuario().getId() : null,
                new Cliente(b.getClienteNombre(), b.getClienteApellidos(), b.getClienteCorreo()),
                new Envio(b.getCalle(), b.getRegion(), b.getComuna()),
                b.getDetalles().stream()
                        .map(d -> new Item(d.getProducto().getId(), d.getProducto().getCodigo(), d.getNombreProducto(),
                                d.getProducto().getArtista(), d.getProducto().getImagen(),
                                d.getPrecioUnitario(), d.getCantidad(), d.getSubtotal()))
                        .toList());
    }
}
