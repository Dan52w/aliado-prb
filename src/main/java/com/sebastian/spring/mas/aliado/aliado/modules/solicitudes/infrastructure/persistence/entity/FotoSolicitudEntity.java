package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "fotos_solicitud")
public class FotoSolicitudEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudEntity solicitud;
    @Column(name = "archivo_key", nullable = false)
    private String archivoKey;
    @Column(nullable = false)
    private int orden;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SolicitudEntity getSolicitud() { return solicitud; }
    public void setSolicitud(SolicitudEntity solicitud) { this.solicitud = solicitud; }
    public String getArchivoKey() { return archivoKey; }
    public void setArchivoKey(String archivoKey) { this.archivoKey = archivoKey; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}
