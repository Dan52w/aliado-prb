package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.mapper.ConfianzaPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Resena;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.ResenaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.jpa.ResenaJpaRepository;

/** Adaptador de reseñas (SPEC-10). */
@Component
public class ResenaPersistenceAdapter implements ResenaRepository {
    private final ResenaJpaRepository resenas;

    public ResenaPersistenceAdapter(ResenaJpaRepository resenas) {
        this.resenas = resenas;
    }

    @Override
    public Optional<Resena> porId(UUID id) {
        return resenas.findById(id).map(ConfianzaPersistenceMapper::aDominio);
    }

    @Override
    public Optional<Resena> porCalificacionId(UUID calificacionId) {
        return resenas.findByCalificacionId(calificacionId).map(ConfianzaPersistenceMapper::aDominio);
    }

    @Override
    public Resena guardar(Resena resena) {
        return ConfianzaPersistenceMapper.aDominio(resenas.save(ConfianzaPersistenceMapper.aEntidad(resena)));
    }
}
