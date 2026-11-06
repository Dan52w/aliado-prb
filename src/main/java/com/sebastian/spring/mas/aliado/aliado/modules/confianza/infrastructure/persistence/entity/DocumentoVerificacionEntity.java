package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "documentos_verificacion")
public class DocumentoVerificacionEntity {
    @Id
    private UUID id;
    @Column(name = "solicitud_verificacion_id", nullable = false)
    private UUID solicitudVerificacionId;
    @Column(name = "archivo_key", nullable = false)
    private String archivoKey;
    @Column(name = "fecha_carga", nullable = false)
    private OffsetDateTime fechaCarga;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSolicitudVerificacionId() { return solicitudVerificacionId; }
    public void setSolicitudVerificacionId(UUID solicitudVerificacionId) { this.solicitudVerificacionId = solicitudVerificacionId; }
    public String getArchivoKey() { return archivoKey; }
    public void setArchivoKey(String archivoKey) { this.archivoKey = archivoKey; }
    public OffsetDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(OffsetDateTime fechaCarga) { this.fechaCarga = fechaCarga; }
}
