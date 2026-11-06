package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto;

import java.util.UUID;

/** Datos necesarios para registrar la aceptación de términos de un usuario recién creado. */
public record RegistrarConsentimientoCommand(
        UUID usuarioId,
        boolean aceptaTerminos,
        String versionTerminos) {}
