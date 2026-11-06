package com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.entity.PerfilEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerfilJpaRepository extends JpaRepository<PerfilEntity, UUID> {
    List<PerfilEntity> findByNombreContainingIgnoreCase(String texto);
}
