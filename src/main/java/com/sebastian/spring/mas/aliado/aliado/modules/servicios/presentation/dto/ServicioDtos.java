package com.sebastian.spring.mas.aliado.aliado.modules.servicios.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Contratos HTTP de contratación y servicio (SPEC-08). */
public final class ServicioDtos {
    private ServicioDtos() {}

    public record ServicioResponse(UUID id, UUID solicitudId, UUID propuestaId, UUID clienteId, UUID prestadorId,
                                   String estado, boolean comunicacionHabilitada, OffsetDateTime fechaContratacion) {}

    public record AceptarResponse(UUID servicioId, String estado) {}

    public record HistorialResponse(String estadoAnterior, String estadoNuevo,
                                    UUID actorId, OffsetDateTime fecha) {}

    public record MensajeRequest(@NotBlank String contenido) {}

    public record MensajeResponse(UUID id, UUID remitenteId, String contenido, OffsetDateTime fechaEnvio) {}

    public record IdEstadoResponse(UUID id, String estado) {}
}
