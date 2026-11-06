package com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.scheduler;

import com.sebastian.spring.mas.aliado.aliado.modules.perfil.application.PerfilService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Marca como EXPIRADA cada hora las solicitudes de eliminación pendientes con más de 24 h (SPEC-02). */
@Component
public class ExpiracionEliminacionScheduler {
    private final PerfilService perfilService;

    public ExpiracionEliminacionScheduler(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @Scheduled(fixedDelay = 3_600_000)
    public void expirarVencidas() {
        perfilService.expirarSolicitudesVencidas();
    }
}
