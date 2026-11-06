package com.sebastian.spring.mas.aliado.aliado.domain;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Política de contraseña adoptada (SPEC-01 no fija valores): mínimo 8, letra y dígito. */
class PasswordPolicyTest {

    @Test
    void aceptaContrasenaValida() {
        assertDoesNotThrow(() -> Usuario.validarPassword("Segura123"));
    }

    @Test
    void rechazaCorta() {
        assertThrows(IllegalArgumentException.class, () -> Usuario.validarPassword("Abc123"));
    }

    @Test
    void rechazaSinDigito() {
        assertThrows(IllegalArgumentException.class, () -> Usuario.validarPassword("SinDigitos"));
    }

    @Test
    void rechazaSinLetra() {
        assertThrows(IllegalArgumentException.class, () -> Usuario.validarPassword("12345678"));
    }

    @Test
    void rechazaNula() {
        assertThrows(IllegalArgumentException.class, () -> Usuario.validarPassword(null));
    }
}
