package com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.BloqueoCuentaService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.ModeracionService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.dto.AdminDtos;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Moderación y bloqueos de cuentas (SPEC-12). Solo rol ADMINISTRADOR (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminModeracionController {
    private final BloqueoCuentaService bloqueos;
    private final ModeracionService moderacion;

    public AdminModeracionController(BloqueoCuentaService bloqueos, ModeracionService moderacion) {
        this.bloqueos = bloqueos;
        this.moderacion = moderacion;
    }

    @PostMapping("/usuarios/{id}/bloqueos")
    public ResponseEntity<AdminDtos.BloqueoResponse> bloquearUsuario(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.BloqueoRequest req) {
        var b = bloqueos.bloquearUsuario(id, SecurityUtils.actual().id(), req.motivo());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AdminDtos.BloqueoResponse(b.id(), b.usuarioId(), b.motivo(), b.activo()));
    }

    @PostMapping("/usuarios/{id}/bloqueos/{bloqueoId}/levantar")
    public ResponseEntity<Void> levantarBloqueo(@PathVariable UUID id, @PathVariable UUID bloqueoId) {
        bloqueos.levantarBloqueo(id, bloqueoId, SecurityUtils.actual().id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/moderacion")
    public ResponseEntity<Void> moderar(@Valid @RequestBody AdminDtos.ModeracionRequest req) {
        moderacion.moderar(SecurityUtils.actual().id(), req.objetivoTipo(), req.objetivoId(), req.accion(), req.motivo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verificaciones/{id}/resolver")
    public ResponseEntity<Void> resolverVerificacion(@PathVariable UUID id,
            @Valid @RequestBody AdminDtos.ResolverVerificacionRequest req) {
        moderacion.resolverVerificacion(id, SecurityUtils.actual().id(), req.estado(), req.justificacion());
        return ResponseEntity.noContent().build();
    }
}
