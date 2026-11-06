package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.entity.ZonaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ZonaJpaRepository extends JpaRepository<ZonaEntity, UUID> {
    @Query("select z from ZonaEntity z where lower(z.nombre) = lower(:nombre)")
    Optional<ZonaEntity> findByNombreIgnoreCase(String nombre);
    List<ZonaEntity> findByEstado(String estado);
}
