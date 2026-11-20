package cl.irondisk.backend.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Pruebas de las reglas de formato del RUN y del correo
class ValidadorUsuarioTest {

    @Test
    void aceptaUnRunConDigitoVerificadorCorrecto() {
        assertThat(ValidadorUsuario.runValido("190110222")).isTrue();
        assertThat(ValidadorUsuario.runValido("123456785")).isTrue();
    }

    @Test
    void rechazaUnRunConDigitoVerificadorIncorrecto() {
        assertThat(ValidadorUsuario.runValido("190110221")).isFalse();
    }

    @Test
    void aceptaSoloLosDominiosPermitidos() {
        assertThat(ValidadorUsuario.dominioPermitido("ana@duoc.cl")).isTrue();
        assertThat(ValidadorUsuario.dominioPermitido("ana@profesor.duoc.cl")).isTrue();
        assertThat(ValidadorUsuario.dominioPermitido("ANA@GMAIL.COM")).isTrue();
        assertThat(ValidadorUsuario.dominioPermitido("ana@hotmail.com")).isFalse();
        assertThat(ValidadorUsuario.dominioPermitido("sin-arroba")).isFalse();
        assertThat(ValidadorUsuario.dominioPermitido(null)).isFalse();
    }
}
