package com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.entity.SolicitudEliminacionEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SolicitudEliminacionJpaRepository extends JpaRepository<SolicitudEliminacionEntity, UUID> {
    Optional<SolicitudEliminacionEntity> findByUsuarioIdAndEstado(UUID usuarioId, String estado);

    @Query("select s from SolicitudEliminacionEntity s where s.estado = 'PENDIENTE' and s.fechaExpiracion < :ahora")
    List<SolicitudEliminacionEntity> findPendientesVencidas(OffsetDateTime ahora);
}
