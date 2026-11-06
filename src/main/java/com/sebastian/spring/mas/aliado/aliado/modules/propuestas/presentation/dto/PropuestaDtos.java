package com.sebastian.spring.mas.aliado.aliado.modules.propuestas.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Contratos HTTP de propuestas (SPEC-07). */
public final class PropuestaDtos {
    private PropuestaDtos() {}

    public record CrearRequest(@NotNull UUID solicitudId, @NotBlank String disponibilidad, String mensaje) {}

    public record EditarRequest(String disponibilidad, String mensaje) {}

    public record PropuestaResponse(UUID id, UUID solicitudId, UUID prestadorId, String disponibilidad,
                                    String mensaje, String estado) {}

    public record IdEstadoResponse(UUID id, String estado) {}
}
