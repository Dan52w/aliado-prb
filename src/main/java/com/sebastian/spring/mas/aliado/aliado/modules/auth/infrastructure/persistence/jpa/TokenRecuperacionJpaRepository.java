package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.TokenRecuperacionEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRecuperacionJpaRepository extends JpaRepository<TokenRecuperacionEntity, UUID> {
    Optional<TokenRecuperacionEntity> findByCodigoHash(String codigoHash);
}
