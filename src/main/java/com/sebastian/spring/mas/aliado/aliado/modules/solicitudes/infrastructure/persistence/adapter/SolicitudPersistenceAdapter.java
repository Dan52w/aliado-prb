package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.EstadoSolicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.model.Solicitud;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.domain.repository.SolicitudRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.jpa.SolicitudJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.mapper.SolicitudPersistenceMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SolicitudPersistenceAdapter implements SolicitudRepository {
    private final SolicitudJpaRepository jpa;

    public SolicitudPersistenceAdapter(SolicitudJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Solicitud> porId(UUID id) {
        return jpa.findById(id).map(SolicitudPersistenceMapper::aDominio);
    }

    @Override
    public List<Solicitud> porCliente(UUID clienteId) {
        return jpa.findByClienteIdOrderByFechaCreacionDesc(clienteId).stream()
                .map(SolicitudPersistenceMapper::aDominio).toList();
    }

    @Override
    public List<Solicitud> publicadas() {
        return jpa.findByEstadoOrderByFechaCreacionDesc(EstadoSolicitud.PUBLICADA.name()).stream()
                .map(SolicitudPersistenceMapper::aDominio).toList();
    }

    @Override
    public Solicitud guardar(Solicitud solicitud) {
        return SolicitudPersistenceMapper.aDominio(jpa.save(SolicitudPersistenceMapper.aEntidad(solicitud)));
    }

    @Override
    public List<Solicitud> buscarPorTexto(String texto) {
        List<Solicitud> resultados = new ArrayList<>(
                jpa.findByDescripcionContainingIgnoreCase(texto).stream()
                        .map(SolicitudPersistenceMapper::aDominio).toList());
        try {
            jpa.findById(UUID.fromString(texto.trim()))
                    .map(SolicitudPersistenceMapper::aDominio)
                    .filter(s -> resultados.stream().noneMatch(r -> r.id().equals(s.id())))
                    .ifPresent(resultados::add);
        } catch (IllegalArgumentException ignored) {
            // No es un UUID: solo la búsqueda por descripción.
        }
        return resultados;
    }
}
