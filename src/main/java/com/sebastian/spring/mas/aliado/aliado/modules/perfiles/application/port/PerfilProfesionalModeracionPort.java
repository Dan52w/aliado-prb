package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.port;

import java.util.UUID;

/**
 * Moderación del perfil profesional (SPEC-12, FR-074): ocultar el perfil sin bloquear la cuenta.
 * La implementa PerfilProfesionalService; la consume la administración.
 */
public interface PerfilProfesionalModeracionPort {
    /** Pone el estado de exposición en OCULTO. */
    void ocultarPerfil(UUID prestadorId);
}
