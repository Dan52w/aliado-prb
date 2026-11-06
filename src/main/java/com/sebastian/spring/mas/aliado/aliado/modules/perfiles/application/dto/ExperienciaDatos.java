package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.dto;

import java.time.LocalDate;

/** Datos de una experiencia profesional al crear el perfil (sin identidad asignada). */
public record ExperienciaDatos(String descripcion, LocalDate fechaInicio, LocalDate fechaFin) {}
