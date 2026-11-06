package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DescargaHistorial(UUID id, UUID usuarioId, OffsetDateTime fechaGeneracion,
                                String archivoKey, String estado) {}
