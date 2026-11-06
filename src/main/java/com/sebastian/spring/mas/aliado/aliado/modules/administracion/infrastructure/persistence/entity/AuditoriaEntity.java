package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "auditoria_administrativa")
public class AuditoriaEntity {
    @Id
    private UUID id;
    @Column(name = "administrador_id", nullable = false)
    private UUID administradorId;
    @Column(nullable = false)
    private String accion;
    @Column(name = "objetivo_tipo", nullable = false)
    private String objetivoTipo;
    @Column(name = "objetivo_id", nullable = false)
    private UUID objetivoId;
    private String detalle;
    @Column(nullable = false)
    private OffsetDateTime fecha;
    @Column(name = "trace_id", nullable = false)
    private String traceId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAdministradorId() { return administradorId; }
    public void setAdministradorId(UUID administradorId) { this.administradorId = administradorId; }
    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public String getObjetivoTipo() { return objetivoTipo; }
    public void setObjetivoTipo(String objetivoTipo) { this.objetivoTipo = objetivoTipo; }
    public UUID getObjetivoId() { return objetivoId; }
    public void setObjetivoId(UUID objetivoId) { this.objetivoId = objetivoId; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
}
