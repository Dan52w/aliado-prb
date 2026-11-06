package com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AccionModeracion(UUID id, UUID administradorId, TipoAccionModeracion tipo,
                               TipoObjetivoModeracion objetivoTipo,
                               UUID objetivoId, String motivo, OffsetDateTime fecha) {}
