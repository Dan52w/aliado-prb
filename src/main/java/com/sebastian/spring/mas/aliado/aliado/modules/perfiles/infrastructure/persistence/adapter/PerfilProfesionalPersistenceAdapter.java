package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.repository.PerfilProfesionalRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.jpa.PerfilProfesionalJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.mapper.PerfilProfesionalPersistenceMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PerfilProfesionalPersistenceAdapter implements PerfilProfesionalRepository {
    private final PerfilProfesionalJpaRepository jpa;
    private final ObjectMapper mapper;

    public PerfilProfesionalPersistenceAdapter(PerfilProfesionalJpaRepository jpa, ObjectMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<PerfilProfesional> porPrestadorId(UUID prestadorId) {
        return jpa.findByPrestadorId(prestadorId)
                .map(e -> PerfilProfesionalPersistenceMapper.aDominio(e, mapper));
    }

    @Override
    public List<PerfilProfesional> visiblesPorCategoriaYZona(UUID categoriaId, UUID zonaId) {
        return jpa.findVisibles(categoriaId, zonaId).stream()
                .map(e -> PerfilProfesionalPersistenceMapper.aDominio(e, mapper)).toList();
    }

    @Override
    public PerfilProfesional guardar(PerfilProfesional perfil) {
        return PerfilProfesionalPersistenceMapper.aDominio(
                jpa.save(PerfilProfesionalPersistenceMapper.aEntidad(perfil, mapper)), mapper);
    }
}
