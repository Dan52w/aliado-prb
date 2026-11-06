package com.sebastian.spring.mas.aliado.aliado.modules.administracion.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.Auditoria;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.BloqueoPreventivo;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.MarcaAltoRiesgo;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.AccionModeracionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.AuditoriaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.BloqueoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.MarcaRiesgoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.AuthService;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.UsuarioConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.ConfianzaService;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.VerificacionAdminPort;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.port.ReporteAdminPort;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.Reporte;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port.ServicioLecturaPort;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Administración, moderación y riesgos (SPEC-12, UC068–UC076).
 * Resolver exige haber revisado el detalle y justificar (SPEC-12).
 * La moderación de contenido es independiente del bloqueo de cuenta.
 */
@Service
public class AdministracionService {
    private final ReporteAdminPort reportes;
    private final BloqueoRepository bloqueos;
    private final AccionModeracionRepository acciones;
    private final MarcaRiesgoRepository marcas;
    private final AuditoriaRepository auditoria;
    private final AuditoriaService auditoriaService;
    private final AuthService authService;
    private final UsuarioConsultaPort usuarios;
    private final ServicioLecturaPort servicios;
    private final ConfianzaService confianza;
    private final VerificacionAdminPort verificaciones;

    public AdministracionService(ReporteAdminPort reportes, BloqueoRepository bloqueos,
                                 AccionModeracionRepository acciones, MarcaRiesgoRepository marcas,
                                 AuditoriaRepository auditoria, AuditoriaService auditoriaService,
                                 AuthService authService, UsuarioConsultaPort usuarios,
                                 ServicioLecturaPort servicios, ConfianzaService confianza,
                                 VerificacionAdminPort verificaciones) {
        this.reportes = reportes;
        this.bloqueos = bloqueos;
        this.acciones = acciones;
        this.marcas = marcas;
        this.auditoria = auditoria;
        this.auditoriaService = auditoriaService;
        this.authService = authService;
        this.usuarios = usuarios;
        this.servicios = servicios;
        this.confianza = confianza;
        this.verificaciones = verificaciones;
    }

    @Transactional(readOnly = true)
    public List<Reporte> colaReportes(String estado) {
        return reportes.cola(estado == null ? "PENDIENTE" : estado);
    }

    @Transactional
    public Reporte detalleReporte(UUID reporteId, UUID administradorId) {
        Reporte reporte = reportes.marcarRevisionDetalle(reporteId, administradorId);
        auditoriaService.registrar(administradorId, "REPORT_REVIEWED", "REPORTE", reporteId, null);
        return reporte;
    }

    @Transactional
    public Reporte resolverReporte(UUID reporteId, UUID administradorId, String resultado, String justificacion) {
        Reporte resuelto = reportes.resolver(reporteId, administradorId, resultado, justificacion);
        auditoriaService.registrar(administradorId, "REPORT_RESOLVED", "REPORTE", reporteId, resultado);
        return resuelto;
    }

    @Transactional
    public BloqueoPreventivo bloquearUsuario(UUID usuarioId, UUID administradorId, String motivo) {
        if (!usuarios.existe(usuarioId)) {
            throw new BusinessException.NotFound("USUARIO_NO_ENCONTRADO", "Usuario no encontrado.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new BusinessException.BadRequest("MOTIVO_REQUERIDO", "El motivo del bloqueo es obligatorio.");
        }
        if (bloqueos.activoDeUsuario(usuarioId).isPresent()) {
            throw new BusinessException.Conflict("BLOQUEO_ACTIVO", "El usuario ya tiene un bloqueo activo.");
        }
        BloqueoPreventivo bloqueo = bloqueos.guardar(new BloqueoPreventivo(UUID.randomUUID(), usuarioId,
                administradorId, motivo.trim(), OffsetDateTime.now(), null, true));
        authService.bloquearCuenta(usuarioId);
        auditoriaService.registrar(administradorId, "ACCOUNT_BLOCKED", "USUARIO", usuarioId, motivo.trim());
        return bloqueo;
    }

    @Transactional
    public void levantarBloqueo(UUID usuarioId, UUID bloqueoId, UUID administradorId) {
        BloqueoPreventivo bloqueo = bloqueos.porId(bloqueoId)
                .filter(b -> b.usuarioId().equals(usuarioId) && b.activo())
                .orElseThrow(() -> new BusinessException.NotFound("BLOQUEO_NO_ENCONTRADO",
                        "Bloqueo activo no encontrado."));
        bloqueos.guardar(new BloqueoPreventivo(bloqueo.id(), usuarioId, bloqueo.administradorId(),
                bloqueo.motivo(), bloqueo.fechaInicio(), OffsetDateTime.now(), false));
        authService.desbloquearCuenta(usuarioId);
        auditoriaService.registrar(administradorId, "ACCOUNT_UNBLOCKED", "USUARIO", usuarioId, null);
    }

    @Transactional
    public void moderar(UUID administradorId, String objetivoTipo, UUID objetivoId, String accion, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new BusinessException.BadRequest("MOTIVO_REQUERIDO", "El motivo de la moderación es obligatorio.");
        }
        acciones.guardar(new com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.AccionModeracion(
                UUID.randomUUID(), administradorId, accion, objetivoTipo, objetivoId, motivo.trim(),
                OffsetDateTime.now()));
        if ("OCULTAR_CONTENIDO".equals(accion) && "RESENA".equals(objetivoTipo)) {
            confianza.ocultarResena(objetivoId);
        }
        auditoriaService.registrar(administradorId, "CONTENT_MODERATED", objetivoTipo, objetivoId, accion);
    }

    @Transactional
    public MarcaAltoRiesgo marcarAltoRiesgo(UUID servicioId, UUID administradorId, String criterio) {
        if (!servicios.porId(servicioId).isPresent()) {
            throw new BusinessException.NotFound("SERVICIO_NO_ENCONTRADO", "Servicio no encontrado.");
        }
        if (criterio == null || criterio.isBlank()) {
            throw new BusinessException.BadRequest("CRITERIO_REQUERIDO", "El criterio es obligatorio.");
        }
        if (marcas.activaDeServicio(servicioId).isPresent()) {
            throw new BusinessException.Conflict("MARCA_ACTIVA", "El servicio ya tiene una marca activa.");
        }
        MarcaAltoRiesgo marca = marcas.guardar(new MarcaAltoRiesgo(UUID.randomUUID(), servicioId,
                criterio.trim(), administradorId, OffsetDateTime.now(), true));
        auditoriaService.registrar(administradorId, "HIGH_RISK_MARKED", "SERVICIO", servicioId, criterio.trim());
        return marca;
    }

    @Transactional(readOnly = true)
    public List<Auditoria> listarAuditoria(int limite) {
        return auditoria.recientes(Math.max(1, Math.min(limite, 100)));
    }

    @Transactional
    public void resolverVerificacion(UUID solicitudId, UUID administradorId, String estado) {
        verificaciones.resolver(solicitudId, estado, administradorId);
        auditoriaService.registrar(administradorId, "VERIFICATION_RESOLVED", "VERIFICACION", solicitudId, estado);
    }
}
