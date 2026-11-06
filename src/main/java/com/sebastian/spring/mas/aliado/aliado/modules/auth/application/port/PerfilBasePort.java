package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port;

import java.util.UUID;

/** Creación del perfil base al registrar un usuario (SPEC-01/SPEC-02, FR-001/FR-002). */
public interface PerfilBasePort {
    void crearPerfilBase(UUID usuarioId);
}
