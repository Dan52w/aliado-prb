package com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import java.time.OffsetDateTime;
import java.util.UUID;

/** Propuesta de un Prestador sobre una solicitud (SPEC-07). Sin precio ni moneda (F07-QA-03). */
public record Propuesta(UUID id, UUID solicitudId, UUID prestadorId, String disponibilidad, String mensaje,
                        EstadoPropuesta estado, OffsetDateTime fechaCreacion, OffsetDateTime fechaActualizacion,
                        int version) {

    public Propuesta retirar(OffsetDateTime ahora) {
        exigirActiva();
        return new Propuesta(id, solicitudId, prestadorId, disponibilidad, mensaje,
                EstadoPropuesta.RETIRADA, fechaCreacion, ahora, version);
    }

    public Propuesta aceptar(OffsetDateTime ahora) {
        exigirActiva();
        return new Propuesta(id, solicitudId, prestadorId, disponibilidad, mensaje,
                EstadoPropuesta.ACEPTADA, fechaCreacion, ahora, version);
    }

    public Propuesta cerrar(OffsetDateTime ahora) {
        exigirActiva();
        return new Propuesta(id, solicitudId, prestadorId, disponibilidad, mensaje,
                EstadoPropuesta.CERRADA, fechaCreacion, ahora, version);
    }

    private void exigirActiva() {
        if (estado != EstadoPropuesta.ACTIVA) {
            throw new BusinessException.Conflict("PROPUESTA_NO_ACTIVA",
                    "La propuesta ya no está activa (estado " + estado + ").");
        }
    }
}
