package com.sebastian.spring.mas.aliado.aliado.domain;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.application.OportunidadService;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.port.PerfilProfesionalConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.Disponibilidad;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.EstadoExposicion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.application.port.SolicitudConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.EstadoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Solicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Urgencia;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Compatibilidad booleana (SPEC-06): categoría + zona + disponibilidad declarada.
 * Sin fórmula ni puntaje.
 */
@ExtendWith(MockitoExtension.class)
class CompatibilidadTest {

    @Mock
    private SolicitudConsultaPort solicitudes;
    @Mock
    private PerfilProfesionalConsultaPort perfiles;
    @Mock
    private CatalogoConsultaPort catalogo;

    private static final UUID CAT = UUID.randomUUID();
    private static final UUID ZONA = UUID.randomUUID();
    private static final UUID PRESTADOR = UUID.randomUUID();
    private static final UUID SOLICITUD_ID = UUID.randomUUID();

    private Solicitud solicitud() {
        return new Solicitud(SOLICITUD_ID, UUID.randomUUID(), CAT, ZONA, "Reparar grifo",
                Urgencia.MEDIA, EstadoSolicitud.PUBLICADA, OffsetDateTime.now(), OffsetDateTime.now(),
                0, List.of(), List.of());
    }

    private PerfilProfesional perfil(boolean conDisponibilidad, boolean publico) {
        return new PerfilProfesional(UUID.randomUUID(), PRESTADOR, Map.of(),
                publico ? EstadoExposicion.PUBLICO : EstadoExposicion.OCULTO,
                OffsetDateTime.now(), OffsetDateTime.now(), Set.of(CAT), Set.of(ZONA),
                List.of(), List.of(),
                conDisponibilidad
                        ? List.of(new Disponibilidad(UUID.randomUUID(), UUID.randomUUID(), 1,
                                LocalTime.of(8, 0), LocalTime.of(12, 0)))
                        : List.of(),
                List.of());
    }

    private OportunidadService servicio() {
        return new OportunidadService(solicitudes, perfiles, catalogo);
    }

    private void preparar(boolean conDisponibilidad, boolean publico) {
        when(solicitudes.porId(SOLICITUD_ID)).thenReturn(Optional.of(solicitud()));
        when(perfiles.porPrestadorId(PRESTADOR)).thenReturn(Optional.of(perfil(conDisponibilidad, publico)));
    }

    @Test
    void compatibleCuandoCoincidenLosTresCriterios() {
        preparar(true, true);
        assertTrue(servicio().esCompatible(SOLICITUD_ID, PRESTADOR));
    }

    @Test
    void noCompatibleSinDisponibilidadDeclarada() {
        preparar(false, true);
        assertFalse(servicio().esCompatible(SOLICITUD_ID, PRESTADOR));
    }

    @Test
    void noCompatibleSiPerfilOculto() {
        preparar(true, false);
        assertFalse(servicio().esCompatible(SOLICITUD_ID, PRESTADOR));
    }

    @Test
    void noCompatibleSiCategoriaNoCoincide() {
        when(solicitudes.porId(SOLICITUD_ID)).thenReturn(Optional.of(solicitud()));
        when(perfiles.porPrestadorId(PRESTADOR)).thenReturn(Optional.of(new PerfilProfesional(
                UUID.randomUUID(), PRESTADOR, Map.of(), EstadoExposicion.PUBLICO, OffsetDateTime.now(),
                OffsetDateTime.now(), Set.of(UUID.randomUUID()), Set.of(ZONA), List.of(), List.of(),
                List.of(new Disponibilidad(UUID.randomUUID(), UUID.randomUUID(), 1, LocalTime.of(8, 0),
                        LocalTime.of(12, 0))), List.of())));
        assertFalse(servicio().esCompatible(SOLICITUD_ID, PRESTADOR));
    }

    @Test
    void noCompatibleSinSolicitudNiPerfil() {
        when(solicitudes.porId(SOLICITUD_ID)).thenReturn(Optional.empty());
        when(perfiles.porPrestadorId(PRESTADOR)).thenReturn(Optional.empty());
        assertFalse(servicio().esCompatible(SOLICITUD_ID, PRESTADOR));
    }
}
