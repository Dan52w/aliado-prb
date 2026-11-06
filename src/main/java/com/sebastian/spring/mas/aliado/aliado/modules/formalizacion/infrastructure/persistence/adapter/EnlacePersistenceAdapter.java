package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.mapper.FormalizacionPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Enlace;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository.EnlaceRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.jpa.EnlaceJpaRepository;

/** Adaptador de enlaces de formalización (SPEC-13). */
@Component
public class EnlacePersistenceAdapter implements EnlaceRepository {
    private final EnlaceJpaRepository enlaces;

    public EnlacePersistenceAdapter(EnlaceJpaRepository enlaces) {
        this.enlaces = enlaces;
    }

    @Override
    public Optional<Enlace> porId(UUID id) {
        return enlaces.findById(id).map(FormalizacionPersistenceMapper::aDominio);
    }

    @Override
    public List<Enlace> porPaso(UUID pasoId) {
        return enlaces.findByPasoId(pasoId).stream().map(FormalizacionPersistenceMapper::aDominio).toList();
    }

    @Override
    public Enlace guardar(Enlace enlace) {
        return FormalizacionPersistenceMapper.aDominio(
                enlaces.save(FormalizacionPersistenceMapper.aEntidad(enlace)));
    }
}
