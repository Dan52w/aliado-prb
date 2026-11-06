package com.sebastian.spring.mas.aliado.aliado.common.exception;

import org.springframework.http.HttpStatus;

/** Error de negocio con código estable y estado HTTP (plan general, sección 13.1). */
public class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public BusinessException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus status() { return status; }
    public String code() { return code; }

    public static final class BadRequest extends BusinessException {
        public BadRequest(String code, String message) { super(HttpStatus.BAD_REQUEST, code, message); }
    }
    public static final class Unauthorized extends BusinessException {
        public Unauthorized(String code, String message) { super(HttpStatus.UNAUTHORIZED, code, message); }
    }
    public static final class Forbidden extends BusinessException {
        public Forbidden(String code, String message) { super(HttpStatus.FORBIDDEN, code, message); }
    }
    public static final class NotFound extends BusinessException {
        public NotFound(String code, String message) { super(HttpStatus.NOT_FOUND, code, message); }
    }
    public static final class Conflict extends BusinessException {
        public Conflict(String code, String message) { super(HttpStatus.CONFLICT, code, message); }
    }
    public static final class Gone extends BusinessException {
        public Gone(String code, String message) { super(HttpStatus.GONE, code, message); }
    }
    public static final class PayloadTooLarge extends BusinessException {
        public PayloadTooLarge(String code, String message) { super(HttpStatus.PAYLOAD_TOO_LARGE, code, message); }
    }
    public static final class UnsupportedMediaType extends BusinessException {
        public UnsupportedMediaType(String code, String message) { super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, code, message); }
    }
}
