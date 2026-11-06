package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.mapper.ConfianzaPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.SolicitudVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.SolicitudVerificacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.jpa.SolicitudVerificacionJpaRepository;

/** Adaptador de solicitudes de verificación (SPEC-10). */
@Component
public class SolicitudVerificacionPersistenceAdapter implements SolicitudVerificacionRepository {
    private final SolicitudVerificacionJpaRepository verificaciones;

    public SolicitudVerificacionPersistenceAdapter(SolicitudVerificacionJpaRepository verificaciones) {
        this.verificaciones = verificaciones;
    }

    @Override
    public Optional<SolicitudVerificacion> porId(UUID id) {
        return verificaciones.findById(id).map(ConfianzaPersistenceMapper::aDominio);
    }

    @Override
    public Optional<SolicitudVerificacion> pendienteDePrestador(UUID prestadorId) {
        return verificaciones.findByPrestadorIdAndEstado(prestadorId, "PENDIENTE")
                .map(ConfianzaPersistenceMapper::aDominio);
    }

    @Override
    public Optional<SolicitudVerificacion> ultimaDePrestador(UUID prestadorId) {
        return verificaciones.findFirstByPrestadorIdOrderByFechaSolicitudDesc(prestadorId)
                .map(ConfianzaPersistenceMapper::aDominio);
    }

    @Override
    public long contarAprobadasDePrestador(UUID prestadorId) {
        return verificaciones.countByPrestadorIdAndEstado(prestadorId, "APROBADA");
    }

    @Override
    public List<SolicitudVerificacion> pendientes() {
        return verificaciones.findByEstadoOrderByFechaSolicitudAsc("PENDIENTE").stream()
                .map(ConfianzaPersistenceMapper::aDominio).toList();
    }

    @Override
    public SolicitudVerificacion guardar(SolicitudVerificacion solicitud) {
        return ConfianzaPersistenceMapper.aDominio(
                verificaciones.save(ConfianzaPersistenceMapper.aEntidad(solicitud)));
    }
}
