package com.sebastian.spring.mas.aliado.aliado.modules.administracion.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "marcas_alto_riesgo")
public class MarcaRiesgoEntity {
    @Id
    private UUID id;
    @Column(name = "servicio_id", nullable = false)
    private UUID servicioId;
    @Column(nullable = false)
    private String criterio;
    @Column(name = "administrador_id", nullable = false)
    private UUID administradorId;
    @Column(nullable = false)
    private OffsetDateTime fecha;
    @Column(nullable = false)
    private boolean activa;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getServicioId() { return servicioId; }
    public void setServicioId(UUID servicioId) { this.servicioId = servicioId; }
    public String getCriterio() { return criterio; }
    public void setCriterio(String criterio) { this.criterio = criterio; }
    public UUID getAdministradorId() { return administradorId; }
    public void setAdministradorId(UUID administradorId) { this.administradorId = administradorId; }
    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
}
