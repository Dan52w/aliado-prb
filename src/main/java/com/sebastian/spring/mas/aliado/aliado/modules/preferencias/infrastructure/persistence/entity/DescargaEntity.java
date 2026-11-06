package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "descargas_historial")
public class DescargaEntity {
    @Id
    private UUID id;
    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;
    @Column(name = "fecha_generacion", nullable = false)
    private OffsetDateTime fechaGeneracion;
    @Column(name = "archivo_key", nullable = false)
    private String archivoKey;
    @Column(nullable = false)
    private String estado;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public OffsetDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(OffsetDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public String getArchivoKey() { return archivoKey; }
    public void setArchivoKey(String archivoKey) { this.archivoKey = archivoKey; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
