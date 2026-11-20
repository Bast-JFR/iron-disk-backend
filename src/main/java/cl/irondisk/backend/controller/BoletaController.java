package cl.irondisk.backend.controller;

import cl.irondisk.backend.config.OpenApiConfig;
import cl.irondisk.backend.dto.BoletaResponse;
import cl.irondisk.backend.dto.CompraRequest;
import cl.irondisk.backend.dto.EstadoRequest;
import cl.irondisk.backend.service.BoletaService;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Compras y boletas
@RestController
@RequestMapping("/api/v1/boletas")
@RequiredArgsConstructor
@Tag(name = "Boletas", description = "Compras de la tienda y gestión de órdenes")
public class BoletaController {

    private final BoletaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar una compra (público)",
            description = "Recibe solo el producto y la cantidad, el precio y el total los calcula el backend. "
                    + "Si la petición trae token, la boleta queda asociada al usuario.")
    @ApiResponse(responseCode = "404", description = "Un producto no existe")
    @ApiResponse(responseCode = "409", description = "Stock insuficiente, no se descuenta nada")
    public BoletaResponse comprar(@Valid @RequestBody CompraRequest req,
                                  @Parameter(hidden = true) @AuthenticationPrincipal UserDetails usuario) {
        return service.crearCompra(req, usuario != null ? usuario.getUsername() : null);
    }

    @GetMapping("/mias")
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Mis compras (con sesión)")
    public List<BoletaResponse> misCompras(@Parameter(hidden = true) @AuthenticationPrincipal UserDetails usuario) {
        return service.misCompras(usuario.getUsername());
    }

    @GetMapping
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Listar todas las boletas (ADMIN y VENDEDOR)")
    public List<BoletaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Ver una boleta con su detalle (ADMIN, VENDEDOR o el cliente dueño)")
    @ApiResponse(responseCode = "403", description = "Un cliente intenta ver una boleta ajena")
    @ApiResponse(responseCode = "404", description = "La boleta no existe")
    public BoletaResponse obtener(@PathVariable Long id,
                                  @Parameter(hidden = true) @AuthenticationPrincipal UserDetails usuario) {
        boolean esPersonal = usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_VENDEDOR"));
        return service.obtener(id, usuario.getUsername(), esPersonal);
    }

    @GetMapping("/usuario/{usuarioId}")
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Historial de compras de un usuario (ADMIN)")
    public List<BoletaResponse> porUsuario(@PathVariable Long usuarioId) {
        return service.porUsuario(usuarioId);
    }

    @PatchMapping("/{id}/estado")
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Cambiar el estado: PAGADA, ENVIADA, ENTREGADA o ANULADA (ADMIN)",
            description = "Al anular, el stock vuelve al inventario. Una boleta anulada no se puede modificar.")
    @ApiResponse(responseCode = "409", description = "La boleta ya está anulada")
    public BoletaResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest req) {
        return service.cambiarEstado(id, req.estado());
    }
}
