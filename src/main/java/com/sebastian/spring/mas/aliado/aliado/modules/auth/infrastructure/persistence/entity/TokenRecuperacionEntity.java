package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens_recuperacion")
public class TokenRecuperacionEntity {
    @Id
    private UUID id;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Column(name = "codigo_hash", nullable = false)
    private String codigoHash;
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
    @Column(name = "fecha_expiracion", nullable = false)
    private OffsetDateTime fechaExpiracion;
    @Column(nullable = false)
    private boolean usado;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getCodigoHash() { return codigoHash; }
    public void setCodigoHash(String codigoHash) { this.codigoHash = codigoHash; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public OffsetDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(OffsetDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    public boolean isUsado() { return usado; }
    public void setUsado(boolean usado) { this.usado = usado; }
}
