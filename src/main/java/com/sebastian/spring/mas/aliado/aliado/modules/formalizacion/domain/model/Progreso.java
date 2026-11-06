package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Progreso(UUID id, UUID prestadorId, UUID rutaId, int valorOrientativo,
                       OffsetDateTime fechaActualizacion) {}
