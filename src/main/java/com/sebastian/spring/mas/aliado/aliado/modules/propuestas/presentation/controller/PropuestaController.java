package com.sebastian.spring.mas.aliado.aliado.modules.propuestas.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.idempotencia.IdempotencyService;
import com.sebastian.spring.mas.aliado.aliado.common.web.BaseController;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.application.PropuestaService;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model.Propuesta;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.presentation.dto.PropuestaDtos;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Creación y gestión de propuestas (SPEC-07). Solo rol PRESTADOR, salvo la lectura del CLIENTE. */
@RestController
@RequestMapping("/api/v1")
public class PropuestaController extends BaseController {
    private final PropuestaService service;

    public PropuestaController(PropuestaService service, ObjectMapper objectMapper,
                               IdempotencyService idempotencyService) {
        super(objectMapper, idempotencyService);
        this.service = service;
    }

    private PropuestaDtos.PropuestaResponse aRespuesta(Propuesta p) {
        return new PropuestaDtos.PropuestaResponse(p.id(), p.solicitudId(), p.prestadorId(),
                p.disponibilidad(), p.mensaje(), p.estado().name());
    }

    @PostMapping("/propuestas")
    public ResponseEntity<?> crear(@Valid @RequestBody PropuestaDtos.CrearRequest req,
                                   @RequestHeader(value = "Idempotency-Key", required = false) String claveHeader) {
        SecurityUtils.exigirRol("PRESTADOR");
        UUID prestadorId = SecurityUtils.actual().id();
        return ejecutarIdempotente(claveHeader, "propuestas.crear", cuerpo -> prestadorId, 201, () -> {
            Propuesta creada = service.crear(prestadorId, req.solicitudId(), req.disponibilidad(), req.mensaje());
            return aRespuesta(creada);
        });
    }

    @PutMapping("/propuestas/{id}")
    public ResponseEntity<PropuestaDtos.PropuestaResponse> editar(@PathVariable UUID id,
            @Valid @RequestBody PropuestaDtos.EditarRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(aRespuesta(service.editar(id, SecurityUtils.actual().id(),
                req.disponibilidad(), req.mensaje())));
    }

    @PostMapping("/propuestas/{id}/retirar")
    public ResponseEntity<PropuestaDtos.IdEstadoResponse> retirar(@PathVariable UUID id) {
        SecurityUtils.exigirRol("PRESTADOR");
        Propuesta retirada = service.retirar(id, SecurityUtils.actual().id());
        return ResponseEntity.ok(new PropuestaDtos.IdEstadoResponse(retirada.id(), retirada.estado().name()));
    }

    @GetMapping("/solicitudes/{solicitudId}/propuestas")
    public ResponseEntity<List<PropuestaDtos.PropuestaResponse>> propuestasDeSolicitud(
            @PathVariable UUID solicitudId) {
        SecurityUtils.exigirRol("CLIENTE");
        return ResponseEntity.ok(service.propuestasDeSolicitud(solicitudId, SecurityUtils.actual().id()).stream()
                .map(this::aRespuesta).toList());
    }
}
