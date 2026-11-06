package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Categoria;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.EstadoCatalogo;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Zona;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.entity.CategoriaEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.infrastructure.persistence.entity.ZonaEntity;

public final class CatalogoPersistenceMapper {
    private CatalogoPersistenceMapper() {}

    public static Categoria aDominio(CategoriaEntity e) {
        return new Categoria(e.getId(), e.getNombre(), EstadoCatalogo.valueOf(e.getEstado()));
    }

    public static CategoriaEntity aEntidad(Categoria c) {
        CategoriaEntity e = new CategoriaEntity();
        e.setId(c.id());
        e.setNombre(c.nombre());
        e.setEstado(c.estado().name());
        return e;
    }

    public static Zona aDominio(ZonaEntity e) {
        return new Zona(e.getId(), e.getNombre(), e.getCoberturaGeografica(), EstadoCatalogo.valueOf(e.getEstado()));
    }

    public static ZonaEntity aEntidad(Zona z) {
        ZonaEntity e = new ZonaEntity();
        e.setId(z.id());
        e.setNombre(z.nombre());
        e.setCoberturaGeografica(z.coberturaGeografica());
        e.setEstado(z.estado().name());
        return e;
    }
}
