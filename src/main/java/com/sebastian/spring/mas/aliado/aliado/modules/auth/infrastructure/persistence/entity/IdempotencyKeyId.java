package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class IdempotencyKeyId implements Serializable {
    private UUID clave;
    private String operacion;

    public IdempotencyKeyId() {}
    public IdempotencyKeyId(UUID clave, String operacion) {
        this.clave = clave;
        this.operacion = operacion;
    }

    public UUID getClave() { return clave; }
    public void setClave(UUID clave) { this.clave = clave; }
    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IdempotencyKeyId that)) return false;
        return Objects.equals(clave, that.clave) && Objects.equals(operacion, that.operacion);
    }

    @Override
    public int hashCode() { return Objects.hash(clave, operacion); }
}
