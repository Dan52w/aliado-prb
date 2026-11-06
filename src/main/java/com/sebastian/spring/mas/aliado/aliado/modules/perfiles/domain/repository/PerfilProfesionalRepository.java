package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerfilProfesionalRepository {
    Optional<PerfilProfesional> porId(UUID id);
    Optional<PerfilProfesional> porPrestadorId(UUID prestadorId);
    List<PerfilProfesional> visiblesPorCategoriaYZona(UUID categoriaId, UUID zonaId);
    PerfilProfesional guardar(PerfilProfesional perfil);
}
