package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.SesionEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionJpaRepository extends JpaRepository<SesionEntity, UUID> {
    Optional<SesionEntity> findByTokenHash(String tokenHash);
    List<SesionEntity> findByUsuarioIdAndEstado(UUID usuarioId, String estado);
}
