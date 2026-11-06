package com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EvidenciaReporte(UUID id, UUID reporteId, String archivoKey, String tipoContenido,
                               OffsetDateTime fechaCarga) {}
