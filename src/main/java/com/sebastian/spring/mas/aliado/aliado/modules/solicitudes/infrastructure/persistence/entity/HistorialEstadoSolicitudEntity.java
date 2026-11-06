package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "historial_estado_solicitud")
public class HistorialEstadoSolicitudEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudEntity solicitud;
    @Column(name = "estado_anterior")
    private String estadoAnterior;
    @Column(name = "estado_nuevo", nullable = false)
    private String estadoNuevo;
    @Column(nullable = false)
    private OffsetDateTime fecha;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SolicitudEntity getSolicitud() { return solicitud; }
    public void setSolicitud(SolicitudEntity solicitud) { this.solicitud = solicitud; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
}
