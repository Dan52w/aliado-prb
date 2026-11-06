package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "reputaciones_prestador")
public class ReputacionEntity {
    @Id
    @Column(name = "prestador_id")
    private UUID prestadorId;
    @Column(name = "valor_agregado", nullable = false)
    private BigDecimal valorAgregado;
    @Column(name = "cantidad_calificaciones", nullable = false)
    private int cantidadCalificaciones;
    @Column(name = "fecha_calculo", nullable = false)
    private OffsetDateTime fechaCalculo;

    public UUID getPrestadorId() { return prestadorId; }
    public void setPrestadorId(UUID prestadorId) { this.prestadorId = prestadorId; }
    public BigDecimal getValorAgregado() { return valorAgregado; }
    public void setValorAgregado(BigDecimal valorAgregado) { this.valorAgregado = valorAgregado; }
    public int getCantidadCalificaciones() { return cantidadCalificaciones; }
    public void setCantidadCalificaciones(int cantidadCalificaciones) { this.cantidadCalificaciones = cantidadCalificaciones; }
    public OffsetDateTime getFechaCalculo() { return fechaCalculo; }
    public void setFechaCalculo(OffsetDateTime fechaCalculo) { this.fechaCalculo = fechaCalculo; }
}
