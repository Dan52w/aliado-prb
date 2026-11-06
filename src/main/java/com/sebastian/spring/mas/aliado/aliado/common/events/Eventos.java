package com.sebastian.spring.mas.aliado.aliado.common.events;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Eventos entre módulos. Ningún módulo MVP depende de otro de forma obligatoria. */
public final class Eventos {
    private Eventos() {}

    public record SolicitudPublicada(UUID solicitudId, UUID clienteId, UUID categoriaId, UUID zonaId,
                                     String urgencia, OffsetDateTime fecha) {}
    public record PropuestaCreada(UUID propuestaId, UUID solicitudId, UUID prestadorId, UUID clienteId,
                                  OffsetDateTime fecha) {}
    public record PropuestaAceptada(UUID servicioId, UUID propuestaId, UUID solicitudId, UUID clienteId,
                                    UUID prestadorId, OffsetDateTime fecha) {}
    public record ServicioEstadoCambiado(UUID servicioId, UUID clienteId, UUID prestadorId,
                                         String estadoAnterior, String estadoNuevo, UUID actorId,
                                         OffsetDateTime fecha, int version) {}
    public record EliminacionConfirmada(UUID usuarioId, OffsetDateTime fecha) {}
}
