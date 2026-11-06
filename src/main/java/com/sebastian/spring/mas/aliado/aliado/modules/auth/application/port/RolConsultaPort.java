package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port;

import java.util.UUID;

/** Consulta de rol para otros módulos (SPEC-14: indicadores solo para prestadores). */
public interface RolConsultaPort {
    boolean esPrestador(UUID usuarioId);
}
