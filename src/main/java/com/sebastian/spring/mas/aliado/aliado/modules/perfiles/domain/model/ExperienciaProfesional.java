package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import java.time.LocalDate;
import java.util.UUID;

public record ExperienciaProfesional(UUID id, UUID perfilId, String descripcion,
                                     LocalDate fechaInicio, LocalDate fechaFin) {

    /** Invariantes de la experiencia: descripción y fecha de inicio obligatorias; la fecha de fin no puede ser anterior a la de inicio. */
    public ExperienciaProfesional {
        if (descripcion == null || descripcion.isBlank() || fechaInicio == null) {
            throw new BusinessException.BadRequest("EXPERIENCIA_INVALIDA",
                    "La experiencia requiere descripción y fecha de inicio.");
        }
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new BusinessException.BadRequest("EXPERIENCIA_INVALIDA",
                    "La fecha de fin no puede ser anterior a la de inicio.");
        }
    }
}
