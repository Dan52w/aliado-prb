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
        if (descripcion == null || descripcion.isBlank()) {
            throw new BusinessException.BadRequest("DESCRIPCION_REQUERIDA", "La descripción es obligatoria.");
        }
        var categoria = catalogo.categoriaPorId(categoriaId)
                .filter(c -> c.estado() == EstadoCatalogo.ACTIVO)
                .orElseThrow(() -> new BusinessException.BadRequest("CATEGORIA_INVALIDA",
                        "La categoría no existe o no está activa."));
        var zona = catalogo.zonaPorId(zonaId)
                .filter(z -> z.estado() == EstadoCatalogo.ACTIVO)
                .orElseThrow(() -> new BusinessException.BadRequest("ZONA_INVALIDA",
                        "La zona no existe o no está activa."));
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
        OffsetDateTime ahora = OffsetDateTime.now();
        Solicitud solicitud = new Solicitud(UUID.randomUUID(), clienteId, categoria.id(), zona.id(),
                descripcion.trim(), urgencia, estado, ahora, ahora, 0, List.of(),
                List.of(new HistorialEstadoSolicitud(UUID.randomUUID(), null, null, estado, ahora)));
        // El historial inicial referencia la solicitud recién creada.
        Solicitud conHistorial = new Solicitud(solicitud.id(), clienteId, categoria.id(), zona.id(),
                descripcion.trim(), urgencia, estado, ahora, ahora, 0, List.of(),
                List.of(new HistorialEstadoSolicitud(UUID.randomUUID(), solicitud.id(), null, estado, ahora)));
        Solicitud guardada = solicitudes.guardar(conHistorial);
        if (estado == EstadoSolicitud.PUBLICADA) {
            eventos.publicar(new Eventos.SolicitudPublicada(guardada.id(), clienteId, categoria.id(),
                    zona.id(), urgencia == null ? null : urgencia.name(), ahora));
        }
        return guardada;
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
        boolean eraBorrador = actual.estado() == EstadoSolicitud.BORRADOR;
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
    public java.util.List<Solicitud> publicadas() {
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
