package cl.irondisk.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Se ejecuta antes de cada peticion, lee el token y deja al usuario con su rol en el contexto de seguridad
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecera = request.getHeader("Authorization");
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            try {
                Claims claims = jwtService.leer(cabecera.substring(7));
                // Solo el token de acceso sirve para entrar, el de renovacion no
                if (JwtService.TIPO_ACCESS.equals(claims.get(JwtService.CLAIM_TIPO))) {
                    UserDetails usuario = User.withUsername(claims.getSubject())
                            .password("")
                            .authorities("ROLE_" + claims.get("rol", String.class))
                            .build();
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
                }
            } catch (JwtException | IllegalArgumentException e) {
                // Token vencido o alterado, la peticion sigue como anonima y el filtro de seguridad responde 401
            }
        }
        chain.doFilter(request, response);
    }
}
