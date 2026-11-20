package cl.irondisk.backend.dto;

import jakarta.validation.constraints.NotBlank;

// Token de renovacion que envia el frontend cuando el de acceso vencio
public record RefreshRequest(
        @NotBlank(message = "El token de renovación es obligatorio") String refreshToken
) {
}
