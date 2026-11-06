package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity.PreferenciaEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity.PreferenciaId;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferenciaJpaRepository extends JpaRepository<PreferenciaEntity, PreferenciaId> {
    List<PreferenciaEntity> findByIdUsuarioId(UUID usuarioId);
}
