package com.sebastian.spring.mas.aliado.aliado.common.exception;

/**
 * Error de negocio con código estable y estado HTTP (plan general, sección 13.1).
 * Java puro: no depende de Spring; el estado se guarda como int y es
 * GlobalExceptionHandler quien lo traduce a HttpStatus en el borde HTTP.
 */
public class BusinessException extends RuntimeException {
    private final int status;
    private final String code;

    public BusinessException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public int status() { return status; }
    public String code() { return code; }

    public static final class BadRequest extends BusinessException {
        public BadRequest(String code, String message) { super(400, code, message); }
    }
    public static final class Unauthorized extends BusinessException {
        public Unauthorized(String code, String message) { super(401, code, message); }
    }
    public static final class Forbidden extends BusinessException {
        public Forbidden(String code, String message) { super(403, code, message); }
    }
    public static final class NotFound extends BusinessException {
        public NotFound(String code, String message) { super(404, code, message); }
    }
    public static final class Conflict extends BusinessException {
        public Conflict(String code, String message) { super(409, code, message); }
    }
    public static final class Gone extends BusinessException {
        public Gone(String code, String message) { super(410, code, message); }
    }
    public static final class PayloadTooLarge extends BusinessException {
        public PayloadTooLarge(String code, String message) { super(413, code, message); }
    }
    public static final class UnsupportedMediaType extends BusinessException {
        public UnsupportedMediaType(String code, String message) { super(415, code, message); }
    }
}
