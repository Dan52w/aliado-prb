package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.EstadoCatalogo;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.mapper.CatalogoPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Zona;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.repository.ZonaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.jpa.ZonaJpaRepository;

/** Adaptador de zonas (SPEC-04/SPEC-12). */
@Component
public class ZonaPersistenceAdapter implements ZonaRepository {
    private final ZonaJpaRepository zonas;

    public ZonaPersistenceAdapter(ZonaJpaRepository zonas) {
        this.zonas = zonas;
    }

    @Override
    public Optional<Zona> porId(UUID id) {
        return zonas.findById(id).map(CatalogoPersistenceMapper::aDominio);
    }

    @Override
    public Optional<Zona> porNombre(String nombre) {
        return zonas.findByNombreIgnoreCase(nombre).map(CatalogoPersistenceMapper::aDominio);
    }

    @Override
    public List<Zona> todas() {
        return zonas.findAll().stream().map(CatalogoPersistenceMapper::aDominio).toList();
    }

    @Override
    public List<Zona> activas() {
        return zonas.findByEstado(EstadoCatalogo.ACTIVO.name()).stream()
                .map(CatalogoPersistenceMapper::aDominio).toList();
    }

    @Override
    public Zona guardar(Zona zona) {
        return CatalogoPersistenceMapper.aDominio(zonas.save(CatalogoPersistenceMapper.aEntidad(zona)));
    }
}
