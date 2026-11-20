package cl.irondisk.backend.dto;

import cl.irondisk.backend.model.EstadoBoleta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

// Estado nuevo de una boleta
public record EstadoRequest(
        @NotNull(message = "El estado es obligatorio") @Schema(example = "ENVIADA") EstadoBoleta estado
) {
}
