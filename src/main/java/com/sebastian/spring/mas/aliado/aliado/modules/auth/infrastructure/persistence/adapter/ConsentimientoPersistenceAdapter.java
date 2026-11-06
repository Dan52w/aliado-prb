package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Consentimiento;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository.ConsentimientoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.jpa.ConsentimientoJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.mapper.AuthPersistenceMapper;
import org.springframework.stereotype.Component;

@Component
public class ConsentimientoPersistenceAdapter implements ConsentimientoRepository {
    private final ConsentimientoJpaRepository jpa;

    public ConsentimientoPersistenceAdapter(ConsentimientoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Consentimiento guardar(Consentimiento consentimiento) {
        jpa.save(AuthPersistenceMapper.aEntidad(consentimiento));
        return consentimiento;
    }
}
