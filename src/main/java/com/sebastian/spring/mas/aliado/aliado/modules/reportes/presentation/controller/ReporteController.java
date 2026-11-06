package com.sebastian.spring.mas.aliado.aliado.modules.reportes.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.ReporteService;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.Reporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.presentation.dto.ReporteDtos;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Reportes y evidencias (SPEC-11). */
@RestController
@RequestMapping("/api/v1")
public class ReporteController {
    private final ReporteService service;

    public ReporteController(ReporteService service) {
        this.service = service;
    }

    private ReporteDtos.ReporteResponse aRespuesta(Reporte r) {
        return new ReporteDtos.ReporteResponse(r.id(), r.tipo().name(), r.objetivoId(), r.motivo(),
                r.estado().name(), r.fechaCreacion(),
                service.evidenciasDe(r.id(), r.reportanteId()).stream()
                        .map(e -> new ReporteDtos.EvidenciaResponse(e.id(), e.archivoKey(), e.tipoContenido()))
                        .toList());
    }

    @PostMapping("/usuarios/{id}/reportes")
    public ResponseEntity<ReporteDtos.ReporteIdResponse> reportarUsuario(@PathVariable UUID id,
            @Valid @RequestBody ReporteDtos.CrearRequest req) {
        UUID reporteId = service.crearReporteUsuario(id, SecurityUtils.actual().id(), req.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReporteDtos.ReporteIdResponse(reporteId));
    }

    @PostMapping("/servicios/{id}/reportes")
    public ResponseEntity<ReporteDtos.ReporteIdResponse> reportarServicio(@PathVariable UUID id,
            @Valid @RequestBody ReporteDtos.CrearRequest req) {
        UUID reporteId = service.crearReporteServicio(id, SecurityUtils.actual().id(), req.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ReporteDtos.ReporteIdResponse(reporteId));
    }

    @PostMapping(value = "/reportes/{id}/evidencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReporteDtos.EvidenciaResponse> agregarEvidencia(@PathVariable UUID id,
            @RequestPart("archivo") MultipartFile archivo) throws IOException {
        var evidencia = service.agregarEvidencia(id, SecurityUtils.actual().id(),
                archivo.getOriginalFilename(), archivo.getContentType(), archivo.getBytes());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ReporteDtos.EvidenciaResponse(evidencia.id(), evidencia.archivoKey(), evidencia.tipoContenido()));
    }

    @GetMapping("/reportes/mios/{id}")
    public ResponseEntity<ReporteDtos.ReporteResponse> miReporte(@PathVariable UUID id) {
        Reporte reporte = service.miReporte(id, SecurityUtils.actual().id());
        return ResponseEntity.ok(aRespuesta(reporte));
    }
}
