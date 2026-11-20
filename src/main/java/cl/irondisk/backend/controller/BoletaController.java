package cl.irondisk.backend.controller;

import cl.irondisk.backend.dto.BoletaResponse;
import cl.irondisk.backend.dto.CompraRequest;
import cl.irondisk.backend.service.BoletaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
