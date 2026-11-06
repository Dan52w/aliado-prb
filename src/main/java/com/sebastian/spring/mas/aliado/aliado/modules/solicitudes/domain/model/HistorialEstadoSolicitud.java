package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model;

import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.EstadoSolicitud;
import java.time.OffsetDateTime;
import java.util.UUID;

public record HistorialEstadoSolicitud(UUID id, UUID solicitudId, EstadoSolicitud estadoAnterior,
                                       EstadoSolicitud estadoNuevo, OffsetDateTime fecha) {}
