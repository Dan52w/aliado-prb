package com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.RiesgoAuditoriaService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.dto.AdminDtos;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Riesgos y auditoría (SPEC-12). Solo rol ADMINISTRADOR (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminRiesgoController {
    private final RiesgoAuditoriaService riesgo;

    public AdminRiesgoController(RiesgoAuditoriaService riesgo) {
        this.riesgo = riesgo;
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
}
