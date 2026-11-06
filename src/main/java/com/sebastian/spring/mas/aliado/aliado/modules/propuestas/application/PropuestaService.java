package com.sebastian.spring.mas.aliado.aliado.modules.propuestas.application;

import com.sebastian.spring.mas.aliado.aliado.common.events.Eventos;
import com.sebastian.spring.mas.aliado.aliado.common.events.PublicadorEventos;
import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.application.port.CompatibilidadPort;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.application.port.PropuestaConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model.EstadoPropuesta;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model.Propuesta;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.repository.PropuestaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.application.port.SolicitudConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.EstadoSolicitud;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creación y gestión de propuestas (SPEC-07, UC039–UC044).
 * Solo sobre solicitud abierta y compatible; disponibilidad obligatoria; mensaje opcional.
 * Creación + notificación PROPUESTA_RECIBIDA en una sola transacción (vía evento).
 */
@Service
public class PropuestaService implements PropuestaConsultaPort {
    private final PropuestaRepository propuestas;
    private final SolicitudConsultaPort solicitudes;
    private final CompatibilidadPort compatibilidad;
    private final PublicadorEventos eventos;

    public PropuestaService(PropuestaRepository propuestas, SolicitudConsultaPort solicitudes,
                            CompatibilidadPort compatibilidad, PublicadorEventos eventos) {
        this.propuestas = propuestas;
        this.solicitudes = solicitudes;
        this.compatibilidad = compatibilidad;
        this.eventos = eventos;
    }

    @Transactional
    public Propuesta crear(UUID prestadorId, UUID solicitudId, String disponibilidad, String mensaje) {
        if (disponibilidad == null || disponibilidad.isBlank()) {
            throw new BusinessException.BadRequest("DISPONIBILIDAD_REQUERIDA",
                    "La disponibilidad es obligatoria en toda propuesta.");
        }
        if (mensaje != null && mensaje.length() > 1000) {
            throw new BusinessException.BadRequest("MENSAJE_MUY_LARGO", "El mensaje admite máximo 1000 caracteres.");
        }
        var solicitud = solicitudes.porId(solicitudId)
                .orElseThrow(() -> new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA",
                        "Solicitud no encontrada."));
        if (solicitud.estado() != EstadoSolicitud.PUBLICADA) {
            throw new BusinessException.Conflict("SOLICITUD_NO_ABIERTA",
                    "La solicitud ya no admite nuevas propuestas.");
        }
        if (!compatibilidad.esCompatible(solicitudId, prestadorId)) {
            throw new BusinessException.Conflict("SOLICITUD_NO_COMPATIBLE",
                    "La solicitud no es compatible con tu perfil profesional.");
        }
        if (propuestas.activaDePrestadorEnSolicitud(solicitudId, prestadorId).isPresent()) {
            throw new BusinessException.Conflict("PROPUESTA_DUPLICADA",
                    "Ya tienes una propuesta activa para esta solicitud.");
        }
        OffsetDateTime ahora = OffsetDateTime.now();
        Propuesta propuesta = new Propuesta(UUID.randomUUID(), solicitudId, prestadorId,
                disponibilidad.trim(), mensaje, EstadoPropuesta.ACTIVA, ahora, ahora, 0);
        Propuesta guardada = propuestas.guardar(propuesta);
        eventos.publicar(new Eventos.PropuestaCreada(guardada.id(), solicitudId, prestadorId,
                solicitud.clienteId(), ahora));
        return guardada;
    }

    @Transactional
    public Propuesta editar(UUID propuestaId, UUID prestadorId, String disponibilidad, String mensaje) {
        Propuesta actual = propiaActiva(propuestaId, prestadorId);
        if (mensaje != null && mensaje.length() > 1000) {
            throw new BusinessException.BadRequest("MENSAJE_MUY_LARGO", "El mensaje admite máximo 1000 caracteres.");
        }
        Propuesta editada = new Propuesta(actual.id(), actual.solicitudId(), actual.prestadorId(),
                disponibilidad == null ? actual.disponibilidad() : disponibilidad.trim(),
                mensaje == null ? actual.mensaje() : mensaje, EstadoPropuesta.ACTIVA,
                actual.fechaCreacion(), OffsetDateTime.now(), actual.version());
        return propuestas.guardar(editada);
    }

    @Transactional
    public Propuesta retirar(UUID propuestaId, UUID prestadorId) {
        Propuesta actual = propiaActiva(propuestaId, prestadorId);
        return propuestas.guardar(actual.retirar(OffsetDateTime.now()));
    }

    private Propuesta propiaActiva(UUID propuestaId, UUID prestadorId) {
        Propuesta propuesta = propuestas.porId(propuestaId)
                .orElseThrow(() -> new BusinessException.NotFound("PROPUESTA_NO_ENCONTRADA",
                        "Propuesta no encontrada."));
        if (!propuesta.prestadorId().equals(prestadorId)) {
            throw new BusinessException.NotFound("PROPUESTA_NO_ENCONTRADA", "Propuesta no encontrada.");
        }
        if (propuesta.estado() != EstadoPropuesta.ACTIVA) {
            throw new BusinessException.Conflict("PROPUESTA_NO_ACTIVA",
                    "La propuesta ya no está activa (estado " + propuesta.estado() + ").");
        }
        return propuesta;
    }

    @Override
    public Optional<Propuesta> porId(UUID id) {
        return propuestas.porId(id);
    }

    @Override
    public List<Propuesta> activasDeSolicitud(UUID solicitudId) {
        return propuestas.activasDeSolicitud(solicitudId);
    }

    @Override
    @Transactional
    public void marcarAceptada(UUID propuestaId) {
        Propuesta propuesta = propuestas.porId(propuestaId)
                .orElseThrow(() -> new BusinessException.NotFound("PROPUESTA_NO_ENCONTRADA",
                        "Propuesta no encontrada."));
        propuestas.guardar(propuesta.aceptar(OffsetDateTime.now()));
    }

    @Override
    @Transactional
    public void cerrarActivasDeSolicitud(UUID solicitudId, UUID exceptoPropuestaId) {
        OffsetDateTime ahora = OffsetDateTime.now();
        for (Propuesta propuesta : propuestas.activasDeSolicitud(solicitudId)) {
            if (!propuesta.id().equals(exceptoPropuestaId)) {
                propuestas.guardar(propuesta.cerrar(ahora));
            }
        }
    }
}
