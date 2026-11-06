package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.CompromisoAgenda;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.Disponibilidad;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.EstadoExposicion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.ExperienciaProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.OrigenCompromiso;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.ServicioPortafolio;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.entity.CompromisoAgendaEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.entity.DisponibilidadEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.entity.ExperienciaEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.entity.PerfilProfesionalEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.infrastructure.persistence.entity.ServicioPortafolioEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public final class PerfilProfesionalPersistenceMapper {
    private PerfilProfesionalPersistenceMapper() {}

    public static PerfilProfesional aDominio(PerfilProfesionalEntity e, ObjectMapper mapper) {
        Map<String, Object> datos = Map.of();
        if (e.getDatosGenerales() != null && !e.getDatosGenerales().isBlank()) {
            try {
                datos = mapper.readValue(e.getDatosGenerales(), new TypeReference<Map<String, Object>>() {});
            } catch (Exception ex) {
                datos = Map.of("raw", e.getDatosGenerales());
            }
        }
        return new PerfilProfesional(e.getId(), e.getPrestadorId(), datos,
                EstadoExposicion.valueOf(e.getEstadoExposicionPublica()), e.getFechaCreacion(),
                e.getFechaActualizacion(), new HashSet<>(e.getCategoriaIds()), new HashSet<>(e.getZonaIds()),
                e.getExperiencias().stream().map(x -> new ExperienciaProfesional(x.getId(), e.getId(),
                        x.getDescripcion(), x.getFechaInicio(), x.getFechaFin())).toList(),
                e.getPortafolio().stream().map(s -> new ServicioPortafolio(s.getId(), e.getId(),
                        s.getDescripcion(), s.getEvidenciaKey(), s.getFechaCreacion())).toList(),
                e.getDisponibilidades().stream().map(d -> new Disponibilidad(d.getId(), e.getId(),
                        d.getDiaSemana(), d.getHoraInicio(), d.getHoraFin())).toList(),
                e.getCompromisos().stream().map(c -> new CompromisoAgenda(c.getId(), e.getId(),
                        c.getFechaHoraInicio(), c.getFechaHoraFin(), OrigenCompromiso.valueOf(c.getOrigen()))).toList());
    }

    public static PerfilProfesionalEntity aEntidad(PerfilProfesional p, ObjectMapper mapper) {
        PerfilProfesionalEntity e = new PerfilProfesionalEntity();
        e.setId(p.id());
        e.setPrestadorId(p.prestadorId());
        try {
            e.setDatosGenerales(p.datosGenerales() == null ? null : mapper.writeValueAsString(p.datosGenerales()));
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo serializar datosGenerales.", ex);
        }
        e.setEstadoExposicionPublica(p.estadoExposicion().name());
        e.setFechaCreacion(p.fechaCreacion());
        e.setFechaActualizacion(p.fechaActualizacion());
        e.setCategoriaIds(new HashSet<>(p.categoriaIds()));
        e.setZonaIds(new HashSet<>(p.zonaIds()));
        List<ExperienciaEntity> experiencias = p.experiencias().stream().map(x -> {
            ExperienciaEntity xe = new ExperienciaEntity();
            xe.setId(x.id());
            xe.setPerfil(e);
            xe.setDescripcion(x.descripcion());
            xe.setFechaInicio(x.fechaInicio());
            xe.setFechaFin(x.fechaFin());
            return xe;
        }).toList();
        e.setExperiencias(new java.util.ArrayList<>(experiencias));
        List<ServicioPortafolioEntity> portafolio = p.portafolio().stream().map(s -> {
            ServicioPortafolioEntity se = new ServicioPortafolioEntity();
            se.setId(s.id());
            se.setPerfil(e);
            se.setDescripcion(s.descripcion());
            se.setEvidenciaKey(s.evidenciaKey());
            se.setFechaCreacion(s.fechaCreacion());
            return se;
        }).toList();
        e.setPortafolio(new java.util.ArrayList<>(portafolio));
        List<DisponibilidadEntity> disponibilidades = p.disponibilidades().stream().map(d -> {
            DisponibilidadEntity de = new DisponibilidadEntity();
            de.setId(d.id());
            de.setPerfil(e);
            de.setDiaSemana(d.diaSemana());
            de.setHoraInicio(d.horaInicio());
            de.setHoraFin(d.horaFin());
            return de;
        }).toList();
        e.setDisponibilidades(new java.util.ArrayList<>(disponibilidades));
        List<CompromisoAgendaEntity> compromisos = p.compromisos().stream().map(c -> {
            CompromisoAgendaEntity ce = new CompromisoAgendaEntity();
            ce.setId(c.id());
            ce.setPerfil(e);
            ce.setFechaHoraInicio(c.fechaHoraInicio());
            ce.setFechaHoraFin(c.fechaHoraFin());
            ce.setOrigen(c.origen().name());
            return ce;
        }).toList();
        e.setCompromisos(new java.util.ArrayList<>(compromisos));
        return e;
    }
}
