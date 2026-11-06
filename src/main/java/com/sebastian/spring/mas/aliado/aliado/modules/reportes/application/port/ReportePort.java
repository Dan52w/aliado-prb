package com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.port;

import java.util.UUID;

/** Contrato público de reportes para confianza (SPEC-10 reporta reseñas; SPEC-11 generaliza). */
public interface ReportePort {
    UUID crearReporteResena(UUID resenaId, UUID reportanteId, String motivo);
    UUID crearReporteUsuario(UUID usuarioObjetivoId, UUID reportanteId, String motivo);
    UUID crearReporteServicio(UUID servicioId, UUID reportanteId, String motivo);
}
