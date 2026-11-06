package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.infrastructure.adapter;

import com.sebastian.spring.mas.aliado.aliado.common.events.Eventos;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port.NotificacionPort;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.application.port.CompatibilidadPort;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Enganches de notificación (SPEC-09): cada evento de negocio registra su aviso
 * dentro de la misma transacción. La entrega externa la hace el worker, nunca aquí.
 */
@Component
public class NotificacionEventListener {
    private final NotificacionPort notificaciones;
    private final CompatibilidadPort compatibilidad;

    public NotificacionEventListener(NotificacionPort notificaciones, CompatibilidadPort compatibilidad) {
        this.notificaciones = notificaciones;
        this.compatibilidad = compatibilidad;
    }

    @EventListener
    public void alPublicarSolicitud(Eventos.SolicitudPublicada evento) {
        for (var prestadorId : compatibilidad.prestadoresCompatibles(evento.solicitudId())) {
            notificaciones.registrar("NUEVA_OPORTUNIDAD", evento.solicitudId(), prestadorId, 1,
                    "Nueva oportunidad compatible con tu perfil.", "PUSH");
        }
    }

    @EventListener
    public void alCrearPropuesta(Eventos.PropuestaCreada evento) {
        notificaciones.registrar("PROPUESTA_RECIBIDA", evento.propuestaId(), evento.clienteId(), 1,
                "Recibiste una nueva propuesta para tu solicitud.", "PUSH");
    }

    @EventListener
    public void alAceptarPropuesta(Eventos.PropuestaAceptada evento) {
        notificaciones.registrar("ACEPTACION_PROPUESTA", evento.servicioId(), evento.prestadorId(), 1,
                "Tu propuesta fue aceptada. El servicio está contratado.", "PUSH");
    }

    @EventListener
    public void alCambiarEstadoServicio(Eventos.ServicioEstadoCambiado evento) {
        // La contraparte es quien no ejecutó la transición.
        var destinatario = evento.actorId().equals(evento.clienteId())
                ? evento.prestadorId() : evento.clienteId();
        notificaciones.registrar("CAMBIO_ESTADO_SERVICIO", evento.servicioId(), destinatario, 1,
                "El servicio cambió de estado a " + evento.estadoNuevo() + ".", "PUSH");
    }
}
