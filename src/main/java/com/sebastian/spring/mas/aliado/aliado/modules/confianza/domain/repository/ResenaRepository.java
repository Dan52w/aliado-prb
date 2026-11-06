package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.Resena;
import java.util.Optional;
import java.util.UUID;

public interface ResenaRepository {
    Optional<Resena> porId(UUID id);
    Optional<Resena> porCalificacionId(UUID calificacionId);
    Resena guardar(Resena resena);
}
