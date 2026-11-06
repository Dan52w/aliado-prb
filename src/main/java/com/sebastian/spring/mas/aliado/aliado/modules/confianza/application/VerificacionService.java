package com.sebastian.spring.mas.aliado.aliado.modules.confianza.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoStorageService;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoValidacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.VerificacionAdminPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.DocumentoVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.EstadoVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.SolicitudVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.DocumentoVerificacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.SolicitudVerificacionRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Solicitudes de verificación de identidad (SPEC-10, UC063–UC064).
 * Ningún nivel se presenta como certificación legal (FR-062).
 */
@Service
public class VerificacionService implements VerificacionAdminPort {
    private final SolicitudVerificacionRepository verificaciones;
    private final DocumentoVerificacionRepository documentos;
    private final ArchivoStorageService storage;

    public VerificacionService(SolicitudVerificacionRepository verificaciones,
                               DocumentoVerificacionRepository documentos, ArchivoStorageService storage) {
        this.verificaciones = verificaciones;
        this.documentos = documentos;
        this.storage = storage;
    }

    public record ArchivoSubido(String nombreOriginal, String tipoContenido, byte[] contenido) {}

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
}
