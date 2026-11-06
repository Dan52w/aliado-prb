package com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port;

import java.util.UUID;

/** Moderación de reseñas (SPEC-12): ocultar contenido no implica bloquear la cuenta. */
public interface ResenaModeracionPort {
    void ocultarResena(UUID resenaId);
}
