package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.exception;

/** La contraseña no satisface la política de dominio. */
public class PasswordInvalidaException extends RuntimeException {
    public PasswordInvalidaException(String message) {
        super(message);
    }
}
