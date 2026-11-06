package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.DocumentoVerificacion;
import java.util.List;
import java.util.UUID;

public interface DocumentoVerificacionRepository {
    List<DocumentoVerificacion> porSolicitudVerificacion(UUID solicitudVerificacionId);
    DocumentoVerificacion guardar(DocumentoVerificacion documento);
}
