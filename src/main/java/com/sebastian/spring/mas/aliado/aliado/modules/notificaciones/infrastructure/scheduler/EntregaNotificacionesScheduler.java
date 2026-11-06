package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.infrastructure.scheduler;

import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.NotificacionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Worker de entrega de notificaciones pendientes (plan general, sección 19). */
@Component
public class EntregaNotificacionesScheduler {
    private final NotificacionService service;

    public EntregaNotificacionesScheduler(NotificacionService service) {
        this.service = service;
    }

    @Scheduled(fixedDelay = 60_000)
    public void entregarPendientes() {
        service.entregarPendientes();
    }
}
