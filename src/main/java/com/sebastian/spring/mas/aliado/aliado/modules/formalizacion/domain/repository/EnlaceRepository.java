package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Enlace;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnlaceRepository {
    Optional<Enlace> porId(UUID id);
    List<Enlace> porPaso(UUID pasoId);
}
