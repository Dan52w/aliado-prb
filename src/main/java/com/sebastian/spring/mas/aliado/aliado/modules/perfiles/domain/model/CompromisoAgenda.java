package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CompromisoAgenda(UUID id, UUID perfilId, OffsetDateTime fechaHoraInicio,
                               OffsetDateTime fechaHoraFin, OrigenCompromiso origen) {}
