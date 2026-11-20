package cl.irondisk.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datos que un usuario puede cambiar de su propio perfil, el RUN y el correo no se editan
public record PerfilRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 50, message = "El nombre no puede superar los 50 caracteres") String nombre,
        @NotBlank(message = "Los apellidos son obligatorios") @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres") String apellidos,
        // Si viene vacia la contraseña no se cambia
        @Size(max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres") String password,
        @NotBlank(message = "La región es obligatoria") String region,
        @NotBlank(message = "La comuna es obligatoria") String comuna,
        @NotBlank(message = "La dirección es obligatoria") @Size(max = 300, message = "La dirección no puede superar los 300 caracteres") String direccion,
        // Formato AAAA-MM-DD, es opcional
        String fechaNacimiento
) {
}
