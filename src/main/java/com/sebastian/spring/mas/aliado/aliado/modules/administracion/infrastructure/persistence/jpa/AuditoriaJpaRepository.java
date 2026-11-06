package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity.AuditoriaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaJpaRepository extends JpaRepository<AuditoriaEntity, UUID> {
    List<AuditoriaEntity> findAllByOrderByFechaDesc(Pageable pageable);
}
