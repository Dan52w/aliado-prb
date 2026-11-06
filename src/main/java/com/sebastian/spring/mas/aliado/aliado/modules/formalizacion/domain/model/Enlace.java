package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model;

import java.util.UUID;

public record Enlace(UUID id, UUID pasoId, String nombre, String url, EstadoEnlace estado) {}
