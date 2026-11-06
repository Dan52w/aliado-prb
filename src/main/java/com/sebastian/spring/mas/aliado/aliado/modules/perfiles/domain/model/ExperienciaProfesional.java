package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public record ExperienciaProfesional(UUID id, UUID perfilId, String descripcion,
                                     LocalDate fechaInicio, LocalDate fechaFin) {}
