package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SolicitudVerificacion(UUID id, UUID prestadorId, EstadoVerificacion estado,
                                    OffsetDateTime fechaSolicitud, OffsetDateTime fechaRevision) {}
