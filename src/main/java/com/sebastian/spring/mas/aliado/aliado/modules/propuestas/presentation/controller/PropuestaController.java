package com.sebastian.spring.mas.aliado.aliado.modules.propuestas.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.BaseController;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.IdempotencyService;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.application.PropuestaService;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model.Propuesta;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.presentation.dto.PropuestaDtos;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Creación y gestión de propuestas (SPEC-07). Solo rol PRESTADOR. */
@RestController
@RequestMapping("/api/v1/propuestas")
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

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody PropuestaDtos.CrearRequest req,
                                   @RequestHeader(value = "Idempotency-Key", required = false) String claveHeader) {
        SecurityUtils.exigirRol("PRESTADOR");
        UUID prestadorId = SecurityUtils.actual().id();
        UUID clave = claveIdempotencia(claveHeader);
        var guardada = clave == null ? java.util.Optional.<ResponseEntity<com.fasterxml.jackson.databind.JsonNode>>empty()
                : respuestaGuardada(clave, "propuestas.crear");
        if (guardada.isPresent()) {
            return guardada.get();
        }
        Propuesta creada = service.crear(prestadorId, req.solicitudId(), req.disponibilidad(), req.mensaje());
        var cuerpo = aRespuesta(creada);
        guardarRespuesta(clave, "propuestas.crear", prestadorId, 201, cuerpo);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuerpo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropuestaDtos.PropuestaResponse> editar(@PathVariable UUID id,
            @Valid @RequestBody PropuestaDtos.EditarRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(aRespuesta(service.editar(id, SecurityUtils.actual().id(),
                req.disponibilidad(), req.mensaje())));
    }

    @PostMapping("/{id}/retirar")
    public ResponseEntity<PropuestaDtos.IdEstadoResponse> retirar(@PathVariable UUID id) {
        SecurityUtils.exigirRol("PRESTADOR");
        Propuesta retirada = service.retirar(id, SecurityUtils.actual().id());
        return ResponseEntity.ok(new PropuestaDtos.IdEstadoResponse(retirada.id(), retirada.estado().name()));
    }
}
