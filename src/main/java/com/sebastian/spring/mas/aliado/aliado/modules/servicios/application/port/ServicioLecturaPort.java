package com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.ServicioContratado;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Lectura de servicios para confianza, reportes e historial (SPEC-08/SPEC-10/SPEC-14). */
public interface ServicioLecturaPort {
    Optional<ServicioContratado> porId(UUID id);
    List<ServicioContratado> porUsuario(UUID usuarioId);
    List<ServicioContratado> porUsuario(UUID usuarioId, int pagina, int tamano);
    boolean esParticipante(UUID servicioId, UUID usuarioId);
}
