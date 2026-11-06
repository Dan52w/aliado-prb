package com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.UUID;

@Entity
@Table(name = "perfiles")
public class PerfilEntity {
    @Id
    @Column(name = "usuario_id")
    private UUID usuarioId;
    private String nombre;
    private String telefono;
    @Column(name = "preferencia_visibilidad", nullable = false)
    private String preferenciaVisibilidad;
    @Version
    private int version;

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getPreferenciaVisibilidad() { return preferenciaVisibilidad; }
    public void setPreferenciaVisibilidad(String preferenciaVisibilidad) { this.preferenciaVisibilidad = preferenciaVisibilidad; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
