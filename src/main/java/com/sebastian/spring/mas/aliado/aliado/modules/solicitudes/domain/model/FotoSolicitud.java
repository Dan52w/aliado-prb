package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model;

import java.util.UUID;

public record FotoSolicitud(UUID id, UUID solicitudId, String archivoKey, int orden) {}
