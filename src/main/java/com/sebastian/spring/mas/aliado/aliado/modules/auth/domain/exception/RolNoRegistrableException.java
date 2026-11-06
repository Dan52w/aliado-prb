package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.exception;

/** El rol administrador nunca puede ser asignado desde el registro público. */
public class RolNoRegistrableException extends RuntimeException {
    public RolNoRegistrableException() {
        super("El rol ADMINISTRADOR no se registra por la API pública.");
    }
}
