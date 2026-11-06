package com.sebastian.spring.mas.aliado.aliado.modules.administracion.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.BloqueoPreventivo;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.BloqueoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.CuentaBloqueoPort;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.UsuarioConsultaPort;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bloqueo preventivo de cuentas (SPEC-12, UC071–UC072).
 * La moderación de contenido es independiente del bloqueo de cuenta.
 */
@Service
public class BloqueoCuentaService {
    private final BloqueoRepository bloqueos;
    private final UsuarioConsultaPort usuarios;
    private final CuentaBloqueoPort cuentas;
    private final AuditoriaService auditoriaService;

    public BloqueoCuentaService(BloqueoRepository bloqueos, UsuarioConsultaPort usuarios,
                                CuentaBloqueoPort cuentas, AuditoriaService auditoriaService) {
        this.bloqueos = bloqueos;
        this.usuarios = usuarios;
        this.cuentas = cuentas;
        this.auditoriaService = auditoriaService;
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
        cuentas.bloquearCuenta(usuarioId);
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
        cuentas.desbloquearCuenta(usuarioId);
        auditoriaService.registrar(administradorId, "ACCOUNT_UNBLOCKED", "USUARIO", usuarioId, null);
    }
}
