package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "calificaciones")
public class CalificacionEntity {
    @Id
    private UUID id;
    @Column(name = "servicio_id", nullable = false)
    private UUID servicioId;
    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;
    @Column(name = "prestador_id", nullable = false)
    private UUID prestadorId;
    @Column(nullable = false)
    private short valor;
    @Column(nullable = false)
    private OffsetDateTime fecha;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getServicioId() { return servicioId; }
    public void setServicioId(UUID servicioId) { this.servicioId = servicioId; }
    public UUID getClienteId() { return clienteId; }
    public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
    public UUID getPrestadorId() { return prestadorId; }
    public void setPrestadorId(UUID prestadorId) { this.prestadorId = prestadorId; }
    public short getValor() { return valor; }
    public void setValor(short valor) { this.valor = valor; }
    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }
}
