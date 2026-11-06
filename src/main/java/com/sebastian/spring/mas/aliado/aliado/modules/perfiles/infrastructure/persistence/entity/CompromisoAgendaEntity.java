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
@Table(name = "compromisos_agenda")
public class CompromisoAgendaEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private PerfilProfesionalEntity perfil;
    @Column(name = "fecha_hora_inicio", nullable = false)
    private OffsetDateTime fechaHoraInicio;
    @Column(name = "fecha_hora_fin", nullable = false)
    private OffsetDateTime fechaHoraFin;
    @Column(nullable = false)
    private String origen;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public PerfilProfesionalEntity getPerfil() { return perfil; }
    public void setPerfil(PerfilProfesionalEntity perfil) { this.perfil = perfil; }
    public OffsetDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public void setFechaHoraInicio(OffsetDateTime fechaHoraInicio) { this.fechaHoraInicio = fechaHoraInicio; }
    public OffsetDateTime getFechaHoraFin() { return fechaHoraFin; }
    public void setFechaHoraFin(OffsetDateTime fechaHoraFin) { this.fechaHoraFin = fechaHoraFin; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
}
