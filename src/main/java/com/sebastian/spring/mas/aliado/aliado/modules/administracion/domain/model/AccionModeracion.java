package com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AccionModeracion(UUID id, UUID administradorId, String tipo, String objetivoTipo,
                               UUID objetivoId, String motivo, OffsetDateTime fecha) {}
