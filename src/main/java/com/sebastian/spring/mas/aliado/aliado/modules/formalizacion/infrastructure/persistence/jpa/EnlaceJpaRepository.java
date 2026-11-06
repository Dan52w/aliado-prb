package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity.EnlaceEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnlaceJpaRepository extends JpaRepository<EnlaceEntity, UUID> {
    List<EnlaceEntity> findByPasoId(UUID pasoId);
}
