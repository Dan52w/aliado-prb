package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.entity.CategoriaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoriaJpaRepository extends JpaRepository<CategoriaEntity, UUID> {
    @Query("select c from CategoriaEntity c where lower(c.nombre) = lower(:nombre)")
    Optional<CategoriaEntity> findByNombreIgnoreCase(String nombre);
    List<CategoriaEntity> findByEstado(String estado);
}
