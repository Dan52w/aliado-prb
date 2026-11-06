package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity.AccionModeracionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccionModeracionJpaRepository extends JpaRepository<AccionModeracionEntity, UUID> {
    List<AccionModeracionEntity> findByObjetivoTipoAndObjetivoId(String objetivoTipo, UUID objetivoId);
}
