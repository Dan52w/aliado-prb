package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "resenas")
public class ResenaEntity {
    @Id
    private UUID id;
    @Column(name = "calificacion_id", nullable = false)
    private UUID calificacionId;
    @Column(nullable = false)
    private String texto;
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
    @Column(name = "estado_visibilidad", nullable = false)
    private String estadoVisibilidad;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCalificacionId() { return calificacionId; }
    public void setCalificacionId(UUID calificacionId) { this.calificacionId = calificacionId; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getEstadoVisibilidad() { return estadoVisibilidad; }
    public void setEstadoVisibilidad(String estadoVisibilidad) { this.estadoVisibilidad = estadoVisibilidad; }
}
