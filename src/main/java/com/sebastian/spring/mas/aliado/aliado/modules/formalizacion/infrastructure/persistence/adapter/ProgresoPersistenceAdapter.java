package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.mapper.FormalizacionPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Progreso;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository.ProgresoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.jpa.ProgresoJpaRepository;

/** Adaptador de progreso de formalización (SPEC-13). */
@Component
public class ProgresoPersistenceAdapter implements ProgresoRepository {
    private final ProgresoJpaRepository progresos;

    public ProgresoPersistenceAdapter(ProgresoJpaRepository progresos) {
        this.progresos = progresos;
    }

    @Override
    public Optional<Progreso> porPrestadorYRuta(UUID prestadorId, UUID rutaId) {
        return progresos.findByPrestadorIdAndRutaId(prestadorId, rutaId)
                .map(FormalizacionPersistenceMapper::aDominio);
    }

    @Override
    public Progreso guardar(Progreso progreso) {
        return FormalizacionPersistenceMapper.aDominio(
                progresos.save(FormalizacionPersistenceMapper.aEntidad(progreso)));
    }
}
