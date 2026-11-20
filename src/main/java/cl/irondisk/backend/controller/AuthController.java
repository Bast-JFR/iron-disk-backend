package cl.irondisk.backend.controller;

import cl.irondisk.backend.config.OpenApiConfig;
import cl.irondisk.backend.dto.AuthResponse;
import cl.irondisk.backend.dto.LoginRequest;
import cl.irondisk.backend.dto.RefreshRequest;
import cl.irondisk.backend.dto.RegistroRequest;
import cl.irondisk.backend.dto.UsuarioResponse;
import cl.irondisk.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y renovación de tokens JWT")
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión (público)",
            description = "Devuelve un token de acceso de 15 minutos y un token de renovación de 7 días.")
    @ApiResponse(responseCode = "401", description = "Correo o contraseña incorrectos")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una cuenta de cliente (público)",
            description = "Siempre crea un usuario con rol CLIENTE y deja la sesión iniciada.")
    @ApiResponse(responseCode = "409", description = "El RUN o el correo ya están registrados")
    public AuthResponse registro(@Valid @RequestBody RegistroRequest req) {
        return service.registro(req);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar la sesión (público)",
            description = "Recibe un token de renovación y devuelve un par de tokens nuevo. Un token de acceso no sirve aquí.")
    @ApiResponse(responseCode = "401", description = "Token vencido, alterado o que no es de renovación")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return service.refresh(req.refreshToken());
    }

    // El frontend lo usa al recargar la pagina para recuperar al usuario de la sesion
    @GetMapping("/me")
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Datos del usuario de la sesión (con sesión)")
    public UsuarioResponse me(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails usuario) {
        return service.usuarioActual(usuario.getUsername());
    }
}
