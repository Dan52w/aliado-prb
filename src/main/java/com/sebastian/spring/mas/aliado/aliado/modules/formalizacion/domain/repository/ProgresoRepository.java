package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Progreso;
import java.util.Optional;
import java.util.UUID;

public interface ProgresoRepository {
    Optional<Progreso> porPrestadorYRuta(UUID prestadorId, UUID rutaId);
    Progreso guardar(Progreso progreso);
}
