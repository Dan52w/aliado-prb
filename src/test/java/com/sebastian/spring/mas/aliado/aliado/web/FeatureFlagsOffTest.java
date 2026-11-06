package com.sebastian.spring.mas.aliado.aliado.web;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.config.FeatureFlags;
import com.sebastian.spring.mas.aliado.aliado.modules.soporte.presentation.controller.SoporteController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** El futuro queda apagado: flags en false y soporte WhatsApp responde deshabilitado. */
@SpringBootTest
class FeatureFlagsOffTest {

    @Autowired
    private FeatureFlags flags;

    @Test
    void flagsFuturosApagados() {
        assertFalse(flags.chatInterno());
        assertFalse(flags.soporteWhatsapp());
        assertFalse(flags.mapaPrestadores());
        assertFalse(flags.pagos().pilotoEnabled());
    }

    @Test
    void soporteWhatsappRespondeDeshabilitado() {
        assertThrows(BusinessException.NotFound.class, () -> new SoporteController(flags).whatsapp());
    }
}
