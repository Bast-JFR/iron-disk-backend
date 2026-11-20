package cl.irondisk.backend.security;

import cl.irondisk.backend.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

// Crea y valida los tokens JWT, firmados con la clave jwt.secret
@Service
public class JwtService {

    public static final String CLAIM_TIPO = "tipo";
    public static final String TIPO_ACCESS = "access";
    public static final String TIPO_REFRESH = "refresh";

    private final SecretKey clave;
    private final long accessMs;
    private final long refreshMs;

    public JwtService(@Value("${jwt.secret}") String secretoBase64,
                      @Value("${jwt.access-expiration-ms}") long accessMs,
                      @Value("${jwt.refresh-expiration-ms}") long refreshMs) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
        this.accessMs = accessMs;
        this.refreshMs = refreshMs;
    }

    // Token de acceso: lleva el correo, el rol y el nombre, y se envia en cada peticion
    public String generarAccessToken(Usuario u) {
        return construir(u.getCorreo(),
                Map.of(CLAIM_TIPO, TIPO_ACCESS, "rol", u.getRol().name(), "nombre", u.getNombre()),
                accessMs);
    }

    // Token de renovacion: solo sirve para pedir un par de tokens nuevo
    public String generarRefreshToken(Usuario u) {
        return construir(u.getCorreo(), Map.of(CLAIM_TIPO, TIPO_REFRESH), refreshMs);
    }

    // Revisa la firma y el vencimiento, lanza JwtException si el token no sirve
    public Claims leer(String token) throws JwtException {
        return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
    }

    public long getAccessMs() {
        return accessMs;
    }

    private String construir(String correo, Map<String, ?> claims, long duracionMs) {
        Date ahora = new Date();
        return Jwts.builder()
                .claims(claims)
                .subject(correo)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + duracionMs))
                .signWith(clave)
                .compact();
    }
}
