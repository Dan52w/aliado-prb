package com.sebastian.spring.mas.aliado.aliado.modules.reportes.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoStorageService;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoValidacion;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.UsuarioConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.ResenaConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.port.ReporteAdminPort;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.port.ReportePort;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.EstadoReporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.EvidenciaReporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.Reporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.TipoReporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.repository.EvidenciaReporteRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.repository.ReporteRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port.ServicioLecturaPort;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reportes y evidencias (SPEC-11, UC065–UC067).
 * Mismo reportante + mismo objetivo + mismo motivo + reporte abierto se consolida
 * en el existente (no se crea un duplicado). Todo reporte va a la cola de moderación.
 */
@Service
public class ReporteService implements ReportePort, ReporteAdminPort {
    private final ReporteRepository reportes;
    private final EvidenciaReporteRepository evidencias;
    private final ResenaConsultaPort resenas;
    private final UsuarioConsultaPort usuarios;
    private final ServicioLecturaPort servicios;
    private final ArchivoStorageService storage;

    public ReporteService(ReporteRepository reportes, EvidenciaReporteRepository evidencias,
                          ResenaConsultaPort resenas, UsuarioConsultaPort usuarios,
                          ServicioLecturaPort servicios, ArchivoStorageService storage) {
        this.reportes = reportes;
        this.evidencias = evidencias;
        this.resenas = resenas;
        this.usuarios = usuarios;
        this.servicios = servicios;
        this.storage = storage;
    }

    private UUID crear(TipoReporte tipo, UUID objetivoId, UUID reportanteId, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new BusinessException.BadRequest("MOTIVO_REQUERIDO", "El motivo es obligatorio.");
        }
        if (motivo.length() > 500) {
            throw new BusinessException.BadRequest("MOTIVO_MUY_LARGO", "El motivo admite máximo 500 caracteres.");
        }
        var existente = reportes.abiertoEquivalente(reportanteId, tipo.name(), objetivoId, motivo.trim());
        if (existente.isPresent()) {
            return existente.get().id();
        }
        OffsetDateTime ahora = OffsetDateTime.now();
        Reporte reporte = new Reporte(UUID.randomUUID(), tipo, reportanteId,
                tipo == TipoReporte.RESENA ? objetivoId : null,
                tipo == TipoReporte.USUARIO ? objetivoId : null,
                tipo == TipoReporte.SERVICIO ? objetivoId : null,
                motivo.trim(), EstadoReporte.PENDIENTE, null, null, null, ahora, null, 0);
        return reportes.guardar(reporte).id();
    }

    @Override
    @Transactional
    public UUID crearReporteResena(UUID resenaId, UUID reportanteId, String motivo) {
        if (!resenas.existe(resenaId)) {
            throw new BusinessException.NotFound("RESENA_NO_ENCONTRADA", "Reseña no encontrada.");
        }
        return crear(TipoReporte.RESENA, resenaId, reportanteId, motivo);
    }

    @Override
    @Transactional
    public UUID crearReporteUsuario(UUID usuarioObjetivoId, UUID reportanteId, String motivo) {
        if (!usuarios.existe(usuarioObjetivoId)) {
            throw new BusinessException.NotFound("USUARIO_NO_ENCONTRADO", "Usuario no encontrado.");
        }
        if (usuarioObjetivoId.equals(reportanteId)) {
            throw new BusinessException.BadRequest("AUTO_REPORTE", "No puedes reportarte a ti mismo.");
        }
        return crear(TipoReporte.USUARIO, usuarioObjetivoId, reportanteId, motivo);
    }

    @Override
    @Transactional
    public UUID crearReporteServicio(UUID servicioId, UUID reportanteId, String motivo) {
        var servicio = servicios.porId(servicioId)
                .orElseThrow(() -> new BusinessException.NotFound("SERVICIO_NO_ENCONTRADO",
                        "Servicio no encontrado."));
        if (!servicio.esParticipante(reportanteId)) {
            throw new BusinessException.Forbidden("NO_PARTICIPANTE",
                    "Solo un participante del servicio puede reportarlo.");
        }
        return crear(TipoReporte.SERVICIO, servicioId, reportanteId, motivo);
    }

    @Transactional
    public EvidenciaReporte agregarEvidencia(UUID reporteId, UUID reportanteId, String nombreOriginal,
                                             String tipoContenido, byte[] contenido) {
        Reporte reporte = reportes.porId(reporteId)
                .orElseThrow(() -> new BusinessException.NotFound("REPORTE_NO_ENCONTRADO",
                        "Reporte no encontrado."));
        if (!reporte.reportanteId().equals(reportanteId)) {
            throw new BusinessException.Forbidden("NO_AUTORIZADO",
                    "Solo el reportante puede adjuntar evidencia a su reporte.");
        }
        if (!reporte.abierto()) {
            throw new BusinessException.Conflict("REPORTE_CERRADO",
                    "El reporte ya está cerrado; no admite más evidencia.");
        }
        ArchivoValidacion.validar(nombreOriginal, tipoContenido, contenido, ArchivoValidacion.DOCUMENTOS);
        String clave = storage.guardar(nombreOriginal, tipoContenido, contenido);
        return evidencias.guardar(new EvidenciaReporte(UUID.randomUUID(), reporteId, clave,
                tipoContenido, OffsetDateTime.now()));
    }

    @Transactional(readOnly = true)
    public Reporte miReporte(UUID reporteId, UUID reportanteId) {
        Reporte reporte = reportes.porId(reporteId)
                .orElseThrow(() -> new BusinessException.NotFound("REPORTE_NO_ENCONTRADO",
                        "Reporte no encontrado."));
        if (!reporte.reportanteId().equals(reportanteId)) {
            throw new BusinessException.Forbidden("NO_AUTORIZADO", "El reporte no te pertenece.");
        }
        return reporte;
    }

    @Transactional(readOnly = true)
    public List<EvidenciaReporte> evidenciasDe(UUID reporteId, UUID reportanteId) {
        miReporte(reporteId, reportanteId);
        return evidencias.porReporte(reporteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> cola(String estado) {
        return reportes.cola(estado);
    }

    @Override
    @Transactional
    public Reporte marcarRevisionDetalle(UUID reporteId, UUID administradorId) {
        Reporte reporte = reportes.porId(reporteId)
                .orElseThrow(() -> new BusinessException.NotFound("REPORTE_NO_ENCONTRADO",
                        "Reporte no encontrado."));
        Reporte actualizado = new Reporte(reporte.id(), reporte.tipo(), reporte.reportanteId(),
                reporte.resenaId(), reporte.usuarioObjetivoId(), reporte.servicioId(), reporte.motivo(),
                EstadoReporte.EN_REVISION, OffsetDateTime.now(), reporte.justificacionResolucion(),
                reporte.revisadoPor(), reporte.fechaCreacion(), reporte.fechaResolucion(), reporte.version());
        return reportes.guardar(actualizado);
    }

    @Override
    @Transactional
    public Reporte resolver(UUID reporteId, UUID administradorId, String resultado, String justificacion) {
        Reporte reporte = reportes.porId(reporteId)
                .orElseThrow(() -> new BusinessException.NotFound("REPORTE_NO_ENCONTRADO",
                        "Reporte no encontrado."));
        EstadoReporte nuevo;
        try {
            nuevo = EstadoReporte.valueOf(resultado);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException.BadRequest("RESULTADO_INVALIDO",
                    "El resultado debe ser RESUELTO o DESCARTADO.");
        }
        if (nuevo != EstadoReporte.RESUELTO && nuevo != EstadoReporte.DESCARTADO) {
            throw new BusinessException.BadRequest("RESULTADO_INVALIDO",
                    "El resultado debe ser RESUELTO o DESCARTADO.");
        }
        if (reporte.detalleRevisadoEn() == null) {
            throw new BusinessException.Conflict("SIN_REVISION_PREVIA",
                    "No se puede resolver sin haber abierto antes el detalle del reporte.");
        }
        if (justificacion == null || justificacion.isBlank()) {
            throw new BusinessException.BadRequest("JUSTIFICACION_REQUERIDA",
                    "La resolución exige una justificación.");
        }
        Reporte resuelto = new Reporte(reporte.id(), reporte.tipo(), reporte.reportanteId(),
                reporte.resenaId(), reporte.usuarioObjetivoId(), reporte.servicioId(), reporte.motivo(),
                nuevo, reporte.detalleRevisadoEn(), justificacion.trim(), administradorId,
                reporte.fechaCreacion(), OffsetDateTime.now(), reporte.version());
        return reportes.guardar(resuelto);
    }
}
