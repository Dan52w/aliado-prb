package com.sebastian.spring.mas.aliado.aliado.modules.propuestas.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model.Propuesta;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Contrato público de propuestas para el módulo de servicios (SPEC-08). */
public interface PropuestaConsultaPort {
    Optional<Propuesta> porId(UUID id);
    List<Propuesta> activasDeSolicitud(UUID solicitudId);
    void marcarAceptada(UUID propuestaId);
    void cerrarActivasDeSolicitud(UUID solicitudId, UUID exceptoPropuestaId);
}
