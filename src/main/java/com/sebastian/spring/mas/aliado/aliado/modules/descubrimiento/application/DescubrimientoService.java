package com.sebastian.spring.mas.aliado.aliado.modules.descubrimiento.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.ConfianzaConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.descubrimiento.domain.model.PrestadorPublico;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.application.port.PerfilVisibilidadPort;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Visibilidad;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.port.PerfilProfesionalConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.EstadoExposicion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Descubrimiento de prestadores (SPEC-03, UC014–UC015). Solo rol CLIENTE.
 * Sin resultados nunca es un error; categoría/zona inexistente devuelve sugerencias (200).
 * Perfil incompleto, oculto o inexistente: mismo 404 genérico, sin distinguir la causa.
 */
@Service
public class DescubrimientoService {
    private final PerfilProfesionalConsultaPort perfiles;
    private final CatalogoConsultaPort catalogo;
    private final ConfianzaConsultaPort confianza;
    private final PerfilVisibilidadPort visibilidad;

    public DescubrimientoService(PerfilProfesionalConsultaPort perfiles, CatalogoConsultaPort catalogo,
                                 ConfianzaConsultaPort confianza, PerfilVisibilidadPort visibilidad) {
        this.perfiles = perfiles;
        this.catalogo = catalogo;
        this.confianza = confianza;
        this.visibilidad = visibilidad;
    }

    public record ResultadoBusqueda(List<PrestadorPublico> prestadores, List<String> sugerencias) {}

    @Transactional(readOnly = true)
    public ResultadoBusqueda buscar(String categoriaParam, String zonaParam) {
        UUID categoriaId = null;
        UUID zonaId = null;
        List<String> sugerencias = List.of();
        if (categoriaParam != null && !categoriaParam.isBlank()) {
            categoriaId = resolverCategoriaId(categoriaParam);
            if (categoriaId == null) {
                sugerencias = catalogo.categoriasActivas().stream().map(c -> c.nombre()).toList();
                return new ResultadoBusqueda(List.of(), sugerencias);
            }
        }
        if (zonaParam != null && !zonaParam.isBlank()) {
            zonaId = resolverZonaId(zonaParam);
            if (zonaId == null) {
                sugerencias = catalogo.zonasActivas().stream().map(z -> z.nombre()).toList();
                return new ResultadoBusqueda(List.of(), sugerencias);
            }
        }
        // SPEC-03: la búsqueda filtra solo por categoría y zona; la disponibilidad
        // declarada se muestra, no se exige para aparecer. La preferencia de
        // visibilidad (SPEC-02, FR-004) sí se aplica: PRIVADO nunca aparece y
        // SOLO_ZONA solo aparece en búsquedas acotadas a su zona.
        UUID zonaBuscada = zonaId;
        List<PrestadorPublico> resultados = perfiles.visiblesPorCategoriaYZona(categoriaId, zonaId).stream()
                .filter(p -> visibleEnBusqueda(p, zonaBuscada))
                .map(this::aPublico).toList();
        return new ResultadoBusqueda(resultados, List.of());
    }

    private boolean visibleEnBusqueda(PerfilProfesional perfil, UUID zonaId) {
        Visibilidad preferencia = visibilidad.preferenciaDe(perfil.prestadorId());
        if (preferencia == Visibilidad.PRIVADO) {
            return false;
        }
        if (preferencia == Visibilidad.SOLO_ZONA && zonaId == null) {
            return false;
        }
        return true;
    }

    @Transactional(readOnly = true)
    public PrestadorPublico detalle(UUID prestadorId) {
        if (visibilidad.preferenciaDe(prestadorId) == Visibilidad.PRIVADO) {
            throw new BusinessException.NotFound("PERFIL_NO_DISPONIBLE",
                    "El perfil no está disponible.");
        }
        PerfilProfesional perfil = perfiles.porPrestadorId(prestadorId)
                .filter(p -> p.estadoExposicion() == EstadoExposicion.PUBLICO)
                .filter(p -> !p.disponibilidades().isEmpty())
                .orElseThrow(() -> new BusinessException.NotFound("PERFIL_NO_DISPONIBLE",
                        "El perfil no está disponible."));
        return aPublico(perfil);
    }

    private UUID resolverCategoriaId(String param) {
        UUID id = comoUuid(param);
        if (id != null) {
            return catalogo.categoriaPorId(id).isPresent() ? id : null;
        }
        return catalogo.categoriaPorNombre(param.trim()).map(c -> c.id()).orElse(null);
    }

    private UUID resolverZonaId(String param) {
        UUID id = comoUuid(param);
        if (id != null) {
            return catalogo.zonaPorId(id).isPresent() ? id : null;
        }
        return catalogo.zonaPorNombre(param.trim()).map(z -> z.id()).orElse(null);
    }

    private UUID comoUuid(String param) {
        try {
            return UUID.fromString(param.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private PrestadorPublico aPublico(PerfilProfesional perfil) {
        List<String> categorias = perfil.categoriaIds().stream()
                .map(id -> catalogo.categoriaPorId(id).map(c -> c.nombre()).orElse(id.toString())).toList();
        List<String> zonas = perfil.zonaIds().stream()
                .map(id -> catalogo.zonaPorId(id).map(z -> z.nombre()).orElse(id.toString())).toList();
        var confianzaInfo = confianza.confianzaDe(perfil.prestadorId());
        return new PrestadorPublico(perfil.prestadorId(), categorias, zonas,
                confianzaInfo.reputacion().valor(), confianzaInfo.reputacion().cantidad(),
                confianzaInfo.nivelVerificacion().name(),
                perfil.disponibilidades().stream()
                        .map(d -> new PrestadorPublico.Bloque(d.diaSemana(), d.horaInicio(), d.horaFin())).toList(),
                PerfilProfesional.datosPublicos(perfil.datosGenerales()),
                confianza.resenasVisiblesDe(perfil.prestadorId()).stream()
                        .map(r -> new PrestadorPublico.ResenaPublica(r.id(), r.valor(), r.texto(),
                                r.fechaCreacion()))
                        .toList());
    }
}
