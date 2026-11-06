package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.infrastructure.persistence.entity.NotificacionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificacionJpaRepository extends JpaRepository<NotificacionEntity, UUID> {
    boolean existsByTipoAndAggregateIdAndDestinatarioIdAndEventVersion(String tipo, UUID aggregateId,
                                                                       UUID destinatarioId, int eventVersion);
    List<NotificacionEntity> findByDestinatarioIdOrderByFechaGeneracionDesc(UUID destinatarioId);
    List<NotificacionEntity> findByDestinatarioIdAndTipoOrderByFechaGeneracionDesc(UUID destinatarioId, String tipo);

    @Query("select n from NotificacionEntity n where n.estadoEntrega in ('PENDIENTE', 'FALLIDA') and n.intentos < 3 order by n.fechaGeneracion asc")
    List<NotificacionEntity> findPendientesParaEntrega();
}
