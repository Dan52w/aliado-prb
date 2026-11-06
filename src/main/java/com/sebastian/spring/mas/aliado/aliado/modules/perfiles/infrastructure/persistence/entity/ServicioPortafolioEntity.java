package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.entity;

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
@Table(name = "servicios_portafolio")
public class ServicioPortafolioEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private PerfilProfesionalEntity perfil;
    @Column(nullable = false)
    private String descripcion;
    @Column(name = "evidencia_key")
    private String evidenciaKey;
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public PerfilProfesionalEntity getPerfil() { return perfil; }
    public void setPerfil(PerfilProfesionalEntity perfil) { this.perfil = perfil; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEvidenciaKey() { return evidenciaKey; }
    public void setEvidenciaKey(String evidenciaKey) { this.evidenciaKey = evidenciaKey; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
