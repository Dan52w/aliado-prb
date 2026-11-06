package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port.NotificacionConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.TipoNotificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.presentation.dto.NotificacionDtos;
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
    private final NotificacionConsultaPort notificaciones;

    public NotificacionController(NotificacionConsultaPort notificaciones) {
        this.notificaciones = notificaciones;
    }

    private NotificacionDtos.NotificacionResponse aRespuesta(NotificacionConsultaPort.NotificacionConsulta n) {
        return new NotificacionDtos.NotificacionResponse(n.id(), n.tipo().name(), n.servicioId(), n.detalle(),
                n.estadoEntrega().name(), n.fechaGeneracion());
    }

    @GetMapping
    public ResponseEntity<List<NotificacionDtos.NotificacionResponse>> listar(
            @RequestParam(required = false) String tipo) {
        if (tipo != null && !tipo.isBlank()) {
            TipoNotificacion tipoEnum;
            try {
                tipoEnum = TipoNotificacion.valueOf(tipo.trim());
            } catch (IllegalArgumentException ex) {
                throw new BusinessException.BadRequest("TIPO_INVALIDO", "Tipo de notificación no reconocido.");
            }
            return ResponseEntity.ok(notificaciones.listarPorTipo(SecurityUtils.actual().id(), tipoEnum).stream()
                    .map(this::aRespuesta).toList());
        }
        return ResponseEntity.ok(notificaciones.listarPropias(SecurityUtils.actual().id()).stream()
                .map(this::aRespuesta).toList());
    }

    @GetMapping("/oportunidades")
    public ResponseEntity<List<NotificacionDtos.NotificacionResponse>> notificacionesOportunidades() {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(notificaciones
                .listarPorTipo(SecurityUtils.actual().id(), TipoNotificacion.NUEVA_OPORTUNIDAD).stream()
                .map(this::aRespuesta).toList());
    }
}
