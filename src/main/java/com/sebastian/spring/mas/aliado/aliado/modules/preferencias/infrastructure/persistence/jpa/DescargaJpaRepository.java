package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity.DescargaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescargaJpaRepository extends JpaRepository<DescargaEntity, UUID> {
    List<DescargaEntity> findByUsuarioIdOrderByFechaGeneracionDesc(UUID usuarioId);
}
