package com.sebastian.spring.mas.aliado.aliado.modules.confianza.application;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.ResenaConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.ResenaRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consulta de existencia de reseñas (SPEC-11).
 * Servicio dedicado y sin dependencias de otros servicios: rompería el ciclo
 * ConfianzaService → ReporteService → ResenaConsultaPort si lo implementara ConfianzaService.
 */
@Service
public class ResenaConsultaService implements ResenaConsultaPort {
    private final ResenaRepository resenas;

    public ResenaConsultaService(ResenaRepository resenas) {
        this.resenas = resenas;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existe(UUID resenaId) {
        return resenas.porId(resenaId).isPresent();
    }
}
