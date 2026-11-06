package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity.DocumentoVerificacionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoVerificacionJpaRepository extends JpaRepository<DocumentoVerificacionEntity, UUID> {
    List<DocumentoVerificacionEntity> findBySolicitudVerificacionId(UUID solicitudVerificacionId);
}
