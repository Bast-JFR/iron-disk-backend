package cl.irondisk.backend.dto;

import cl.irondisk.backend.model.Usuario;

import java.time.LocalDate;

// Datos de un usuario que salen por la API, nunca incluye la contraseña
public record UsuarioResponse(
        Long id,
        String run,
        String nombre,
        String apellidos,
        String correo,
        String rol,
        LocalDate fechaNacimiento,
        String region,
        String comuna,
        String direccion
) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getRun(), u.getNombre(), u.getApellidos(), u.getCorreo(),
                u.getRol().name(), u.getFechaNacimiento(), u.getRegion(), u.getComuna(), u.getDireccion());
    }
}
