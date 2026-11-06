package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity.RutaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaJpaRepository extends JpaRepository<RutaEntity, UUID> {
    List<RutaEntity> findByActivaTrue();
}
