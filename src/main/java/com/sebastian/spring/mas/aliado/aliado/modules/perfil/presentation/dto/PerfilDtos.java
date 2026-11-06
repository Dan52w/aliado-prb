package com.sebastian.spring.mas.aliado.aliado.modules.perfil.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Contratos HTTP de perfil y privacidad (SPEC-02). */
public final class PerfilDtos {
    private PerfilDtos() {}

    public record PerfilResponse(UUID usuarioId, String nombre, String telefono, String preferenciaVisibilidad,
                                 String zonaAproximada, String direccionExacta, int version) {}

    public record ActualizarPerfilRequest(String nombre,
                                          @Pattern(regexp = "[+0-9 ()-]{7,30}", message = "Teléfono inválido") String telefono,
                                          Integer version) {}

    public record UbicacionRequest(@NotBlank String zonaAproximada, String direccionExacta) {}

    public record VisibilidadRequest(@NotBlank String preferenciaVisibilidad) {}

    public record PerfilPublicoResponse(UUID usuarioId, String zonaAproximada) {}

    public record EliminacionResponse(String estado, OffsetDateTime expiraEn) {}
}
