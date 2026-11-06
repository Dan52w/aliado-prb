package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.Notificacion;
import java.util.List;
import java.util.UUID;

public interface NotificacionRepository {
    boolean existeEvento(String tipo, UUID aggregateId, UUID destinatarioId, int eventVersion);
    List<Notificacion> porDestinatario(UUID destinatarioId);
    List<Notificacion> porDestinatarioYTipo(UUID destinatarioId, String tipo);
    List<Notificacion> pendientesParaEntrega();
    Notificacion guardar(Notificacion notificacion);
}
