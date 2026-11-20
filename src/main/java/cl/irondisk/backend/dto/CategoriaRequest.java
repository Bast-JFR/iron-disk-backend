package cl.irondisk.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datos para crear o editar una categoria
public record CategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 50, message = "El nombre no puede superar los 50 caracteres") String nombre,
        @Size(max = 300, message = "La descripción no puede superar los 300 caracteres") String descripcion,
        @Size(max = 300, message = "La ruta de la imagen no puede superar los 300 caracteres") String imagen
) {
}
