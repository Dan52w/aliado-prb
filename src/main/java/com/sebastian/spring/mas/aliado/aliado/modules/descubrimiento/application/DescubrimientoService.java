package com.sebastian.spring.mas.aliado.aliado.modules.descubrimiento.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.ConfianzaConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.descubrimiento.domain.model.PrestadorPublico;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.port.PerfilProfesionalConsultaPort;
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

    public DescubrimientoService(PerfilProfesionalConsultaPort perfiles, CatalogoConsultaPort catalogo,
                                 ConfianzaConsultaPort confianza) {
        this.perfiles = perfiles;
        this.catalogo = catalogo;
        this.confianza = confianza;
    }

    public record ResultadoBusqueda(List<PrestadorPublico> prestadores, List<String> sugerencias) {}

    @Transactional(readOnly = true)
    public ResultadoBusqueda buscar(String categoriaParam, String zonaParam) {
        UUID categoriaId = null;
        UUID zonaId = null;
        List<String> sugerencias = List.of();
        if (categoriaParam != null && !categoriaParam.isBlank()) {
            categoriaId = resolverId(categoriaParam, true);
            if (categoriaId == null) {
                sugerencias = catalogo.categoriasActivas().stream().map(c -> c.nombre()).toList();
                return new ResultadoBusqueda(List.of(), sugerencias);
            }
        }
        if (zonaParam != null && !zonaParam.isBlank()) {
            zonaId = resolverId(zonaParam, false);
            if (zonaId == null) {
                sugerencias = catalogo.zonasActivas().stream().map(z -> z.nombre()).toList();
                return new ResultadoBusqueda(List.of(), sugerencias);
            }
        }
        List<PrestadorPublico> resultados = perfiles.visiblesPorCategoriaYZona(categoriaId, zonaId).stream()
                .filter(p -> !p.disponibilidades().isEmpty())
                .map(this::aPublico).toList();
        return new ResultadoBusqueda(resultados, List.of());
    }

    @Transactional(readOnly = true)
    public PrestadorPublico detalle(UUID prestadorId) {
        PerfilProfesional perfil = perfiles.porPrestadorId(prestadorId)
                .filter(p -> p.estadoExposicion() == com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.EstadoExposicion.PUBLICO)
                .filter(p -> !p.disponibilidades().isEmpty())
                .orElseThrow(() -> new BusinessException.NotFound("PERFIL_NO_DISPONIBLE",
                        "El perfil no está disponible."));
        return aPublico(perfil);
    }

    private UUID resolverId(String param, boolean esCategoria) {
        try {
            UUID id = UUID.fromString(param.trim());
            boolean existe = esCategoria
                    ? catalogo.categoriaPorId(id).isPresent()
                    : catalogo.zonaPorId(id).isPresent();
            return existe ? id : null;
        } catch (IllegalArgumentException ex) {
            return esCategoria
                    ? catalogo.categoriaPorNombre(param.trim()).map(c -> c.id()).orElse(null)
                    : catalogo.zonaPorNombre(param.trim()).map(z -> z.id()).orElse(null);
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
                confianzaInfo.nivelVerificacion(),
                perfil.disponibilidades().stream()
                        .map(d -> new PrestadorPublico.Bloque(d.diaSemana(), d.horaInicio(), d.horaFin())).toList(),
                perfil.datosGenerales());
    }
}
