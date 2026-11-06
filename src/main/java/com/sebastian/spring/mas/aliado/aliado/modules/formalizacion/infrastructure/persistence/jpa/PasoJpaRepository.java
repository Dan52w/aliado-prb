package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity.PasoEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasoJpaRepository extends JpaRepository<PasoEntity, UUID> {
    List<PasoEntity> findByRutaIdOrderByOrdenAsc(UUID rutaId);
}
