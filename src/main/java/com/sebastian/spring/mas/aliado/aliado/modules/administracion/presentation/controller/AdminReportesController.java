package com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.ReporteModeracionService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.dto.AdminDtos;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.EvidenciaReporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.Reporte;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Cola y resolución de reportes (SPEC-12). Solo rol ADMINISTRADOR (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminReportesController {
    private final ReporteModeracionService reportes;

    public AdminReportesController(ReporteModeracionService reportes) {
        this.reportes = reportes;
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
}
