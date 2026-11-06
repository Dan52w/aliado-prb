package com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application;

import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port.CanalEntregaNotificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port.NotificacionPort;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.Canal;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.EstadoEntrega;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.Notificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.model.TipoNotificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.domain.repository.NotificacionRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notificaciones (SPEC-09, UC057–UC058; SPEC-06/07 como productores).
 * Registro dentro de la transacción de negocio; entrega asíncrona posterior con reintentos.
 * Sin endpoint de creación: una notificación nunca se genera sin un evento real (SC-004).
 */
@Service
public class NotificacionService implements NotificacionPort {
    private final NotificacionRepository notificaciones;
    private final CanalEntregaNotificacion canal;

    public NotificacionService(NotificacionRepository notificaciones, CanalEntregaNotificacion canal) {
        this.notificaciones = notificaciones;
        this.canal = canal;
    }

    /** DTO de lectura (SPEC-09): servicioId se proyecta desde aggregateId cuando aplica. */
    public record NotificacionDto(UUID id, String tipo, UUID servicioId, String detalle,
                                  String estadoEntrega, OffsetDateTime fechaGeneracion) {}

    @Override
    @Transactional
    public void registrar(String tipo, UUID aggregateId, UUID destinatarioId, int eventVersion,
                          String detalle, String canalNombre) {
        TipoNotificacion tipoEnum = TipoNotificacion.valueOf(tipo);
        if (notificaciones.existeEvento(tipo, aggregateId, destinatarioId, eventVersion)) {
            return; // Idempotencia por evento (plan general 19.3): no duplicar avisos.
        }
        Canal canalEnum = canalNombre == null ? Canal.PUSH : Canal.valueOf(canalNombre);
        Notificacion notificacion = new Notificacion(UUID.randomUUID(), destinatarioId, tipoEnum, aggregateId,
                eventVersion, detalle, canalEnum, EstadoEntrega.PENDIENTE, 0, OffsetDateTime.now(), null);
        notificaciones.guardar(notificacion);
    }

    @Transactional(readOnly = true)
    public List<NotificacionDto> listarPropias(UUID destinatarioId) {
        return notificaciones.porDestinatario(destinatarioId).stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificacionDto> listarPorTipo(UUID destinatarioId, String tipo) {
        return notificaciones.porDestinatarioYTipo(destinatarioId, tipo).stream().map(this::aDto).toList();
    }

    private NotificacionDto aDto(Notificacion n) {
        UUID servicioId = (n.tipo() == TipoNotificacion.ACEPTACION_PROPUESTA
                || n.tipo() == TipoNotificacion.CAMBIO_ESTADO_SERVICIO) ? n.aggregateId() : null;
        return new NotificacionDto(n.id(), n.tipo().name(), servicioId, n.detalle(),
                n.estadoEntrega().name(), n.fechaGeneracion());
    }

    /** Worker de entrega: reintenta PENDIENTE/FALLIDA; a los 3 intentos queda FALLIDA. */
    @Transactional
    public void entregarPendientes() {
        for (Notificacion pendiente : notificaciones.pendientesParaEntrega()) {
            boolean entregada = false;
            try {
                entregada = canal.entregar(pendiente);
            } catch (Exception ignored) {
                entregada = false;
            }
            OffsetDateTime ahora = OffsetDateTime.now();
            if (entregada) {
                notificaciones.guardar(new Notificacion(pendiente.id(), pendiente.destinatarioId(),
                        pendiente.tipo(), pendiente.aggregateId(), pendiente.eventVersion(), pendiente.detalle(),
                        pendiente.canal(), EstadoEntrega.ENTREGADA, pendiente.intentos() + 1,
                        pendiente.fechaGeneracion(), ahora));
            } else {
                int intentos = pendiente.intentos() + 1;
                notificaciones.guardar(new Notificacion(pendiente.id(), pendiente.destinatarioId(),
                        pendiente.tipo(), pendiente.aggregateId(), pendiente.eventVersion(), pendiente.detalle(),
                        pendiente.canal(), intentos >= 3 ? EstadoEntrega.FALLIDA : EstadoEntrega.PENDIENTE,
                        intentos, pendiente.fechaGeneracion(), null));
            }
        }
    }
}
