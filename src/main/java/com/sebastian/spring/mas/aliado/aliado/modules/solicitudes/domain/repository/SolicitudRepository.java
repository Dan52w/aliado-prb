package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Solicitud;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitudRepository {
    Optional<Solicitud> porId(UUID id);
    List<Solicitud> porCliente(UUID clienteId);
    List<Solicitud> publicadas();
    Solicitud guardar(Solicitud solicitud);
    /** Búsqueda admin (SPEC-12, FR-075): por descripción (contiene) o id exacto. */
    List<Solicitud> buscarPorTexto(String texto);
}
