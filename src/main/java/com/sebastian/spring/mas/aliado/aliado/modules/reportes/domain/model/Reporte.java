package com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Reporte (SPEC-11): exactamente un objetivo según su tipo (CHECK ck_reporte_un_objetivo). */
public record Reporte(UUID id, TipoReporte tipo, UUID reportanteId, UUID resenaId, UUID usuarioObjetivoId,
                      UUID servicioId, String motivo, EstadoReporte estado, OffsetDateTime detalleRevisadoEn,
                      String justificacionResolucion, UUID revisadoPor, OffsetDateTime fechaCreacion,
                      OffsetDateTime fechaResolucion, int version) {

    public boolean abierto() {
        return estado == EstadoReporte.PENDIENTE || estado == EstadoReporte.EN_REVISION;
    }

    public UUID objetivoId() {
        return switch (tipo) {
            case RESENA -> resenaId;
            case USUARIO -> usuarioObjetivoId;
            case SERVICIO -> servicioId;
        };
    }
}
