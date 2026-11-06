package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto;

import java.util.UUID;

/** Salida del caso de uso de registro; independiente de HTTP y persistencia. */
public record ResultadoRegistro(UUID id, String email, String rol) {}
