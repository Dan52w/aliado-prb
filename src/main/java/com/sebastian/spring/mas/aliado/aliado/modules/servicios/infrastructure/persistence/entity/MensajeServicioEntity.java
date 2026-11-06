package com.sebastian.spring.mas.aliado.aliado.modules.servicios.infrastructure.persistence.entity;

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
@Table(name = "mensajes_servicio")
public class MensajeServicioEntity {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private ServicioEntity servicio;
    @Column(name = "remitente_id", nullable = false)
    private UUID remitenteId;
    @Column(nullable = false)
    private String contenido;
    @Column(name = "fecha_envio", nullable = false)
    private OffsetDateTime fechaEnvio;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ServicioEntity getServicio() { return servicio; }
    public void setServicio(ServicioEntity servicio) { this.servicio = servicio; }
    public UUID getRemitenteId() { return remitenteId; }
    public void setRemitenteId(UUID remitenteId) { this.remitenteId = remitenteId; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public OffsetDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(OffsetDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}
