package com.sebastian.spring.mas.aliado.aliado.domain;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.RolConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.ConfianzaService;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Calificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Reputacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.CalificacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.ReputacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.ResenaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.SolicitudVerificacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.port.ReportePort;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port.ServicioLecturaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.EstadoServicio;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.ServicioContratado;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Reputación = media aritmética de calificaciones (decisión adoptada, SPEC-10 no define fórmula).
 * Se verifica a través de ConfianzaService.calificar.
 */
@ExtendWith(MockitoExtension.class)
class ReputacionTest {

    @Mock
    private CalificacionRepository calificaciones;
    @Mock
    private ResenaRepository resenas;
    @Mock
    private ReputacionRepository reputaciones;
    @Mock
    private SolicitudVerificacionRepository verificaciones;
    @Mock
    private ServicioLecturaPort servicios;
    @Mock
    private ReportePort reportes;
    @Mock
    private RolConsultaPort roles;

    private ConfianzaService servicio() {
        return new ConfianzaService(calificaciones, resenas, reputaciones, verificaciones, servicios, reportes,
                roles);
    }

    @Test
    void mediaDeDosCalificaciones() {
        UUID servicioId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID prestadorId = UUID.randomUUID();
        var servicio = new ServicioContratado(servicioId, UUID.randomUUID(), UUID.randomUUID(), clienteId,
                prestadorId, EstadoServicio.FINALIZADO, true, OffsetDateTime.now(), 0, List.of(), List.of());
        when(servicios.porId(servicioId)).thenReturn(Optional.of(servicio));
        when(calificaciones.porServicioId(servicioId)).thenReturn(Optional.empty());
        when(calificaciones.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(calificaciones.porPrestadorId(prestadorId)).thenReturn(List.of(
                new Calificacion(UUID.randomUUID(), servicioId, clienteId, prestadorId, 4, OffsetDateTime.now()),
                new Calificacion(UUID.randomUUID(), servicioId, clienteId, prestadorId, 5, OffsetDateTime.now())));

        servicio().calificar(servicioId, clienteId, 5);

        ArgumentCaptor<Reputacion> captor = ArgumentCaptor.forClass(Reputacion.class);
        verify(reputaciones).guardar(captor.capture());
        assertEquals(0, new BigDecimal("4.50").compareTo(captor.getValue().valorAgregado()));
        assertEquals(2, captor.getValue().cantidadCalificaciones());
    }
}
