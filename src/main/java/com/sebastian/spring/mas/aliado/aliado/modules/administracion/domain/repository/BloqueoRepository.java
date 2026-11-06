package com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.BloqueoPreventivo;
import java.util.Optional;
import java.util.UUID;

public interface BloqueoRepository {
    Optional<BloqueoPreventivo> activoDeUsuario(UUID usuarioId);
    Optional<BloqueoPreventivo> porId(UUID id);
    BloqueoPreventivo guardar(BloqueoPreventivo bloqueo);
}
