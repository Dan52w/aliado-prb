package com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.EvidenciaReporte;
import java.util.List;
import java.util.UUID;

public interface EvidenciaReporteRepository {
    List<EvidenciaReporte> porReporte(UUID reporteId);
    EvidenciaReporte guardar(EvidenciaReporte evidencia);
}
