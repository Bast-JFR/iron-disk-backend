package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.LoginRequest;
import cl.irondisk.backend.exception.CredencialesInvalidasException;
import cl.irondisk.backend.model.Rol;
import cl.irondisk.backend.model.Usuario;
import cl.irondisk.backend.repository.UsuarioRepository;
import cl.irondisk.backend.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

// Pruebas del login y la renovacion de sesion, se usa un JwtService real para revisar los tokens de verdad
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // Clave de prueba en Base64, de 64 bytes
    private static final String SECRETO = "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MA==";

    @Mock
    private UsuarioRepository usuarioRepo;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;
    private AuthService service;
    private Usuario ana;

    @BeforeEach
    void preparar() {
        jwtService = new JwtService(SECRETO, 900000, 604800000);
        service = new AuthService(usuarioRepo, usuarioService, passwordEncoder, jwtService);
        ana = Usuario.builder().id(1L).run("190110222").nombre("Ana").apellidos("Perez").correo("ana@gmail.com")
                .password("hash").rol(Rol.CLIENTE).region("R").comuna("C").direccion("D").build();
    }

    @Test
    void conClaveIncorrectaYConCorreoInexistenteSeRespondeElMismoMensaje() {
        when(usuarioRepo.findByCorreoIgnoreCase("ana@gmail.com")).thenReturn(Optional.of(ana));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);
        when(usuarioRepo.findByCorreoIgnoreCase("nadie@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login(new LoginRequest("ana@gmail.com", "mala")))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessage(AuthService.CREDENCIALES_INVALIDAS);
        assertThatThrownBy(() -> service.login(new LoginRequest("nadie@gmail.com", "x")))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessage(AuthService.CREDENCIALES_INVALIDAS);
    }

    @Test
    void unTokenDeAccesoNoSirveParaRenovarLaSesion() {
        String access = jwtService.generarAccessToken(ana);

        assertThatThrownBy(() -> service.refresh(access))
                .isInstanceOf(CredencialesInvalidasException.class)
                .hasMessageContaining("renovación");
    }

}
