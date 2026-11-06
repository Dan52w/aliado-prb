package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.DescargaHistorial;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.PreferenciaNotificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.TipoAviso;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity.DescargaEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity.PreferenciaEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity.PreferenciaId;

public final class PreferenciasPersistenceMapper {
    private PreferenciasPersistenceMapper() {}

    public static PreferenciaNotificacion aDominio(PreferenciaEntity e) {
        return new PreferenciaNotificacion(e.getId().getUsuarioId(),
                TipoAviso.valueOf(e.getId().getTipoAviso()), e.getId().getCanal(),
                e.isHabilitada(), e.isConfigurable());
    }

    public static PreferenciaEntity aEntidad(PreferenciaNotificacion p) {
        PreferenciaEntity e = new PreferenciaEntity();
        e.setId(new PreferenciaId(p.usuarioId(), p.tipoAviso().name(), p.canal()));
        e.setHabilitada(p.habilitada());
        e.setConfigurable(p.configurable());
        return e;
    }

    public static DescargaHistorial aDominio(DescargaEntity e) {
        return new DescargaHistorial(e.getId(), e.getUsuarioId(), e.getFechaGeneracion(),
                e.getArchivoKey(), e.getEstado());
    }

    public static DescargaEntity aEntidad(DescargaHistorial d) {
        DescargaEntity e = new DescargaEntity();
        e.setId(d.id());
        e.setUsuarioId(d.usuarioId());
        e.setFechaGeneracion(d.fechaGeneracion());
        e.setArchivoKey(d.archivoKey());
        e.setEstado(d.estado());
        return e;
    }
}
