package com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.MarcaAltoRiesgo;
import java.util.Optional;
import java.util.UUID;

public interface MarcaRiesgoRepository {
    Optional<MarcaAltoRiesgo> activaDeServicio(UUID servicioId);
    MarcaAltoRiesgo guardar(MarcaAltoRiesgo marca);
}
