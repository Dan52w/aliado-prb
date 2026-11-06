package com.sebastian.spring.mas.aliado.aliado.modules.confianza.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoStorageService;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoValidacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.ConfianzaConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.VerificacionAdminPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Calificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.DocumentoVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.EstadoVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.EstadoVisibilidadResena;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Reputacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Resena;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.SolicitudVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.CalificacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.DocumentoVerificacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.ReputacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.ResenaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.SolicitudVerificacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.port.ReportePort;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port.ServicioLecturaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.EstadoServicio;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Calificaciones, reseñas, reputación y verificación (SPEC-10, UC059–UC064).
 * Reputación = media aritmética de las calificaciones (decisión documentada: la fórmula
 * no estaba definida en los SPEC). Nivel de verificación derivado de solicitudes APROBADAS:
 * 0 = SIN_VERIFICAR, >=1 = BASICO; AMPLIADO queda reservado para decisión de producto.
 * Ningún nivel se presenta como certificación legal (FR-062).
 */
@Service
public class ConfianzaService implements ConfianzaConsultaPort, VerificacionAdminPort {
    private final CalificacionRepository calificaciones;
    private final ResenaRepository resenas;
    private final ReputacionRepository reputaciones;
    private final SolicitudVerificacionRepository verificaciones;
    private final DocumentoVerificacionRepository documentos;
    private final ServicioLecturaPort servicios;
    private final ArchivoStorageService storage;
    private final ReportePort reportes;

    public ConfianzaService(CalificacionRepository calificaciones, ResenaRepository resenas,
                            ReputacionRepository reputaciones,
                            SolicitudVerificacionRepository verificaciones,
                            DocumentoVerificacionRepository documentos, ServicioLecturaPort servicios,
                            ArchivoStorageService storage, ReportePort reportes) {
        this.calificaciones = calificaciones;
        this.resenas = resenas;
        this.reputaciones = reputaciones;
        this.verificaciones = verificaciones;
        this.documentos = documentos;
        this.servicios = servicios;
        this.storage = storage;
        this.reportes = reportes;
    }

    @Transactional
    public Calificacion calificar(UUID servicioId, UUID clienteId, int valor) {
        if (valor < 1 || valor > 5) {
            throw new BusinessException.BadRequest("VALOR_INVALIDO", "La calificación debe estar entre 1 y 5.");
        }
        var servicio = servicios.porId(servicioId)
                .orElseThrow(() -> new BusinessException.NotFound("SERVICIO_NO_ENCONTRADO",
                        "Servicio no encontrado."));
        if (!servicio.clienteId().equals(clienteId)) {
            throw new BusinessException.Forbidden("NO_AUTORIZADO",
                    "Solo el cliente del servicio puede calificarlo.");
        }
        if (servicio.estado() != EstadoServicio.FINALIZADO) {
            throw new BusinessException.Conflict("SERVICIO_NO_FINALIZADO",
                    "Solo se puede calificar un servicio finalizado.");
        }
        if (calificaciones.porServicioId(servicioId).isPresent()) {
            throw new BusinessException.Conflict("YA_CALIFICADO",
                    "El servicio ya fue calificado (máximo una calificación por servicio).");
        }
        Calificacion calificacion = new Calificacion(UUID.randomUUID(), servicioId, clienteId,
                servicio.prestadorId(), valor, OffsetDateTime.now());
        Calificacion guardada = calificaciones.guardar(calificacion);
        recalcularReputacion(servicio.prestadorId());
        return guardada;
    }

    private void recalcularReputacion(UUID prestadorId) {
        List<Calificacion> todas = calificaciones.porPrestadorId(prestadorId);
        BigDecimal promedio = todas.isEmpty() ? BigDecimal.ZERO
                : BigDecimal.valueOf(todas.stream().mapToInt(Calificacion::valor).average().orElse(0))
                        .setScale(2, RoundingMode.HALF_UP);
        reputaciones.guardar(new Reputacion(prestadorId, promedio, todas.size(), OffsetDateTime.now()));
    }

    @Transactional
    public Resena agregarResena(UUID calificacionId, UUID clienteId, String texto) {
        Calificacion calificacion = calificaciones.porId(calificacionId)
                .orElseThrow(() -> new BusinessException.NotFound("CALIFICACION_NO_ENCONTRADA",
                        "Calificación no encontrada."));
        if (!calificacion.clienteId().equals(clienteId)) {
            throw new BusinessException.Forbidden("NO_AUTORIZADO",
                    "Solo el autor de la calificación puede agregar la reseña.");
        }
        if (texto == null || texto.isBlank()) {
            throw new BusinessException.BadRequest("TEXTO_REQUERIDO", "El texto de la reseña es obligatorio.");
        }
        if (resenas.porCalificacionId(calificacionId).isPresent()) {
            throw new BusinessException.Conflict("RESENA_YA_EXISTE",
                    "La calificación ya tiene una reseña (máximo una por calificación).");
        }
        return resenas.guardar(new Resena(UUID.randomUUID(), calificacionId, texto.trim(),
                OffsetDateTime.now(), EstadoVisibilidadResena.VISIBLE));
    }

    @Override
    @Transactional(readOnly = true)
    public ConfianzaInfo confianzaDe(UUID prestadorId) {
        Reputacion reputacion = reputaciones.porPrestadorId(prestadorId).orElse(null);
        String nivel = verificaciones.contarAprobadasDePrestador(prestadorId) > 0 ? "BASICO" : "SIN_VERIFICAR";
        return new ConfianzaInfo(
                new ReputacionInfo(reputacion == null ? BigDecimal.ZERO : reputacion.valorAgregado(),
                        reputacion == null ? 0 : reputacion.cantidadCalificaciones()),
                nivel);
    }

    @Transactional
    public SolicitudVerificacion solicitarVerificacion(UUID prestadorId, List<ArchivoSubido> archivos) {
        if (verificaciones.pendienteDePrestador(prestadorId).isPresent()) {
            throw new BusinessException.Conflict("VERIFICACION_PENDIENTE",
                    "Ya tienes una solicitud de verificación pendiente.");
        }
        if (archivos == null || archivos.isEmpty()) {
            throw new BusinessException.BadRequest("DOCUMENTO_REQUERIDO",
                    "La verificación requiere al menos un documento.");
        }
        if (archivos.size() > 3) {
            throw new BusinessException.BadRequest("LIMITE_DOCUMENTOS",
                    "La verificación admite como máximo 3 documentos.");
        }
        OffsetDateTime ahora = OffsetDateTime.now();
        SolicitudVerificacion solicitud = verificaciones.guardar(new SolicitudVerificacion(
                UUID.randomUUID(), prestadorId, EstadoVerificacion.PENDIENTE, ahora, null));
        for (ArchivoSubido archivo : archivos) {
            ArchivoValidacion.validar(archivo.nombreOriginal(), archivo.tipoContenido(), archivo.contenido(),
                    ArchivoValidacion.DOCUMENTOS);
            String clave = storage.guardar(archivo.nombreOriginal(), archivo.tipoContenido(), archivo.contenido());
            documentos.guardar(new DocumentoVerificacion(UUID.randomUUID(), solicitud.id(), clave, ahora));
        }
        return solicitud;
    }

    public record ArchivoSubido(String nombreOriginal, String tipoContenido, byte[] contenido) {}

    @Transactional(readOnly = true)
    public SolicitudVerificacion miVerificacion(UUID prestadorId) {
        return verificaciones.ultimaDePrestador(prestadorId)
                .orElseThrow(() -> new BusinessException.NotFound("SIN_VERIFICACION",
                        "No tienes solicitudes de verificación."));
    }

    @Override
    @Transactional
    public void resolver(UUID solicitudId, String estado, UUID administradorId) {
        EstadoVerificacion nuevo;
        try {
            nuevo = EstadoVerificacion.valueOf(estado);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException.BadRequest("ESTADO_INVALIDO", "Estado inválido: use APROBADA o RECHAZADA.");
        }
        if (nuevo == EstadoVerificacion.PENDIENTE) {
            throw new BusinessException.BadRequest("ESTADO_INVALIDO", "No se puede resolver como PENDIENTE.");
        }
        SolicitudVerificacion solicitud = verificaciones.porId(solicitudId)
                .orElseThrow(() -> new BusinessException.NotFound("VERIFICACION_NO_ENCONTRADA",
                        "Solicitud de verificación no encontrada."));
        if (solicitud.estado() != EstadoVerificacion.PENDIENTE) {
            throw new BusinessException.Conflict("VERIFICACION_YA_RESUELTA",
                    "La solicitud ya fue resuelta.");
        }
        verificaciones.guardar(new SolicitudVerificacion(solicitud.id(), solicitud.prestadorId(), nuevo,
                solicitud.fechaSolicitud(), OffsetDateTime.now()));
    }

    @Transactional
    public UUID reportarResena(UUID resenaId, UUID reportanteId, String motivo) {
        resenas.porId(resenaId)
                .orElseThrow(() -> new BusinessException.NotFound("RESENA_NO_ENCONTRADA",
                        "Reseña no encontrada."));
        return reportes.crearReporteResena(resenaId, reportanteId, motivo);
    }

    @Transactional
    public void ocultarResena(UUID resenaId) {
        Resena resena = resenas.porId(resenaId)
                .orElseThrow(() -> new BusinessException.NotFound("RESENA_NO_ENCONTRADA",
                        "Reseña no encontrada."));
        resenas.guardar(new Resena(resena.id(), resena.calificacionId(), resena.texto(),
                resena.fechaCreacion(), EstadoVisibilidadResena.OCULTA));
    }
}
