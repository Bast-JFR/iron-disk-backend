package cl.irondisk.backend.dto;

import cl.irondisk.backend.model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Creacion y edicion de usuarios por el administrador
// Al editar, si la contraseña viene vacia no se cambia
public record UsuarioRequest(
        @NotBlank(message = "El RUN es obligatorio") @Schema(example = "204567891") String run,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 50, message = "El nombre no puede superar los 50 caracteres") @Schema(example = "Diego") String nombre,
        @NotBlank(message = "Los apellidos son obligatorios") @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres") @Schema(example = "Pérez Mora") String apellidos,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no tiene un formato válido") @Size(max = 100, message = "El correo no puede superar los 100 caracteres") @Schema(example = "diego.perez@duoc.cl") String correo,
        @Size(max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres") @Schema(example = "vendedor1") String password,
        @NotNull(message = "El rol es obligatorio") @Schema(example = "VENDEDOR") Rol rol,
        @NotBlank(message = "La región es obligatoria") @Schema(example = "Biobío") String region,
        @NotBlank(message = "La comuna es obligatoria") @Schema(example = "Concepción") String comuna,
        @NotBlank(message = "La dirección es obligatoria") @Size(max = 300, message = "La dirección no puede superar los 300 caracteres") @Schema(example = "Barros Arana 890") String direccion,
        // Formato AAAA-MM-DD, es opcional
        @Schema(example = "1995-03-20") String fechaNacimiento
) {
}
