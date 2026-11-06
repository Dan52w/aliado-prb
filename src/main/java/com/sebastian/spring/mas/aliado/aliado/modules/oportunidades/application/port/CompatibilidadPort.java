package com.sebastian.spring.mas.aliado.aliado.modules.oportunidades.application.port;

import java.util.List;
import java.util.UUID;

/** Contrato público de compatibilidad (SPEC-06): coincidencia booleana categoría + zona + disponibilidad. */
public interface CompatibilidadPort {
    boolean esCompatible(UUID solicitudId, UUID prestadorId);
    List<UUID> prestadoresCompatibles(UUID solicitudId);
}
