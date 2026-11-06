package com.sebastian.spring.mas.aliado.aliado.modules.perfil.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.application.PerfilService;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.SolicitudEliminacion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.presentation.dto.PerfilDtos;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Gestión de perfil y privacidad (SPEC-02). */
@RestController
@RequestMapping("/api/v1/perfil")
public class PerfilController {
    private final PerfilService perfilService;

    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    private PerfilDtos.PerfilResponse aRespuesta(PerfilService.PerfilPropio p) {
        return new PerfilDtos.PerfilResponse(p.usuarioId(), p.nombre(), p.telefono(),
                p.preferenciaVisibilidad(), p.zonaAproximada(), p.direccionExacta(), p.version());
    }

    @GetMapping("/me")
    public ResponseEntity<PerfilDtos.PerfilResponse> me() {
        return ResponseEntity.ok(aRespuesta(perfilService.consultarMe(SecurityUtils.actual().id())));
    }

    @PatchMapping("/me")
    public ResponseEntity<PerfilDtos.PerfilResponse> actualizar(
            @Valid @RequestBody PerfilDtos.ActualizarPerfilRequest req) {
        return ResponseEntity.ok(aRespuesta(perfilService.actualizarMe(
                SecurityUtils.actual().id(), req.nombre(), req.telefono(), req.version())));
    }

    @PutMapping("/me/ubicacion")
    public ResponseEntity<PerfilDtos.PerfilResponse> ubicacion(
            @Valid @RequestBody PerfilDtos.UbicacionRequest req) {
        return ResponseEntity.ok(aRespuesta(perfilService.configurarUbicacion(
                SecurityUtils.actual().id(), req.zonaAproximada(), req.direccionExacta())));
    }

    @PatchMapping("/me/visibilidad")
    public ResponseEntity<PerfilDtos.PerfilResponse> visibilidad(
            @Valid @RequestBody PerfilDtos.VisibilidadRequest req) {
        return ResponseEntity.ok(aRespuesta(perfilService.configurarVisibilidad(
                SecurityUtils.actual().id(), req.preferenciaVisibilidad())));
    }

    @GetMapping("/{usuarioId}/publico")
    public ResponseEntity<PerfilDtos.PerfilPublicoResponse> publico(@PathVariable UUID usuarioId) {
        PerfilService.PerfilPropio p = perfilService.perfilPublico(usuarioId);
        return ResponseEntity.ok(new PerfilDtos.PerfilPublicoResponse(p.usuarioId(), p.zonaAproximada()));
    }

    @PostMapping("/me/eliminacion")
    public ResponseEntity<PerfilDtos.EliminacionResponse> solicitarEliminacion() {
        SolicitudEliminacion s = perfilService.solicitarEliminacion(SecurityUtils.actual().id());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new PerfilDtos.EliminacionResponse(s.estado().name(), s.fechaExpiracion()));
    }

    @PostMapping("/me/eliminacion/confirmar")
    public ResponseEntity<PerfilDtos.EliminacionResponse> confirmarEliminacion() {
        SolicitudEliminacion s = perfilService.confirmarEliminacion(SecurityUtils.actual().id());
        return ResponseEntity.ok(new PerfilDtos.EliminacionResponse(s.estado().name(), s.fechaExpiracion()));
    }
}
