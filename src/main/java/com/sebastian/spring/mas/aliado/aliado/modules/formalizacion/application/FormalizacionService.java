package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.application.port.FormalizacionAdminPort;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Enlace;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.EstadoEnlace;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Paso;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Progreso;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Ruta;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository.EnlaceRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository.PasoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository.ProgresoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.repository.RutaRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Formalización y vinculación (SPEC-13, UC069–UC072).
 * El contenido es orientativo: nunca certifica ni declara estatus legal o formal.
 */
@Service
public class FormalizacionService implements FormalizacionAdminPort {
    /** Aviso legal visible siempre junto al contenido (SPEC-13). */
    public static final String AVISO_LEGAL =
            "Contenido orientativo e informativo: no certifica ni declara ningún estatus legal o formal.";

    private final RutaRepository rutas;
    private final PasoRepository pasos;
    private final EnlaceRepository enlaces;
    private final ProgresoRepository progresos;

    public FormalizacionService(RutaRepository rutas, PasoRepository pasos, EnlaceRepository enlaces,
                                ProgresoRepository progresos) {
        this.rutas = rutas;
        this.pasos = pasos;
        this.enlaces = enlaces;
        this.progresos = progresos;
    }

    public record PasoConEnlaces(Paso paso, List<Enlace> enlaces) {}

    @Transactional(readOnly = true)
    public List<Ruta> rutasActivas() {
        return rutas.activas();
    }

    @Transactional(readOnly = true)
    public List<PasoConEnlaces> pasosDe(UUID rutaId) {
        rutas.porId(rutaId)
                .orElseThrow(() -> new BusinessException.NotFound("RUTA_NO_ENCONTRADA",
                        "Ruta de formalización no encontrada."));
        return pasos.porRuta(rutaId).stream()
                .map(p -> new PasoConEnlaces(p, enlaces.porPaso(p.id()))).toList();
    }

    @Transactional(readOnly = true)
    public Enlace enlace(UUID enlaceId) {
        return enlaces.porId(enlaceId)
                .orElseThrow(() -> new BusinessException.NotFound("ENLACE_NO_ENCONTRADO",
                        "Enlace no encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Progreso> progreso(UUID prestadorId) {
        List<Progreso> registros = rutas.activas().stream()
                .map(r -> progresos.porPrestadorYRuta(prestadorId, r.id()).orElse(null))
                .filter(p -> p != null).toList();
        // Sin progreso previo no hay nada que mostrar: 404, no una lista vacía que
        // parece un progreso en cero (coherente con miVerificacion, SPEC-10).
        if (registros.isEmpty()) {
            throw new BusinessException.NotFound("SIN_PROGRESO",
                    "Aún no tienes progreso registrado en formalización.");
        }
        return registros;
    }

    /** Gestión admin de enlaces (SPEC-13): deshabilitar o corregir sin tocar la BD a mano. */
    @Override
    @Transactional
    public Enlace actualizarEnlace(UUID enlaceId, String nombre, String url, String estado) {
        Enlace actual = enlaces.porId(enlaceId)
                .orElseThrow(() -> new BusinessException.NotFound("ENLACE_NO_ENCONTRADO",
                        "Enlace no encontrado."));
        if (nombre != null && nombre.isBlank()) {
            throw new BusinessException.BadRequest("NOMBRE_REQUERIDO", "El nombre no puede estar vacío.");
        }
        EstadoEnlace nuevoEstado = actual.estado();
        if (estado != null) {
            try {
                nuevoEstado = EstadoEnlace.valueOf(estado.trim());
            } catch (IllegalArgumentException ex) {
                throw new BusinessException.BadRequest("ESTADO_INVALIDO",
                        "Estado inválido: use DISPONIBLE o NO_DISPONIBLE.");
            }
        }
        return enlaces.guardar(new Enlace(actual.id(), actual.pasoId(),
                nombre == null ? actual.nombre() : nombre.trim(),
                url == null ? actual.url() : url.trim(), nuevoEstado));
    }

    @Transactional
    public Progreso actualizarProgreso(UUID prestadorId, UUID rutaId, int valor) {
        if (valor < 0 || valor > 100) {
            throw new BusinessException.BadRequest("VALOR_INVALIDO",
                    "El valor orientativo debe estar entre 0 y 100.");
        }
        rutas.porId(rutaId)
                .orElseThrow(() -> new BusinessException.NotFound("RUTA_NO_ENCONTRADA",
                        "Ruta de formalización no encontrada."));
        Progreso existente = progresos.porPrestadorYRuta(prestadorId, rutaId).orElse(null);
        Progreso progreso = new Progreso(existente == null ? UUID.randomUUID() : existente.id(),
                prestadorId, rutaId, valor, OffsetDateTime.now());
        return progresos.guardar(progreso);
    }
}
