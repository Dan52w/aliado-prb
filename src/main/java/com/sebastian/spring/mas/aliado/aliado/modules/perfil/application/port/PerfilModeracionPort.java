package com.sebastian.spring.mas.aliado.aliado.modules.perfil.application.port;

import java.util.UUID;

/**
 * Moderación del perfil básico (SPEC-12, FR-074): ocultar el perfil sin bloquear la cuenta.
 * La implementa PerfilService; la consume la administración.
 */
public interface PerfilModeracionPort {
    /** Oculta el perfil público (preferencia PRIVADO). */
    void ocultarPerfil(UUID usuarioId);
}
