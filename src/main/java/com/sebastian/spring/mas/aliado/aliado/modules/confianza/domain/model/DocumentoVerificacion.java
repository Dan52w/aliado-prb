package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DocumentoVerificacion(UUID id, UUID solicitudVerificacionId, String archivoKey,
                                    OffsetDateTime fechaCarga) {}
