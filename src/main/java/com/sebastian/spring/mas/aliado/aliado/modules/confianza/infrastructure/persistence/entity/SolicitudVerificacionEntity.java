package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "solicitudes_verificacion")
public class SolicitudVerificacionEntity {
    @Id
    private UUID id;
    @Column(name = "prestador_id", nullable = false)
    private UUID prestadorId;
    @Column(nullable = false)
    private String estado;
    @Column(name = "fecha_solicitud", nullable = false)
    private OffsetDateTime fechaSolicitud;
    @Column(name = "fecha_revision")
    private OffsetDateTime fechaRevision;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPrestadorId() { return prestadorId; }
    public void setPrestadorId(UUID prestadorId) { this.prestadorId = prestadorId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(OffsetDateTime fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public OffsetDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(OffsetDateTime fechaRevision) { this.fechaRevision = fechaRevision; }
}
