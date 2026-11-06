package com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.dto.AdminDtos;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoAdminPort;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Administración del catálogo (SPEC-12). Solo rol ADMINISTRADOR (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminCatalogoController {
    private final CatalogoAdminPort catalogo;

    public AdminCatalogoController(CatalogoAdminPort catalogo) {
        this.catalogo = catalogo;
    }

    @PostMapping("/categorias")
    public ResponseEntity<AdminDtos.CategoriaResponse> crearCategoria(@Valid @RequestBody AdminDtos.CategoriaRequest req) {
        var c = catalogo.crearCategoria(SecurityUtils.actual().id(), req.nombre());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminDtos.CategoriaResponse(c.id(), c.nombre(), c.estado().name()));
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<AdminDtos.CategoriaResponse>> listarCategorias() {
        return ResponseEntity.ok(catalogo.listarCategorias().stream()
                .map(c -> new AdminDtos.CategoriaResponse(c.id(), c.nombre(), c.estado().name())).toList());
    }

    @PutMapping("/categorias/{id}")
    public ResponseEntity<AdminDtos.CategoriaResponse> actualizarCategoria(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.CategoriaUpdateRequest req) {
        var c = catalogo.actualizarCategoria(SecurityUtils.actual().id(), id, req.nombre(), req.estado());
        return ResponseEntity.ok(new AdminDtos.CategoriaResponse(c.id(), c.nombre(), c.estado().name()));
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> desactivarCategoria(@PathVariable UUID id) {
        catalogo.desactivarCategoria(SecurityUtils.actual().id(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/zonas")
    public ResponseEntity<AdminDtos.ZonaResponse> crearZona(@Valid @RequestBody AdminDtos.ZonaRequest req) {
        var z = catalogo.crearZona(SecurityUtils.actual().id(), req.nombre(), req.coberturaGeografica());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminDtos.ZonaResponse(z.id(), z.nombre(), z.coberturaGeografica(), z.estado().name()));
    }

    @GetMapping("/zonas")
    public ResponseEntity<List<AdminDtos.ZonaResponse>> listarZonas() {
        return ResponseEntity.ok(catalogo.listarZonas().stream()
                .map(z -> new AdminDtos.ZonaResponse(z.id(), z.nombre(), z.coberturaGeografica(), z.estado().name()))
                .toList());
    }

    @PutMapping("/zonas/{id}")
    public ResponseEntity<AdminDtos.ZonaResponse> actualizarZona(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.ZonaUpdateRequest req) {
        var z = catalogo.actualizarZona(SecurityUtils.actual().id(), id, req.nombre(), req.coberturaGeografica(), req.estado());
        return ResponseEntity.ok(new AdminDtos.ZonaResponse(z.id(), z.nombre(), z.coberturaGeografica(), z.estado().name()));
    }

    @DeleteMapping("/zonas/{id}")
    public ResponseEntity<Void> desactivarZona(@PathVariable UUID id) {
        catalogo.desactivarZona(SecurityUtils.actual().id(), id);
        return ResponseEntity.noContent().build();
    }
}
