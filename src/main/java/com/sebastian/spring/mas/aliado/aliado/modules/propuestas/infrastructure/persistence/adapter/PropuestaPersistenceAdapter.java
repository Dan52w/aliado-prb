package com.sebastian.spring.mas.aliado.aliado.modules.propuestas.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model.EstadoPropuesta;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.model.Propuesta;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.domain.repository.PropuestaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.infrastructure.persistence.jpa.PropuestaJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.propuestas.infrastructure.persistence.mapper.PropuestaPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PropuestaPersistenceAdapter implements PropuestaRepository {
    private final PropuestaJpaRepository jpa;

    public PropuestaPersistenceAdapter(PropuestaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Propuesta> porId(UUID id) {
        return jpa.findById(id).map(PropuestaPersistenceMapper::aDominio);
    }

    @Override
    public Optional<Propuesta> activaDePrestadorEnSolicitud(UUID solicitudId, UUID prestadorId) {
        return jpa.findBySolicitudIdAndPrestadorIdAndEstado(solicitudId, prestadorId, EstadoPropuesta.ACTIVA.name())
                .map(PropuestaPersistenceMapper::aDominio);
    }

    @Override
    public List<Propuesta> activasDeSolicitud(UUID solicitudId) {
        return jpa.findBySolicitudIdAndEstado(solicitudId, EstadoPropuesta.ACTIVA.name()).stream()
                .map(PropuestaPersistenceMapper::aDominio).toList();
    }

    @Override
    public List<Propuesta> porSolicitud(UUID solicitudId) {
        return jpa.findBySolicitudId(solicitudId).stream().map(PropuestaPersistenceMapper::aDominio).toList();
    }

    @Override
    public Propuesta guardar(Propuesta propuesta) {
        return PropuestaPersistenceMapper.aDominio(jpa.save(PropuestaPersistenceMapper.aEntidad(propuesta)));
    }
}
