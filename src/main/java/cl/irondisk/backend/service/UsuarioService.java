package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.RegistroRequest;
import cl.irondisk.backend.exception.DatosInvalidosException;
import cl.irondisk.backend.exception.ReglaNegocioException;
import cl.irondisk.backend.model.Rol;
import cl.irondisk.backend.model.Usuario;
import cl.irondisk.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// Logica de negocio de los usuarios
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository repo;
    private final PasswordEncoder passwordEncoder;

    // El registro publico siempre crea un cliente, sin importar lo que envie el frontend
    @Transactional
    public Usuario crearCliente(RegistroRequest req) {
        Usuario u = new Usuario();
        aplicarDatos(u, req.run(), req.nombre(), req.apellidos(), req.correo(), req.region(), req.comuna(),
                req.direccion(), req.fechaNacimiento());
        u.setRol(Rol.CLIENTE);
        u.setPassword(encriptar(req.password()));
        return repo.save(u);
    }

    // Valida las reglas del usuario y copia los datos a la entidad
    private void aplicarDatos(Usuario u, String run, String nombre, String apellidos, String correo,
                              String region, String comuna, String direccion, String fechaNacimiento) {
        String runNormalizado = ValidadorUsuario.normalizarRun(run);
        if (!ValidadorUsuario.runValido(runNormalizado)) {
            throw new DatosInvalidosException("El RUN no es válido. Va sin puntos ni guion, por ejemplo 19011022K");
        }
        repo.findByRun(runNormalizado)
                .filter(otro -> !otro.getId().equals(u.getId()))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un usuario con el RUN " + runNormalizado);
                });

        String correoNormalizado = correo.trim().toLowerCase();
        if (!ValidadorUsuario.dominioPermitido(correoNormalizado)) {
            throw new DatosInvalidosException("Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com");
        }
        repo.findByCorreoIgnoreCase(correoNormalizado)
                .filter(otro -> !otro.getId().equals(u.getId()))
                .ifPresent(otro -> {
                    throw new ReglaNegocioException("Ya existe un usuario con el correo " + correoNormalizado);
                });

        u.setRun(runNormalizado);
        u.setCorreo(correoNormalizado);
        u.setNombre(nombre.trim());
        u.setApellidos(apellidos.trim());
        u.setRegion(region);
        u.setComuna(comuna);
        u.setDireccion(direccion.trim());
        u.setFechaNacimiento(parsearFecha(fechaNacimiento));
    }

    // La contraseña debe tener entre 4 y 10 caracteres y se guarda con BCrypt
    private String encriptar(String password) {
        if (password == null || password.length() < 4 || password.length() > 10) {
            throw new DatosInvalidosException("La contraseña debe tener entre 4 y 10 caracteres");
        }
        return passwordEncoder.encode(password);
    }

    private static LocalDate parsearFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }
        try {
            LocalDate f = LocalDate.parse(fecha);
            if (f.isAfter(LocalDate.now())) {
                throw new DatosInvalidosException("La fecha de nacimiento no puede ser futura");
            }
            return f;
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("La fecha de nacimiento debe tener el formato AAAA-MM-DD");
        }
    }
}
