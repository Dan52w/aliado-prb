package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Zona;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ZonaRepository {
    Optional<Zona> porId(UUID id);
    Optional<Zona> porNombre(String nombre);
    List<Zona> todas();
    List<Zona> activas();
    Zona guardar(Zona zona);
}
