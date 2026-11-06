package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Categoria;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Zona;
import java.util.List;
import java.util.UUID;

/** Administración del catálogo (SPEC-12): categorías y zonas administrables. */
public interface CatalogoAdminPort {
    Categoria crearCategoria(UUID administradorId, String nombre);
    Categoria actualizarCategoria(UUID administradorId, UUID id, String nombre, String estado);
    void desactivarCategoria(UUID administradorId, UUID id);
    List<Categoria> listarCategorias();

    Zona crearZona(UUID administradorId, String nombre, String coberturaGeografica);
    Zona actualizarZona(UUID administradorId, UUID id, String nombre, String coberturaGeografica, String estado);
    void desactivarZona(UUID administradorId, UUID id);
    List<Zona> listarZonas();
}
