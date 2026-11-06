package com.sebastian.spring.mas.aliado.aliado.modules.perfil.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Visibilidad;
import java.util.UUID;

/**
 * Lectura centralizada de la preferencia de visibilidad (SPEC-02, FR-004).
 * Descubrimiento y oportunidades la consultan aquí en vez de reimplementarla.
 */
public interface PerfilVisibilidadPort {
    /** Preferencia del usuario; SOLO_ZONA si nunca configuró una. */
    Visibilidad preferenciaDe(UUID usuarioId);
}
