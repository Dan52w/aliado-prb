package com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.mapper.ConfianzaPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model.DocumentoVerificacion;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.repository.DocumentoVerificacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.confianza.infrastructure.persistence.jpa.DocumentoVerificacionJpaRepository;

/** Adaptador de documentos de verificación (SPEC-10). */
@Component
public class DocumentoVerificacionPersistenceAdapter implements DocumentoVerificacionRepository {
    private final DocumentoVerificacionJpaRepository documentos;

    public DocumentoVerificacionPersistenceAdapter(DocumentoVerificacionJpaRepository documentos) {
        this.documentos = documentos;
    }

    @Override
    public List<DocumentoVerificacion> porSolicitudVerificacion(UUID solicitudVerificacionId) {
        return documentos.findBySolicitudVerificacionId(solicitudVerificacionId).stream()
                .map(ConfianzaPersistenceMapper::aDominio).toList();
    }

    @Override
    public DocumentoVerificacion guardar(DocumentoVerificacion documento) {
        return ConfianzaPersistenceMapper.aDominio(
                documentos.save(ConfianzaPersistenceMapper.aEntidad(documento)));
    }
}
