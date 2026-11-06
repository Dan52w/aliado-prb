package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Contratos HTTP de solicitudes (SPEC-04). */
public final class SolicitudDtos {
    private SolicitudDtos() {}

    public record CrearRequest(
            @NotNull UUID categoriaId,
            @NotBlank String descripcion,
            @NotNull UUID zonaId,
            String urgencia,
            String estadoDeseado) {}

    public record EditarRequest(UUID categoriaId, String descripcion, UUID zonaId, String urgencia) {}

    public record SolicitudResponse(UUID id, UUID clienteId, UUID categoriaId, UUID zonaId, String descripcion,
                                    String urgencia, String estado, OffsetDateTime fechaCreacion,
                                    OffsetDateTime fechaActualizacion, List<String> fotos) {}

    public record FotoResponse(UUID id, String archivoKey, int orden) {}

    public record IdEstadoResponse(UUID id, String estado) {}
}
