package com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MensajeServicio(UUID id, UUID servicioId, UUID remitenteId, String contenido, OffsetDateTime fechaEnvio) {}
