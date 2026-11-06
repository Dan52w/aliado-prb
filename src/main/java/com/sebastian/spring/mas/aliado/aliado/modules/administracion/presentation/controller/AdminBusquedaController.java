package com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.application.BusquedaAdminService;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.presentation.dto.AdminDtos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Búsqueda administrativa (SPEC-12, UC075). Solo rol ADMINISTRADOR (ver SecurityConfig). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminBusquedaController {
    private final BusquedaAdminService busqueda;

    public AdminBusquedaController(BusquedaAdminService busqueda) {
        this.busqueda = busqueda;
    }

    @GetMapping("/busqueda")
    public ResponseEntity<AdminDtos.BusquedaResponse> busqueda(@RequestParam String q) {
        if (q == null || q.isBlank()) {
            throw new BusinessException.BadRequest("BUSQUEDA_REQUERIDA",
                    "El parámetro q es obligatorio para buscar.");
        }
        var resultado = busqueda.buscar(q.trim());
        return ResponseEntity.ok(new AdminDtos.BusquedaResponse(
                resultado.usuarios().stream()
                        .map(u -> new AdminDtos.UsuarioResumen(u.id(), u.email(), u.rol()))
                        .toList(),
                resultado.solicitudes().stream()
                        .map(s -> new AdminDtos.SolicitudResumen(s.id(), s.descripcion(), s.estado()))
                        .toList()));
    }
}
