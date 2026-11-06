package com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model;

import java.util.UUID;

/** Datos básicos editables del usuario (SPEC-02). */
public record Perfil(UUID usuarioId, String nombre, String telefono,
                     Visibilidad preferenciaVisibilidad, int version) {}
