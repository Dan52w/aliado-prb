package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.Notificacion;

/** Puerto de entrega externa (push/correo). El MVP usa un adaptador de registro (P-05). */
public interface CanalEntregaNotificacion {
    /** True si el canal aceptó la entrega. */
    boolean entregar(Notificacion notificacion);
}
