package com.sebastian.spring.mas.aliado.aliado.modules.confianza.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.ConfianzaService;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.VerificacionService;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.presentation.dto.ConfianzaDtos;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

/** Calificaciones, reseñas, reputación y verificación (SPEC-10). */
@RestController
@RequestMapping("/api/v1")
public class ConfianzaController {
    private final ConfianzaService service;
    private final VerificacionService verificaciones;

    public ConfianzaController(ConfianzaService service, VerificacionService verificaciones) {
        this.service = service;
        this.verificaciones = verificaciones;
    }

    @PostMapping("/servicios/{servicioId}/calificacion")
    public ResponseEntity<ConfianzaDtos.CalificacionResponse> calificar(@PathVariable UUID servicioId,
            @Valid @RequestBody ConfianzaDtos.CalificarRequest req) {
        var calificacion = service.calificar(servicioId, SecurityUtils.actual().id(), req.valor());
        var confianza = service.confianzaDe(calificacion.prestadorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ConfianzaDtos.CalificacionResponse(calificacion.id(), confianza.reputacion().valor()));
    }

    @PostMapping("/calificaciones/{id}/resena")
    public ResponseEntity<ConfianzaDtos.ResenaResponse> resena(@PathVariable UUID id,
            @Valid @RequestBody ConfianzaDtos.ResenaRequest req) {
        var resena = service.agregarResena(id, SecurityUtils.actual().id(), req.texto());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ConfianzaDtos.ResenaResponse(resena.id()));
    }

    @GetMapping("/prestadores/{prestadorId}/confianza")
    public ResponseEntity<ConfianzaDtos.ConfianzaResponse> confianza(@PathVariable UUID prestadorId) {
        var info = service.confianzaDe(prestadorId);
        return ResponseEntity.ok(new ConfianzaDtos.ConfianzaResponse(
                info.reputacion().valor(), info.reputacion().cantidad(), info.nivelVerificacion().name()));
    }

    @GetMapping("/prestadores/{prestadorId}/resenas")
    public ResponseEntity<List<ConfianzaDtos.ResenaPublicaResponse>> resenas(@PathVariable UUID prestadorId) {
        return ResponseEntity.ok(service.resenasVisiblesDe(prestadorId).stream()
                .map(r -> new ConfianzaDtos.ResenaPublicaResponse(r.id(), r.valor(), r.texto(),
                        r.fechaCreacion()))
                .toList());
    }

    @PostMapping(value = "/verificaciones", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ConfianzaDtos.VerificacionResponse> solicitarVerificacion(
            @RequestPart("documentos") List<MultipartFile> documentos) {
        SecurityUtils.exigirRol("PRESTADOR");
        List<VerificacionService.ArchivoSubido> archivos = new ArrayList<>();
        for (MultipartFile archivo : documentos) {
            byte[] contenido;
            try {
                contenido = archivo.getBytes();
            } catch (IOException ex) {
                throw new BusinessException.BadRequest("ARCHIVO_INVALIDO",
                        "No se pudo leer el archivo enviado.");
            }
            archivos.add(new VerificacionService.ArchivoSubido(archivo.getOriginalFilename(),
                    archivo.getContentType(), contenido));
        }
        var solicitud = verificaciones.solicitarVerificacion(SecurityUtils.actual().id(), archivos);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ConfianzaDtos.VerificacionResponse(solicitud.id(), solicitud.estado().name()));
    }

    @GetMapping("/verificaciones/mia")
    public ResponseEntity<ConfianzaDtos.VerificacionResponse> miVerificacion() {
        SecurityUtils.exigirRol("PRESTADOR");
        var solicitud = verificaciones.miVerificacion(SecurityUtils.actual().id());
        return ResponseEntity.ok(new ConfianzaDtos.VerificacionResponse(solicitud.id(), solicitud.estado().name()));
    }

    @PostMapping("/resenas/{id}/reportes")
    public ResponseEntity<ConfianzaDtos.ReporteResponse> reportarResena(@PathVariable UUID id,
            @Valid @RequestBody ConfianzaDtos.ReporteRequest req) {
        UUID reporteId = service.reportarResena(id, SecurityUtils.actual().id(), req.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ConfianzaDtos.ReporteResponse(reporteId));
    }
}
