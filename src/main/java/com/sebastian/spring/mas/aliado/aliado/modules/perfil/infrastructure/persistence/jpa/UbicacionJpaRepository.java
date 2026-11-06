package com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.entity.UbicacionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UbicacionJpaRepository extends JpaRepository<UbicacionEntity, UUID> {}
