package cl.irondisk.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Swagger queda en http://localhost:8080/swagger-ui.html
// El esquema bearerAuth agrega el boton Authorize para probar los endpoints protegidos
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Iron Disk API",
        version = "v1",
        description = "API REST de la tienda Iron Disk: autenticación, usuarios, categorías, productos, compras y reportes. "
                + "Para probar los endpoints protegidos ejecuta POST /api/v1/auth/login, copia el accessToken y presiona Authorize."))
@SecurityScheme(name = OpenApiConfig.BEARER, type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    public static final String BEARER = "bearerAuth";
}
