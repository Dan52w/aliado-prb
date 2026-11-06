package com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.application.port.CompatibilidadPort;
import com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.domain.model.Oportunidad;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.application.port.PerfilVisibilidadPort;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Visibilidad;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.port.PerfilProfesionalConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.EstadoExposicion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.application.port.SolicitudConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.EstadoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Solicitud;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Compatibilidad y oportunidades (SPEC-06, UC034–UC038).
 * Sin fórmula ni puntaje: la coincidencia es booleana por categoría, zona y disponibilidad.
 * Sin datos suficientes, la solicitud es no evaluable y no se muestra como compatible.
 */
@Service
public class OportunidadService implements CompatibilidadPort {
    private final SolicitudConsultaPort solicitudes;
    private final PerfilProfesionalConsultaPort perfiles;
    private final CatalogoConsultaPort catalogo;
    private final PerfilVisibilidadPort visibilidad;

    public OportunidadService(SolicitudConsultaPort solicitudes, PerfilProfesionalConsultaPort perfiles,
                              CatalogoConsultaPort catalogo, PerfilVisibilidadPort visibilidad) {
        this.solicitudes = solicitudes;
        this.perfiles = perfiles;
        this.catalogo = catalogo;
        this.visibilidad = visibilidad;
    }

    @Transactional(readOnly = true)
    public List<Oportunidad> listar(UUID prestadorId, String categoriaFiltro, String urgenciaFiltro) {
        PerfilProfesional perfil = perfiles.porPrestadorId(prestadorId).orElse(null);
        if (perfil == null || perfil.estadoExposicion() != EstadoExposicion.PUBLICO) {
            return List.of();
        }
        UUID categoriaId = null;
        if (categoriaFiltro != null && !categoriaFiltro.isBlank()) {
            categoriaId = resolverCategoria(categoriaFiltro);
            if (categoriaId == null) {
                return List.of();
            }
        }
        UUID categoriaBuscada = categoriaId;
        return solicitudes.publicadas().stream()
                .filter(s -> compatible(s, perfil))
                .filter(s -> categoriaBuscada == null || s.categoriaId().equals(categoriaBuscada))
                .filter(s -> urgenciaFiltro == null || urgenciaFiltro.isBlank()
                        || (s.urgencia() != null && s.urgencia().name().equalsIgnoreCase(urgenciaFiltro)))
                .map(this::aOportunidad).toList();
    }

    @Transactional(readOnly = true)
    public Oportunidad detalle(UUID solicitudId, UUID prestadorId) {
        PerfilProfesional perfil = perfiles.porPrestadorId(prestadorId).orElse(null);
        Solicitud solicitud = solicitudes.porId(solicitudId).orElse(null);
        if (perfil == null || solicitud == null || !compatible(solicitud, perfil)) {
            // 404 unificado, sin distinguir inexistente de no compatible (FR-037).
            throw new BusinessException.NotFound("OPORTUNIDAD_NO_ENCONTRADA", "Oportunidad no encontrada.");
        }
        return aOportunidad(solicitud);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esCompatible(UUID solicitudId, UUID prestadorId) {
        Optional<Solicitud> solicitud = solicitudes.porId(solicitudId);
        Optional<PerfilProfesional> perfil = perfiles.porPrestadorId(prestadorId);
        return solicitud.isPresent() && perfil.isPresent() && compatible(solicitud.get(), perfil.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> prestadoresCompatibles(UUID solicitudId) {
        Solicitud solicitud = solicitudes.porId(solicitudId).orElse(null);
        if (solicitud == null || solicitud.estado() != EstadoSolicitud.PUBLICADA) {
            return List.of();
        }
        return perfiles.visiblesPorCategoriaYZona(solicitud.categoriaId(), solicitud.zonaId()).stream()
                .filter(p -> compatible(solicitud, p))
                .map(PerfilProfesional::prestadorId).toList();
    }

    private boolean compatible(Solicitud solicitud, PerfilProfesional perfil) {
        if (solicitud.estado() != EstadoSolicitud.PUBLICADA) {
            return false;
        }
        if (perfil.estadoExposicion() != EstadoExposicion.PUBLICO) {
            return false;
        }
        // SPEC-02 FR-004: un perfil PRIVADO nunca se ofrece ni se notifica como compatible.
        if (visibilidad.preferenciaDe(perfil.prestadorId()) == Visibilidad.PRIVADO) {
            return false;
        }
        if (!perfil.categoriaIds().contains(solicitud.categoriaId())) {
            return false;
        }
        if (!perfil.zonaIds().contains(solicitud.zonaId())) {
            return false;
        }
        // P-02: la solicitud no define ventana temporal; la disponibilidad es tener bloques declarados.
        return !perfil.disponibilidades().isEmpty();
    }

    private UUID resolverCategoria(String filtro) {
        try {
            return UUID.fromString(filtro.trim());
        } catch (IllegalArgumentException ex) {
            return catalogo.categoriaPorNombre(filtro.trim()).map(c -> c.id()).orElse(null);
        }
    }

    private Oportunidad aOportunidad(Solicitud s) {
        String categoria = catalogo.categoriaPorId(s.categoriaId()).map(c -> c.nombre()).orElse(s.categoriaId().toString());
        String zona = catalogo.zonaPorId(s.zonaId()).map(z -> z.nombre()).orElse(s.zonaId().toString());
        return new Oportunidad(s.id(), categoria, zona,
                s.urgencia() == null ? null : s.urgencia().name(), s.descripcion(), s.fechaCreacion(),
                s.fotos().stream().map(f -> f.archivoKey()).toList());
    }
}
