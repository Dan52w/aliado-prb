package com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.NotificacionService;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.application.OportunidadService;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.domain.model.Oportunidad;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Tablero de oportunidades del Prestador (SPEC-06). Solo rol PRESTADOR. */
@RestController
@RequestMapping("/api/v1")
public class OportunidadController {
    private final OportunidadService service;
    private final NotificacionService notificaciones;

    public OportunidadController(OportunidadService service, NotificacionService notificaciones) {
        this.service = service;
        this.notificaciones = notificaciones;
    }

    @GetMapping("/oportunidades")
    public ResponseEntity<List<Oportunidad>> listar(@RequestParam(required = false) String categoria,
                                                    @RequestParam(required = false) String urgencia) {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(service.listar(SecurityUtils.actual().id(), categoria, urgencia));
    }

    @GetMapping("/oportunidades/{solicitudId}")
    public ResponseEntity<Oportunidad> detalle(@PathVariable UUID solicitudId) {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(service.detalle(solicitudId, SecurityUtils.actual().id()));
    }

    @GetMapping("/notificaciones/oportunidades")
    public ResponseEntity<List<NotificacionService.NotificacionDto>> notificacionesOportunidades() {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(notificaciones.listarPorTipo(SecurityUtils.actual().id(), "NUEVA_OPORTUNIDAD"));
    }
}
