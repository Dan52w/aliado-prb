package com.sebastian.spring.mas.aliado.aliado.modules.reportes.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.EstadoReporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.EvidenciaReporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.Reporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.domain.model.TipoReporte;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.infrastructure.persistence.entity.EvidenciaReporteEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.reportes.infrastructure.persistence.entity.ReporteEntity;

public final class ReportePersistenceMapper {
    private ReportePersistenceMapper() {}

    public static Reporte aDominio(ReporteEntity e) {
        return new Reporte(e.getId(), TipoReporte.valueOf(e.getTipo()), e.getReportanteId(), e.getResenaId(),
                e.getUsuarioObjetivoId(), e.getServicioId(), e.getMotivo(), EstadoReporte.valueOf(e.getEstado()),
                e.getDetalleRevisadoEn(), e.getJustificacionResolucion(), e.getRevisadoPor(),
                e.getFechaCreacion(), e.getFechaResolucion(), e.getVersion());
    }

    public static ReporteEntity aEntidad(Reporte r) {
        ReporteEntity e = new ReporteEntity();
        e.setId(r.id());
        e.setTipo(r.tipo().name());
        e.setReportanteId(r.reportanteId());
        e.setResenaId(r.resenaId());
        e.setUsuarioObjetivoId(r.usuarioObjetivoId());
        e.setServicioId(r.servicioId());
        e.setMotivo(r.motivo());
        e.setEstado(r.estado().name());
        e.setDetalleRevisadoEn(r.detalleRevisadoEn());
        e.setJustificacionResolucion(r.justificacionResolucion());
        e.setRevisadoPor(r.revisadoPor());
        e.setFechaCreacion(r.fechaCreacion());
        e.setFechaResolucion(r.fechaResolucion());
        e.setVersion(r.version());
        return e;
    }

    public static EvidenciaReporte aDominio(EvidenciaReporteEntity e) {
        return new EvidenciaReporte(e.getId(), e.getReporteId(), e.getArchivoKey(), e.getTipoContenido(),
                e.getFechaCarga());
    }

    public static EvidenciaReporteEntity aEntidad(EvidenciaReporte v) {
        EvidenciaReporteEntity e = new EvidenciaReporteEntity();
        e.setId(v.id());
        e.setReporteId(v.reporteId());
        e.setArchivoKey(v.archivoKey());
        e.setTipoContenido(v.tipoContenido());
        e.setFechaCarga(v.fechaCarga());
        return e;
    }
}
