package com.sebastian.spring.mas.aliado.aliado.modules.servicios.infrastructure.persistence.entity;

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
@Table(name = "servicios_contratados")
public class ServicioEntity {
    @Id
    private UUID id;
    @Column(name = "solicitud_id", nullable = false)
    private UUID solicitudId;
    @Column(name = "propuesta_id", nullable = false)
    private UUID propuestaId;
    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;
    @Column(name = "prestador_id", nullable = false)
    private UUID prestadorId;
    @Column(nullable = false)
    private String estado;
    @Column(name = "comunicacion_habilitada", nullable = false)
    private boolean comunicacionHabilitada;
    @Column(name = "fecha_contratacion", nullable = false)
    private OffsetDateTime fechaContratacion;
    @Version
    private int version;

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("fecha ASC")
    private List<HistorialEstadoServicioEntity> historial = new ArrayList<>();

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("fechaEnvio ASC")
    private List<MensajeServicioEntity> mensajes = new ArrayList<>();

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSolicitudId() { return solicitudId; }
    public void setSolicitudId(UUID solicitudId) { this.solicitudId = solicitudId; }
    public UUID getPropuestaId() { return propuestaId; }
    public void setPropuestaId(UUID propuestaId) { this.propuestaId = propuestaId; }
    public UUID getClienteId() { return clienteId; }
    public void setClienteId(UUID clienteId) { this.clienteId = clienteId; }
    public UUID getPrestadorId() { return prestadorId; }
    public void setPrestadorId(UUID prestadorId) { this.prestadorId = prestadorId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public boolean isComunicacionHabilitada() { return comunicacionHabilitada; }
    public void setComunicacionHabilitada(boolean comunicacionHabilitada) { this.comunicacionHabilitada = comunicacionHabilitada; }
    public OffsetDateTime getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(OffsetDateTime fechaContratacion) { this.fechaContratacion = fechaContratacion; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public List<HistorialEstadoServicioEntity> getHistorial() { return historial; }
    public void setHistorial(List<HistorialEstadoServicioEntity> historial) { this.historial = historial; }
    public List<MensajeServicioEntity> getMensajes() { return mensajes; }
    public void setMensajes(List<MensajeServicioEntity> mensajes) { this.mensajes = mensajes; }
}
