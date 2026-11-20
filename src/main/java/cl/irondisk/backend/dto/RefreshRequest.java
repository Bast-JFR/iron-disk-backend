package cl.irondisk.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

// Token de renovacion que envia el frontend cuando el de acceso vencio
public record RefreshRequest(
        @NotBlank(message = "El token de renovación es obligatorio") @Schema(example = "eyJhbGciOiJIUzUxMiJ9...") String refreshToken
) {
}
