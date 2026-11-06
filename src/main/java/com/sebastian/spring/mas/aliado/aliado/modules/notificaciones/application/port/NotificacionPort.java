package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port;

import java.util.UUID;

/** Contrato público de notificaciones: registro idempotente por evento (plan general 19.3). */
public interface NotificacionPort {
    void registrar(String tipo, UUID aggregateId, UUID destinatarioId, int eventVersion, String detalle, String canal);
}
