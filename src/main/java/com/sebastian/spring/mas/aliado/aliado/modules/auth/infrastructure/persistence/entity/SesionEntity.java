package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "sesiones")
public class SesionEntity {
    @Id
    private UUID id;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;
    @Column(name = "fecha_inicio", nullable = false)
    private OffsetDateTime fechaInicio;
    @Column(name = "fecha_expiracion", nullable = false)
    private OffsetDateTime fechaExpiracion;
    @Column(name = "fecha_cierre")
    private OffsetDateTime fechaCierre;
    @Column(nullable = false)
    private String estado;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }
    public OffsetDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(OffsetDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
    public OffsetDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(OffsetDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    public OffsetDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(OffsetDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
