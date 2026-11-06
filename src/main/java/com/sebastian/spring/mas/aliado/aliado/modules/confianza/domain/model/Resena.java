package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Resena(UUID id, UUID calificacionId, String texto, OffsetDateTime fechaCreacion,
                     EstadoVisibilidadResena estadoVisibilidad) {}
