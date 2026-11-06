package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.application.port;

import java.util.UUID;

/**
 * Lectura de preferencias de notificación (SPEC-14, plan F14-T004).
 * Notificaciones la consulta antes de registrar y antes de entregar, para no
 * avisar por un canal que el usuario desactivó. Los nombres se pasan como texto
 * para no acoplar los enums de ambos módulos: TipoNotificacion y TipoAviso
 * comparten los 4 nombres de aviso, más AVISO_SEGURIDAD_CUENTA.
 */
public interface ConsultaPreferenciasPort {
    /** true si el aviso está habilitado para ese usuario y canal (o por defecto). */
    boolean avisoHabilitado(UUID usuarioId, String tipoAviso, String canal);
}
