package com.sebastian.spring.mas.aliado.aliado.modules.administracion.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.AccionModeracion;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.TipoAccionModeracion;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.TipoObjetivoModeracion;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.AccionModeracionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.ResenaModeracionPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.VerificacionAdminPort;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Decisiones de moderación del administrador (SPEC-12): acciones sobre contenido
 * y resolución de solicitudes de verificación. Moderar contenido no implica bloquear
 * la cuenta; toda mutación genera auditoría inmutable.
 */
@Service
public class ModeracionService {
    private final AccionModeracionRepository acciones;
    private final ResenaModeracionPort resenas;
    private final VerificacionAdminPort verificaciones;
    private final AuditoriaService auditoriaService;

    public ModeracionService(AccionModeracionRepository acciones, ResenaModeracionPort resenas,
                             VerificacionAdminPort verificaciones, AuditoriaService auditoriaService) {
        this.acciones = acciones;
        this.resenas = resenas;
        this.verificaciones = verificaciones;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public void moderar(UUID administradorId, String objetivoTipo, UUID objetivoId, String accion, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new BusinessException.BadRequest("MOTIVO_REQUERIDO", "El motivo de la moderación es obligatorio.");
        }
        TipoAccionModeracion tipoAccion;
        try {
            tipoAccion = TipoAccionModeracion.valueOf(accion);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BusinessException.BadRequest("ACCION_INVALIDA", "Acción de moderación inválida.");
        }
        TipoObjetivoModeracion tipoObjetivo;
        try {
            tipoObjetivo = TipoObjetivoModeracion.valueOf(objetivoTipo);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BusinessException.BadRequest("OBJETIVO_INVALIDO", "Objetivo de moderación inválido.");
        }
        acciones.guardar(new AccionModeracion(UUID.randomUUID(), administradorId, tipoAccion, tipoObjetivo,
                objetivoId, motivo.trim(), OffsetDateTime.now()));
        if (tipoAccion == TipoAccionModeracion.OCULTAR_CONTENIDO
                && tipoObjetivo == TipoObjetivoModeracion.RESENA) {
            resenas.ocultarResena(objetivoId);
        }
        auditoriaService.registrar(administradorId, "CONTENT_MODERATED", tipoObjetivo.name(), objetivoId,
                tipoAccion.name());
    }

    @Transactional
    public void resolverVerificacion(UUID solicitudId, UUID administradorId, String estado) {
        verificaciones.resolver(solicitudId, estado, administradorId);
        auditoriaService.registrar(administradorId, "VERIFICATION_RESOLVED", "VERIFICACION", solicitudId, estado);
    }
}
