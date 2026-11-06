package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.presentation.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Contratos HTTP de la bandeja de notificaciones (SPEC-09). */
public final class NotificacionDtos {
    private NotificacionDtos() {}

    public record NotificacionResponse(UUID id, String tipo, UUID servicioId, UUID solicitudId, String detalle,
                                       String estadoEntrega, OffsetDateTime fechaGeneracion) {}
}
