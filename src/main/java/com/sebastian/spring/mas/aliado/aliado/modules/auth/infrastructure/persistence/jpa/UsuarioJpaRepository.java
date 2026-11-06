package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.UsuarioEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, UUID> {
    @Query("select u from UsuarioEntity u where lower(u.email) = lower(:email)")
    Optional<UsuarioEntity> findByEmailIgnoreCase(String email);

    @Query("select count(u) > 0 from UsuarioEntity u where lower(u.email) = lower(:email)")
    boolean existsByEmailIgnoreCase(String email);
}
