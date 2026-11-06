package com.sebastian.spring.mas.aliado.aliado.modules.servicios.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public record HistorialEstadoServicio(UUID id, UUID servicioId, EstadoServicio estadoAnterior,
                                      EstadoServicio estadoNuevo, UUID actorId, OffsetDateTime fecha) {}
