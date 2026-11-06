package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port;

import java.util.UUID;

/** Bloqueo preventivo de cuentas (SPEC-12): la moderación de contenido no implica bloquear. */
public interface CuentaBloqueoPort {
    void bloquearCuenta(UUID usuarioId);
    void desbloquearCuenta(UUID usuarioId);
}
