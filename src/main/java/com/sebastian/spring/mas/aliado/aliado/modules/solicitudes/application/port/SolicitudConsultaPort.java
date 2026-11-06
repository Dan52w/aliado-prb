package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Solicitud;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Contrato público de solicitudes para otros módulos. */
public interface SolicitudConsultaPort {
    Optional<Solicitud> porId(UUID id);
    List<Solicitud> publicadas();
    void marcarContratada(UUID solicitudId);
    /** Búsqueda admin (SPEC-12, FR-075): por descripción o id. */
    List<Solicitud> buscarPorTexto(String texto);
}
