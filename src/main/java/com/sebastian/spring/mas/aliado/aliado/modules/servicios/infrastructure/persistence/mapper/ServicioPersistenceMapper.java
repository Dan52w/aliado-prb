package com.sebastian.spring.mas.aliado.aliado.modules.servicios.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.EstadoServicio;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.HistorialEstadoServicio;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.MensajeServicio;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.ServicioContratado;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.infrastructure.persistence.entity.HistorialEstadoServicioEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.infrastructure.persistence.entity.MensajeServicioEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.infrastructure.persistence.entity.ServicioEntity;
import java.util.ArrayList;
import java.util.List;

public final class ServicioPersistenceMapper {
    private ServicioPersistenceMapper() {}

    public static ServicioContratado aDominio(ServicioEntity e) {
        return new ServicioContratado(e.getId(), e.getSolicitudId(), e.getPropuestaId(), e.getClienteId(),
                e.getPrestadorId(), EstadoServicio.valueOf(e.getEstado()), e.isComunicacionHabilitada(),
                e.getFechaContratacion(), e.getVersion(),
                e.getHistorial().stream().map(h -> new HistorialEstadoServicio(h.getId(), e.getId(),
                        h.getEstadoAnterior() == null ? null : EstadoServicio.valueOf(h.getEstadoAnterior()),
                        EstadoServicio.valueOf(h.getEstadoNuevo()), h.getActorId(), h.getFecha())).toList(),
                e.getMensajes().stream().map(m -> new MensajeServicio(m.getId(), e.getId(),
                        m.getRemitenteId(), m.getContenido(), m.getFechaEnvio())).toList());
    }

    public static ServicioEntity aEntidad(ServicioContratado s) {
        ServicioEntity e = new ServicioEntity();
        e.setId(s.id());
        e.setSolicitudId(s.solicitudId());
        e.setPropuestaId(s.propuestaId());
        e.setClienteId(s.clienteId());
        e.setPrestadorId(s.prestadorId());
        e.setEstado(s.estado().name());
        e.setComunicacionHabilitada(s.comunicacionHabilitada());
        e.setFechaContratacion(s.fechaContratacion());
        e.setVersion(s.version());
        List<HistorialEstadoServicioEntity> historial = s.historial().stream().map(h -> {
            HistorialEstadoServicioEntity he = new HistorialEstadoServicioEntity();
            he.setId(h.id());
            he.setServicio(e);
            he.setEstadoAnterior(h.estadoAnterior() == null ? null : h.estadoAnterior().name());
            he.setEstadoNuevo(h.estadoNuevo().name());
            he.setActorId(h.actorId());
            he.setFecha(h.fecha());
            return he;
        }).toList();
        e.setHistorial(new ArrayList<>(historial));
        List<MensajeServicioEntity> mensajes = s.mensajes().stream().map(m -> {
            MensajeServicioEntity me = new MensajeServicioEntity();
            me.setId(m.id());
            me.setServicio(e);
            me.setRemitenteId(m.remitenteId());
            me.setContenido(m.contenido());
            me.setFechaEnvio(m.fechaEnvio());
            return me;
        }).toList();
        e.setMensajes(new ArrayList<>(mensajes));
        return e;
    }
}
