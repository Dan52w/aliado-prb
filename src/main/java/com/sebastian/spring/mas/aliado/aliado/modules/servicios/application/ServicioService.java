package com.sebastian.spring.mas.aliado.aliado.modules.servicios.application;

import com.sebastian.spring.mas.aliado.aliado.common.events.Eventos;
import com.sebastian.spring.mas.aliado.aliado.common.events.PublicadorEventos;
import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.application.port.PropuestaConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model.EstadoPropuesta;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model.Propuesta;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port.ServicioConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port.ServicioLecturaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.EstadoServicio;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.HistorialEstadoServicio;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.MensajeServicio;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.ServicioContratado;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.repository.ServicioRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.application.port.SolicitudConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.EstadoSolicitud;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contratación y ciclo de vida del servicio (SPEC-08, UC045–UC056).
 * La aceptación es una sola operación consistente: crea el servicio, acepta la propuesta,
 * cierra el resto, marca la solicitud CONTRATADA y habilita la comunicación.
 */
@Service
public class ServicioService implements ServicioConsultaPort, ServicioLecturaPort {
    private final ServicioRepository servicios;
    private final PropuestaConsultaPort propuestas;
    private final SolicitudConsultaPort solicitudes;
    private final PublicadorEventos eventos;

    public ServicioService(ServicioRepository servicios, PropuestaConsultaPort propuestas,
                           SolicitudConsultaPort solicitudes, PublicadorEventos eventos) {
        this.servicios = servicios;
        this.propuestas = propuestas;
        this.solicitudes = solicitudes;
        this.eventos = eventos;
    }

    @Transactional(readOnly = true)
    public List<Propuesta> propuestasDeSolicitud(UUID solicitudId, UUID clienteId) {
        var solicitud = solicitudes.porId(solicitudId)
                .orElseThrow(() -> new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA",
                        "Solicitud no encontrada."));
        if (!solicitud.clienteId().equals(clienteId)) {
            throw new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA", "Solicitud no encontrada.");
        }
        return propuestas.activasDeSolicitud(solicitudId);
    }

    @Transactional
    public ServicioContratado aceptarPropuesta(UUID propuestaId, UUID clienteId) {
        Propuesta propuesta = propuestas.porId(propuestaId)
                .orElseThrow(() -> new BusinessException.NotFound("PROPUESTA_NO_ENCONTRADA",
                        "Propuesta no encontrada."));
        var solicitud = solicitudes.porId(propuesta.solicitudId())
                .orElseThrow(() -> new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA",
                        "Solicitud no encontrada."));
        if (!solicitud.clienteId().equals(clienteId)) {
            throw new BusinessException.Forbidden("NO_AUTORIZADO",
                    "Solo el cliente dueño de la solicitud puede aceptar propuestas.");
        }
        if (propuesta.estado() != EstadoPropuesta.ACTIVA) {
            throw new BusinessException.Conflict("PROPUESTA_NO_ACTIVA", "La propuesta ya no está activa.");
        }
        if (solicitud.estado() != EstadoSolicitud.PUBLICADA) {
            throw new BusinessException.Conflict("SOLICITUD_NO_ABIERTA", "La solicitud ya no está abierta.");
        }
        if (servicios.porSolicitudId(solicitud.id()).isPresent()) {
            throw new BusinessException.Conflict("SERVICIO_YA_CONTRATADO",
                    "La solicitud ya tiene un servicio contratado.");
        }
        OffsetDateTime ahora = OffsetDateTime.now();
        ServicioContratado servicio = new ServicioContratado(UUID.randomUUID(), solicitud.id(), propuesta.id(),
                clienteId, propuesta.prestadorId(), EstadoServicio.CONTRATADO, true, ahora, 0,
                List.of(new HistorialEstadoServicio(UUID.randomUUID(), null, null, EstadoServicio.CONTRATADO,
                        clienteId, ahora)), List.of());
        // El historial inicial referencia el servicio recién creado.
        ServicioContratado conHistorial = new ServicioContratado(servicio.id(), solicitud.id(), propuesta.id(),
                clienteId, propuesta.prestadorId(), EstadoServicio.CONTRATADO, true, ahora, 0,
                List.of(new HistorialEstadoServicio(UUID.randomUUID(), servicio.id(), null,
                        EstadoServicio.CONTRATADO, clienteId, ahora)), List.of());
        ServicioContratado guardado = servicios.guardar(conHistorial);
        propuestas.marcarAceptada(propuesta.id());
        propuestas.cerrarActivasDeSolicitud(solicitud.id(), propuesta.id());
        solicitudes.marcarContratada(solicitud.id());
        eventos.publicar(new Eventos.PropuestaAceptada(guardado.id(), propuesta.id(), solicitud.id(),
                clienteId, propuesta.prestadorId(), ahora));
        return guardado;
    }

    @Transactional(readOnly = true)
    public ServicioContratado detalle(UUID servicioId, UUID usuarioId) {
        return participante(servicioId, usuarioId);
    }

    @Transactional
    public ServicioContratado enEjecucion(UUID servicioId, UUID prestadorId) {
        ServicioContratado servicio = participante(servicioId, prestadorId);
        if (!servicio.prestadorId().equals(prestadorId)) {
            throw new BusinessException.Forbidden("NO_AUTORIZADO", "Solo el prestador del servicio puede iniciarlo.");
        }
        return transitar(servicio, EstadoServicio.EN_EJECUCION, prestadorId);
    }

    @Transactional
    public ServicioContratado terminado(UUID servicioId, UUID prestadorId) {
        ServicioContratado servicio = participante(servicioId, prestadorId);
        if (!servicio.prestadorId().equals(prestadorId)) {
            throw new BusinessException.Forbidden("NO_AUTORIZADO", "Solo el prestador del servicio puede terminarlo.");
        }
        return transitar(servicio, EstadoServicio.TERMINADO, prestadorId);
    }

    @Transactional
    public ServicioContratado confirmarFinalizacion(UUID servicioId, UUID clienteId) {
        ServicioContratado servicio = participante(servicioId, clienteId);
        if (!servicio.clienteId().equals(clienteId)) {
            throw new BusinessException.Forbidden("NO_AUTORIZADO",
                    "Solo el cliente del servicio puede confirmar la finalización.");
        }
        return transitar(servicio, EstadoServicio.FINALIZADO, clienteId);
    }

    @Transactional
    public ServicioContratado cancelar(UUID servicioId, UUID usuarioId) {
        ServicioContratado servicio = participante(servicioId, usuarioId);
        return transitar(servicio, EstadoServicio.CANCELADO, usuarioId);
    }

    private ServicioContratado transitar(ServicioContratado servicio, EstadoServicio nuevo, UUID actorId) {
        OffsetDateTime ahora = OffsetDateTime.now();
        EstadoServicio anterior = servicio.estado();
        ServicioContratado actualizado = servicios.guardar(servicio.transitarA(nuevo, actorId, ahora));
        eventos.publicar(new Eventos.ServicioEstadoCambiado(actualizado.id(), actualizado.clienteId(),
                actualizado.prestadorId(), anterior.name(), nuevo.name(), actorId, ahora));
        return actualizado;
    }

    @Transactional(readOnly = true)
    public List<HistorialEstadoServicio> historial(UUID servicioId, UUID usuarioId) {
        return participante(servicioId, usuarioId).historial();
    }

    @Transactional
    public MensajeServicio enviarMensaje(UUID servicioId, UUID remitenteId, String contenido) {
        ServicioContratado servicio = participante(servicioId, remitenteId);
        if (!servicio.comunicacionHabilitada()) {
            throw new BusinessException.Conflict("COMUNICACION_NO_HABILITADA",
                    "La comunicación aún no está habilitada para este servicio.");
        }
        if (contenido == null || contenido.isBlank() || contenido.length() > 2000) {
            throw new BusinessException.BadRequest("MENSAJE_INVALIDO",
                    "El mensaje es obligatorio y admite máximo 2000 caracteres.");
        }
        MensajeServicio mensaje = new MensajeServicio(UUID.randomUUID(), servicioId, remitenteId,
                contenido.trim(), OffsetDateTime.now());
        var mensajes = new java.util.ArrayList<>(servicio.mensajes());
        mensajes.add(mensaje);
        servicios.guardar(new ServicioContratado(servicio.id(), servicio.solicitudId(), servicio.propuestaId(),
                servicio.clienteId(), servicio.prestadorId(), servicio.estado(),
                servicio.comunicacionHabilitada(), servicio.fechaContratacion(), servicio.version(),
                servicio.historial(), List.copyOf(mensajes)));
        return mensaje;
    }

    @Transactional(readOnly = true)
    public List<MensajeServicio> listarMensajes(UUID servicioId, UUID usuarioId) {
        ServicioContratado servicio = participante(servicioId, usuarioId);
        if (!servicio.comunicacionHabilitada()) {
            throw new BusinessException.Conflict("COMUNICACION_NO_HABILITADA",
                    "La comunicación aún no está habilitada para este servicio.");
        }
        return servicio.mensajes();
    }

    private ServicioContratado participante(UUID servicioId, UUID usuarioId) {
        ServicioContratado servicio = servicios.porId(servicioId)
                .orElseThrow(() -> new BusinessException.NotFound("SERVICIO_NO_ENCONTRADO",
                        "Servicio no encontrado."));
        if (!servicio.esParticipante(usuarioId)) {
            throw new BusinessException.NotFound("SERVICIO_NO_ENCONTRADO", "Servicio no encontrado.");
        }
        return servicio;
    }

    @Override
    public boolean tieneServicioActivo(UUID usuarioId) {
        return !servicios.activosDeUsuario(usuarioId).isEmpty();
    }

    @Override
    public Optional<ServicioContratado> porId(UUID id) {
        return servicios.porId(id);
    }

    @Override
    public List<ServicioContratado> porUsuario(UUID usuarioId) {
        return servicios.porUsuario(usuarioId);
    }

    @Override
    public boolean esParticipante(UUID servicioId, UUID usuarioId) {
        return servicios.porId(servicioId).map(s -> s.esParticipante(usuarioId)).orElse(false);
    }
}
