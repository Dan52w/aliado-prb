package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.entity.PerfilProfesionalEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PerfilProfesionalJpaRepository extends JpaRepository<PerfilProfesionalEntity, UUID> {
    Optional<PerfilProfesionalEntity> findByPrestadorId(UUID prestadorId);

    @Query("select distinct p from PerfilProfesionalEntity p where p.estadoExposicionPublica = 'PUBLICO'"
            + " and (:categoriaId is null or :categoriaId member of p.categoriaIds)"
            + " and (:zonaId is null or :zonaId member of p.zonaIds)")
    List<PerfilProfesionalEntity> findVisibles(UUID categoriaId, UUID zonaId);
}
