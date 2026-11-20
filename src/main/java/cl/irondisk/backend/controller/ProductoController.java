package cl.irondisk.backend.controller;

import cl.irondisk.backend.config.OpenApiConfig;
import cl.irondisk.backend.dto.ProductoRequest;
import cl.irondisk.backend.dto.ProductoResponse;
import cl.irondisk.backend.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Productos: ver el catalogo es publico, crear, editar, eliminar y ver los criticos es solo para ADMIN
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Catálogo de discos, ver es público y administrar es solo ADMIN")
public class ProductoController {

    private final ProductoService service;

    // Los filtros son opcionales: categoriaId, formato y q (texto en el nombre o el artista)
    @GetMapping
    @Operation(summary = "Catálogo con filtros opcionales (público)")
    public List<ProductoResponse> listar(
            @Parameter(description = "Id de la categoría") @RequestParam(required = false) Long categoriaId,
            @Parameter(description = "Vinilo, CD o Cassette") @RequestParam(required = false) String formato,
            @Parameter(description = "Texto que se busca en el nombre o el artista") @RequestParam(required = false) String q) {
        return service.listar(categoriaId, formato, q);
    }

    @GetMapping("/ofertas")
    @Operation(summary = "Productos con descuento (público)")
    public List<ProductoResponse> ofertas() {
        return service.ofertas();
    }

    @GetMapping("/criticos")
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Productos con stock igual o menor a su stock crítico (ADMIN)")
    public List<ProductoResponse> criticos() {
        return service.criticos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de un producto (público)")
    @ApiResponse(responseCode = "404", description = "El producto no existe")
    public ProductoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Crear un producto (ADMIN)")
    @ApiResponse(responseCode = "409", description = "El código ya existe")
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest req) {
        return service.crear(req);
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Editar un producto (ADMIN)")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = OpenApiConfig.BEARER)
    @Operation(summary = "Eliminar un producto (ADMIN)")
    @ApiResponse(responseCode = "409", description = "El producto aparece en boletas de venta")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
