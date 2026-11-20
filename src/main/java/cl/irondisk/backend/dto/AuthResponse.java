package cl.irondisk.backend.dto;

// Respuesta del login, el registro y la renovacion de sesion
public record AuthResponse(
        String accessToken,
        String refreshToken,
        long expiraEnMs,
        UsuarioResponse usuario
) {
}
