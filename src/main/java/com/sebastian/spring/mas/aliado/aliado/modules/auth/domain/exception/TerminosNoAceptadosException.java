package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.exception;

/** El consentimiento de términos y tratamiento de datos es obligatorio para registrar la cuenta. */
public class TerminosNoAceptadosException extends RuntimeException {
    public TerminosNoAceptadosException() {
        super("Debe aceptar los términos y el tratamiento de datos.");
    }
}
