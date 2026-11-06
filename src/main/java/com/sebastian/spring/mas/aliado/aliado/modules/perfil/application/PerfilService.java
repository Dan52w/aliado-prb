package com.sebastian.spring.mas.aliado.aliado.modules.perfil.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.EstadoEliminacion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Perfil;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.SolicitudEliminacion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Ubicacion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Visibilidad;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.repository.PerfilRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.repository.SolicitudEliminacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.repository.UbicacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port.ServicioConsultaPort;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestión de perfil y privacidad (SPEC-02, UC008–UC013).
 * La dirección exacta solo se expone al propio usuario; a terceros, solo la zona aproximada.
 * La eliminación real (anonimización) no se ejecuta: la política no está definida en los SPEC
 * (F02-QA-03); la confirmación queda registrada y la cuenta sigue activa hasta definirla.
 */
@Service
public class PerfilService {
    private final PerfilRepository perfiles;
    private final UbicacionRepository ubicaciones;
    private final SolicitudEliminacionRepository eliminaciones;
    private final ServicioConsultaPort servicios;

    public PerfilService(PerfilRepository perfiles, UbicacionRepository ubicaciones,
                         SolicitudEliminacionRepository eliminaciones, ServicioConsultaPort servicios) {
        this.perfiles = perfiles;
        this.ubicaciones = ubicaciones;
        this.eliminaciones = eliminaciones;
        this.servicios = servicios;
    }

    public record PerfilPropio(UUID usuarioId, String nombre, String telefono, String preferenciaVisibilidad,
                               String zonaAproximada, String direccionExacta, int version) {}

    @Transactional
    public void crearPerfilBase(UUID usuarioId) {
        perfiles.guardar(new Perfil(usuarioId, null, null, Visibilidad.SOLO_ZONA, 0));
    }

    @Transactional(readOnly = true)
    public PerfilPropio consultarMe(UUID usuarioId) {
        Perfil perfil = perfiles.porUsuarioId(usuarioId)
                .orElseThrow(() -> new BusinessException.NotFound("PERFIL_NO_ENCONTRADO", "Perfil no encontrado."));
        Ubicacion ubicacion = ubicaciones.porUsuarioId(usuarioId).orElse(null);
        return new PerfilPropio(usuarioId, perfil.nombre(), perfil.telefono(),
                perfil.preferenciaVisibilidad().name(),
                ubicacion == null ? null : ubicacion.zonaAproximada(),
                ubicacion == null ? null : ubicacion.direccionExacta(),
                perfil.version());
    }

    @Transactional
    public PerfilPropio actualizarMe(UUID usuarioId, String nombre, String telefono, Integer version) {
        Perfil actual = perfiles.porUsuarioId(usuarioId)
                .orElseThrow(() -> new BusinessException.NotFound("PERFIL_NO_ENCONTRADO", "Perfil no encontrado."));
        if (version != null && version != actual.version()) {
            throw new BusinessException.Conflict("CONFLICTO_VERSION",
                    "El perfil cambió desde tu última lectura; vuelve a cargarlo.");
        }
        if (nombre != null && nombre.isBlank()) {
            throw new BusinessException.BadRequest("NOMBRE_INVALIDO", "El nombre no puede estar vacío.");
        }
        if (telefono != null && !telefono.matches("[+0-9 ()-]{7,30}")) {
            throw new BusinessException.BadRequest("TELEFONO_INVALIDO", "El teléfono tiene un formato inválido.");
        }
        Perfil actualizado = new Perfil(usuarioId,
                nombre == null ? actual.nombre() : nombre,
                telefono == null ? actual.telefono() : telefono,
                actual.preferenciaVisibilidad(), actual.version());
        perfiles.guardar(actualizado);
        return consultarMe(usuarioId);
    }

    @Transactional
    public PerfilPropio configurarUbicacion(UUID usuarioId, String zonaAproximada, String direccionExacta) {
        if (zonaAproximada == null || zonaAproximada.isBlank()) {
            throw new BusinessException.BadRequest("ZONA_REQUERIDA", "La zona aproximada es obligatoria.");
        }
        Ubicacion actual = ubicaciones.porUsuarioId(usuarioId).orElse(null);
        // Editar la zona no altera la dirección exacta ya comprometida en un servicio activo.
        String direccion = direccionExacta != null ? direccionExacta
                : (actual == null ? null : actual.direccionExacta());
        Ubicacion nueva = new Ubicacion(usuarioId, zonaAproximada.trim(), direccion,
                actual == null ? 0 : actual.version());
        ubicaciones.guardar(nueva);
        return consultarMe(usuarioId);
    }

    @Transactional
    public PerfilPropio configurarVisibilidad(UUID usuarioId, String preferencia) {
        Visibilidad visibilidad;
        try {
            visibilidad = Visibilidad.valueOf(preferencia);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BusinessException.BadRequest("VISIBILIDAD_INVALIDA",
                    "Preferencia de visibilidad inválida. Valores: PUBLICO, SOLO_ZONA, PRIVADO.");
        }
        Perfil actual = perfiles.porUsuarioId(usuarioId)
                .orElseThrow(() -> new BusinessException.NotFound("PERFIL_NO_ENCONTRADO", "Perfil no encontrado."));
        perfiles.guardar(new Perfil(usuarioId, actual.nombre(), actual.telefono(), visibilidad, actual.version()));
        return consultarMe(usuarioId);
    }

    @Transactional(readOnly = true)
    public PerfilPropio perfilPublico(UUID usuarioId) {
        Ubicacion ubicacion = ubicaciones.porUsuarioId(usuarioId)
                .orElseThrow(() -> new BusinessException.NotFound(
                        "PERFIL_NO_DISPONIBLE", "El perfil no está disponible."));
        return new PerfilPropio(usuarioId, null, null, null, ubicacion.zonaAproximada(), null, 0);
    }

    @Transactional
    public SolicitudEliminacion solicitarEliminacion(UUID usuarioId) {
        if (servicios.tieneServicioActivo(usuarioId)) {
            throw new BusinessException.Conflict("SERVICIO_EN_EJECUCION",
                    "Tienes un servicio en ejecución; la eliminación queda bloqueada hasta que finalice.");
        }
        if (eliminaciones.pendienteDeUsuario(usuarioId).isPresent()) {
            throw new BusinessException.Conflict("ELIMINACION_PENDIENTE",
                    "Ya tienes una solicitud de eliminación pendiente.");
        }
        OffsetDateTime ahora = OffsetDateTime.now();
        SolicitudEliminacion solicitud = new SolicitudEliminacion(UUID.randomUUID(), usuarioId, ahora,
                ahora.plusHours(24), EstadoEliminacion.PENDIENTE, null);
        return eliminaciones.guardar(solicitud);
    }

    @Transactional
    public SolicitudEliminacion confirmarEliminacion(UUID usuarioId) {
        OffsetDateTime ahora = OffsetDateTime.now();
        SolicitudEliminacion solicitud = eliminaciones.pendienteDeUsuario(usuarioId)
                .orElseThrow(() -> new BusinessException.NotFound(
                        "SIN_SOLICITUD_PENDIENTE", "No tienes una solicitud de eliminación pendiente."));
        if (solicitud.vencida(ahora)) {
            eliminaciones.guardar(new SolicitudEliminacion(solicitud.id(), usuarioId, solicitud.fechaSolicitud(),
                    solicitud.fechaExpiracion(), EstadoEliminacion.EXPIRADA, null));
            throw new BusinessException.Gone("SOLICITUD_EXPIRADA",
                    "La solicitud expiró (24 h). Genera una nueva solicitud.");
        }
        SolicitudEliminacion confirmada = new SolicitudEliminacion(solicitud.id(), usuarioId,
                solicitud.fechaSolicitud(), solicitud.fechaExpiracion(), EstadoEliminacion.CONFIRMADA, ahora);
        return eliminaciones.guardar(confirmada);
    }

    @Transactional
    public void expirarSolicitudesVencidas() {
        OffsetDateTime ahora = OffsetDateTime.now();
        for (SolicitudEliminacion vencida : eliminaciones.pendientesVencidas(ahora)) {
            eliminaciones.guardar(new SolicitudEliminacion(vencida.id(), vencida.usuarioId(),
                    vencida.fechaSolicitud(), vencida.fechaExpiracion(), EstadoEliminacion.EXPIRADA, null));
        }
    }

    public Optional<Perfil> perfilDe(UUID usuarioId) {
        return perfiles.porUsuarioId(usuarioId);
    }
}
