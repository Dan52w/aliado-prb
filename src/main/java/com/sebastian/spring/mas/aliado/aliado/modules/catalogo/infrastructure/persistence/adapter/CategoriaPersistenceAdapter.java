package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.EstadoCatalogo;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.mapper.CatalogoPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Categoria;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.repository.CategoriaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.jpa.CategoriaJpaRepository;

/** Adaptador de categorías (SPEC-04/SPEC-12). */
@Component
public class CategoriaPersistenceAdapter implements CategoriaRepository {
    private final CategoriaJpaRepository categorias;

    public CategoriaPersistenceAdapter(CategoriaJpaRepository categorias) {
        this.categorias = categorias;
    }

    @Override
    public Optional<Categoria> porId(UUID id) {
        return categorias.findById(id).map(CatalogoPersistenceMapper::aDominio);
    }

    @Override
    public Optional<Categoria> porNombre(String nombre) {
        return categorias.findByNombreIgnoreCase(nombre).map(CatalogoPersistenceMapper::aDominio);
    }

    @Override
    public List<Categoria> todas() {
        return categorias.findAll().stream().map(CatalogoPersistenceMapper::aDominio).toList();
    }

    @Override
    public List<Categoria> activas() {
        return categorias.findByEstado(EstadoCatalogo.ACTIVO.name()).stream()
                .map(CatalogoPersistenceMapper::aDominio).toList();
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        return CatalogoPersistenceMapper.aDominio(categorias.save(CatalogoPersistenceMapper.aEntidad(categoria)));
    }
}
