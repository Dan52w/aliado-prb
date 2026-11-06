package com.sebastian.spring.mas.aliado.aliado.modules.administracion.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.Auditoria;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.model.MarcaAltoRiesgo;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.AuditoriaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.administracion.domain.repository.MarcaRiesgoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.servicios.application.port.ServicioLecturaPort;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Marcas de alto riesgo y lectura de auditoría (SPEC-12, UC073–UC076). */
@Service
public class RiesgoAuditoriaService {
    private final MarcaRiesgoRepository marcas;
    private final ServicioLecturaPort servicios;
    private final AuditoriaRepository auditoria;
    private final AuditoriaService auditoriaService;

    public RiesgoAuditoriaService(MarcaRiesgoRepository marcas, ServicioLecturaPort servicios,
                                  AuditoriaRepository auditoria, AuditoriaService auditoriaService) {
        this.marcas = marcas;
        this.servicios = servicios;
        this.auditoria = auditoria;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public MarcaAltoRiesgo marcarAltoRiesgo(UUID servicioId, UUID administradorId, String criterio) {
        if (!servicios.porId(servicioId).isPresent()) {
            throw new BusinessException.NotFound("SERVICIO_NO_ENCONTRADO", "Servicio no encontrado.");
        }
        if (criterio == null || criterio.isBlank()) {
            throw new BusinessException.BadRequest("CRITERIO_REQUERIDO", "El criterio es obligatorio.");
        }
        if (marcas.activaDeServicio(servicioId).isPresent()) {
            throw new BusinessException.Conflict("MARCA_ACTIVA", "El servicio ya tiene una marca activa.");
        }
        MarcaAltoRiesgo marca = marcas.guardar(new MarcaAltoRiesgo(UUID.randomUUID(), servicioId,
                criterio.trim(), administradorId, OffsetDateTime.now(), true));
        auditoriaService.registrar(administradorId, "HIGH_RISK_MARKED", "SERVICIO", servicioId, criterio.trim());
        return marca;
    }

    @Transactional(readOnly = true)
    public List<Auditoria> listarAuditoria(int limite) {
        return auditoria.recientes(Math.max(1, Math.min(limite, 100)));
    }
}
