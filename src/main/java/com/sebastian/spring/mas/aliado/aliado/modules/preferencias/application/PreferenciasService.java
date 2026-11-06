package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.storage.ArchivoStorageService;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.RolConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.ConfianzaConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.notificaciones.application.port.NotificacionConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.DescargaHistorial;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.PreferenciaNotificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.TipoAviso;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.repository.DescargaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.repository.PreferenciaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port.ServicioLecturaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.EstadoServicio;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model.ServicioContratado;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Preferencias de notificación, historial e indicadores (SPEC-14, UC073–UC079).
 * Las preferencias por defecto cubren los 5 tipos de aviso en PUSH y EMAIL.
 * AVISO_SEGURIDAD_CUENTA no es configurable: siempre habilitado.
 */
@Service
public class PreferenciasService {
    private final PreferenciaRepository preferencias;
    private final DescargaRepository descargas;
    private final ServicioLecturaPort servicios;
    private final ConfianzaConsultaPort confianza;
    private final NotificacionConsultaPort notificaciones;
    private final RolConsultaPort roles;
    private final ArchivoStorageService storage;

    public PreferenciasService(PreferenciaRepository preferencias, DescargaRepository descargas,
                               ServicioLecturaPort servicios, ConfianzaConsultaPort confianza,
                               NotificacionConsultaPort notificaciones, RolConsultaPort roles,
                               ArchivoStorageService storage) {
        this.preferencias = preferencias;
        this.descargas = descargas;
        this.servicios = servicios;
        this.confianza = confianza;
        this.notificaciones = notificaciones;
        this.roles = roles;
        this.storage = storage;
    }

    public record Actividad(String tipo, String resumen, OffsetDateTime fecha) {}
    public record Indicadores(long totalServicios, long enCurso, long finalizados,
                              double reputacion, long cantidadCalificaciones) {}

    private boolean esConfigurable(TipoAviso tipo) {
        return tipo != TipoAviso.AVISO_SEGURIDAD_CUENTA;
    }

    @Transactional(readOnly = true)
    public List<PreferenciaNotificacion> listarPreferencias(UUID usuarioId) {
        List<PreferenciaNotificacion> guardadas = preferencias.porUsuario(usuarioId);
        List<PreferenciaNotificacion> resultado = new ArrayList<>();
        for (TipoAviso tipo : TipoAviso.values()) {
            for (String canal : List.of("PUSH", "EMAIL")) {
                resultado.add(guardadas.stream()
                        .filter(p -> p.tipoAviso() == tipo && p.canal().equals(canal))
                        .findFirst()
                        .orElse(new PreferenciaNotificacion(usuarioId, tipo, canal, true, esConfigurable(tipo))));
            }
        }
        return resultado;
    }

    @Transactional
    public PreferenciaNotificacion actualizarPreferencia(UUID usuarioId, TipoAviso tipo, String canal,
                                                         boolean habilitada) {
        if (!"PUSH".equals(canal) && !"EMAIL".equals(canal)) {
            throw new BusinessException.BadRequest("CANAL_INVALIDO", "El canal debe ser PUSH o EMAIL.");
        }
        if (!esConfigurable(tipo) && !habilitada) {
            throw new BusinessException.Conflict("AVISO_NO_CONFIGURABLE",
                    "El aviso de seguridad de cuenta no puede deshabilitarse.");
        }
        PreferenciaNotificacion preferencia = preferencias.porUsuarioTipoCanal(usuarioId, tipo.name(), canal)
                .map(p -> new PreferenciaNotificacion(usuarioId, tipo, canal, habilitada, esConfigurable(tipo)))
                .orElse(new PreferenciaNotificacion(usuarioId, tipo, canal, habilitada, esConfigurable(tipo)));
        return preferencias.guardar(preferencia);
    }

    @Transactional(readOnly = true)
    public List<ServicioContratado> historial(UUID usuarioId, int pagina, int tamano) {
        return servicios.porUsuario(usuarioId, pagina, tamano);
    }

    @Transactional
    public DescargaHistorial exportarHistorial(UUID usuarioId) {
        List<ServicioContratado> todos = servicios.porUsuario(usuarioId);
        String clave = storage.guardar("historial.csv", "text/csv", HistorialCsvExporter.exportar(todos));
        return descargas.guardar(new DescargaHistorial(UUID.randomUUID(), usuarioId, OffsetDateTime.now(),
                clave, "LISTA"));
    }

    /** Contenido de una exportación propia lista para descargar (SPEC-14). */
    @Transactional(readOnly = true)
    public byte[] descargarExportacion(UUID usuarioId, UUID descargaId) {
        DescargaHistorial descarga = descargas.porId(descargaId)
                .filter(d -> d.usuarioId().equals(usuarioId))
                .orElseThrow(() -> new BusinessException.NotFound("DESCARGA_NO_ENCONTRADA",
                        "Exportación no encontrada."));
        if (!"LISTA".equals(descarga.estado())) {
            throw new BusinessException.Conflict("DESCARGA_NO_LISTA",
                    "La exportación aún no está lista para descargar.");
        }
        return storage.leer(descarga.archivoKey());
    }

    @Transactional(readOnly = true)
    public Indicadores misIndicadores(UUID prestadorId) {
        if (!roles.esPrestador(prestadorId)) {
            throw new BusinessException.Forbidden("SOLO_PRESTADOR",
                    "Los indicadores son solo para prestadores.");
        }
        List<ServicioContratado> todos = servicios.porUsuario(prestadorId);
        // SPEC-14 (T007): el SPEC no fija las fórmulas. TERMINADO es trabajo terminado
        // por el prestador y pendiente de confirmación del cliente: cuenta como
        // finalizado, no como en curso. CANCELADO no cuenta en ningún subtotal.
        long finalizados = todos.stream()
                .filter(s -> s.estado() == EstadoServicio.FINALIZADO
                        || s.estado() == EstadoServicio.TERMINADO)
                .count();
        long enCurso = todos.stream()
                .filter(s -> s.estado() == EstadoServicio.EN_EJECUCION
                        || s.estado() == EstadoServicio.CONTRATADO)
                .count();
        var confianzaPrestador = confianza.confianzaDe(prestadorId);
        return new Indicadores(todos.size(), enCurso, finalizados,
                confianzaPrestador.reputacion().valor().doubleValue(),
                confianzaPrestador.reputacion().cantidad());
    }

    @Transactional(readOnly = true)
    public List<Actividad> actividadReciente(UUID usuarioId) {
        List<Actividad> actividades = new ArrayList<>();
        servicios.porUsuario(usuarioId).forEach(s -> actividades.add(new Actividad("SERVICIO",
                "Servicio " + s.estado().name().toLowerCase(), s.fechaContratacion())));
        notificaciones.listarPropias(usuarioId).forEach(n -> actividades.add(
                new Actividad("NOTIFICACION", n.tipo().name() + ": " + n.detalle(), n.fechaGeneracion())));
        return actividades.stream()
                .sorted(Comparator.comparing(Actividad::fecha).reversed())
                .limit(20)
                .toList();
    }
}
