package cl.irondisk.backend.controller;

import cl.irondisk.backend.dto.AuthResponse;
import cl.irondisk.backend.dto.LoginRequest;
import cl.irondisk.backend.dto.RefreshRequest;
import cl.irondisk.backend.dto.RegistroRequest;
import cl.irondisk.backend.dto.UsuarioResponse;
import cl.irondisk.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// Endpoints de autenticacion
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registro(@Valid @RequestBody RegistroRequest req) {
        return service.registro(req);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return service.refresh(req.refreshToken());
    }

    // El frontend lo usa al recargar la pagina para recuperar al usuario de la sesion
    @GetMapping("/me")
    public UsuarioResponse me(@AuthenticationPrincipal UserDetails usuario) {
        return service.usuarioActual(usuario.getUsername());
    }
}
