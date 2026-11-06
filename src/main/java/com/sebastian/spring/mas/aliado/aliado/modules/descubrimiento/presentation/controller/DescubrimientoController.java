package com.sebastian.spring.mas.aliado.aliado.modules.descubrimiento.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.descubrimiento.application.DescubrimientoService;
import com.sebastian.spring.mas.aliado.aliado.modules.descubrimiento.domain.model.PrestadorPublico;
import com.sebastian.spring.mas.aliado.aliado.modules.descubrimiento.presentation.dto.DescubrimientoDtos;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Descubrimiento de prestadores (SPEC-03). Solo CLIENTE, incluido visitante sin sesión denegado (403). */
@RestController
@RequestMapping("/api/v1/descubrimiento")
public class DescubrimientoController {
    private final DescubrimientoService service;

    public DescubrimientoController(DescubrimientoService service) {
        this.service = service;
    }

    private DescubrimientoDtos.PrestadorPublicoResponse aRespuesta(PrestadorPublico p) {
        return new DescubrimientoDtos.PrestadorPublicoResponse(p.id(), p.categorias(), p.zonas(),
                p.reputacionAgregada(), p.cantidadCalificaciones(), p.nivelVerificacion(),
                p.disponibilidad().stream()
                        .map(b -> new DescubrimientoDtos.BloqueResponse(b.diaSemana(), b.horaInicio(), b.horaFin()))
                        .toList(),
                p.datosGenerales());
    }

    @GetMapping("/prestadores")
    public ResponseEntity<DescubrimientoDtos.BusquedaResponse> buscar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String zona) {
        SecurityUtils.exigirRol("CLIENTE");
        var resultado = service.buscar(categoria, zona);
        return ResponseEntity.ok(new DescubrimientoDtos.BusquedaResponse(
                resultado.prestadores().stream().map(this::aRespuesta).toList(), resultado.sugerencias()));
    }

    @GetMapping("/prestadores/{id}/perfil")
    public ResponseEntity<DescubrimientoDtos.PrestadorPublicoResponse> detalle(@PathVariable UUID id) {
        SecurityUtils.exigirRol("CLIENTE");
        return ResponseEntity.ok(aRespuesta(service.detalle(id)));
    }
}
