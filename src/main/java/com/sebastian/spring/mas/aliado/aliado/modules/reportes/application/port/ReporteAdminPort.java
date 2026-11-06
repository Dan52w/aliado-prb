package com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.reportes.application.EvidenciaContenido;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.EvidenciaReporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.Reporte;
import java.util.List;
import java.util.UUID;

/** Contrato público de reportes para la administración (SPEC-12). */
public interface ReporteAdminPort {
    List<Reporte> cola(String estado);
    Reporte marcarRevisionDetalle(UUID reporteId, UUID administradorId);
    Reporte resolver(UUID reporteId, UUID administradorId, String resultado, String justificacion);
    /** Evidencias de un reporte, para que el moderador las vea (SPEC-11). */
    List<EvidenciaReporte> evidenciasDe(UUID reporteId);
    /** Contenido de una evidencia, para descarga del moderador (SPEC-11). */
    EvidenciaContenido descargarEvidencia(UUID reporteId, UUID evidenciaId);
}
