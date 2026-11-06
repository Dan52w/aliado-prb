package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.adapter;

import com.sebastian.spring.mas.aliado.aliado.common.events.Eventos;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.CuentaBloqueoPort;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Eliminación confirmada (SPEC-02): la cuenta se desactiva de verdad.
 * Bloquearla impide el login y cierra todas sus sesiones (mismo efecto observable
 * que un bloqueo preventivo; la anonimización de datos sigue pendiente de producto).
 */
@Component
public class EliminacionEventListener {
    private final CuentaBloqueoPort cuentas;

    public EliminacionEventListener(CuentaBloqueoPort cuentas) {
        this.cuentas = cuentas;
    }

    @EventListener
    public void alConfirmarEliminacion(Eventos.EliminacionConfirmada evento) {
        cuentas.bloquearCuenta(evento.usuarioId());
    }
}
