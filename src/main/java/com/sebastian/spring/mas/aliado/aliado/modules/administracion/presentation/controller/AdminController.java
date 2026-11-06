package com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.BloqueoCuentaService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.BusquedaAdminService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.ModeracionService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.ReporteModeracionService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.RiesgoAuditoriaService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.dto.AdminDtos;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoAdminPort;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.application.port.FormalizacionAdminPort;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.EvidenciaReporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.Reporte;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final CatalogoAdminPort catalogo;
    private final ReporteModeracionService reportes;
    private final BloqueoCuentaService bloqueos;
    private final ModeracionService moderacion;
    private final RiesgoAuditoriaService riesgo;
    private final BusquedaAdminService busqueda;
    private final FormalizacionAdminPort formalizacion;

    public AdminController(CatalogoAdminPort catalogo, ReporteModeracionService reportes,
                           BloqueoCuentaService bloqueos, ModeracionService moderacion,
                           RiesgoAuditoriaService riesgo, BusquedaAdminService busqueda,
                           FormalizacionAdminPort formalizacion) {
        this.catalogo = catalogo;
        this.reportes = reportes;
        this.bloqueos = bloqueos;
        this.moderacion = moderacion;
        this.riesgo = riesgo;
        this.busqueda = busqueda;
        this.formalizacion = formalizacion;
    }

    private AdminDtos.ReporteResponse aReporte(Reporte r) {
        return aReporte(r, List.of());
    }

    private AdminDtos.ReporteResponse aReporte(Reporte r, List<EvidenciaReporte> evidencias) {
        return new AdminDtos.ReporteResponse(r.id(), r.tipo().name(), r.objetivoId(), r.motivo(),
                r.estado().name(), r.fechaCreacion(), r.detalleRevisadoEn(),
                evidencias.stream()
                        .map(e -> new AdminDtos.EvidenciaResumen(e.id(), e.tipoContenido(), e.fechaCarga()))
                        .toList());
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

    @GetMapping("/reportes")
    public ResponseEntity<List<AdminDtos.ReporteResponse>> colaReportes(
            @RequestParam(defaultValue = "PENDIENTE") String estado) {
        return ResponseEntity.ok(reportes.colaReportes(estado).stream().map(this::aReporte).toList());
    }

    @GetMapping("/reportes/{id}")
    public ResponseEntity<AdminDtos.ReporteResponse> detalleReporte(@PathVariable UUID id) {
        Reporte reporte = reportes.detalleReporte(id, SecurityUtils.actual().id());
        return ResponseEntity.ok(aReporte(reporte, reportes.evidenciasDe(id)));
    }

    @GetMapping("/reportes/{id}/evidencias/{evidenciaId}")
    public ResponseEntity<byte[]> descargarEvidencia(@PathVariable UUID id, @PathVariable UUID evidenciaId) {
        var contenido = reportes.descargarEvidencia(id, evidenciaId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contenido.tipoContenido()))
                .body(contenido.contenido());
    }

    @PostMapping("/reportes/{id}/resolver")
    public ResponseEntity<AdminDtos.ReporteResponse> resolverReporte(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.ResolverReporteRequest req) {
        return ResponseEntity.ok(aReporte(reportes.resolverReporte(id, SecurityUtils.actual().id(),
                req.resultado(), req.justificacion())));
    }

    @PostMapping("/usuarios/{id}/bloqueos")
    public ResponseEntity<AdminDtos.BloqueoResponse> bloquearUsuario(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.BloqueoRequest req) {
        var b = bloqueos.bloquearUsuario(id, SecurityUtils.actual().id(), req.motivo());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminDtos.BloqueoResponse(b.id(), b.usuarioId(), b.motivo(), b.activo()));
    }

    @PostMapping("/usuarios/{id}/bloqueos/{bloqueoId}/levantar")
    public ResponseEntity<Void> levantarBloqueo(@PathVariable UUID id, @PathVariable UUID bloqueoId) {
        bloqueos.levantarBloqueo(id, bloqueoId, SecurityUtils.actual().id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/moderacion")
    public ResponseEntity<Void> moderar(@Valid @RequestBody AdminDtos.ModeracionRequest req) {
        moderacion.moderar(SecurityUtils.actual().id(), req.objetivoTipo(), req.objetivoId(), req.accion(), req.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/servicios/{id}/alto-riesgo")
    public ResponseEntity<AdminDtos.AltoRiesgoResponse> altoRiesgo(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.AltoRiesgoRequest req) {
        var m = riesgo.marcarAltoRiesgo(id, SecurityUtils.actual().id(), req.criterio());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminDtos.AltoRiesgoResponse(m.id(), m.servicioId(), m.criterio(), m.activa()));
    }

    @GetMapping("/auditoria")
    public ResponseEntity<List<AdminDtos.AuditoriaResponse>> auditoria(
            @RequestParam(defaultValue = "50") int limite) {
        return ResponseEntity.ok(riesgo.listarAuditoria(limite).stream()
                .map(a -> new AdminDtos.AuditoriaResponse(a.id(), a.accion(), a.objetivoTipo(), a.objetivoId(),
                        a.detalle(), a.fecha(), a.traceId())).toList());
    }

    @PostMapping("/verificaciones/{id}/resolver")
    public ResponseEntity<Void> resolverVerificacion(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.ResolverVerificacionRequest req) {
        moderacion.resolverVerificacion(id, SecurityUtils.actual().id(), req.estado(), req.justificacion());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/busqueda")
    public ResponseEntity<AdminDtos.BusquedaResponse> busqueda(@RequestParam String q) {
        if (q == null || q.isBlank()) {
            throw new BusinessException.BadRequest("BUSQUEDA_REQUERIDA",
                    "El parámetro q es obligatorio para buscar.");
        }
        var resultado = busqueda.buscar(q.trim());
        return ResponseEntity.ok(new AdminDtos.BusquedaResponse(
                resultado.usuarios().stream()
                        .map(u -> new AdminDtos.UsuarioResumen(u.id(), u.email(), u.rol()))
                        .toList(),
                resultado.solicitudes().stream()
                        .map(s -> new AdminDtos.SolicitudResumen(s.id(), s.descripcion(), s.estado()))
                        .toList()));
    }

    @PutMapping("/formalizacion/enlaces/{id}")
    public ResponseEntity<AdminDtos.EnlaceResponse> actualizarEnlace(@PathVariable UUID id,
            @RequestBody AdminDtos.ActualizarEnlaceRequest req) {
        var e = formalizacion.actualizarEnlace(id, req.nombre(), req.url(), req.estado());
        return ResponseEntity.ok(new AdminDtos.EnlaceResponse(e.id(), e.nombre(), e.url(), e.estado().name()));
    }
}
