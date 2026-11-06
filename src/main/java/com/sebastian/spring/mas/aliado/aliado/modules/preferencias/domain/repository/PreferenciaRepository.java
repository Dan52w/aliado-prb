package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.PreferenciaNotificacion;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PreferenciaRepository {
    List<PreferenciaNotificacion> porUsuario(UUID usuarioId);
    Optional<PreferenciaNotificacion> porUsuarioTipoCanal(UUID usuarioId, String tipoAviso, String canal);
    PreferenciaNotificacion guardar(PreferenciaNotificacion preferencia);
}
