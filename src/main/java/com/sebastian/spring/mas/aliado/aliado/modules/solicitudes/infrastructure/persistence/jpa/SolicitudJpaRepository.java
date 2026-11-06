package com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.jpa;

import com.sebastian.spring.mas.aliado.aliado.modules.solicitudes.infrastructure.persistence.entity.SolicitudEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudJpaRepository extends JpaRepository<SolicitudEntity, UUID> {
    List<SolicitudEntity> findByClienteIdOrderByFechaCreacionDesc(UUID clienteId);
    List<SolicitudEntity> findByEstadoOrderByFechaCreacionDesc(String estado);
    List<SolicitudEntity> findByDescripcionContainingIgnoreCase(String texto);
}
