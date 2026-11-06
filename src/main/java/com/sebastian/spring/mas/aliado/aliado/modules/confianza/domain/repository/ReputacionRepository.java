package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Reputacion;
import java.util.Optional;
import java.util.UUID;

public interface ReputacionRepository {
    Optional<Reputacion> porPrestadorId(UUID prestadorId);
    Reputacion guardar(Reputacion reputacion);
}
