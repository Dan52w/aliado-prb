package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.EstadoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.FotoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.HistorialEstadoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Solicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Urgencia;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.entity.FotoSolicitudEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.entity.HistorialEstadoSolicitudEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.entity.SolicitudEntity;
import java.util.List;

public final class SolicitudPersistenceMapper {
    private SolicitudPersistenceMapper() {}

    public static Solicitud aDominio(SolicitudEntity e) {
        List<FotoSolicitud> fotos = e.getFotos().stream()
                .map(f -> new FotoSolicitud(f.getId(), e.getId(), f.getArchivoKey(), f.getOrden())).toList();
        List<HistorialEstadoSolicitud> historial = e.getHistorial().stream()
                .map(h -> new HistorialEstadoSolicitud(h.getId(), e.getId(),
                        h.getEstadoAnterior() == null ? null : EstadoSolicitud.valueOf(h.getEstadoAnterior()),
                        EstadoSolicitud.valueOf(h.getEstadoNuevo()), h.getFecha())).toList();
        return new Solicitud(e.getId(), e.getClienteId(), e.getCategoriaId(), e.getZonaId(), e.getDescripcion(),
                e.getUrgencia() == null ? null : Urgencia.valueOf(e.getUrgencia()),
                EstadoSolicitud.valueOf(e.getEstado()), e.getFechaCreacion(), e.getFechaActualizacion(),
                e.getVersion(), fotos, historial);
    }

    public static SolicitudEntity aEntidad(Solicitud s) {
        SolicitudEntity e = new SolicitudEntity();
        e.setId(s.id());
        e.setClienteId(s.clienteId());
        e.setCategoriaId(s.categoriaId());
        e.setZonaId(s.zonaId());
        e.setDescripcion(s.descripcion());
        e.setUrgencia(s.urgencia() == null ? null : s.urgencia().name());
        e.setEstado(s.estado().name());
        e.setFechaCreacion(s.fechaCreacion());
        e.setFechaActualizacion(s.fechaActualizacion());
        e.setVersion(s.version());
        List<FotoSolicitudEntity> fotos = s.fotos().stream().map(f -> {
            FotoSolicitudEntity fe = new FotoSolicitudEntity();
            fe.setId(f.id());
            fe.setSolicitud(e);
            fe.setArchivoKey(f.archivoKey());
            fe.setOrden(f.orden());
            return fe;
        }).toList();
        e.setFotos(new java.util.ArrayList<>(fotos));
        List<HistorialEstadoSolicitudEntity> historial = s.historial().stream().map(h -> {
            HistorialEstadoSolicitudEntity he = new HistorialEstadoSolicitudEntity();
            he.setId(h.id());
            he.setSolicitud(e);
            he.setEstadoAnterior(h.estadoAnterior() == null ? null : h.estadoAnterior().name());
            he.setEstadoNuevo(h.estadoNuevo().name());
            he.setFecha(h.fecha());
            return he;
        }).toList();
        e.setHistorial(new java.util.ArrayList<>(historial));
        return e;
    }
}
