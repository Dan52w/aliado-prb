package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.AccionModeracion;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.Auditoria;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.BloqueoPreventivo;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.MarcaAltoRiesgo;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity.AccionModeracionEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity.AuditoriaEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity.BloqueoEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity.MarcaRiesgoEntity;

public final class AdministracionPersistenceMapper {
    private AdministracionPersistenceMapper() {}

    public static BloqueoPreventivo aDominio(BloqueoEntity e) {
        return new BloqueoPreventivo(e.getId(), e.getUsuarioId(), e.getAdministradorId(), e.getMotivo(),
                e.getFechaInicio(), e.getFechaFin(), e.isActivo());
    }

    public static BloqueoEntity aEntidad(BloqueoPreventivo b) {
        BloqueoEntity e = new BloqueoEntity();
        e.setId(b.id());
        e.setUsuarioId(b.usuarioId());
        e.setAdministradorId(b.administradorId());
        e.setMotivo(b.motivo());
        e.setFechaInicio(b.fechaInicio());
        e.setFechaFin(b.fechaFin());
        e.setActivo(b.activo());
        return e;
    }

    public static AccionModeracion aDominio(AccionModeracionEntity e) {
        return new AccionModeracion(e.getId(), e.getAdministradorId(), e.getTipo(), e.getObjetivoTipo(),
                e.getObjetivoId(), e.getMotivo(), e.getFecha());
    }

    public static AccionModeracionEntity aEntidad(AccionModeracion a) {
        AccionModeracionEntity e = new AccionModeracionEntity();
        e.setId(a.id());
        e.setAdministradorId(a.administradorId());
        e.setTipo(a.tipo());
        e.setObjetivoTipo(a.objetivoTipo());
        e.setObjetivoId(a.objetivoId());
        e.setMotivo(a.motivo());
        e.setFecha(a.fecha());
        return e;
    }

    public static MarcaAltoRiesgo aDominio(MarcaRiesgoEntity e) {
        return new MarcaAltoRiesgo(e.getId(), e.getServicioId(), e.getCriterio(), e.getAdministradorId(),
                e.getFecha(), e.isActiva());
    }

    public static MarcaRiesgoEntity aEntidad(MarcaAltoRiesgo m) {
        MarcaRiesgoEntity e = new MarcaRiesgoEntity();
        e.setId(m.id());
        e.setServicioId(m.servicioId());
        e.setCriterio(m.criterio());
        e.setAdministradorId(m.administradorId());
        e.setFecha(m.fecha());
        e.setActiva(m.activa());
        return e;
    }

    public static Auditoria aDominio(AuditoriaEntity e) {
        return new Auditoria(e.getId(), e.getAdministradorId(), e.getAccion(), e.getObjetivoTipo(),
                e.getObjetivoId(), e.getDetalle(), e.getFecha(), e.getTraceId());
    }

    public static AuditoriaEntity aEntidad(Auditoria a) {
        AuditoriaEntity e = new AuditoriaEntity();
        e.setId(a.id());
        e.setAdministradorId(a.administradorId());
        e.setAccion(a.accion());
        e.setObjetivoTipo(a.objetivoTipo());
        e.setObjetivoId(a.objetivoId());
        e.setDetalle(a.detalle());
        e.setFecha(a.fecha());
        e.setTraceId(a.traceId());
        return e;
    }
}
