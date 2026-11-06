package com.sebastian.spring.mas.aliado.aliado.common.idempotencia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Servicio transversal de idempotencia (plan general, sección 14). */
@Service
public class IdempotencyService implements IdempotenciaPort {
    private final IdempotencyRepository repositorio;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyRepository repositorio, ObjectMapper objectMapper) {
        this.repositorio = repositorio;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Respuesta> buscar(UUID clave, String operacion) {
        if (clave == null) {
            return Optional.empty();
        }
        return repositorio.buscar(clave, operacion).flatMap(guardada -> {
            try {
                return Optional.of(new Respuesta(guardada.statusCode(), objectMapper.readTree(guardada.cuerpoJson())));
            } catch (Exception ex) {
                return Optional.empty();
            }
        });
    }

    @Override
    @Transactional
    public void guardar(UUID clave, String operacion, UUID usuarioId, int statusCode, JsonNode cuerpo) {
        if (clave == null) {
            return;
        }
        repositorio.guardar(clave, operacion, usuarioId, statusCode, cuerpo.toString());
    }
}
