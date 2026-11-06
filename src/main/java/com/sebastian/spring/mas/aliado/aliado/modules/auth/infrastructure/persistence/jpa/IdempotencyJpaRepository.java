package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.IdempotencyKeyEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.IdempotencyKeyId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdempotencyJpaRepository extends JpaRepository<IdempotencyKeyEntity, IdempotencyKeyId> {}
