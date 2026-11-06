package com.sebastian.spring.mas.aliado.aliado.common.auditoria;

import java.util.UUID;

/**
 * Registro de auditoría administrativa. La implementa AuditoriaService; la usan
 * los módulos que deben dejar rastro de sus mutaciones (p. ej. el catálogo).
 */
public interface AuditoriaPort {
    void registrar(UUID administradorId, String accion, String objetivoTipo, UUID objetivoId, String detalle);
}
