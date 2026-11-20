package cl.irondisk.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Datos para crear o editar un producto
public record ProductoRequest(
        @NotBlank(message = "El código es obligatorio") @Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres") @Schema(example = "IM-001") String codigo,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede superar los 100 caracteres") @Schema(example = "The Number of the Beast") String nombre,
        @NotBlank(message = "El artista es obligatorio") @Size(max = 100, message = "El artista no puede superar los 100 caracteres") @Schema(example = "Iron Maiden") String artista,
        @NotBlank(message = "El formato es obligatorio") @Pattern(regexp = "Vinilo|CD|Cassette", message = "El formato debe ser Vinilo, CD o Cassette") @Schema(example = "Vinilo") String formato,
        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres") @Schema(example = "Clásico de 1982 con Run to the Hills.") String descripcion,
        @NotNull(message = "El precio es obligatorio") @Min(value = 0, message = "El precio no puede ser menor a 0") @Schema(example = "32990") Integer precio,
        // Descuento en porcentaje, si no se envia es cero
        @Min(value = 0, message = "La oferta no puede ser negativa") @Max(value = 90, message = "La oferta no puede superar el 90%") @Schema(example = "15") Integer ofertaPorcentaje,
        @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser menor a 0") @Schema(example = "12") Integer stock,
        // Si no se envia queda en cero
        @Min(value = 0, message = "El stock crítico no puede ser menor a 0") @Schema(example = "3") Integer stockCritico,
        @Size(max = 300, message = "La ruta de la imagen no puede superar los 300 caracteres") @Schema(example = "img/maiden-beast.png") String imagen,
        @NotNull(message = "La categoría es obligatoria") @Schema(example = "1") Long categoriaId
) {
}
