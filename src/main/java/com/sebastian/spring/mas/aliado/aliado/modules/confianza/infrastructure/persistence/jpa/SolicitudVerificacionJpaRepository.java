package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity.SolicitudVerificacionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SolicitudVerificacionJpaRepository extends JpaRepository<SolicitudVerificacionEntity, UUID> {
    Optional<SolicitudVerificacionEntity> findByPrestadorIdAndEstado(UUID prestadorId, String estado);
    Optional<SolicitudVerificacionEntity> findFirstByPrestadorIdOrderByFechaSolicitudDesc(UUID prestadorId);
    long countByPrestadorIdAndEstado(UUID prestadorId, String estado);
    List<SolicitudVerificacionEntity> findByEstadoOrderByFechaSolicitudAsc(String estado);

    @Query("select s from SolicitudVerificacionEntity s where s.prestadorId = :prestadorId order by s.fechaSolicitud desc")
    List<SolicitudVerificacionEntity> findAllByPrestador(UUID prestadorId);
}
