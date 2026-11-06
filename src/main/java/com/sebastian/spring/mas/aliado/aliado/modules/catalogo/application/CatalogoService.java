package com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.port.AuditoriaPort;
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
    private final AuditoriaPort auditoria;

    public CatalogoService(CategoriaRepository categorias, ZonaRepository zonas, AuditoriaPort auditoria) {
        this.categorias = categorias;
        this.zonas = zonas;
        this.auditoria = auditoria;
    }

    @Override
    @Transactional
    public Categoria crearCategoria(UUID administradorId, String nombre) {
        validarNombre(nombre);
        if (categorias.porNombre(nombre.trim()).isPresent()) {
            throw new BusinessException.Conflict("CATEGORIA_DUPLICADA", "Ya existe una categoría con ese nombre.");
        }
        Categoria creada = categorias.guardar(new Categoria(UUID.randomUUID(), nombre.trim(), EstadoCatalogo.ACTIVO));
        auditoria.registrar(administradorId, "CATEGORY_CREATED", "CATEGORIA", creada.id(), creada.nombre());
        return creada;
    }

    @Override
    @Transactional
    public Categoria actualizarCategoria(UUID administradorId, UUID id, String nombre, String estado) {
        Categoria actual = categoriaPorIdOError(id);
        if (nombre != null && nombre.isBlank()) {
            throw new BusinessException.BadRequest("NOMBRE_REQUERIDO", "El nombre no puede estar vacío.");
        }
        String nombreFinal = nombre == null ? actual.nombre() : nombre.trim();
        categorias.porNombre(nombreFinal)
                .filter(otra -> !otra.id().equals(id))
                .ifPresent(otra -> {
                    throw new BusinessException.Conflict("CATEGORIA_DUPLICADA",
                            "Ya existe otra categoría con ese nombre.");
                });
        EstadoCatalogo nuevoEstado = estadoOActual(estado, actual.estado());
        Categoria actualizada = categorias.guardar(new Categoria(id, nombreFinal, nuevoEstado));
        auditoria.registrar(administradorId, "CATEGORY_UPDATED", "CATEGORIA", id, nombreFinal);
        return actualizada;
    }

    /** Borrado lógico (SPEC-12): desactivar no rompe borradores ni coberturas existentes. */
    @Override
    @Transactional
    public void desactivarCategoria(UUID administradorId, UUID id) {
        Categoria actual = categoriaPorIdOError(id);
        categorias.guardar(new Categoria(id, actual.nombre(), EstadoCatalogo.INACTIVO));
        auditoria.registrar(administradorId, "CATEGORY_DEACTIVATED", "CATEGORIA", id, actual.nombre());
    }

    @Override
    @Transactional
    public Zona crearZona(UUID administradorId, String nombre, String coberturaGeografica) {
        validarNombre(nombre);
        if (zonas.porNombre(nombre.trim()).isPresent()) {
            throw new BusinessException.Conflict("ZONA_DUPLICADA", "Ya existe una zona con ese nombre.");
        }
        Zona creada = zonas.guardar(new Zona(UUID.randomUUID(), nombre.trim(), coberturaGeografica, EstadoCatalogo.ACTIVO));
        auditoria.registrar(administradorId, "ZONE_CREATED", "ZONA", creada.id(), creada.nombre());
        return creada;
    }

    @Override
    @Transactional
    public Zona actualizarZona(UUID administradorId, UUID id, String nombre, String coberturaGeografica, String estado) {
        Zona actual = zonaPorIdOError(id);
        if (nombre != null && nombre.isBlank()) {
            throw new BusinessException.BadRequest("NOMBRE_REQUERIDO", "El nombre no puede estar vacío.");
        }
        String nombreFinal = nombre == null ? actual.nombre() : nombre.trim();
        zonas.porNombre(nombreFinal)
                .filter(otra -> !otra.id().equals(id))
                .ifPresent(otra -> {
                    throw new BusinessException.Conflict("ZONA_DUPLICADA",
                            "Ya existe otra zona con ese nombre.");
                });
        EstadoCatalogo nuevoEstado = estadoOActual(estado, actual.estado());
        Zona actualizada = zonas.guardar(new Zona(id, nombreFinal,
                coberturaGeografica == null ? actual.coberturaGeografica() : coberturaGeografica,
                nuevoEstado));
        auditoria.registrar(administradorId, "ZONE_UPDATED", "ZONA", id, nombreFinal);
        return actualizada;
    }

    @Override
    @Transactional
    public void desactivarZona(UUID administradorId, UUID id) {
        Zona actual = zonaPorIdOError(id);
        zonas.guardar(new Zona(id, actual.nombre(), actual.coberturaGeografica(), EstadoCatalogo.INACTIVO));
        auditoria.registrar(administradorId, "ZONE_DEACTIVATED", "ZONA", id, actual.nombre());
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
