package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.BaseController;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.common.idempotencia.IdempotencyService;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.application.SolicitudService;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Solicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.presentation.dto.SolicitudDtos;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Gestión de solicitudes de servicio (SPEC-04). Todas las rutas exigen rol CLIENTE. */
@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudController extends BaseController {
    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService,
                               ObjectMapper objectMapper, IdempotencyService idempotencyService) {
        super(objectMapper, idempotencyService);
        this.solicitudService = solicitudService;
    }

    private SolicitudDtos.SolicitudResponse aRespuesta(Solicitud s) {
        return new SolicitudDtos.SolicitudResponse(s.id(), s.clienteId(), s.categoriaId(), s.zonaId(),
                s.descripcion(), s.urgencia() == null ? null : s.urgencia().name(), s.estado().name(),
                s.fechaCreacion(), s.fechaActualizacion(),
                s.fotos().stream().map(f -> f.archivoKey()).toList());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody SolicitudDtos.CrearRequest req,
                                   @RequestHeader(value = "Idempotency-Key", required = false) String claveHeader) {
        SecurityUtils.exigirRol("CLIENTE");
        UUID clienteId = SecurityUtils.actual().id();
        return ejecutarIdempotente(claveHeader, "solicitudes.crear", cuerpo -> clienteId, 201, () -> {
            Solicitud creada = solicitudService.crear(clienteId, req.categoriaId(), req.descripcion(),
                    req.zonaId(), req.urgencia(), req.estadoDeseado());
            return aRespuesta(creada);
        });
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolicitudDtos.SolicitudResponse> editar(@PathVariable UUID id,
            @Valid @RequestBody SolicitudDtos.EditarRequest req) {
        SecurityUtils.exigirRol("CLIENTE");
        Solicitud editada = solicitudService.editar(id, SecurityUtils.actual().id(), req.categoriaId(),
                req.descripcion(), req.zonaId(), req.urgencia());
        return ResponseEntity.ok(aRespuesta(editada));
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<SolicitudDtos.SolicitudResponse> publicar(@PathVariable UUID id) {
        SecurityUtils.exigirRol("CLIENTE");
        return ResponseEntity.ok(aRespuesta(solicitudService.publicar(id, SecurityUtils.actual().id())));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<SolicitudDtos.IdEstadoResponse> cancelar(@PathVariable UUID id) {
        SecurityUtils.exigirRol("CLIENTE");
        Solicitud cancelada = solicitudService.cancelar(id, SecurityUtils.actual().id());
        return ResponseEntity.ok(new SolicitudDtos.IdEstadoResponse(cancelada.id(), cancelada.estado().name()));
    }

    @GetMapping("/mias")
    public ResponseEntity<List<SolicitudDtos.SolicitudResponse>> mias() {
        SecurityUtils.exigirRol("CLIENTE");
        return ResponseEntity.ok(solicitudService.listarMias(SecurityUtils.actual().id()).stream()
                .map(this::aRespuesta).toList());
    }

    @GetMapping("/mias/{id}")
    public ResponseEntity<SolicitudDtos.SolicitudResponse> detalle(@PathVariable UUID id) {
        SecurityUtils.exigirRol("CLIENTE");
        return ResponseEntity.ok(aRespuesta(solicitudService.detalleMio(id, SecurityUtils.actual().id())));
    }

    @PostMapping(value = "/{id}/fotos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SolicitudDtos.FotoResponse> agregarFoto(@PathVariable UUID id,
            @RequestPart("archivo") MultipartFile archivo) throws IOException {
        SecurityUtils.exigirRol("CLIENTE");
        var foto = solicitudService.agregarFoto(id, SecurityUtils.actual().id(),
                archivo.getOriginalFilename(), archivo.getContentType(), archivo.getBytes());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SolicitudDtos.FotoResponse(foto.id(), foto.archivoKey(), foto.orden()));
    }
}
