package com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.UUID;

@Entity
@Table(name = "ubicaciones")
public class UbicacionEntity {
    @Id
    @Column(name = "usuario_id")
    private UUID usuarioId;
    @Column(name = "zona_aproximada", nullable = false)
    private String zonaAproximada;
    @Column(name = "direccion_exacta_cifrada")
    private String direccionExactaCifrada;
    @Version
    private int version;

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getZonaAproximada() { return zonaAproximada; }
    public void setZonaAproximada(String zonaAproximada) { this.zonaAproximada = zonaAproximada; }
    public String getDireccionExactaCifrada() { return direccionExactaCifrada; }
    public void setDireccionExactaCifrada(String direccionExactaCifrada) { this.direccionExactaCifrada = direccionExactaCifrada; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
