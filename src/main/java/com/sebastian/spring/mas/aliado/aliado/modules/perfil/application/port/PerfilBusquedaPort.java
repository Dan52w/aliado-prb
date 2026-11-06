package com.sebastian.spring.mas.aliado.aliado.modules.perfil.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Perfil;
import java.util.List;

/** Búsqueda de perfiles por nombre para la administración (SPEC-12, FR-075). */
public interface PerfilBusquedaPort {
    List<Perfil> buscarPorNombre(String texto);
}
