package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Paso;
import java.util.List;
import java.util.UUID;

public interface PasoRepository {
    List<Paso> porRuta(UUID rutaId);
}
