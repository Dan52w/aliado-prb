package com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port;

import java.util.UUID;

/** Contrato público de servicios para otros módulos (SPEC-02/SPEC-08). */
public interface ServicioConsultaPort {
    /** True si el usuario participa en un servicio no finalizado ni cancelado. */
    boolean tieneServicioActivo(UUID usuarioId);
}
