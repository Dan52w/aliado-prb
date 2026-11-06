package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Contrato público del perfil profesional para descubrimiento y oportunidades. */
public interface PerfilProfesionalConsultaPort {
    Optional<PerfilProfesional> porPrestadorId(UUID prestadorId);
    Optional<PerfilProfesional> porId(UUID id);
    List<PerfilProfesional> visiblesPorCategoriaYZona(UUID categoriaId, UUID zonaId);
}
