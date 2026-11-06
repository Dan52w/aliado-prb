package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "consentimientos")
public class ConsentimientoEntity {
    @Id
    private UUID id;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Column(name = "version_terminos", nullable = false)
    private String versionTerminos;
    @Column(name = "fecha_aceptacion", nullable = false)
    private OffsetDateTime fechaAceptacion;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getVersionTerminos() { return versionTerminos; }
    public void setVersionTerminos(String versionTerminos) { this.versionTerminos = versionTerminos; }
    public OffsetDateTime getFechaAceptacion() { return fechaAceptacion; }
    public void setFechaAceptacion(OffsetDateTime fechaAceptacion) { this.fechaAceptacion = fechaAceptacion; }
}
