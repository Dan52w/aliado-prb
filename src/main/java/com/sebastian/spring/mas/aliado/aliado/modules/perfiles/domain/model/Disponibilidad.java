package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model;

import java.time.LocalTime;
import java.util.UUID;

public record Disponibilidad(UUID id, UUID perfilId, Integer diaSemana,
                             LocalTime horaInicio, LocalTime horaFin) {}
