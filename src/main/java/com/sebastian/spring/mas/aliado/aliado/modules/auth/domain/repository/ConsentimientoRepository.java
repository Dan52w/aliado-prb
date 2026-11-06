package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Consentimiento;

public interface ConsentimientoRepository {
    Consentimiento guardar(Consentimiento consentimiento);
}
