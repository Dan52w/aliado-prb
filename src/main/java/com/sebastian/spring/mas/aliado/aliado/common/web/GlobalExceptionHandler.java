package com.sebastian.spring.mas.aliado.aliado.common.web;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> negocio(BusinessException ex, HttpServletRequest req) {
        return respuesta(ex.status(), ex.code(), ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacion(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .orElse("Datos inválidos.");
        return respuesta(HttpStatus.BAD_REQUEST, "DATOS_INVALIDOS", detalle, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> inesperado(Exception ex, HttpServletRequest req) {
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR_INTERNO", "Ocurrió un error inesperado.", req);
    }

    private ResponseEntity<ApiError> respuesta(HttpStatus status, String code, String message, HttpServletRequest req) {
        ApiError error = new ApiError(OffsetDateTime.now(), status.value(), code, message,
                req.getRequestURI(), UUID.randomUUID().toString());
        return ResponseEntity.status(status).body(error);
    }
}
