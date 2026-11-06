package com.sebastian.spring.mas.aliado.aliado.modules.perfiles.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.CompromisoAgenda;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.OrigenCompromiso;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.model.PerfilProfesional;
import com.sebastian.spring.mas.aliado.aliado.modules.perfiles.domain.repository.PerfilProfesionalRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Agenda de compromisos del prestador (SPEC-05). Extraída del perfil para centrar cada servicio en su agregado. */
@Service
public class AgendaService {
    private final PerfilProfesionalRepository perfiles;

    public AgendaService(PerfilProfesionalRepository perfiles) {
        this.perfiles = perfiles;
    }

    @Transactional
    public CompromisoAgenda crearCompromiso(UUID prestadorId, OffsetDateTime inicio, OffsetDateTime fin) {
        return guardarCompromiso(prestadorId, null, inicio, fin);
    }

    @Transactional
    public CompromisoAgenda actualizarCompromiso(UUID prestadorId, UUID compromisoId,
                                                 OffsetDateTime inicio, OffsetDateTime fin) {
        if (compromisoId == null) {
            throw new BusinessException.BadRequest("COMPROMISO_INVALIDO",
                    "PUT /mio/agenda exige el id del compromiso; sin id usa POST para crear.");
        }
        return guardarCompromiso(prestadorId, compromisoId, inicio, fin);
    }

    private CompromisoAgenda guardarCompromiso(UUID prestadorId, UUID compromisoId,
                                               OffsetDateTime inicio, OffsetDateTime fin) {
        PerfilProfesional actual = propio(prestadorId);
        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new BusinessException.BadRequest("COMPROMISO_INVALIDO",
                    "El compromiso requiere fin posterior al inicio.");
        }
        if (compromisoId != null && actual.compromisos().stream().noneMatch(c -> c.id().equals(compromisoId))) {
            throw new BusinessException.NotFound("COMPROMISO_NO_ENCONTRADO", "Compromiso no encontrado.");
        }
        // Coherente con la regla disponibilidad/agenda (plan-05 T026): dos compromisos
        // no pueden solaparse; en actualización se excluye el propio compromiso.
        for (CompromisoAgenda otro : actual.compromisos()) {
            if (compromisoId != null && otro.id().equals(compromisoId)) {
                continue;
            }
            if (inicio.isBefore(otro.fechaHoraFin()) && otro.fechaHoraInicio().isBefore(fin)) {
                throw new BusinessException.Conflict("CONFLICTO_AGENDA",
                        "El compromiso se solapa con otro ya registrado.");
            }
        }
        CompromisoAgenda compromiso = new CompromisoAgenda(
                compromisoId == null ? UUID.randomUUID() : compromisoId, actual.id(),
                inicio, fin, OrigenCompromiso.MANUAL);
        perfiles.guardar(actual.conCompromiso(compromiso));
        return compromiso;
    }

    @Transactional(readOnly = true)
    public List<CompromisoAgenda> listarAgenda(UUID prestadorId) {
        return propio(prestadorId).compromisos();
    }

    private PerfilProfesional propio(UUID prestadorId) {
        return perfiles.porPrestadorId(prestadorId)
                .orElseThrow(() -> new BusinessException.NotFound("PERFIL_NO_ENCONTRADO",
                        "Primero debes crear tu perfil profesional."));
    }
}
