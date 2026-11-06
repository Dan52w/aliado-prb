package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "acciones_moderacion")
public class AccionModeracionEntity {
    @Id
    private UUID id;
    @Column(name = "administrador_id", nullable = false)
    private UUID administradorId;
    @Column(nullable = false)
    private String tipo;
    @Column(name = "objetivo_tipo", nullable = false)
    private String objetivoTipo;
    @Column(name = "objetivo_id", nullable = false)
    private UUID objetivoId;
    @Column(nullable = false)
    private String motivo;
    @Column(nullable = false)
    private OffsetDateTime fecha;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getAdministradorId() { return administradorId; }
    public void setAdministradorId(UUID administradorId) { this.administradorId = administradorId; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getObjetivoTipo() { return objetivoTipo; }
    public void setObjetivoTipo(String objetivoTipo) { this.objetivoTipo = objetivoTipo; }
    public UUID getObjetivoId() { return objetivoId; }
    public void setObjetivoId(UUID objetivoId) { this.objetivoId = objetivoId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
}
