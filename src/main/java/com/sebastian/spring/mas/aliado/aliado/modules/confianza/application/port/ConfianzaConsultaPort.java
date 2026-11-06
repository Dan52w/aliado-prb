package com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.NivelVerificacion;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Contrato público de confianza para descubrimiento e indicadores. */
public interface ConfianzaConsultaPort {
    record ReputacionInfo(BigDecimal valor, int cantidad) {}
    record ConfianzaInfo(ReputacionInfo reputacion, NivelVerificacion nivelVerificacion) {}
    record ResenaPublica(UUID id, int valor, String texto, OffsetDateTime fechaCreacion) {}

    ConfianzaInfo confianzaDe(UUID prestadorId);

    /** Reseñas visibles del prestador, con su calificación, para la vista pública (SPEC-10). */
    List<ResenaPublica> resenasVisiblesDe(UUID prestadorId);
}
