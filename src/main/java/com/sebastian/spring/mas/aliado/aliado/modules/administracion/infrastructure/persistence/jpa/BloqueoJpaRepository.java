package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity.BloqueoEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloqueoJpaRepository extends JpaRepository<BloqueoEntity, UUID> {
    Optional<BloqueoEntity> findByUsuarioIdAndActivoTrue(UUID usuarioId);
}
