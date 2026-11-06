package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.dto;

import java.time.LocalTime;

/** Datos de un bloque de disponibilidad al actualizar el perfil (sin identidad asignada). */
public record DisponibilidadDatos(Integer diaSemana, LocalTime horaInicio, LocalTime horaFin) {}
