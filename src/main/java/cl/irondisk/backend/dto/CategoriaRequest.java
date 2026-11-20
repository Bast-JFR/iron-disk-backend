package cl.irondisk.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datos para crear o editar una categoria
public record CategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 50, message = "El nombre no puede superar los 50 caracteres") @Schema(example = "Metal") String nombre,
        @Size(max = 300, message = "La descripción no puede superar los 300 caracteres") @Schema(example = "Del heavy clásico al thrash y el power metal.") String descripcion,
        @Size(max = 300, message = "La ruta de la imagen no puede superar los 300 caracteres") @Schema(example = "img/fondo-concierto1.jpg") String imagen
) {
}
