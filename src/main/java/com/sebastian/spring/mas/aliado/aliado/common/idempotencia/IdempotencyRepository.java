package com.sebastian.spring.mas.aliado.aliado.common.idempotencia;

import java.util.Optional;
import java.util.UUID;

/** Puerto transversal (plan general, sección 14): respuestas de comandos reintentables. */
public interface IdempotencyRepository {
    record RespuestaGuardada(int statusCode, String cuerpoJson) {}

    Optional<RespuestaGuardada> buscar(UUID clave, String operacion);
    void guardar(UUID clave, String operacion, UUID usuarioId, int statusCode, String cuerpoJson);
}
