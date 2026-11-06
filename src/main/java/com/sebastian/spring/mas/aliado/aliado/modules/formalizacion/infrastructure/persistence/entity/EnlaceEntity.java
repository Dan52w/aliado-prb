package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "enlaces_formalizacion")
public class EnlaceEntity {
    @Id
    private UUID id;
    @Column(name = "paso_id", nullable = false)
    private UUID pasoId;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String estado;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPasoId() { return pasoId; }
    public void setPasoId(UUID pasoId) { this.pasoId = pasoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
