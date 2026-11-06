package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model;

import java.util.UUID;

public record Ruta(UUID id, String nombre, String descripcion, boolean activa) {}
