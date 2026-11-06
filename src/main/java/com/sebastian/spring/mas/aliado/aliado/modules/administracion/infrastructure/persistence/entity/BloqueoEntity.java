package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bloqueos_preventivos")
public class BloqueoEntity {
    @Id
    private UUID id;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Column(name = "administrador_id", nullable = false)
    private UUID administradorId;
    @Column(nullable = false)
    private String motivo;
    @Column(name = "fecha_inicio", nullable = false)
    private OffsetDateTime fechaInicio;
    @Column(name = "fecha_fin")
    private OffsetDateTime fechaFin;
    @Column(nullable = false)
    private boolean activo;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public UUID getAdministradorId() { return administradorId; }
    public void setAdministradorId(UUID administradorId) { this.administradorId = administradorId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public OffsetDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(OffsetDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public OffsetDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(OffsetDateTime fechaFin) { this.fechaFin = fechaFin; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
