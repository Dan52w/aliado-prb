package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.mapper.ConfianzaPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Reputacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.ReputacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.jpa.ReputacionJpaRepository;

/** Adaptador de reputación agregada (SPEC-10). */
@Component
public class ReputacionPersistenceAdapter implements ReputacionRepository {
    private final ReputacionJpaRepository reputaciones;

    public ReputacionPersistenceAdapter(ReputacionJpaRepository reputaciones) {
        this.reputaciones = reputaciones;
    }

    @Override
    public Optional<Reputacion> porPrestadorId(UUID prestadorId) {
        return reputaciones.findById(prestadorId).map(ConfianzaPersistenceMapper::aDominio);
    }

    @Override
    public Reputacion guardar(Reputacion reputacion) {
        return ConfianzaPersistenceMapper.aDominio(reputaciones.save(ConfianzaPersistenceMapper.aEntidad(reputacion)));
    }
}
