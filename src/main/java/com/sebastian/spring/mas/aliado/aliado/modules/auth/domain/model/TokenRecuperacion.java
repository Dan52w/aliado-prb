package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Código de recuperación de un solo uso (SPEC-01, FR-006). */
public record TokenRecuperacion(UUID id, UUID usuarioId, String codigoHash,
                                OffsetDateTime fechaCreacion, OffsetDateTime fechaExpiracion, boolean usado) {
    public boolean vigente(OffsetDateTime ahora) {
        return !usado && fechaExpiracion.isAfter(ahora);
    }
}
