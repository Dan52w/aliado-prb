package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Ruta;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RutaRepository {
    List<Ruta> activas();
    Optional<Ruta> porId(UUID id);
}
