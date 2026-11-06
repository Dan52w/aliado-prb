package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoAdminPort;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Categoria;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.EstadoCatalogo;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.model.Zona;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.repository.CategoriaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.domain.repository.ZonaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Catálogo administrable (SPEC-04 crea las tablas; SPEC-12 las administra). */
@Service
public class CatalogoService implements CatalogoConsultaPort, CatalogoAdminPort {
    private final CategoriaRepository categorias;
    private final ZonaRepository zonas;

    public CatalogoService(CategoriaRepository categorias, ZonaRepository zonas) {
        this.categorias = categorias;
        this.zonas = zonas;
    }

    @Transactional
    public Categoria crearCategoria(String nombre) {
        validarNombre(nombre);
        if (categorias.porNombre(nombre.trim()).isPresent()) {
            throw new BusinessException.Conflict("CATEGORIA_DUPLICADA", "Ya existe una categoría con ese nombre.");
        }
        return categorias.guardar(new Categoria(UUID.randomUUID(), nombre.trim(), EstadoCatalogo.ACTIVO));
    }

    @Transactional
    public Categoria actualizarCategoria(UUID id, String nombre, String estado) {
        Categoria actual = categoriaPorIdOError(id);
        EstadoCatalogo nuevoEstado = estadoOActual(estado, actual.estado());
        return categorias.guardar(new Categoria(id,
                nombre == null ? actual.nombre() : nombre.trim(), nuevoEstado));
    }

    /** Borrado lógico (SPEC-12): desactivar no rompe borradores ni coberturas existentes. */
    @Transactional
    public void desactivarCategoria(UUID id) {
        Categoria actual = categoriaPorIdOError(id);
        categorias.guardar(new Categoria(id, actual.nombre(), EstadoCatalogo.INACTIVO));
    }

    @Transactional
    public Zona crearZona(String nombre, String coberturaGeografica) {
        validarNombre(nombre);
        if (zonas.porNombre(nombre.trim()).isPresent()) {
            throw new BusinessException.Conflict("ZONA_DUPLICADA", "Ya existe una zona con ese nombre.");
        }
        return zonas.guardar(new Zona(UUID.randomUUID(), nombre.trim(), coberturaGeografica, EstadoCatalogo.ACTIVO));
    }

    @Transactional
    public Zona actualizarZona(UUID id, String nombre, String coberturaGeografica, String estado) {
        Zona actual = zonaPorIdOError(id);
        EstadoCatalogo nuevoEstado = estadoOActual(estado, actual.estado());
        return zonas.guardar(new Zona(id,
                nombre == null ? actual.nombre() : nombre.trim(),
                coberturaGeografica == null ? actual.coberturaGeografica() : coberturaGeografica,
                nuevoEstado));
    }

    @Transactional
    public void desactivarZona(UUID id) {
        Zona actual = zonaPorIdOError(id);
        zonas.guardar(new Zona(id, actual.nombre(), actual.coberturaGeografica(), EstadoCatalogo.INACTIVO));
    }

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categorias.todas();
    }

    @Transactional(readOnly = true)
    public List<Zona> listarZonas() {
        return zonas.todas();
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BusinessException.BadRequest("NOMBRE_REQUERIDO", "El nombre es obligatorio.");
        }
    }

    private Categoria categoriaPorIdOError(UUID id) {
        return categorias.porId(id)
                .orElseThrow(() -> new BusinessException.NotFound("CATEGORIA_NO_ENCONTRADA",
                        "Categoría no encontrada."));
    }

    private Zona zonaPorIdOError(UUID id) {
        return zonas.porId(id)
                .orElseThrow(() -> new BusinessException.NotFound("ZONA_NO_ENCONTRADA", "Zona no encontrada."));
    }

    private EstadoCatalogo estadoOActual(String estado, EstadoCatalogo actual) {
        if (estado == null) {
            return actual;
        }
        try {
            return EstadoCatalogo.valueOf(estado);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException.BadRequest("ESTADO_INVALIDO",
                    "Estado inválido: use ACTIVO o INACTIVO.");
        }
    }

    @Override
    public Optional<Categoria> categoriaPorId(UUID id) { return categorias.porId(id); }

    @Override
    public Optional<Zona> zonaPorId(UUID id) { return zonas.porId(id); }

    @Override
    public Optional<Categoria> categoriaPorNombre(String nombre) { return categorias.porNombre(nombre); }

    @Override
    public Optional<Zona> zonaPorNombre(String nombre) { return zonas.porNombre(nombre); }

    @Override
    public List<Categoria> categoriasActivas() { return categorias.activas(); }

    @Override
    public List<Zona> zonasActivas() { return zonas.activas(); }
}
