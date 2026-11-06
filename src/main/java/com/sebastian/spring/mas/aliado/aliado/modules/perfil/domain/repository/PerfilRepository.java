package com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Perfil;
import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository {
    Optional<Perfil> porUsuarioId(UUID usuarioId);
    Perfil guardar(Perfil perfil);
}
