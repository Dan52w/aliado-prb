package com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Contratos HTTP de administración (SPEC-12). */
public final class AdminDtos {
    private AdminDtos() {}

    public record CategoriaRequest(@NotBlank String nombre) {}
    public record CategoriaUpdateRequest(String nombre, String estado) {}
    public record CategoriaResponse(UUID id, String nombre, String estado) {}

    public record ZonaRequest(@NotBlank String nombre, String coberturaGeografica) {}
    public record ZonaUpdateRequest(String nombre, String coberturaGeografica, String estado) {}
    public record ZonaResponse(UUID id, String nombre, String coberturaGeografica, String estado) {}

    public record ReporteResponse(UUID id, String tipo, UUID objetivoId, String motivo, String estado,
                                  OffsetDateTime fechaCreacion, OffsetDateTime detalleRevisadoEn) {}
    public record ResolverReporteRequest(@NotBlank String resultado, @NotBlank String justificacion) {}

    public record BloqueoRequest(@NotBlank String motivo) {}
    public record BloqueoResponse(UUID id, UUID usuarioId, String motivo, boolean activo) {}

    public record ModeracionRequest(@NotBlank String objetivoTipo, @NotNull UUID objetivoId,
                                    @NotBlank String accion, @NotBlank String motivo) {}
    public record AltoRiesgoRequest(@NotBlank String criterio) {}
    public record AltoRiesgoResponse(UUID id, UUID servicioId, String criterio, boolean activa) {}

    public record AuditoriaResponse(UUID id, String accion, String objetivoTipo, UUID objetivoId,
                                    String detalle, OffsetDateTime fecha, String traceId) {}

    public record ResolverVerificacionRequest(@NotBlank String estado) {}

    public record BusquedaResponse(java.util.List<UsuarioResumen> usuarios,
                                   java.util.List<SolicitudResumen> solicitudes) {}
    public record UsuarioResumen(UUID id, String email, String rol) {}
    public record SolicitudResumen(UUID id, String descripcion, String estado) {}
}
