package cl.irondisk.backend.service;

import cl.irondisk.backend.dto.RegistroRequest;
import cl.irondisk.backend.exception.ReglaNegocioException;
import cl.irondisk.backend.model.Rol;
import cl.irondisk.backend.model.Usuario;
import cl.irondisk.backend.repository.BoletaRepository;
import cl.irondisk.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Pruebas de la logica de usuarios, los repositorios se reemplazan por mocks
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repo;
    @Mock
    private BoletaRepository boletaRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UsuarioService service;

    private RegistroRequest registro;

    @BeforeEach
    void preparar() {
        registro = new RegistroRequest("190110222", "Ana", "Perez", "ana@gmail.com", "ana123",
                "Valparaíso", "Viña del Mar", "Calle 1", null);
    }

    @Test
    void elRegistroPublicoSiempreCreaUnClienteConLaClaveEncriptada() {
        when(passwordEncoder.encode("ana123")).thenReturn("hash-bcrypt");
        when(repo.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = service.crearCliente(registro);

        assertThat(creado.getRol()).isEqualTo(Rol.CLIENTE);
        assertThat(creado.getPassword()).isEqualTo("hash-bcrypt").isNotEqualTo("ana123");
        assertThat(creado.getCorreo()).isEqualTo("ana@gmail.com");
    }

    @Test
    void unAdministradorNoPuedeEliminarSuPropiaCuenta() {
        Usuario admin = Usuario.builder().id(1L).correo("admin@duoc.cl").rol(Rol.ADMIN).build();
        when(repo.findById(1L)).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> service.eliminar(1L, "ADMIN@duoc.cl"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("propia cuenta");
        verify(repo, never()).delete(any());
    }

}
