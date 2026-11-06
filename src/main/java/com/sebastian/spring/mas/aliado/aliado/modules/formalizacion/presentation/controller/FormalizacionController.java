package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.config.FeatureFlags;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.application.FormalizacionService;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.presentation.dto.FormalizacionDtos;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Formalización (SPEC-13). La vinculación institucional (UC080) es Future: apagada. */
@RestController
@RequestMapping("/api/v1/formalizacion")
public class FormalizacionController {
    private final FormalizacionService service;
    private final FeatureFlags features;

    public FormalizacionController(FormalizacionService service, FeatureFlags features) {
        this.service = service;
        this.features = features;
    }

    @GetMapping("/ruta")
    public ResponseEntity<List<FormalizacionDtos.RutaResponse>> ruta() {
        SecurityUtils.exigirRol("PRESTADOR");
        var rutas = service.rutasActivas().stream()
                .map(r -> new FormalizacionDtos.RutaResponse(r.id(), r.nombre(), r.descripcion(),
                        service.pasosDe(r.id()).stream()
                                .map(p -> new FormalizacionDtos.PasoResponse(p.paso().id(), p.paso().titulo(),
                                        p.paso().descripcion(), p.paso().orden(),
                                        p.enlaces().stream()
                                                .map(e -> new FormalizacionDtos.EnlaceResponse(e.id(), e.nombre(),
                                                        e.url(), e.estado().name()))
                                                .toList()))
                                .toList(),
                        FormalizacionService.AVISO_LEGAL))
                .toList();
        return ResponseEntity.ok(rutas);
    }

    @GetMapping("/enlaces/{id}")
    public ResponseEntity<FormalizacionDtos.EnlaceResponse> enlace(@PathVariable UUID id) {
        var e = service.enlace(id);
        return ResponseEntity.ok(new FormalizacionDtos.EnlaceResponse(e.id(), e.nombre(), e.url(),
                e.estado().name()));
    }

    @GetMapping("/progreso")
    public ResponseEntity<List<FormalizacionDtos.ProgresoResponse>> progreso() {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(service.progreso(SecurityUtils.actual().id()).stream()
                .map(p -> new FormalizacionDtos.ProgresoResponse(p.rutaId(), p.valorOrientativo(),
                        FormalizacionService.AVISO_LEGAL))
                .toList());
    }

    @PutMapping("/progreso")
    public ResponseEntity<FormalizacionDtos.ProgresoResponse> actualizarProgreso(
            @Valid @RequestBody FormalizacionDtos.ActualizarProgresoRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        var p = service.actualizarProgreso(SecurityUtils.actual().id(), req.rutaId(), req.valor());
        return ResponseEntity.ok(new FormalizacionDtos.ProgresoResponse(p.rutaId(), p.valorOrientativo(),
                FormalizacionService.AVISO_LEGAL));
    }

    @GetMapping("/vinculacion")
    public ResponseEntity<Void> vinculacion() {
        // UC080 consulta el flag: apagado sigue siendo 404; encendido, 501 porque
        // no hay implementación detrás (nunca 200 vacío ni éxito fingido).
        if (!features.integracionInstitucional()) {
            throw new BusinessException.NotFound("FUNCIONALIDAD_DESHABILITADA",
                    "La vinculación institucional (UC080) es una función futura y no está disponible.");
        }
        throw new BusinessException(501, "NO_IMPLEMENTADO",
                "La vinculación institucional está habilitada pero aún no implementada.");
    }
}
