package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity.ProgresoEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgresoJpaRepository extends JpaRepository<ProgresoEntity, UUID> {
    Optional<ProgresoEntity> findByPrestadorIdAndRutaId(UUID prestadorId, UUID rutaId);
}
