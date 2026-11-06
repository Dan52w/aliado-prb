package com.sebastian.spring.mas.aliado.aliado.modules.reportes.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reportes")
public class ReporteEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String tipo;
    @Column(name = "reportante_id", nullable = false)
    private UUID reportanteId;
    @Column(name = "resena_id")
    private UUID resenaId;
    @Column(name = "usuario_objetivo_id")
    private UUID usuarioObjetivoId;
    @Column(name = "servicio_id")
    private UUID servicioId;
    @Column(nullable = false)
    private String motivo;
    @Column(nullable = false)
    private String estado;
    @Column(name = "detalle_revisado_en")
    private OffsetDateTime detalleRevisadoEn;
    @Column(name = "justificacion_resolucion")
    private String justificacionResolucion;
    @Column(name = "revisado_por")
    private UUID revisadoPor;
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
    @Column(name = "fecha_resolucion")
    private OffsetDateTime fechaResolucion;
    @Version
    private int version;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public UUID getReportanteId() { return reportanteId; }
    public void setReportanteId(UUID reportanteId) { this.reportanteId = reportanteId; }
    public UUID getResenaId() { return resenaId; }
    public void setResenaId(UUID resenaId) { this.resenaId = resenaId; }
    public UUID getUsuarioObjetivoId() { return usuarioObjetivoId; }
    public void setUsuarioObjetivoId(UUID usuarioObjetivoId) { this.usuarioObjetivoId = usuarioObjetivoId; }
    public UUID getServicioId() { return servicioId; }
    public void setServicioId(UUID servicioId) { this.servicioId = servicioId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getDetalleRevisadoEn() { return detalleRevisadoEn; }
    public void setDetalleRevisadoEn(OffsetDateTime detalleRevisadoEn) { this.detalleRevisadoEn = detalleRevisadoEn; }
    public String getJustificacionResolucion() { return justificacionResolucion; }
    public void setJustificacionResolucion(String justificacionResolucion) { this.justificacionResolucion = justificacionResolucion; }
    public UUID getRevisadoPor() { return revisadoPor; }
    public void setRevisadoPor(UUID revisadoPor) { this.revisadoPor = revisadoPor; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public OffsetDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(OffsetDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
