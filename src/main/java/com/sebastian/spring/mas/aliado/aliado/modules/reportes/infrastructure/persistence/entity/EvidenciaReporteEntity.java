package com.sebastian.spring.mas.aliado.aliado.modules.reportes.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "evidencias_reporte")
public class EvidenciaReporteEntity {
    @Id
    private UUID id;
    @Column(name = "reporte_id", nullable = false)
    private UUID reporteId;
    @Column(name = "archivo_key", nullable = false)
    private String archivoKey;
    @Column(name = "tipo_contenido", nullable = false)
    private String tipoContenido;
    @Column(name = "fecha_carga", nullable = false)
    private OffsetDateTime fechaCarga;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getReporteId() { return reporteId; }
    public void setReporteId(UUID reporteId) { this.reporteId = reporteId; }
    public String getArchivoKey() { return archivoKey; }
    public void setArchivoKey(String archivoKey) { this.archivoKey = archivoKey; }
    public String getTipoContenido() { return tipoContenido; }
    public void setTipoContenido(String tipoContenido) { this.tipoContenido = tipoContenido; }
    public OffsetDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(OffsetDateTime fechaCarga) { this.fechaCarga = fechaCarga; }
}
