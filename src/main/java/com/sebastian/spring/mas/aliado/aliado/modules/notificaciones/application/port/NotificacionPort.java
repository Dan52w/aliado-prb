package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.Canal;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.TipoNotificacion;
import java.util.UUID;

/** Contrato público de notificaciones: registro idempotente por evento (plan general 19.3). */
public interface NotificacionPort {
    void registrar(TipoNotificacion tipo, UUID aggregateId, UUID destinatarioId, int eventVersion,
                   String detalle, Canal canal);
}
