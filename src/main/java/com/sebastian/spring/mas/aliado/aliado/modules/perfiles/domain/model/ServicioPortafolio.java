package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ServicioPortafolio(UUID id, UUID perfilId, String descripcion,
                                 String evidenciaKey, OffsetDateTime fechaCreacion) {}
