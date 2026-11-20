package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.AuthResponse;
import cl.irondisk.backend.dto.LoginRequest;
import cl.irondisk.backend.dto.RegistroRequest;
import cl.irondisk.backend.dto.UsuarioResponse;
import cl.irondisk.backend.exception.CredencialesInvalidasException;
import cl.irondisk.backend.model.Usuario;
import cl.irondisk.backend.repository.UsuarioRepository;
import cl.irondisk.backend.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Inicio de sesion, registro y renovacion de tokens. Si las credenciales fallan se responde siempre el mismo mensaje
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

    // Recibe un token de renovacion valido y entrega un par de tokens nuevo
    public AuthResponse refresh(String refreshToken) {
        Claims claims;
        try {
            claims = jwtService.leer(refreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CredencialesInvalidasException("Tu sesión expiró, vuelve a iniciar sesión");
        }
        // Un token de acceso no sirve para renovar la sesion
        if (!JwtService.TIPO_REFRESH.equals(claims.get(JwtService.CLAIM_TIPO))) {
            throw new CredencialesInvalidasException("El token enviado no es un token de renovación");
        }
        Usuario u = usuarioRepo.findByCorreoIgnoreCase(claims.getSubject())
                .orElseThrow(() -> new CredencialesInvalidasException("El usuario de la sesión ya no existe"));
        return emitirTokens(u);
    }

    // Datos del usuario dueño del token
    public UsuarioResponse usuarioActual(String correo) {
        return usuarioRepo.findByCorreoIgnoreCase(correo)
                .map(UsuarioResponse::from)
                .orElseThrow(() -> new CredencialesInvalidasException("El usuario de la sesión ya no existe"));
    }

    private AuthResponse emitirTokens(Usuario u) {
        return new AuthResponse(
                jwtService.generarAccessToken(u),
                jwtService.generarRefreshToken(u),
                jwtService.getAccessMs(),
                UsuarioResponse.from(u));
    }
}
