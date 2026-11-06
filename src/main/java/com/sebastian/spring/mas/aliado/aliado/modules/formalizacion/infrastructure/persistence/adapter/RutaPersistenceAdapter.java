package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.mapper.FormalizacionPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Ruta;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository.RutaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.jpa.RutaJpaRepository;

/** Adaptador de rutas de formalización (SPEC-13). */
@Component
public class RutaPersistenceAdapter implements RutaRepository {
    private final RutaJpaRepository rutas;

    public RutaPersistenceAdapter(RutaJpaRepository rutas) {
        this.rutas = rutas;
    }

    @Override
    public List<Ruta> activas() {
        return rutas.findByActivaTrue().stream().map(FormalizacionPersistenceMapper::aDominio).toList();
    }

    @Override
    public Optional<Ruta> porId(UUID id) {
        return rutas.findById(id).map(FormalizacionPersistenceMapper::aDominio);
    }
}
