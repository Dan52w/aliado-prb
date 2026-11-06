package com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Solicitud de eliminación de cuenta: expira a las 24 h si no se confirma (SPEC-02). */
public record SolicitudEliminacion(UUID id, UUID usuarioId, OffsetDateTime fechaSolicitud,
                                   OffsetDateTime fechaExpiracion, EstadoEliminacion estado,
                                   OffsetDateTime fechaConfirmacion) {
    public boolean vencida(OffsetDateTime ahora) {
        return estado == EstadoEliminacion.PENDIENTE && fechaExpiracion.isBefore(ahora);
    }
}
