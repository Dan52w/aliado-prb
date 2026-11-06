package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity.ReputacionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReputacionJpaRepository extends JpaRepository<ReputacionEntity, UUID> {}
