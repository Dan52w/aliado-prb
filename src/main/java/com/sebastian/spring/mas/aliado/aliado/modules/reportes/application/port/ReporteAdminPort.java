package com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.Reporte;
import java.util.List;
import java.util.UUID;

/** Contrato público de reportes para la administración (SPEC-12). */
public interface ReporteAdminPort {
    List<Reporte> cola(String estado);
    Reporte marcarRevisionDetalle(UUID reporteId, UUID administradorId);
    Reporte resolver(UUID reporteId, UUID administradorId, String resultado, String justificacion);
}
