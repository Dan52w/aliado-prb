package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.service;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto.RegistrarConsentimientoCommand;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.usecase.RegistrarConsentimientoUseCase;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.exception.TerminosNoAceptadosException;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Consentimiento;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository.ConsentimientoRepository;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Aplica la regla de consentimiento obligatorio y lo persiste mediante el puerto de dominio. */
public class RegistrarConsentimientoService implements RegistrarConsentimientoUseCase {
    private final ConsentimientoRepository consentimientoRepository;

    public RegistrarConsentimientoService(ConsentimientoRepository consentimientoRepository) {
        this.consentimientoRepository = consentimientoRepository;
    }

    @Override
    public Consentimiento ejecutar(RegistrarConsentimientoCommand command) {
        if (!command.aceptaTerminos() || command.versionTerminos() == null || command.versionTerminos().isBlank()) {
            throw new TerminosNoAceptadosException();
        }
        Consentimiento consentimiento = new Consentimiento(UUID.randomUUID(), command.usuarioId(),
                command.versionTerminos(), OffsetDateTime.now());
        return consentimientoRepository.guardar(consentimiento);
    }
}
