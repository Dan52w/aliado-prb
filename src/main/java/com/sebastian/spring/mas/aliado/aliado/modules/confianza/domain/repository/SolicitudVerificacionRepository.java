package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.SolicitudVerificacion;
import java.util.Optional;
import java.util.UUID;

public interface SolicitudVerificacionRepository {
    Optional<SolicitudVerificacion> porId(UUID id);
    Optional<SolicitudVerificacion> pendienteDePrestador(UUID prestadorId);
    Optional<SolicitudVerificacion> ultimaDePrestador(UUID prestadorId);
    long contarAprobadasDePrestador(UUID prestadorId);
    SolicitudVerificacion guardar(SolicitudVerificacion solicitud);
}
