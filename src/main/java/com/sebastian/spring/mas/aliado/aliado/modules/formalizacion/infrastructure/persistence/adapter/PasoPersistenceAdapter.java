package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.mapper.FormalizacionPersistenceMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Paso;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository.PasoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.jpa.PasoJpaRepository;

/** Adaptador de pasos de formalización (SPEC-13). */
@Component
public class PasoPersistenceAdapter implements PasoRepository {
    private final PasoJpaRepository pasos;

    public PasoPersistenceAdapter(PasoJpaRepository pasos) {
        this.pasos = pasos;
    }

    @Override
    public List<Paso> porRuta(UUID rutaId) {
        return pasos.findByRutaIdOrderByOrdenAsc(rutaId).stream()
                .map(FormalizacionPersistenceMapper::aDominio).toList();
    }
}
