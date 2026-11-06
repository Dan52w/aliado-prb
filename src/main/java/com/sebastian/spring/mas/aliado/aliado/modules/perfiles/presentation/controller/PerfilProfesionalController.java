package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.web.SecurityUtils;
import com.sebastian.spring.mas.aliado.aliado.modules.catalogo.application.port.CatalogoConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.application.port.ConfianzaConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application.PerfilProfesionalService;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.Disponibilidad;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.EstadoExposicion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.ExperienciaProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.presentation.dto.PerfilProfesionalDtos;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Perfil profesional del Prestador (SPEC-05). Todas las rutas exigen rol PRESTADOR. */
@RestController
@RequestMapping("/api/v1/perfiles")
public class PerfilProfesionalController {
    private final PerfilProfesionalService service;
    private final ConfianzaConsultaPort confianza;
    private final CatalogoConsultaPort catalogo;

    public PerfilProfesionalController(PerfilProfesionalService service, ConfianzaConsultaPort confianza,
                                       CatalogoConsultaPort catalogo) {
        this.service = service;
        this.confianza = confianza;
        this.catalogo = catalogo;
    }

    private PerfilProfesionalDtos.PerfilResponse aRespuesta(PerfilProfesional p) {
        return new PerfilProfesionalDtos.PerfilResponse(p.id(), p.prestadorId(), p.datosGenerales(),
                p.estadoExposicion().name(), List.copyOf(p.categoriaIds()), List.copyOf(p.zonaIds()),
                p.experiencias().stream().map(e -> new PerfilProfesionalDtos.ExperienciaResponse(
                        e.id(), e.descripcion(), e.fechaInicio(), e.fechaFin())).toList(),
                p.portafolio().stream().map(s -> new PerfilProfesionalDtos.PortafolioResponse(
                        s.id(), s.descripcion(), s.evidenciaKey(), s.fechaCreacion())).toList(),
                p.disponibilidades().stream().map(d -> new PerfilProfesionalDtos.DisponibilidadResponse(
                        d.id(), d.diaSemana(), d.horaInicio(), d.horaFin())).toList(),
                p.compromisos().stream().map(c -> new PerfilProfesionalDtos.CompromisoResponse(
                        c.id(), c.fechaHoraInicio(), c.fechaHoraFin(), c.origen().name())).toList());
    }

    @PostMapping
    public ResponseEntity<PerfilProfesionalDtos.PerfilResponse> crear(
            @Valid @RequestBody PerfilProfesionalDtos.CrearRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        List<ExperienciaProfesional> experiencias = req.experiencia() == null ? List.of()
                : req.experiencia().stream().map(e -> new ExperienciaProfesional(
                        UUID.randomUUID(), null, e.descripcion(), e.fechaInicio(), e.fechaFin())).toList();
        PerfilProfesional creado = service.crear(SecurityUtils.actual().id(), req.datosGenerales(),
                req.categoriaIds(), req.zonaIds(), experiencias);
        return ResponseEntity.status(HttpStatus.CREATED).body(aRespuesta(creado));
    }

    @PutMapping("/mio")
    public ResponseEntity<PerfilProfesionalDtos.PerfilResponse> actualizar(
            @Valid @RequestBody PerfilProfesionalDtos.ActualizarDatosRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(aRespuesta(service.actualizarDatos(SecurityUtils.actual().id(), req.datosGenerales())));
    }

    @PutMapping("/mio/categorias")
    public ResponseEntity<PerfilProfesionalDtos.PerfilResponse> categorias(
            @Valid @RequestBody PerfilProfesionalDtos.IdsRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(aRespuesta(service.actualizarCategorias(SecurityUtils.actual().id(), req.ids())));
    }

    @PutMapping("/mio/zonas")
    public ResponseEntity<PerfilProfesionalDtos.PerfilResponse> zonas(
            @Valid @RequestBody PerfilProfesionalDtos.IdsRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(aRespuesta(service.actualizarZonas(SecurityUtils.actual().id(), req.ids())));
    }

    @PostMapping("/mio/experiencia")
    public ResponseEntity<PerfilProfesionalDtos.ExperienciaResponse> experiencia(
            @Valid @RequestBody PerfilProfesionalDtos.ExperienciaRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        var e = service.agregarExperiencia(SecurityUtils.actual().id(), req.descripcion(),
                req.fechaInicio(), req.fechaFin());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new PerfilProfesionalDtos.ExperienciaResponse(e.id(), e.descripcion(), e.fechaInicio(), e.fechaFin()));
    }

    @PostMapping(value = "/mio/portafolio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PerfilProfesionalDtos.PortafolioResponse> portafolio(
            @RequestPart("descripcion") String descripcion,
            @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        SecurityUtils.exigirRol("PRESTADOR");
        byte[] contenido = null;
        if (archivo != null) {
            try {
                contenido = archivo.getBytes();
            } catch (IOException ex) {
                throw new BusinessException.BadRequest("ARCHIVO_INVALIDO",
                        "No se pudo leer el archivo enviado.");
            }
        }
        var s = service.agregarPortafolio(SecurityUtils.actual().id(), descripcion,
                archivo == null ? null : archivo.getOriginalFilename(),
                archivo == null ? null : archivo.getContentType(), contenido);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new PerfilProfesionalDtos.PortafolioResponse(s.id(), s.descripcion(), s.evidenciaKey(), s.fechaCreacion()));
    }

    @DeleteMapping("/mio/portafolio/{id}")
    public ResponseEntity<Void> eliminarPortafolio(@PathVariable UUID id) {
        SecurityUtils.exigirRol("PRESTADOR");
        service.eliminarPortafolio(SecurityUtils.actual().id(), id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/mio/disponibilidad")
    public ResponseEntity<PerfilProfesionalDtos.PerfilResponse> disponibilidad(
            @Valid @RequestBody PerfilProfesionalDtos.DisponibilidadRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        List<Disponibilidad> bloques = req.bloques().stream()
                .map(b -> new Disponibilidad(null, null, b.diaSemana(), b.horaInicio(), b.horaFin())).toList();
        return ResponseEntity.ok(aRespuesta(service.actualizarDisponibilidad(SecurityUtils.actual().id(), bloques)));
    }

    @PostMapping("/mio/agenda")
    public ResponseEntity<PerfilProfesionalDtos.CompromisoResponse> crearCompromiso(
            @Valid @RequestBody PerfilProfesionalDtos.CompromisoRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        var c = service.crearCompromiso(SecurityUtils.actual().id(), req.fechaHoraInicio(), req.fechaHoraFin());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new PerfilProfesionalDtos.CompromisoResponse(c.id(), c.fechaHoraInicio(), c.fechaHoraFin(), c.origen().name()));
    }

    @PutMapping("/mio/agenda")
    public ResponseEntity<PerfilProfesionalDtos.CompromisoResponse> actualizarCompromiso(
            @Valid @RequestBody PerfilProfesionalDtos.CompromisoRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        var c = service.actualizarCompromiso(SecurityUtils.actual().id(), req.id(),
                req.fechaHoraInicio(), req.fechaHoraFin());
        return ResponseEntity.ok(
                new PerfilProfesionalDtos.CompromisoResponse(c.id(), c.fechaHoraInicio(), c.fechaHoraFin(), c.origen().name()));
    }

    @GetMapping("/mio/agenda")
    public ResponseEntity<List<PerfilProfesionalDtos.CompromisoResponse>> agenda() {
        SecurityUtils.exigirRol("PRESTADOR");
        return ResponseEntity.ok(service.listarAgenda(SecurityUtils.actual().id()).stream()
                .map(c -> new PerfilProfesionalDtos.CompromisoResponse(c.id(), c.fechaHoraInicio(),
                        c.fechaHoraFin(), c.origen().name())).toList());
    }

    @GetMapping("/mio/vista-previa")
    public ResponseEntity<PerfilProfesionalDtos.VistaPreviaResponse> vistaPrevia() {
        SecurityUtils.exigirRol("PRESTADOR");
        // Vista previa (SPEC-05): exactamente lo que vería un tercero. Sin compromisos,
        // portafolio ni experiencias; CON reputación, nivel de verificación, reseñas
        // visibles y datos generales filtrados por la lista blanca (SPEC-03).
        // Se compone aquí y no en el servicio: el servicio de perfiles no puede
        // depender de confianza sin cerrar un ciclo entre módulos.
        UUID prestadorId = SecurityUtils.actual().id();
        PerfilProfesional perfil = service.mio(prestadorId);
        var confianzaInfo = confianza.confianzaDe(prestadorId);
        return ResponseEntity.ok(new PerfilProfesionalDtos.VistaPreviaResponse(prestadorId,
                perfil.categoriaIds().stream()
                        .map(id -> catalogo.categoriaPorId(id).map(c -> c.nombre()).orElse(id.toString()))
                        .toList(),
                perfil.zonaIds().stream()
                        .map(id -> catalogo.zonaPorId(id).map(z -> z.nombre()).orElse(id.toString()))
                        .toList(),
                confianzaInfo.reputacion().valor(), confianzaInfo.reputacion().cantidad(),
                confianzaInfo.nivelVerificacion().name(),
                perfil.disponibilidades().stream().map(d -> new PerfilProfesionalDtos.DisponibilidadResponse(
                        d.id(), d.diaSemana(), d.horaInicio(), d.horaFin())).toList(),
                PerfilProfesional.datosPublicos(perfil.datosGenerales()),
                confianza.resenasVisiblesDe(prestadorId).stream()
                        .map(r -> new PerfilProfesionalDtos.ResenaPublicaResponse(
                                r.id(), r.valor(), r.texto(), r.fechaCreacion()))
                        .toList()));
    }

    @PutMapping("/mio/exposicion")
    public ResponseEntity<PerfilProfesionalDtos.PerfilResponse> exposicion(
            @Valid @RequestBody PerfilProfesionalDtos.ExposicionRequest req) {
        SecurityUtils.exigirRol("PRESTADOR");
        EstadoExposicion nuevo;
        try {
            nuevo = EstadoExposicion.valueOf(req.estadoExposicion().trim());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new BusinessException.BadRequest("EXPOSICION_INVALIDA",
                    "estadoExposicion debe ser PUBLICO u OCULTO.");
        }
        return ResponseEntity.ok(aRespuesta(
                service.cambiarExposicion(SecurityUtils.actual().id(), nuevo)));
    }
}
