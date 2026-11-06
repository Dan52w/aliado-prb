package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.infrastructure.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port.CanalEntregaNotificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.Notificacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Canal de entrega del MVP (P-05): registra el intento en el log y lo marca entregado.
 * El proveedor real (FCM/correo) se conecta aquí sin tocar los casos de uso.
 */
@Component
public class LogCanalEntregaAdapter implements CanalEntregaNotificacion {
    private static final Logger log = LoggerFactory.getLogger(LogCanalEntregaAdapter.class);

    @Override
    public boolean entregar(Notificacion notificacion) {
        log.info("Entrega de notificación {} tipo={} destinatario={} canal={}",
                notificacion.id(), notificacion.tipo(), notificacion.destinatarioId(), notificacion.canal());
        return true;
    }
}
