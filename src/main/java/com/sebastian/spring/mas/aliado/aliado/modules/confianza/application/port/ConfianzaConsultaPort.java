package com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.NivelVerificacion;
import java.math.BigDecimal;
import java.util.UUID;

/** Contrato público de confianza para descubrimiento e indicadores. */
public interface ConfianzaConsultaPort {
    record ReputacionInfo(BigDecimal valor, int cantidad) {}
    record ConfianzaInfo(ReputacionInfo reputacion, NivelVerificacion nivelVerificacion) {}

    ConfianzaInfo confianzaDe(UUID prestadorId);
}
