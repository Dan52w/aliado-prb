package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.application.PreferenciasService;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.TipoAviso;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.presentation.dto.PreferenciasDtos;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Preferencias, historial, indicadores y actividad (SPEC-14). */
@RestController
@RequestMapping("/api/v1")
public class PreferenciasController {
    private final PreferenciasService service;

    public PreferenciasController(PreferenciasService service) {
        this.service = service;
    }

    @GetMapping("/preferencias/notificaciones")
    public ResponseEntity<List<PreferenciasDtos.PreferenciaResponse>> listar() {
        return ResponseEntity.ok(service.listarPreferencias(SecurityUtils.actual().id()).stream()
                .map(p -> new PreferenciasDtos.PreferenciaResponse(p.tipoAviso().name(), p.canal(),
                        p.habilitada(), p.configurable()))
                .toList());
    }

    @PutMapping("/preferencias/notificaciones")
    public ResponseEntity<PreferenciasDtos.PreferenciaResponse> actualizar(
            @Valid @RequestBody PreferenciasDtos.ActualizarPreferenciaRequest req) {
        TipoAviso tipo;
        try {
            tipo = TipoAviso.valueOf(req.tipoAviso());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException.BadRequest("TIPO_AVISO_INVALIDO", "Tipo de aviso no reconocido.");
        }
        var p = service.actualizarPreferencia(SecurityUtils.actual().id(), tipo, req.canal(), req.habilitada());
        return ResponseEntity.ok(new PreferenciasDtos.PreferenciaResponse(p.tipoAviso().name(), p.canal(),
                p.habilitada(), p.configurable()));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<PreferenciasDtos.ServicioResumen>> historial(
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "20") int tamano) {
        return ResponseEntity.ok(service.historial(SecurityUtils.actual().id(), pagina, tamano).stream()
                .map(s -> new PreferenciasDtos.ServicioResumen(s.id(), s.estado().name(), s.fechaContratacion()))
                .toList());
    }

    @PostMapping("/historial/exportaciones")
    public ResponseEntity<PreferenciasDtos.DescargaResponse> exportar() {
        var d = service.exportarHistorial(SecurityUtils.actual().id());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new PreferenciasDtos.DescargaResponse(d.id(), d.archivoKey(), d.estado()));
    }

    @GetMapping("/prestadores/mis-indicadores")
    public ResponseEntity<PreferenciasDtos.IndicadoresResponse> misIndicadores() {
        var i = service.misIndicadores(SecurityUtils.actual().id());
        return ResponseEntity.ok(new PreferenciasDtos.IndicadoresResponse(i.totalServicios(), i.enCurso(),
                i.finalizados(), i.reputacion(), i.cantidadCalificaciones()));
    }

    @GetMapping("/actividad-reciente")
    public ResponseEntity<List<PreferenciasDtos.ActividadResponse>> actividadReciente() {
        return ResponseEntity.ok(service.actividadReciente(SecurityUtils.actual().id()).stream()
                .map(a -> new PreferenciasDtos.ActividadResponse(a.tipo(), a.resumen(), a.fecha()))
                .toList());
    }
}
