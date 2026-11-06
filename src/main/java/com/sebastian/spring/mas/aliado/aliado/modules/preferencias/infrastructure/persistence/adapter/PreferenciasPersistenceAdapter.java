package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.PreferenciaNotificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.repository.PreferenciaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.jpa.PreferenciaJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.mapper.PreferenciasPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PreferenciasPersistenceAdapter implements PreferenciaRepository {
    private final PreferenciaJpaRepository preferencias;

    public PreferenciasPersistenceAdapter(PreferenciaJpaRepository preferencias) {
        this.preferencias = preferencias;
    }

    @Override
    public List<PreferenciaNotificacion> porUsuario(UUID usuarioId) {
        return preferencias.findByIdUsuarioId(usuarioId).stream()
                .map(PreferenciasPersistenceMapper::aDominio).toList();
    }

    @Override
    public Optional<PreferenciaNotificacion> porUsuarioTipoCanal(UUID usuarioId, String tipoAviso, String canal) {
        return preferencias.findById(new com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity.PreferenciaId(
                usuarioId, tipoAviso, canal)).map(PreferenciasPersistenceMapper::aDominio);
    }

    @Override
    public PreferenciaNotificacion guardar(PreferenciaNotificacion preferencia) {
        return PreferenciasPersistenceMapper.aDominio(
                preferencias.save(PreferenciasPersistenceMapper.aEntidad(preferencia)));
    }
}
