package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.NotificacionService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Bandeja de notificaciones propias (SPEC-09). Sin endpoint de creación. */
@RestController
@RequestMapping("/api/v1/notificaciones")
public class NotificacionController {
    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionService.NotificacionDto>> listar(
            @RequestParam(required = false) String tipo) {
        if (tipo != null && !tipo.isBlank()) {
            return ResponseEntity.ok(service.listarPorTipo(SecurityUtils.actual().id(), tipo));
        }
        return ResponseEntity.ok(service.listarPropias(SecurityUtils.actual().id()));
    }
}
