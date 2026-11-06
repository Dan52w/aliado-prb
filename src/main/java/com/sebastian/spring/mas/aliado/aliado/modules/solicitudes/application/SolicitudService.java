package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.application;

import com.sebastian.spring.mas.aliado.aliado.common.events.Eventos;
import com.sebastian.spring.mas.aliado.aliado.common.events.PublicadorEventos;
import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoStorageService;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoValidacion;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.EstadoCatalogo;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.application.port.SolicitudConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.EstadoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.FotoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.HistorialEstadoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Solicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Urgencia;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.repository.SolicitudRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestión de solicitudes de servicio (SPEC-04, UC016–UC023).
 * Zona y urgencia obligatorias al publicar (FR-017); el borrador no es visible para prestadores.
 * Al publicar se emite SolicitudPublicada: las notificaciones de oportunidad (SPEC-06)
 * se generan por evento, sin acoplar este módulo al de notificaciones.
 */
@Service
public class SolicitudService implements SolicitudConsultaPort {
    private final SolicitudRepository solicitudes;
    private final CatalogoConsultaPort catalogo;
    private final ArchivoStorageService storage;
    private final PublicadorEventos eventos;

    public SolicitudService(SolicitudRepository solicitudes, CatalogoConsultaPort catalogo,
                            ArchivoStorageService storage, PublicadorEventos eventos) {
        this.solicitudes = solicitudes;
        this.catalogo = catalogo;
        this.storage = storage;
        this.eventos = eventos;
    }

    @Transactional
    public Solicitud crear(UUID clienteId, UUID categoriaId, String descripcion, UUID zonaId,
                           String urgenciaTexto, String estadoDeseado) {
        // El estado se resuelve primero: el borrador es parcial (SPEC-04) y la
        // publicación exige todo. V8 relajó los NOT NULL de categoria/zona/descripción.
        EstadoSolicitud estado = resolverEstado(estadoDeseado);
        boolean borrador = estado == EstadoSolicitud.BORRADOR;
        String descripcionLimpia = validarDescripcion(descripcion, borrador);
        UUID categoriaValida = validarCategoria(categoriaId, borrador);
        UUID zonaValida = validarZona(zonaId, borrador);
        Urgencia urgencia = validarUrgencia(urgenciaTexto, estado);
        OffsetDateTime ahora = OffsetDateTime.now();
        UUID solicitudId = UUID.randomUUID();
        Solicitud solicitud = new Solicitud(solicitudId, clienteId, categoriaValida, zonaValida,
                descripcionLimpia, urgencia, estado, ahora, ahora, 0, List.of(),
                List.of(new HistorialEstadoSolicitud(UUID.randomUUID(), solicitudId, null, estado, ahora)));
        Solicitud guardada = solicitudes.guardar(solicitud);
        if (estado == EstadoSolicitud.PUBLICADA) {
            eventos.publicar(new Eventos.SolicitudPublicada(guardada.id(), clienteId, categoriaValida,
                    zonaValida, urgencia == null ? null : urgencia.name(), ahora));
        }
        return guardada;
    }

    private EstadoSolicitud resolverEstado(String estadoDeseado) {
        EstadoSolicitud estado = EstadoSolicitud.PUBLICADA;
        if (estadoDeseado != null && !estadoDeseado.isBlank()) {
            try {
                estado = EstadoSolicitud.valueOf(estadoDeseado);
            } catch (IllegalArgumentException ex) {
                throw new BusinessException.BadRequest("ESTADO_INVALIDO",
                        "estadoDeseado debe ser BORRADOR o PUBLICADA.");
            }
            if (estado != EstadoSolicitud.BORRADOR && estado != EstadoSolicitud.PUBLICADA) {
                throw new BusinessException.BadRequest("ESTADO_INVALIDO",
                        "estadoDeseado debe ser BORRADOR o PUBLICADA.");
            }
        }
        return estado;
    }

    private String validarDescripcion(String descripcion, boolean borrador) {
        String descripcionLimpia = descripcion == null ? null : descripcion.trim();
        if (!borrador && (descripcionLimpia == null || descripcionLimpia.isBlank())) {
            throw new BusinessException.BadRequest("DESCRIPCION_REQUERIDA", "La descripción es obligatoria.");
        }
        if (borrador && descripcionLimpia != null && descripcionLimpia.isBlank()) {
            descripcionLimpia = null;
        }
        return descripcionLimpia;
    }

    private UUID validarCategoria(UUID categoriaId, boolean borrador) {
        if (categoriaId != null) {
            return catalogo.categoriaPorId(categoriaId)
                    .filter(c -> c.estado() == EstadoCatalogo.ACTIVO)
                    .orElseThrow(() -> new BusinessException.BadRequest("CATEGORIA_INVALIDA",
                            "La categoría no existe o no está activa.")).id();
        }
        if (!borrador) {
            throw new BusinessException.BadRequest("CATEGORIA_INVALIDA",
                    "La categoría es obligatoria para publicar la solicitud.");
        }
        return null;
    }

    private UUID validarZona(UUID zonaId, boolean borrador) {
        if (zonaId != null) {
            return catalogo.zonaPorId(zonaId)
                    .filter(z -> z.estado() == EstadoCatalogo.ACTIVO)
                    .orElseThrow(() -> new BusinessException.BadRequest("ZONA_INVALIDA",
                            "La zona no existe o no está activa.")).id();
        }
        if (!borrador) {
            throw new BusinessException.BadRequest("ZONA_INVALIDA",
                    "La zona es obligatoria para publicar la solicitud.");
        }
        return null;
    }

    private Urgencia validarUrgencia(String urgenciaTexto, EstadoSolicitud estado) {
        Urgencia urgencia = null;
        if (urgenciaTexto != null && !urgenciaTexto.isBlank()) {
            try {
                urgencia = Urgencia.valueOf(urgenciaTexto);
            } catch (IllegalArgumentException ex) {
                throw new BusinessException.BadRequest("URGENCIA_INVALIDA",
                        "Urgencia inválida. Valores: BAJA, MEDIA, ALTA, URGENTE.");
            }
        }
        if (estado == EstadoSolicitud.PUBLICADA && urgencia == null) {
            throw new BusinessException.BadRequest("URGENCIA_REQUERIDA",
                    "La urgencia es obligatoria para publicar la solicitud.");
        }
        return urgencia;
    }

    @Transactional
    public Solicitud editar(UUID solicitudId, UUID clienteId, UUID categoriaId, String descripcion,
                            UUID zonaId, String urgenciaTexto) {
        Solicitud actual = propiaAbierta(solicitudId, clienteId);
        OffsetDateTime ahora = OffsetDateTime.now();
        UUID nuevaCategoria = actual.categoriaId();
        UUID nuevaZona = actual.zonaId();
        Urgencia nuevaUrgencia = actual.urgencia();
        if (categoriaId != null) {
            nuevaCategoria = catalogo.categoriaPorId(categoriaId)
                    .filter(c -> c.estado() == EstadoCatalogo.ACTIVO)
                    .orElseThrow(() -> new BusinessException.BadRequest("CATEGORIA_INVALIDA",
                            "La categoría no existe o no está activa.")).id();
        }
        if (zonaId != null) {
            nuevaZona = catalogo.zonaPorId(zonaId)
                    .filter(z -> z.estado() == EstadoCatalogo.ACTIVO)
                    .orElseThrow(() -> new BusinessException.BadRequest("ZONA_INVALIDA",
                            "La zona no existe o no está activa.")).id();
        }
        if (urgenciaTexto != null) {
            try {
                nuevaUrgencia = Urgencia.valueOf(urgenciaTexto);
            } catch (IllegalArgumentException ex) {
                throw new BusinessException.BadRequest("URGENCIA_INVALIDA", "Urgencia inválida.");
            }
        }
        Solicitud editada = new Solicitud(actual.id(), clienteId, nuevaCategoria, nuevaZona,
                descripcion == null ? actual.descripcion() : descripcion.trim(), nuevaUrgencia,
                actual.estado(), actual.fechaCreacion(), ahora, actual.version(), actual.fotos(), actual.historial());
        return solicitudes.guardar(editada);
    }

    /** Publica un borrador (BORRADOR -> PUBLICADA) y emite el evento de oportunidad. */
    @Transactional
    public Solicitud publicar(UUID solicitudId, UUID clienteId) {
        Solicitud actual = propiaAbierta(solicitudId, clienteId);
        if (actual.estado() != EstadoSolicitud.BORRADOR) {
            throw new BusinessException.Conflict("TRANSICION_INVALIDA", "Solo un borrador puede publicarse.");
        }
        // SPEC-12: publicar un borrador revalida que esté completo y que categoría y
        // zona sigan activas en ese momento (pudieron desactivarse tras crear el borrador).
        if (actual.descripcion() == null || actual.descripcion().isBlank()
                || actual.categoriaId() == null || actual.zonaId() == null) {
            throw new BusinessException.BadRequest("BORRADOR_INCOMPLETO",
                    "Para publicar faltan la descripción, la categoría o la zona.");
        }
        catalogo.categoriaPorId(actual.categoriaId())
                .filter(c -> c.estado() == EstadoCatalogo.ACTIVO)
                .orElseThrow(() -> new BusinessException.BadRequest("CATEGORIA_INVALIDA",
                        "La categoría no existe o no está activa."));
        catalogo.zonaPorId(actual.zonaId())
                .filter(z -> z.estado() == EstadoCatalogo.ACTIVO)
                .orElseThrow(() -> new BusinessException.BadRequest("ZONA_INVALIDA",
                        "La zona no existe o no está activa."));
        if (actual.urgencia() == null) {
            throw new BusinessException.BadRequest("URGENCIA_REQUERIDA",
                    "La urgencia es obligatoria para publicar la solicitud.");
        }
        Solicitud publicada = solicitudes.guardar(actual.transitarA(EstadoSolicitud.PUBLICADA, OffsetDateTime.now()));
        eventos.publicar(new Eventos.SolicitudPublicada(publicada.id(), clienteId, publicada.categoriaId(),
                publicada.zonaId(), publicada.urgencia().name(), OffsetDateTime.now()));
        return publicada;
    }

    @Transactional
    public Solicitud cancelar(UUID solicitudId, UUID clienteId) {
        Solicitud actual = propiaAbierta(solicitudId, clienteId);
        return solicitudes.guardar(actual.transitarA(EstadoSolicitud.CANCELADA, OffsetDateTime.now()));
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listarMias(UUID clienteId) {
        return solicitudes.porCliente(clienteId);
    }

    @Transactional(readOnly = true)
    public Solicitud detalleMio(UUID solicitudId, UUID clienteId) {
        // 404 unificado: inexistente o ajena, sin distinguir la causa (FR-022).
        return solicitudes.porId(solicitudId)
                .filter(s -> s.clienteId().equals(clienteId))
                .orElseThrow(() -> new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA",
                        "Solicitud no encontrada."));
    }

    @Transactional
    public FotoSolicitud agregarFoto(UUID solicitudId, UUID clienteId, String nombreOriginal,
                                     String tipoContenido, byte[] contenido) {
        Solicitud actual = propiaAbierta(solicitudId, clienteId);
        if (actual.fotos().size() >= 10) {
            throw new BusinessException.BadRequest("LIMITE_FOTOS", "La solicitud admite como máximo 10 fotos.");
        }
        ArchivoValidacion.validar(nombreOriginal, tipoContenido, contenido, ArchivoValidacion.IMAGENES);
        String clave = storage.guardar(nombreOriginal, tipoContenido, contenido);
        FotoSolicitud foto = new FotoSolicitud(UUID.randomUUID(), solicitudId, clave, actual.fotos().size() + 1);
        solicitudes.guardar(actual.conFoto(foto));
        return foto;
    }

    /** Contenido de una foto: el cliente dueño, o cualquier autenticado si es PUBLICADA. */
    @Transactional(readOnly = true)
    public byte[] descargarFoto(UUID solicitudId, UUID fotoId, UUID usuarioId) {
        Solicitud solicitud = solicitudes.porId(solicitudId)
                .orElseThrow(() -> new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA",
                        "Solicitud no encontrada."));
        boolean dueno = solicitud.clienteId().equals(usuarioId);
        boolean publica = solicitud.estado() == EstadoSolicitud.PUBLICADA;
        if (!dueno && !publica) {
            throw new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA", "Solicitud no encontrada.");
        }
        FotoSolicitud foto = solicitud.fotos().stream()
                .filter(f -> f.id().equals(fotoId))
                .findFirst()
                .orElseThrow(() -> new BusinessException.NotFound("FOTO_NO_ENCONTRADA",
                        "Foto no encontrada."));
        return storage.leer(foto.archivoKey());
    }

    @Transactional
    public FotoSolicitud reemplazarFoto(UUID solicitudId, UUID fotoId, UUID clienteId, String nombreOriginal,
                                        String tipoContenido, byte[] contenido) {
        Solicitud actual = propiaAbierta(solicitudId, clienteId);
        FotoSolicitud anterior = actual.fotos().stream()
                .filter(f -> f.id().equals(fotoId))
                .findFirst()
                .orElseThrow(() -> new BusinessException.NotFound("FOTO_NO_ENCONTRADA",
                        "Foto no encontrada."));
        ArchivoValidacion.validar(nombreOriginal, tipoContenido, contenido, ArchivoValidacion.IMAGENES);
        String claveNueva = storage.guardar(nombreOriginal, tipoContenido, contenido);
        FotoSolicitud reemplazada = new FotoSolicitud(anterior.id(), solicitudId, claveNueva, anterior.orden());
        solicitudes.guardar(actual.conFotoReemplazada(reemplazada));
        storage.eliminar(anterior.archivoKey());
        return reemplazada;
    }

    @Transactional
    public void eliminarFoto(UUID solicitudId, UUID fotoId, UUID clienteId) {
        Solicitud actual = propiaAbierta(solicitudId, clienteId);
        FotoSolicitud foto = actual.fotos().stream()
                .filter(f -> f.id().equals(fotoId))
                .findFirst()
                .orElseThrow(() -> new BusinessException.NotFound("FOTO_NO_ENCONTRADA",
                        "Foto no encontrada."));
        solicitudes.guardar(actual.sinFoto(fotoId));
        storage.eliminar(foto.archivoKey());
    }

    private Solicitud propiaAbierta(UUID solicitudId, UUID clienteId) {
        Solicitud solicitud = solicitudes.porId(solicitudId)
                .orElseThrow(() -> new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA",
                        "Solicitud no encontrada."));
        if (!solicitud.clienteId().equals(clienteId)) {
            throw new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA", "Solicitud no encontrada.");
        }
        if (!solicitud.abierta()) {
            throw new BusinessException.Conflict("SOLICITUD_CERRADA",
                    "La solicitud ya no admite cambios (estado " + solicitud.estado() + ").");
        }
        return solicitud;
    }

    @Override
    public Optional<Solicitud> porId(UUID id) {
        return solicitudes.porId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> buscarPorTexto(String texto) {
        return solicitudes.buscarPorTexto(texto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> publicadas() {
        return solicitudes.publicadas();
    }

    @Override
    @Transactional
    public void marcarContratada(UUID solicitudId) {
        Solicitud actual = solicitudes.porId(solicitudId)
                .orElseThrow(() -> new BusinessException.NotFound("SOLICITUD_NO_ENCONTRADA",
                        "Solicitud no encontrada."));
        solicitudes.guardar(actual.transitarA(EstadoSolicitud.CONTRATADA, OffsetDateTime.now()));
    }
}
