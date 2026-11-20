package cl.irondisk.backend.controller;

import cl.irondisk.backend.config.OpenApiConfig;
import cl.irondisk.backend.dto.PerfilRequest;
import cl.irondisk.backend.dto.UsuarioResponse;
import cl.irondisk.backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Perfil del usuario que inicio sesion, cualquier rol
@RestController
@RequestMapping("/api/v1/perfil")
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.BEARER)
@Tag(name = "Perfil", description = "Datos propios del usuario que inició sesión")
public class PerfilController {

    private final UsuarioService service;

    @GetMapping
    @Operation(summary = "Ver mi perfil (con sesión)")
    public UsuarioResponse ver(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails usuario) {
        return service.perfil(usuario.getUsername());
    }

    @PutMapping
    @Operation(summary = "Editar mi perfil (con sesión)",
            description = "El RUN y el correo no se pueden cambiar. Si la contraseña viene vacía no se modifica.")
    public UsuarioResponse editar(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails usuario,
                                  @Valid @RequestBody PerfilRequest req) {
        return service.actualizarPerfil(usuario.getUsername(), req);
    }
}
