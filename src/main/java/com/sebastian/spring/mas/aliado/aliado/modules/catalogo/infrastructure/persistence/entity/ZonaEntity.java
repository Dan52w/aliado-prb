package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "zonas")
public class ZonaEntity {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String nombre;
    @Column(name = "cobertura_geografica")
    private String coberturaGeografica;
    @Column(nullable = false)
    private String estado;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCoberturaGeografica() { return coberturaGeografica; }
    public void setCoberturaGeografica(String coberturaGeografica) { this.coberturaGeografica = coberturaGeografica; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
