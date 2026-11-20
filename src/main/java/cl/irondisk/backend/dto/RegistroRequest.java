package cl.irondisk.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Registro publico desde la tienda, no trae rol porque siempre se crea un cliente
public record RegistroRequest(
        @NotBlank(message = "El RUN es obligatorio") String run,
        @NotBlank(message = "El nombre es obligatorio") @Size(max = 50, message = "El nombre no puede superar los 50 caracteres") String nombre,
        @NotBlank(message = "Los apellidos son obligatorios") @Size(max = 100, message = "Los apellidos no pueden superar los 100 caracteres") String apellidos,
        @NotBlank(message = "El correo es obligatorio") @Email(message = "El correo no tiene un formato válido") @Size(max = 100, message = "El correo no puede superar los 100 caracteres") String correo,
        @NotBlank(message = "La contraseña es obligatoria") @Size(min = 4, max = 10, message = "La contraseña debe tener entre 4 y 10 caracteres") String password,
        @NotBlank(message = "La región es obligatoria") String region,
        @NotBlank(message = "La comuna es obligatoria") String comuna,
        @NotBlank(message = "La dirección es obligatoria") @Size(max = 300, message = "La dirección no puede superar los 300 caracteres") String direccion,
        // Formato AAAA-MM-DD, es opcional
        String fechaNacimiento
) {
}
