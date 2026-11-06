package com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.AdministracionService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.dto.AdminDtos;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.CatalogoService;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.Reporte;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Administración, moderación y riesgos (SPEC-12). Solo rol ADMINISTRADOR (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final CatalogoService catalogo;
    private final AdministracionService admin;

    public AdminController(CatalogoService catalogo, AdministracionService admin) {
        this.catalogo = catalogo;
        this.admin = admin;
    }

    private AdminDtos.ReporteResponse aReporte(Reporte r) {
        return new AdminDtos.ReporteResponse(r.id(), r.tipo().name(), r.objetivoId(), r.motivo(),
                r.estado().name(), r.fechaCreacion(), r.detalleRevisadoEn());
    }

    @PostMapping("/categorias")
    public ResponseEntity<AdminDtos.CategoriaResponse> crearCategoria(@Valid @RequestBody AdminDtos.CategoriaRequest req) {
        var c = catalogo.crearCategoria(req.nombre());
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
        var c = catalogo.actualizarCategoria(id, req.nombre(), req.estado());
        return ResponseEntity.ok(new AdminDtos.CategoriaResponse(c.id(), c.nombre(), c.estado().name()));
    }

    @DeleteMapping("/categorias/{id}")
    public ResponseEntity<Void> desactivarCategoria(@PathVariable UUID id) {
        catalogo.desactivarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/zonas")
    public ResponseEntity<AdminDtos.ZonaResponse> crearZona(@Valid @RequestBody AdminDtos.ZonaRequest req) {
        var z = catalogo.crearZona(req.nombre(), req.coberturaGeografica());
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
        var z = catalogo.actualizarZona(id, req.nombre(), req.coberturaGeografica(), req.estado());
        return ResponseEntity.ok(new AdminDtos.ZonaResponse(z.id(), z.nombre(), z.coberturaGeografica(), z.estado().name()));
    }

    @DeleteMapping("/zonas/{id}")
    public ResponseEntity<Void> desactivarZona(@PathVariable UUID id) {
        catalogo.desactivarZona(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reportes")
    public ResponseEntity<List<AdminDtos.ReporteResponse>> colaReportes(
            @RequestParam(defaultValue = "PENDIENTE") String estado) {
        return ResponseEntity.ok(admin.colaReportes(estado).stream().map(this::aReporte).toList());
    }

    @GetMapping("/reportes/{id}")
    public ResponseEntity<AdminDtos.ReporteResponse> detalleReporte(@PathVariable UUID id) {
        return ResponseEntity.ok(aReporte(admin.detalleReporte(id, SecurityUtils.actual().id())));
    }

    @PostMapping("/reportes/{id}/resolver")
    public ResponseEntity<AdminDtos.ReporteResponse> resolverReporte(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.ResolverReporteRequest req) {
        return ResponseEntity.ok(aReporte(admin.resolverReporte(id, SecurityUtils.actual().id(),
                req.resultado(), req.justificacion())));
    }

    @PostMapping("/usuarios/{id}/bloqueos")
    public ResponseEntity<AdminDtos.BloqueoResponse> bloquearUsuario(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.BloqueoRequest req) {
        var b = admin.bloquearUsuario(id, SecurityUtils.actual().id(), req.motivo());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminDtos.BloqueoResponse(b.id(), b.usuarioId(), b.motivo(), b.activo()));
    }

    @PostMapping("/usuarios/{id}/bloqueos/{bloqueoId}/levantar")
    public ResponseEntity<Void> levantarBloqueo(@PathVariable UUID id, @PathVariable UUID bloqueoId) {
        admin.levantarBloqueo(id, bloqueoId, SecurityUtils.actual().id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/moderacion")
    public ResponseEntity<Void> moderar(@Valid @RequestBody AdminDtos.ModeracionRequest req) {
        admin.moderar(SecurityUtils.actual().id(), req.objetivoTipo(), req.objetivoId(), req.accion(), req.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/servicios/{id}/alto-riesgo")
    public ResponseEntity<AdminDtos.AltoRiesgoResponse> altoRiesgo(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.AltoRiesgoRequest req) {
        var m = admin.marcarAltoRiesgo(id, SecurityUtils.actual().id(), req.criterio());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminDtos.AltoRiesgoResponse(m.id(), m.servicioId(), m.criterio(), m.activa()));
    }

    @GetMapping("/auditoria")
    public ResponseEntity<List<AdminDtos.AuditoriaResponse>> auditoria(
            @RequestParam(defaultValue = "50") int limite) {
        return ResponseEntity.ok(admin.listarAuditoria(limite).stream()
                .map(a -> new AdminDtos.AuditoriaResponse(a.id(), a.accion(), a.objetivoTipo(), a.objetivoId(),
                        a.detalle(), a.fecha(), a.traceId())).toList());
    }

    @PostMapping("/verificaciones/{id}/resolver")
    public ResponseEntity<Void> resolverVerificacion(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.ResolverVerificacionRequest req) {
        admin.resolverVerificacion(id, SecurityUtils.actual().id(), req.estado());
        return ResponseEntity.noContent().build();
    }
}
