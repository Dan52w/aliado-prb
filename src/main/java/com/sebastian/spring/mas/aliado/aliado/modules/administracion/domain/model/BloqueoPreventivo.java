package com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BloqueoPreventivo(UUID id, UUID usuarioId, UUID administradorId, String motivo,
                                OffsetDateTime fechaInicio, OffsetDateTime fechaFin, boolean activo) {}
