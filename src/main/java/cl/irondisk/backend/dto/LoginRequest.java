package cl.irondisk.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

// Datos para iniciar sesion
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio") @Schema(example = "camila.soto@gmail.com") String correo,
        @NotBlank(message = "La contraseña es obligatoria") @Schema(example = "cliente1") String password
) {
}
