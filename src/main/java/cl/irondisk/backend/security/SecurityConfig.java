package cl.irondisk.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

// Configuracion de seguridad: quien puede usar cada endpoint
// Roles: ADMIN tiene acceso total, VENDEDOR solo ve productos y ordenes, CLIENTE solo usa la tienda
// Las reglas se revisan en orden y gana la primera que coincide, por eso las especificas van antes
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // La API no usa cookies de sesion, por eso no aplica CSRF
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                // Sin sesiones en el servidor, cada peticion se autentica con su token
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger y pagina de error
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error").permitAll()

                        // Autenticacion publica
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/registro",
                                "/api/v1/auth/refresh").permitAll()

                        // Catalogo publico, menos los productos criticos
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos/criticos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/productos", "/api/v1/productos/**",
                                "/api/v1/categorias", "/api/v1/categorias/**").permitAll()

                        // Compras: se puede comprar con o sin cuenta
                        .requestMatchers(HttpMethod.POST, "/api/v1/boletas").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/mias").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/usuario/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas").hasAnyRole("ADMIN", "VENDEDOR")
                        // El servicio revisa que una boleta ajena no la vea un cliente
                        .requestMatchers(HttpMethod.GET, "/api/v1/boletas/*").authenticated()

                        // Cualquier usuario con sesion
                        .requestMatchers("/api/v1/auth/me", "/api/v1/perfil").authenticated()

                        // Todo lo demas, como crear, editar o eliminar, usuarios y reportes, es solo para ADMIN
                        .anyRequest().hasRole("ADMIN"))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) ->
                                escribirError(res, HttpServletResponse.SC_UNAUTHORIZED, "Debes iniciar sesión o tu sesión expiró"))
                        .accessDeniedHandler((req, res, ex) ->
                                escribirError(res, HttpServletResponse.SC_FORBIDDEN, "No tienes permisos para esta acción")))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // BCrypt guarda la clave como hash, no se puede desencriptar y solo se compara
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // CORS permite que el frontend React, que corre en otro puerto, llame a la API
    @Bean
    public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.origenes}") String[] origenes) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origenes));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // Los errores 401 y 403 salen con el mismo formato JSON del resto de la API
    private static void escribirError(HttpServletResponse res, int status, String mensaje) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().write("{\"status\":" + status + ",\"mensaje\":\"" + mensaje
                + "\",\"errores\":null,\"fecha\":\"" + LocalDateTime.now() + "\"}");
    }
}
