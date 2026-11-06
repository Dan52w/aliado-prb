package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.DescargaHistorial;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.repository.DescargaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.jpa.DescargaJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.infrastructure.persistence.mapper.PreferenciasPersistenceMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Adaptador de descargas de historial (SPEC-14). */
@Component
public class DescargaPersistenceAdapter implements DescargaRepository {
    private final DescargaJpaRepository descargas;

    public DescargaPersistenceAdapter(DescargaJpaRepository descargas) {
        this.descargas = descargas;
    }

    @Override
    public DescargaHistorial guardar(DescargaHistorial descarga) {
        return PreferenciasPersistenceMapper.aDominio(
                descargas.save(PreferenciasPersistenceMapper.aEntidad(descarga)));
    }

    @Override
    public List<DescargaHistorial> porUsuario(UUID usuarioId) {
        return descargas.findByUsuarioIdOrderByFechaGeneracionDesc(usuarioId).stream()
                .map(PreferenciasPersistenceMapper::aDominio).toList();
    }

    @Override
    public Optional<DescargaHistorial> porId(UUID id) {
        return descargas.findById(id).map(PreferenciasPersistenceMapper::aDominio);
    }
}
