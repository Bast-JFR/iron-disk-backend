package cl.irondisk.backend.exception;

import java.time.LocalDateTime;
import java.util.Map;

// Forma unica de todos los errores de la API, el frontend muestra el campo mensaje
public record ApiError(int status, String mensaje, Map<String, String> errores, LocalDateTime fecha) {

    public static ApiError de(int status, String mensaje, Map<String, String> errores) {
        return new ApiError(status, mensaje, errores, LocalDateTime.now());
    }
}
