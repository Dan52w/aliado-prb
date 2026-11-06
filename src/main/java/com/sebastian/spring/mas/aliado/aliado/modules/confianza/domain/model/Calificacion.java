package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Calificacion(UUID id, UUID servicioId, UUID clienteId, UUID prestadorId, int valor, OffsetDateTime fecha) {}
