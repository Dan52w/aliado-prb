package com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitudes_eliminacion")
public class SolicitudEliminacionEntity {
    @Id
    private UUID id;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Column(name = "fecha_solicitud", nullable = false)
    private OffsetDateTime fechaSolicitud;
    @Column(name = "fecha_expiracion", nullable = false)
    private OffsetDateTime fechaExpiracion;
    @Column(nullable = false)
    private String estado;
    @Column(name = "fecha_confirmacion")
    private OffsetDateTime fechaConfirmacion;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public OffsetDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(OffsetDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public OffsetDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(OffsetDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getFechaConfirmacion() { return fechaConfirmacion; }
    public void setFechaConfirmacion(OffsetDateTime fechaConfirmacion) { this.fechaConfirmacion = fechaConfirmacion; }
}
