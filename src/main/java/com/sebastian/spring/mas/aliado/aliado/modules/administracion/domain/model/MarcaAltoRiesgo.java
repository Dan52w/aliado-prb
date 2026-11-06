package com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MarcaAltoRiesgo(UUID id, UUID servicioId, String criterio, UUID administradorId,
                              OffsetDateTime fecha, boolean activa) {}
