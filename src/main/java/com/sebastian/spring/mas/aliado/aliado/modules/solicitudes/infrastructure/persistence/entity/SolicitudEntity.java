package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "solicitudes")
public class SolicitudEntity {
    @Id
    private UUID id;
    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;
    @Column(name = "categoria_id")
    private UUID categoriaId;
    @Column(name = "zona_id")
    private UUID zonaId;
    private String descripcion;
    private String urgencia;
    @Column(nullable = false)
    private String estado;
    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;
    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;
    @Version
    private int version;

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orden ASC")
    private List<FotoSolicitudEntity> fotos = new ArrayList<>();

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("fecha ASC")
    private List<HistorialEstadoSolicitudEntity> historial = new ArrayList<>();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getClienteId() { return clienteId; }
    public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
    public UUID getCategoriaId() { return categoriaId; }
    public void setCategoriaId(UUID categoriaId) { this.categoriaId = categoriaId; }
    public UUID getZonaId() { return zonaId; }
    public void setZonaId(UUID zonaId) { this.zonaId = zonaId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUrgencia() { return urgencia; }
    public void setUrgencia(String urgencia) { this.urgencia = urgencia; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public OffsetDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(OffsetDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public List<FotoSolicitudEntity> getFotos() { return fotos; }
    public void setFotos(List<FotoSolicitudEntity> fotos) { this.fotos = fotos; }
    public List<HistorialEstadoSolicitudEntity> getHistorial() { return historial; }
    public void setHistorial(List<HistorialEstadoSolicitudEntity> historial) { this.historial = historial; }
}
