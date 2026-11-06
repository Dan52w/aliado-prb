package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Rol;

/** Entrada del caso de uso de registro; independiente del transporte HTTP. */
public record RegistrarUsuarioCommand(
        String email,
        String password,
        Rol rol,
        boolean aceptaTerminos,
        String versionTerminos) {}
