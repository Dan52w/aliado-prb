package com.sebastian.spring.mas.aliado.aliado.domain;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.EstadoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Solicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Urgencia;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Máquina de estados de Solicitud (SPEC-04). */
class SolicitudEstadoTest {

    private Solicitud solicitud(EstadoSolicitud estado) {
        return new Solicitud(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Reparar grifo", Urgencia.MEDIA, estado, OffsetDateTime.now(), OffsetDateTime.now(), 0,
                List.of(), List.of());
    }

    @Test
    void borradorYPublicadaSonAbiertas() {
        assertTrue(solicitud(EstadoSolicitud.BORRADOR).abierta());
        assertTrue(solicitud(EstadoSolicitud.PUBLICADA).abierta());
    }

    @Test
    void canceladaYContratadaNoSonAbiertas() {
        assertFalse(solicitud(EstadoSolicitud.CANCELADA).abierta());
        assertFalse(solicitud(EstadoSolicitud.CONTRATADA).abierta());
    }

    @Test
    void cancelarDesdePublicadaRegistraHistorial() {
        var cancelada = solicitud(EstadoSolicitud.PUBLICADA)
                .transitarA(EstadoSolicitud.CANCELADA, OffsetDateTime.now());
        assertEquals(EstadoSolicitud.CANCELADA, cancelada.estado());
        assertEquals(1, cancelada.historial().size());
        assertEquals(EstadoSolicitud.PUBLICADA, cancelada.historial().get(0).estadoAnterior());
    }

    @Test
    void transicionDesdeCanceladaEsInvalida() {
        assertThrows(BusinessException.Conflict.class, () -> solicitud(EstadoSolicitud.CANCELADA)
                .transitarA(EstadoSolicitud.PUBLICADA, OffsetDateTime.now()));
    }
}
