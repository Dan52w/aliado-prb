package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "preferencias_notificacion")
public class PreferenciaEntity {
    @EmbeddedId
    private PreferenciaId id;
    @Column(nullable = false)
    private boolean habilitada;
    @Column(nullable = false)
    private boolean configurable;

    public PreferenciaId getId() { return id; }
    public void setId(PreferenciaId id) { this.id = id; }
    public boolean isHabilitada() { return habilitada; }
    public void setHabilitada(boolean habilitada) { this.habilitada = habilitada; }
    public boolean isConfigurable() { return configurable; }
    public void setConfigurable(boolean configurable) { this.configurable = configurable; }
}
