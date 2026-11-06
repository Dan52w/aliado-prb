package com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.SolicitudEliminacion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SolicitudEliminacionRepository {
    Optional<SolicitudEliminacion> pendienteDeUsuario(UUID usuarioId);
    List<SolicitudEliminacion> pendientesVencidas(java.time.OffsetDateTime ahora);
    SolicitudEliminacion guardar(SolicitudEliminacion solicitud);
}
