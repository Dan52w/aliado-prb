package com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.EstadoEliminacion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Perfil;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.SolicitudEliminacion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Ubicacion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Visibilidad;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.entity.PerfilEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.entity.SolicitudEliminacionEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.entity.UbicacionEntity;

/** Conversión explícita dominio <-> persistencia. La dirección exacta viaja cifrada; el descifrado es del servicio. */
public final class PerfilPersistenceMapper {
    private PerfilPersistenceMapper() {}

    public static Perfil aDominio(PerfilEntity e) {
        return new Perfil(e.getUsuarioId(), e.getNombre(), e.getTelefono(),
                Visibilidad.valueOf(e.getPreferenciaVisibilidad()), e.getVersion());
    }

    public static PerfilEntity aEntidad(Perfil p) {
        PerfilEntity e = new PerfilEntity();
        e.setUsuarioId(p.usuarioId());
        e.setNombre(p.nombre());
        e.setTelefono(p.telefono());
        e.setPreferenciaVisibilidad(p.preferenciaVisibilidad().name());
        e.setVersion(p.version());
        return e;
    }

    public static Ubicacion aDominio(UbicacionEntity e, String direccionPlana) {
        return new Ubicacion(e.getUsuarioId(), e.getZonaAproximada(), direccionPlana, e.getVersion());
    }

    public static UbicacionEntity aEntidad(Ubicacion u, String direccionCifrada) {
        UbicacionEntity e = new UbicacionEntity();
        e.setUsuarioId(u.usuarioId());
        e.setZonaAproximada(u.zonaAproximada());
        e.setDireccionExactaCifrada(direccionCifrada);
        e.setVersion(u.version());
        return e;
    }

    public static SolicitudEliminacion aDominio(SolicitudEliminacionEntity e) {
        return new SolicitudEliminacion(e.getId(), e.getUsuarioId(), e.getFechaSolicitud(),
                e.getFechaExpiracion(), EstadoEliminacion.valueOf(e.getEstado()), e.getFechaConfirmacion());
    }

    public static SolicitudEliminacionEntity aEntidad(SolicitudEliminacion s) {
        SolicitudEliminacionEntity e = new SolicitudEliminacionEntity();
        e.setId(s.id());
        e.setUsuarioId(s.usuarioId());
        e.setFechaSolicitud(s.fechaSolicitud());
        e.setFechaExpiracion(s.fechaExpiracion());
        e.setEstado(s.estado().name());
        e.setFechaConfirmacion(s.fechaConfirmacion());
        return e;
    }
}
