package com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port;

import java.util.UUID;

/** Consulta de existencia de reseñas para otros módulos (SPEC-11 reporta reseñas). */
public interface ResenaConsultaPort {
    boolean existe(UUID resenaId);
}
