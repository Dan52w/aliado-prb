package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.presentation.dto;

import java.util.UUID;

/** Contratos HTTP del catálogo público de lectura (SPEC-04). */
public final class CatalogoDtos {
    private CatalogoDtos() {}

    public record CategoriaResponse(UUID id, String nombre) {}

    public record ZonaResponse(UUID id, String nombre, String coberturaGeografica) {}
}
