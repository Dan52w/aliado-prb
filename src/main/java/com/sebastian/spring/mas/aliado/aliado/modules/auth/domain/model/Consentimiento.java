package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Aceptación de términos y tratamiento de datos (Ley 1581, SPEC-01 FR-002). */
public record Consentimiento(UUID id, UUID usuarioId, String versionTerminos, OffsetDateTime fechaAceptacion) {}
