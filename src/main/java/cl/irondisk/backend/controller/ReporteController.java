package cl.irondisk.backend.controller;

import cl.irondisk.backend.config.OpenApiConfig;
import cl.irondisk.backend.dto.ResumenVentasResponse;
import cl.irondisk.backend.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Reportes de ventas, solo para el administrador
@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Resumen de ventas para el administrador")
@SecurityRequirement(name = OpenApiConfig.BEARER)
public class ReporteController {

    private final ReporteService service;

    @GetMapping("/resumen")
    @Operation(summary = "Resumen de ventas (solo ADMIN)",
            description = "Total vendido, cantidad de boletas, ticket promedio y los productos más vendidos. "
                    + "Las boletas anuladas no se cuentan.")
    @ApiResponse(responseCode = "401", description = "Falta el token o no es válido")
    @ApiResponse(responseCode = "403", description = "El usuario no es administrador")
    public ResumenVentasResponse resumen() {
        return service.resumen();
    }
}
