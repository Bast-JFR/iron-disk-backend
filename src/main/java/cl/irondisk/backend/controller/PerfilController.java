package cl.irondisk.backend.controller;

import cl.irondisk.backend.dto.PerfilRequest;
import cl.irondisk.backend.dto.UsuarioResponse;
import cl.irondisk.backend.service.UsuarioService;
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
public class PerfilController {

    private final UsuarioService service;

    @GetMapping
    public UsuarioResponse ver(@AuthenticationPrincipal UserDetails usuario) {
        return service.perfil(usuario.getUsername());
    }

    @PutMapping
    public UsuarioResponse editar(@AuthenticationPrincipal UserDetails usuario, @Valid @RequestBody PerfilRequest req) {
        return service.actualizarPerfil(usuario.getUsername(), req);
    }
}
