package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.presentation.dto.CatalogoDtos;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Catálogo público de lectura (SPEC-04): el cliente necesita las categorías y
 * zonas activas para elegir sus IDs al crear una solicitud. Solo lectura,
 * cualquier usuario autenticado; la gestión sigue en el admin (SPEC-12).
 */
@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoController {
    private final CatalogoConsultaPort catalogo;

    public CatalogoController(CatalogoConsultaPort catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CatalogoDtos.CategoriaResponse>> categorias() {
        return ResponseEntity.ok(catalogo.categoriasActivas().stream()
                .map(c -> new CatalogoDtos.CategoriaResponse(c.id(), c.nombre()))
                .toList());
    }

    @GetMapping("/zonas")
    public ResponseEntity<List<CatalogoDtos.ZonaResponse>> zonas() {
        return ResponseEntity.ok(catalogo.zonasActivas().stream()
                .map(z -> new CatalogoDtos.ZonaResponse(z.id(), z.nombre(), z.coberturaGeografica()))
                .toList());
    }
}
