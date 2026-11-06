package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model;

import java.util.UUID;

public record Categoria(UUID id, String nombre, EstadoCatalogo estado) {}
