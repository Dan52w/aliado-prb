package com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Ubicacion;
import java.util.Optional;
import java.util.UUID;

public interface UbicacionRepository {
    Optional<Ubicacion> porUsuarioId(UUID usuarioId);
    Ubicacion guardar(Ubicacion ubicacion);
}
