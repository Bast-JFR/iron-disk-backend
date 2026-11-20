package cl.irondisk.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Datos para crear o editar un producto
public record ProductoRequest(
        @NotBlank(message = "El código es obligatorio") @Size(min = 3, max = 20, message = "El código debe tener entre 3 y 20 caracteres") String codigo,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 100, message = "El nombre no puede superar los 100 caracteres") String nombre,
        @NotBlank(message = "El artista es obligatorio") @Size(max = 100, message = "El artista no puede superar los 100 caracteres") String artista,
        @NotBlank(message = "El formato es obligatorio") @Pattern(regexp = "Vinilo|CD|Cassette", message = "El formato debe ser Vinilo, CD o Cassette") String formato,
        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres") String descripcion,
        @NotNull(message = "El precio es obligatorio") @Min(value = 0, message = "El precio no puede ser menor a 0") Integer precio,
        // Descuento en porcentaje, si no se envia es cero
        @Min(value = 0, message = "La oferta no puede ser negativa") @Max(value = 90, message = "La oferta no puede superar el 90%") Integer ofertaPorcentaje,
        @NotNull(message = "El stock es obligatorio") @Min(value = 0, message = "El stock no puede ser menor a 0") Integer stock,
        // Si no se envia queda en cero
        @Min(value = 0, message = "El stock crítico no puede ser menor a 0") Integer stockCritico,
        @Size(max = 300, message = "La ruta de la imagen no puede superar los 300 caracteres") String imagen,
        @NotNull(message = "La categoría es obligatoria") Long categoriaId
) {
}
