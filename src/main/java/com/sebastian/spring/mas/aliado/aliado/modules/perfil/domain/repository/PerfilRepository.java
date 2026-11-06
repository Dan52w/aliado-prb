package com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Perfil;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerfilRepository {
    Optional<Perfil> porUsuarioId(UUID usuarioId);
    Perfil guardar(Perfil perfil);
    /** Búsqueda admin (SPEC-12, FR-075): por nombre (contiene, sin distinguir mayúsculas). */
    List<Perfil> buscarPorNombre(String texto);
}
