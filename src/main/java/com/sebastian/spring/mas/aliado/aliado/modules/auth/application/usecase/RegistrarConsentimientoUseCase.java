package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.usecase;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto.RegistrarConsentimientoCommand;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Consentimiento;

/** Contrato de entrada para exigir y registrar el consentimiento durante el alta pública. */
public interface RegistrarConsentimientoUseCase {
    Consentimiento ejecutar(RegistrarConsentimientoCommand command);
}
