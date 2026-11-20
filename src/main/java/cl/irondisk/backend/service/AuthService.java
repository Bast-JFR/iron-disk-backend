package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.AuthResponse;
import cl.irondisk.backend.dto.LoginRequest;
import cl.irondisk.backend.dto.RegistroRequest;
import cl.irondisk.backend.dto.UsuarioResponse;
import cl.irondisk.backend.exception.CredencialesInvalidasException;
import cl.irondisk.backend.model.Usuario;
import cl.irondisk.backend.repository.UsuarioRepository;
import cl.irondisk.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Inicio de sesion y registro. Si las credenciales fallan se responde siempre el mismo mensaje
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";

    private final UsuarioRepository usuarioRepo;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest req) {
        Usuario u = usuarioRepo.findByCorreoIgnoreCase(req.correo().trim())
                .filter(usuario -> passwordEncoder.matches(req.password(), usuario.getPassword()))
                .orElseThrow(() -> new CredencialesInvalidasException(CREDENCIALES_INVALIDAS));
        return emitirTokens(u);
    }

    // Despues de registrarse el usuario queda con la sesion iniciada
    @Transactional
    public AuthResponse registro(RegistroRequest req) {
        return emitirTokens(usuarioService.crearCliente(req));
    }

    private AuthResponse emitirTokens(Usuario u) {
        return new AuthResponse(
                jwtService.generarAccessToken(u),
                jwtService.generarRefreshToken(u),
                jwtService.getAccessMs(),
                UsuarioResponse.from(u));
    }
}
