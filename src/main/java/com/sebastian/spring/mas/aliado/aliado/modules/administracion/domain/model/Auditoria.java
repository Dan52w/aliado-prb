package com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Registro de auditoría append-only (RNF-005): nunca se actualiza ni se borra. */
public record Auditoria(UUID id, UUID administradorId, String accion, String objetivoTipo, UUID objetivoId,
                        String detalle, OffsetDateTime fecha, String traceId) {}
