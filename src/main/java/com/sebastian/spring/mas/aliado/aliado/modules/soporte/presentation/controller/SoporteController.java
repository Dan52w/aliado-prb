package com.sebastian.spring.mas.aliado.aliado.modules.soporte.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.config.FeatureFlags;
import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Soporte (SPEC-15): chat interno, WhatsApp, onboarding, mapa, insignias,
 * referidos e idiomas son Could/Future. El flag está apagado: se responde
 * FUNCIONALIDAD_DESHABILITADA, nunca como disponible para usuarios.
 */
@RestController
@RequestMapping("/api/v1/soporte")
public class SoporteController {
    private final FeatureFlags flags;

    public SoporteController(FeatureFlags flags) {
        this.flags = flags;
    }

    @GetMapping("/whatsapp")
    public ResponseEntity<String> whatsapp() {
        if (!flags.soporteWhatsapp()) {
            throw new BusinessException.NotFound("FUNCIONALIDAD_DESHABILITADA",
                    "El soporte por WhatsApp es una función futura y no está disponible en el MVP.");
        }
        return ResponseEntity.ok("Soporte por WhatsApp");
    }
}
