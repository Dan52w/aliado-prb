package com.sebastian.spring.mas.aliado.aliado.common.idempotencia;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Optional;
import java.util.UUID;

/** Idempotencia por clave de operación (plan general, sección 14). */
public interface IdempotenciaPort {
    record Respuesta(int statusCode, JsonNode cuerpo) {}

    /** Respuesta guardada para la clave y operación, si ya fue procesada. */
    Optional<Respuesta> buscar(UUID clave, String operacion);

    /** Guarda la respuesta de una operación procesada. Sin clave no hace nada. */
    void guardar(UUID clave, String operacion, UUID usuarioId, int statusCode, JsonNode cuerpo);
}
