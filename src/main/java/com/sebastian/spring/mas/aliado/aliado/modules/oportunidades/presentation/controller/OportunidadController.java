package com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.application.OportunidadService;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.domain.model.Oportunidad;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.presentation.dto.OportunidadDtos;
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

    public OportunidadController(OportunidadService service) {
        this.service = service;
    }

    private OportunidadDtos.OportunidadResponse aRespuesta(Oportunidad o) {
        return new OportunidadDtos.OportunidadResponse(o.solicitudId(), o.categoria(), o.zonaAproximada(),
                o.urgencia(), o.descripcion(), o.fechaCreacion(), o.fotos());
    }

    @GetMapping("/oportunidades")
    public ResponseEntity<List<OportunidadDtos.OportunidadResponse>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String urgencia) {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(service.listar(SecurityUtils.actual().id(), categoria, urgencia).stream()
                .map(this::aRespuesta).toList());
    }

    @GetMapping("/oportunidades/{solicitudId}")
    public ResponseEntity<OportunidadDtos.OportunidadResponse> detalle(@PathVariable UUID solicitudId) {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(aRespuesta(service.detalle(solicitudId, SecurityUtils.actual().id())));
    }
}
