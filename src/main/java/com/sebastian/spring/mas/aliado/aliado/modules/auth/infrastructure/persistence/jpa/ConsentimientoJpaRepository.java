package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.ConsentimientoEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentimientoJpaRepository extends JpaRepository<ConsentimientoEntity, UUID> {}
