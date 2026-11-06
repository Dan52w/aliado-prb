package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port;

/** Worker de entrega de notificaciones pendientes (plan general, sección 19). */
public interface EntregaNotificacionesPort {
    void entregarPendientes();
}
