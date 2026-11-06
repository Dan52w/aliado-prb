package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model;

import java.util.UUID;

public record Paso(UUID id, UUID rutaId, String titulo, String descripcion, int orden) {}
