package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "pasos_formalizacion")
public class PasoEntity {
    @Id
    private UUID id;
    @Column(name = "ruta_id", nullable = false)
    private UUID rutaId;
    @Column(nullable = false)
    private String titulo;
    private String descripcion;
    @Column(nullable = false)
    private int orden;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRutaId() { return rutaId; }
    public void setRutaId(UUID rutaId) { this.rutaId = rutaId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}
