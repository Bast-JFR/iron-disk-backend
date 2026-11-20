package cl.irondisk.backend.dto;

import jakarta.validation.constraints.NotBlank;

// Datos para iniciar sesion
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio") String correo,
        @NotBlank(message = "La contraseña es obligatoria") String password
) {
}
