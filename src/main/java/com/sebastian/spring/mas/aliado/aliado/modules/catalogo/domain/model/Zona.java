package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model;

import java.util.UUID;

public record Zona(UUID id, String nombre, String coberturaGeografica, EstadoCatalogo estado) {}
