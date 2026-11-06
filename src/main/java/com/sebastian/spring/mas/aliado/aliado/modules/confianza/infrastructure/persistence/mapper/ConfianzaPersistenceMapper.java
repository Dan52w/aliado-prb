package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Calificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.DocumentoVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.EstadoVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.EstadoVisibilidadResena;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Reputacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Resena;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.SolicitudVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity.CalificacionEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity.DocumentoVerificacionEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity.ReputacionEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity.ResenaEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity.SolicitudVerificacionEntity;

public final class ConfianzaPersistenceMapper {
    private ConfianzaPersistenceMapper() {}

    public static Calificacion aDominio(CalificacionEntity e) {
        return new Calificacion(e.getId(), e.getServicioId(), e.getClienteId(), e.getPrestadorId(),
                e.getValor(), e.getFecha());
    }

    public static CalificacionEntity aEntidad(Calificacion c) {
        CalificacionEntity e = new CalificacionEntity();
        e.setId(c.id());
        e.setServicioId(c.servicioId());
        e.setClienteId(c.clienteId());
        e.setPrestadorId(c.prestadorId());
        e.setValor((short) c.valor());
        e.setFecha(c.fecha());
        return e;
    }

    public static Resena aDominio(ResenaEntity e) {
        return new Resena(e.getId(), e.getCalificacionId(), e.getTexto(), e.getFechaCreacion(),
                EstadoVisibilidadResena.valueOf(e.getEstadoVisibilidad()));
    }

    public static ResenaEntity aEntidad(Resena r) {
        ResenaEntity e = new ResenaEntity();
        e.setId(r.id());
        e.setCalificacionId(r.calificacionId());
        e.setTexto(r.texto());
        e.setFechaCreacion(r.fechaCreacion());
        e.setEstadoVisibilidad(r.estadoVisibilidad().name());
        return e;
    }

    public static Reputacion aDominio(ReputacionEntity e) {
        return new Reputacion(e.getPrestadorId(), e.getValorAgregado(), e.getCantidadCalificaciones(),
                e.getFechaCalculo());
    }

    public static ReputacionEntity aEntidad(Reputacion r) {
        ReputacionEntity e = new ReputacionEntity();
        e.setPrestadorId(r.prestadorId());
        e.setValorAgregado(r.valorAgregado());
        e.setCantidadCalificaciones(r.cantidadCalificaciones());
        e.setFechaCalculo(r.fechaCalculo());
        return e;
    }

    public static SolicitudVerificacion aDominio(SolicitudVerificacionEntity e) {
        return new SolicitudVerificacion(e.getId(), e.getPrestadorId(), EstadoVerificacion.valueOf(e.getEstado()),
                e.getFechaSolicitud(), e.getFechaRevision());
    }

    public static SolicitudVerificacionEntity aEntidad(SolicitudVerificacion s) {
        SolicitudVerificacionEntity e = new SolicitudVerificacionEntity();
        e.setId(s.id());
        e.setPrestadorId(s.prestadorId());
        e.setEstado(s.estado().name());
        e.setFechaSolicitud(s.fechaSolicitud());
        e.setFechaRevision(s.fechaRevision());
        return e;
    }

    public static DocumentoVerificacion aDominio(DocumentoVerificacionEntity e) {
        return new DocumentoVerificacion(e.getId(), e.getSolicitudVerificacionId(), e.getArchivoKey(),
                e.getFechaCarga());
    }

    public static DocumentoVerificacionEntity aEntidad(DocumentoVerificacion d) {
        DocumentoVerificacionEntity e = new DocumentoVerificacionEntity();
        e.setId(d.id());
        e.setSolicitudVerificacionId(d.solicitudVerificacionId());
        e.setArchivoKey(d.archivoKey());
        e.setFechaCarga(d.fechaCarga());
        return e;
    }
}
