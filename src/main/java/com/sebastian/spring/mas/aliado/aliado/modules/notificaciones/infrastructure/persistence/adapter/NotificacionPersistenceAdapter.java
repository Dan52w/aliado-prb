package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.Notificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.repository.NotificacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.infrastructure.persistence.jpa.NotificacionJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.infrastructure.persistence.mapper.NotificacionPersistenceMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class NotificacionPersistenceAdapter implements NotificacionRepository {
    private final NotificacionJpaRepository jpa;

    public NotificacionPersistenceAdapter(NotificacionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean existeEvento(String tipo, UUID aggregateId, UUID destinatarioId, int eventVersion) {
        return jpa.existsByTipoAndAggregateIdAndDestinatarioIdAndEventVersion(tipo, aggregateId, destinatarioId, eventVersion);
    }

    @Override
    public List<Notificacion> porDestinatario(UUID destinatarioId) {
        return jpa.findByDestinatarioIdOrderByFechaGeneracionDesc(destinatarioId).stream()
                .map(NotificacionPersistenceMapper::aDominio).toList();
    }

    @Override
    public List<Notificacion> porDestinatarioYTipo(UUID destinatarioId, String tipo) {
        return jpa.findByDestinatarioIdAndTipoOrderByFechaGeneracionDesc(destinatarioId, tipo).stream()
                .map(NotificacionPersistenceMapper::aDominio).toList();
    }

    @Override
    public List<Notificacion> pendientesParaEntrega() {
        return jpa.findPendientesParaEntrega().stream().map(NotificacionPersistenceMapper::aDominio).toList();
    }

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        return NotificacionPersistenceMapper.aDominio(jpa.save(NotificacionPersistenceMapper.aEntidad(notificacion)));
    }
}
