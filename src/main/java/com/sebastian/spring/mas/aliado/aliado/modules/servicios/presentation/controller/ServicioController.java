package com.sebastian.spring.mas.aliado.aliado.modules.servicios.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.idempotencia.IdempotencyService;
import com.sebastian.spring.mas.aliado.aliado.common.web.BaseController;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.ServicioService;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.ServicioContratado;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.presentation.dto.ServicioDtos;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Contratación y ciclo de vida del servicio (SPEC-08). */
@RestController
@RequestMapping("/api/v1")
public class ServicioController extends BaseController {
    private final ServicioService service;

    public ServicioController(ServicioService service, ObjectMapper objectMapper,
                              IdempotencyService idempotencyService) {
        super(objectMapper, idempotencyService);
        this.service = service;
    }

    private ServicioDtos.ServicioResponse aRespuesta(ServicioContratado s) {
        return new ServicioDtos.ServicioResponse(s.id(), s.solicitudId(), s.propuestaId(), s.clienteId(),
                s.prestadorId(), s.estado().name(), s.comunicacionHabilitada(), s.fechaContratacion());
    }

    @PostMapping("/propuestas/{id}/aceptar")
    public ResponseEntity<?> aceptar(@PathVariable UUID id,
                                     @RequestHeader(value = "Idempotency-Key", required = false) String claveHeader) {
        SecurityUtils.exigirRol("CLIENTE");
        UUID clienteId = SecurityUtils.actual().id();
        return ejecutarIdempotente(claveHeader, "servicios.aceptar", cuerpo -> clienteId, 200, () -> {
            ServicioContratado servicio = service.aceptarPropuesta(id, clienteId);
            return new ServicioDtos.AceptarResponse(servicio.id(), servicio.estado().name());
        });
    }

    @GetMapping("/servicios/{id}")
    public ResponseEntity<ServicioDtos.ServicioResponse> detalle(@PathVariable UUID id) {
        return ResponseEntity.ok(aRespuesta(service.detalle(id, SecurityUtils.actual().id())));
    }

    @PostMapping("/servicios/{id}/en-ejecucion")
    public ResponseEntity<ServicioDtos.IdEstadoResponse> enEjecucion(@PathVariable UUID id) {
        ServicioContratado s = service.enEjecucion(id, SecurityUtils.actual().id());
        return ResponseEntity.ok(new ServicioDtos.IdEstadoResponse(s.id(), s.estado().name()));
    }

    @PostMapping("/servicios/{id}/terminado")
    public ResponseEntity<ServicioDtos.IdEstadoResponse> terminado(@PathVariable UUID id) {
        ServicioContratado s = service.terminado(id, SecurityUtils.actual().id());
        return ResponseEntity.ok(new ServicioDtos.IdEstadoResponse(s.id(), s.estado().name()));
    }

    @PostMapping("/servicios/{id}/confirmar-finalizacion")
    public ResponseEntity<ServicioDtos.IdEstadoResponse> confirmarFinalizacion(@PathVariable UUID id) {
        ServicioContratado s = service.confirmarFinalizacion(id, SecurityUtils.actual().id());
        return ResponseEntity.ok(new ServicioDtos.IdEstadoResponse(s.id(), s.estado().name()));
    }

    @PostMapping("/servicios/{id}/cancelar")
    public ResponseEntity<ServicioDtos.IdEstadoResponse> cancelar(@PathVariable UUID id) {
        ServicioContratado s = service.cancelar(id, SecurityUtils.actual().id());
        return ResponseEntity.ok(new ServicioDtos.IdEstadoResponse(s.id(), s.estado().name()));
    }

    @GetMapping("/servicios/{id}/historial")
    public ResponseEntity<List<ServicioDtos.HistorialResponse>> historial(@PathVariable UUID id) {
        return ResponseEntity.ok(service.historial(id, SecurityUtils.actual().id()).stream()
                .map(h -> new ServicioDtos.HistorialResponse(
                        h.estadoAnterior() == null ? null : h.estadoAnterior().name(),
                        h.estadoNuevo().name(), h.actorId(), h.fecha())).toList());
    }

    @PostMapping("/servicios/{id}/mensajes")
    public ResponseEntity<ServicioDtos.MensajeResponse> enviarMensaje(@PathVariable UUID id,
            @Valid @RequestBody ServicioDtos.MensajeRequest req) {
        var m = service.enviarMensaje(id, SecurityUtils.actual().id(), req.contenido());
        return ResponseEntity.status(201)
                .body(new ServicioDtos.MensajeResponse(m.id(), m.remitenteId(), m.contenido(), m.fechaEnvio()));
    }

    @GetMapping("/servicios/{id}/mensajes")
    public ResponseEntity<List<ServicioDtos.MensajeResponse>> listarMensajes(@PathVariable UUID id) {
        return ResponseEntity.ok(service.listarMensajes(id, SecurityUtils.actual().id()).stream()
                .map(m -> new ServicioDtos.MensajeResponse(m.id(), m.remitenteId(), m.contenido(), m.fechaEnvio()))
                .toList());
    }
}
