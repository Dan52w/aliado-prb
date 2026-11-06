package com.sebastian.spring.mas.aliado.aliado.modules.reportes.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.reportes.infrastructure.persistence.entity.EvidenciaReporteEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvidenciaReporteJpaRepository extends JpaRepository<EvidenciaReporteEntity, UUID> {
    List<EvidenciaReporteEntity> findByReporteId(UUID reporteId);
}
