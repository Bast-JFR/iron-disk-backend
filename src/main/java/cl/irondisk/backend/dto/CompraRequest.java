package cl.irondisk.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

// Datos de una compra: quien compra, donde se envia y que productos lleva
public record CompraRequest(
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 50, message = "El nombre no puede superar los 50 caracteres") @Schema(example = "Camila") String nombre,
        @NotBlank(message = "Los apellidos son obligatorios") @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres") @Schema(example = "Soto Rojas") String apellidos,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no tiene un formato válido") @Size(max = 100, message = "El correo no puede superar los 100 caracteres") @Schema(example = "camila.soto@gmail.com") String correo,
        @NotBlank(message = "La dirección es obligatoria") @Size(max = 300, message = "La dirección no puede superar los 300 caracteres") @Schema(example = "Calle Valparaíso 567") String calle,
        @NotBlank(message = "La región es obligatoria") @Size(max = 100, message = "La región no puede superar los 100 caracteres") @Schema(example = "Valparaíso") String region,
        @NotBlank(message = "La comuna es obligatoria") @Size(max = 100, message = "La comuna no puede superar los 100 caracteres") @Schema(example = "Viña del Mar") String comuna,
        @NotEmpty(message = "El carrito está vacío") @Size(max = 50, message = "Máximo 50 productos distintos por compra") List<@Valid ItemCompraRequest> items
) {
}
