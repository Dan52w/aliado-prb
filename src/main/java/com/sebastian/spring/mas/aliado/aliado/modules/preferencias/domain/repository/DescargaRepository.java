package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model.DescargaHistorial;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DescargaRepository {
    DescargaHistorial guardar(DescargaHistorial descarga);
    List<DescargaHistorial> porUsuario(UUID usuarioId);
    Optional<DescargaHistorial> porId(UUID id);
}
