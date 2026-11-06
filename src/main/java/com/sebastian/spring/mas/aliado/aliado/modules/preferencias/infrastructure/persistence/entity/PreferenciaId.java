package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class PreferenciaId implements Serializable {
    @Column(name = "usuario_id")
    private UUID usuarioId;
    @Column(name = "tipo_aviso")
    private String tipoAviso;
    private String canal;

    public PreferenciaId() {}

    public PreferenciaId(UUID usuarioId, String tipoAviso, String canal) {
        this.usuarioId = usuarioId;
        this.tipoAviso = tipoAviso;
        this.canal = canal;
    }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }
    public String getTipoAviso() { return tipoAviso; }
    public void setTipoAviso(String tipoAviso) { this.tipoAviso = tipoAviso; }
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PreferenciaId other)) return false;
        return Objects.equals(usuarioId, other.usuarioId)
                && Objects.equals(tipoAviso, other.tipoAviso) && Objects.equals(canal, other.canal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuarioId, tipoAviso, canal);
    }
}
