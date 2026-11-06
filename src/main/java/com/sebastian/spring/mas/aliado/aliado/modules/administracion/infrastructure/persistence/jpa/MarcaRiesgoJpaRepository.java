package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity.MarcaRiesgoEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRiesgoJpaRepository extends JpaRepository<MarcaRiesgoEntity, UUID> {
    Optional<MarcaRiesgoEntity> findByServicioIdAndActivaTrue(UUID servicioId);
}
