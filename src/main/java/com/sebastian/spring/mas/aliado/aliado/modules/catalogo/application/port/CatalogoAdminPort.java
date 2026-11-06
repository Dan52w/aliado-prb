package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Categoria;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Zona;
import java.util.List;
import java.util.UUID;

/** Administración del catálogo (SPEC-12): categorías y zonas administrables. */
public interface CatalogoAdminPort {
    Categoria crearCategoria(String nombre);
    Categoria actualizarCategoria(UUID id, String nombre, String estado);
    void desactivarCategoria(UUID id);
    List<Categoria> listarCategorias();

    Zona crearZona(String nombre, String coberturaGeografica);
    Zona actualizarZona(UUID id, String nombre, String coberturaGeografica, String estado);
    void desactivarZona(UUID id);
    List<Zona> listarZonas();
}
