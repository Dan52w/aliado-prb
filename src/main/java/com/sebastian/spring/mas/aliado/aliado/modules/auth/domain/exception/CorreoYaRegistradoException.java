package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.exception;

/** Se intenta crear una segunda cuenta con el mismo correo. */
public class CorreoYaRegistradoException extends RuntimeException {
    public CorreoYaRegistradoException() {
        super("El correo ya está registrado.");
    }
}
