package cl.irondisk.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// El frontend solo indica que producto compra y cuantas unidades, el precio lo pone el backend
public record ItemCompraRequest(
        @NotNull(message = "El producto es obligatorio") @Schema(example = "1") Long productoId,
        @NotNull(message = "La cantidad es obligatoria") @Min(value = 1, message = "La cantidad mínima es 1") @Max(value = 99, message = "La cantidad máxima es 99") @Schema(example = "2") Integer cantidad
) {
}
