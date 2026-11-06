package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record Reputacion(UUID prestadorId, BigDecimal valorAgregado, int cantidadCalificaciones,
                         OffsetDateTime fechaCalculo) {}
