package com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model;

import java.util.UUID;

/** Ubicación: zona aproximada (pública) y dirección exacta (privada, cifrada en reposo). */
public record Ubicacion(UUID usuarioId, String zonaAproximada, String direccionExacta, int version) {}
