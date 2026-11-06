package com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Enlace;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.EstadoEnlace;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Paso;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Progreso;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.domain.model.Ruta;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity.EnlaceEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity.PasoEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity.ProgresoEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.formalizacion.infrastructure.persistence.entity.RutaEntity;

public final class FormalizacionPersistenceMapper {
    private FormalizacionPersistenceMapper() {}

    public static Ruta aDominio(RutaEntity e) {
        return new Ruta(e.getId(), e.getNombre(), e.getDescripcion(), e.isActiva());
    }

    public static Paso aDominio(PasoEntity e) {
        return new Paso(e.getId(), e.getRutaId(), e.getTitulo(), e.getDescripcion(), e.getOrden());
    }

    public static Enlace aDominio(EnlaceEntity e) {
        return new Enlace(e.getId(), e.getPasoId(), e.getNombre(), e.getUrl(), EstadoEnlace.valueOf(e.getEstado()));
    }

    public static Progreso aDominio(ProgresoEntity e) {
        return new Progreso(e.getId(), e.getPrestadorId(), e.getRutaId(), e.getValorOrientativo(),
                e.getFechaActualizacion());
    }

    public static ProgresoEntity aEntidad(Progreso p) {
        ProgresoEntity e = new ProgresoEntity();
        e.setId(p.id());
        e.setPrestadorId(p.prestadorId());
        e.setRutaId(p.rutaId());
        e.setValorOrientativo(p.valorOrientativo());
        e.setFechaActualizacion(p.fechaActualizacion());
        return e;
    }

    public static EnlaceEntity aEntidad(Enlace enlace) {
        EnlaceEntity e = new EnlaceEntity();
        e.setId(enlace.id());
        e.setPasoId(enlace.pasoId());
        e.setNombre(enlace.nombre());
        e.setUrl(enlace.url());
        e.setEstado(enlace.estado().name());
        return e;
    }
}
