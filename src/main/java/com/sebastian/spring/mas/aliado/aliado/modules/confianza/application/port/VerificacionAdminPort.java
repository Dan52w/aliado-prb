package com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port;

import java.util.UUID;

/** Resolución administrativa de verificaciones (SPEC-12 administra; SPEC-10 define la solicitud). */
public interface VerificacionAdminPort {
    void resolver(UUID solicitudId, String estado, UUID administradorId);
}
