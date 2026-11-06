package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity.ResenaEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResenaJpaRepository extends JpaRepository<ResenaEntity, UUID> {
    Optional<ResenaEntity> findByCalificacionId(UUID calificacionId);
}
