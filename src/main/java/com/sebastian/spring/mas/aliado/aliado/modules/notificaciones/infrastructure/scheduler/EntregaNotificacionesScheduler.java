package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.infrastructure.scheduler;

import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port.EntregaNotificacionesPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Worker de entrega de notificaciones pendientes (plan general, sección 19). */
@Component
public class EntregaNotificacionesScheduler {
    private final EntregaNotificacionesPort entrega;

    public EntregaNotificacionesScheduler(EntregaNotificacionesPort entrega) {
        this.entrega = entrega;
    }

    @Scheduled(fixedDelay = 60_000)
    public void entregarPendientes() {
        entrega.entregarPendientes();
    }
}
