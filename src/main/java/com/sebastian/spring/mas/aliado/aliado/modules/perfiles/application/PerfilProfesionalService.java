package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoStorageService;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoValidacion;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.EstadoCatalogo;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.port.PerfilProfesionalConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.CompromisoAgenda;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.Disponibilidad;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.EstadoExposicion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.ExperienciaProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.OrigenCompromiso;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.ServicioPortafolio;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.repository.PerfilProfesionalRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Perfil profesional (SPEC-05, UC024–UC033).
 * Un prestador, un perfil; crear exige al menos una categoría Y una zona (FR-024).
 * Conflicto disponibilidad/agenda (plan-05 T026): una disponibilidad que choca con un
 * compromiso ya registrado se rechaza con 409.
 */
@Service
public class PerfilProfesionalService implements PerfilProfesionalConsultaPort {
    private final PerfilProfesionalRepository perfiles;
    private final CatalogoConsultaPort catalogo;
    private final ArchivoStorageService storage;

    public PerfilProfesionalService(PerfilProfesionalRepository perfiles, CatalogoConsultaPort catalogo,
                                    ArchivoStorageService storage) {
        this.perfiles = perfiles;
        this.catalogo = catalogo;
        this.storage = storage;
    }

    @Transactional
    public PerfilProfesional crear(UUID prestadorId, Map<String, Object> datosGenerales,
                                   List<UUID> categoriaIds, List<UUID> zonaIds,
                                   List<ExperienciaProfesional> experiencias) {
        if (perfiles.porPrestadorId(prestadorId).isPresent()) {
            throw new BusinessException.Conflict("PERFIL_YA_EXISTE",
                    "El prestador ya tiene un perfil profesional; edítalo en lugar de crear otro.");
        }
        Set<UUID> categorias = validarCategorias(categoriaIds);
        Set<UUID> zonas = validarZonas(zonaIds);
        OffsetDateTime ahora = OffsetDateTime.now();
        PerfilProfesional perfil = new PerfilProfesional(UUID.randomUUID(), prestadorId,
                datosGenerales == null ? Map.of() : datosGenerales, EstadoExposicion.PUBLICO, ahora, ahora,
                categorias, zonas, experiencias == null ? List.of() : experiencias,
                List.of(), List.of(), List.of());
        return perfiles.guardar(perfil);
    }

    @Transactional
    public PerfilProfesional actualizarDatos(UUID prestadorId, Map<String, Object> datosGenerales) {
        PerfilProfesional actual = propio(prestadorId);
        return perfiles.guardar(actual.conDatos(datosGenerales == null ? Map.of() : datosGenerales));
    }

    @Transactional
    public PerfilProfesional actualizarCategorias(UUID prestadorId, List<UUID> categoriaIds) {
        PerfilProfesional actual = propio(prestadorId);
        return perfiles.guardar(actual.conCategorias(validarCategorias(categoriaIds)));
    }

    @Transactional
    public PerfilProfesional actualizarZonas(UUID prestadorId, List<UUID> zonaIds) {
        PerfilProfesional actual = propio(prestadorId);
        return perfiles.guardar(actual.conZonas(validarZonas(zonaIds)));
    }

    @Transactional
    public ExperienciaProfesional agregarExperiencia(UUID prestadorId, String descripcion,
                                                     LocalDate fechaInicio, LocalDate fechaFin) {
        PerfilProfesional actual = propio(prestadorId);
        if (descripcion == null || descripcion.isBlank() || fechaInicio == null) {
            throw new BusinessException.BadRequest("EXPERIENCIA_INVALIDA",
                    "La experiencia requiere descripción y fecha de inicio.");
        }
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new BusinessException.BadRequest("EXPERIENCIA_INVALIDA",
                    "La fecha de fin no puede ser anterior a la de inicio.");
        }
        ExperienciaProfesional experiencia = new ExperienciaProfesional(UUID.randomUUID(), actual.id(),
                descripcion.trim(), fechaInicio, fechaFin);
        var lista = new ArrayList<>(actual.experiencias());
        lista.add(experiencia);
        perfiles.guardar(actual.conExperiencias(List.copyOf(lista)));
        return experiencia;
    }

    @Transactional
    public ServicioPortafolio agregarPortafolio(UUID prestadorId, String descripcion,
                                                String nombreArchivo, String tipoContenido, byte[] contenido) {
        PerfilProfesional actual = propio(prestadorId);
        if (descripcion == null || descripcion.isBlank()) {
            throw new BusinessException.BadRequest("PORTAFOLIO_INVALIDO", "La descripción es obligatoria.");
        }
        String evidenciaKey = null;
        if (contenido != null && contenido.length > 0) {
            ArchivoValidacion.validar(nombreArchivo, tipoContenido, contenido, ArchivoValidacion.IMAGENES);
            evidenciaKey = storage.guardar(nombreArchivo, tipoContenido, contenido);
        }
        ServicioPortafolio servicio = new ServicioPortafolio(UUID.randomUUID(), actual.id(),
                descripcion.trim(), evidenciaKey, OffsetDateTime.now());
        var lista = new ArrayList<>(actual.portafolio());
        lista.add(servicio);
        perfiles.guardar(actual.conPortafolio(List.copyOf(lista)));
        return servicio;
    }

    @Transactional
    public void eliminarPortafolio(UUID prestadorId, UUID servicioId) {
        PerfilProfesional actual = propio(prestadorId);
        if (actual.portafolio().stream().noneMatch(s -> s.id().equals(servicioId))) {
            throw new BusinessException.NotFound("PORTAFOLIO_NO_ENCONTRADO",
                    "El servicio de portafolio no existe o no te pertenece.");
        }
        perfiles.guardar(actual.sinPortafolio(servicioId));
    }

    @Transactional
    public PerfilProfesional actualizarDisponibilidad(UUID prestadorId, List<Disponibilidad> bloques) {
        PerfilProfesional actual = propio(prestadorId);
        for (Disponibilidad bloque : bloques) {
            if (bloque.horaInicio() == null || bloque.horaFin() == null
                    || !bloque.horaFin().isAfter(bloque.horaInicio())) {
                throw new BusinessException.BadRequest("DISPONIBILIDAD_INVALIDA",
                        "Cada bloque requiere hora de fin posterior a la de inicio.");
            }
            if (bloque.diaSemana() != null && (bloque.diaSemana() < 1 || bloque.diaSemana() > 7)) {
                throw new BusinessException.BadRequest("DISPONIBILIDAD_INVALIDA",
                        "diaSemana debe estar entre 1 y 7.");
            }
            for (CompromisoAgenda compromiso : actual.compromisos()) {
                if (chocaConCompromiso(bloque, compromiso)) {
                    throw new BusinessException.Conflict("CONFLICTO_AGENDA",
                            "La disponibilidad entra en conflicto con un compromiso ya registrado.");
                }
            }
        }
        List<Disponibilidad> conIds = bloques.stream()
                .map(b -> new Disponibilidad(b.id() == null ? UUID.randomUUID() : b.id(), actual.id(),
                        b.diaSemana(), b.horaInicio(), b.horaFin())).toList();
        return perfiles.guardar(actual.conDisponibilidades(conIds));
    }

    private boolean chocaConCompromiso(Disponibilidad bloque, CompromisoAgenda compromiso) {
        int diaCompromiso = compromiso.fechaHoraInicio().getDayOfWeek().getValue();
        if (bloque.diaSemana() != null && bloque.diaSemana() != diaCompromiso) {
            return false;
        }
        LocalTime inicio = compromiso.fechaHoraInicio().toLocalTime();
        LocalTime fin = compromiso.fechaHoraFin().toLocalTime();
        return bloque.horaInicio().isBefore(fin) && inicio.isBefore(bloque.horaFin());
    }

    @Transactional
    public CompromisoAgenda crearCompromiso(UUID prestadorId, OffsetDateTime inicio, OffsetDateTime fin) {
        return guardarCompromiso(prestadorId, null, inicio, fin);
    }

    @Transactional
    public CompromisoAgenda actualizarCompromiso(UUID prestadorId, UUID compromisoId,
                                                 OffsetDateTime inicio, OffsetDateTime fin) {
        return guardarCompromiso(prestadorId, compromisoId, inicio, fin);
    }

    private CompromisoAgenda guardarCompromiso(UUID prestadorId, UUID compromisoId,
                                               OffsetDateTime inicio, OffsetDateTime fin) {
        PerfilProfesional actual = propio(prestadorId);
        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new BusinessException.BadRequest("COMPROMISO_INVALIDO",
                    "El compromiso requiere fin posterior al inicio.");
        }
        if (compromisoId != null && actual.compromisos().stream().noneMatch(c -> c.id().equals(compromisoId))) {
            throw new BusinessException.NotFound("COMPROMISO_NO_ENCONTRADO", "Compromiso no encontrado.");
        }
        CompromisoAgenda compromiso = new CompromisoAgenda(
                compromisoId == null ? UUID.randomUUID() : compromisoId, actual.id(),
                inicio, fin, OrigenCompromiso.MANUAL);
        perfiles.guardar(actual.conCompromiso(compromiso));
        return compromiso;
    }

    @Transactional(readOnly = true)
    public List<CompromisoAgenda> listarAgenda(UUID prestadorId) {
        return propio(prestadorId).compromisos();
    }

    @Transactional(readOnly = true)
    public PerfilProfesional mio(UUID prestadorId) {
        return propio(prestadorId);
    }

    private PerfilProfesional propio(UUID prestadorId) {
        return perfiles.porPrestadorId(prestadorId)
                .orElseThrow(() -> new BusinessException.NotFound("PERFIL_NO_ENCONTRADO",
                        "Primero debes crear tu perfil profesional."));
    }

    private Set<UUID> validarCategorias(List<UUID> ids) {
        return validarExistentesActivos(ids, "categoría",
                id -> catalogo.categoriaPorId(id).map(c -> c.estado() == EstadoCatalogo.ACTIVO).orElse(false));
    }

    private Set<UUID> validarZonas(List<UUID> ids) {
        return validarExistentesActivos(ids, "zona de atención",
                id -> catalogo.zonaPorId(id).map(z -> z.estado() == EstadoCatalogo.ACTIVO).orElse(false));
    }

    private Set<UUID> validarExistentesActivos(List<UUID> ids, String que, Predicate<UUID> activo) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException.BadRequest("CATALOGO_REQUERIDO",
                    "El perfil requiere al menos una " + que + ".");
        }
        Set<UUID> validos = new HashSet<>();
        for (UUID id : ids) {
            if (!activo.test(id)) {
                throw new BusinessException.BadRequest("CATALOGO_INVALIDO",
                        "Un elemento del catálogo no existe o no está activo: " + id);
            }
            validos.add(id);
        }
        return validos;
    }

    @Override
    public Optional<PerfilProfesional> porPrestadorId(UUID prestadorId) {
        return perfiles.porPrestadorId(prestadorId);
    }

    @Override
    public List<PerfilProfesional> visiblesPorCategoriaYZona(UUID categoriaId, UUID zonaId) {
        return perfiles.visiblesPorCategoriaYZona(categoriaId, zonaId);
    }
}
