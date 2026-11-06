package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Categoria;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository {
    Optional<Categoria> porId(UUID id);
    Optional<Categoria> porNombre(String nombre);
    List<Categoria> todas();
    List<Categoria> activas();
    Categoria guardar(Categoria categoria);
}
