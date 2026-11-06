package com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.common.security.CifradoPort;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.model.Ubicacion;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.domain.repository.UbicacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.jpa.UbicacionJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.perfil.infrastructure.persistence.mapper.PerfilPersistenceMapper;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Adaptador de ubicaciones (SPEC-02): la dirección exacta se cifra/descifra aquí, nunca en el dominio. */
@Component
public class UbicacionPersistenceAdapter implements UbicacionRepository {
    private final UbicacionJpaRepository ubicaciones;
    private final CifradoPort crypto;

    public UbicacionPersistenceAdapter(UbicacionJpaRepository ubicaciones, CifradoPort crypto) {
        this.ubicaciones = ubicaciones;
        this.crypto = crypto;
    }

    @Override
    public Optional<Ubicacion> porUsuarioId(UUID usuarioId) {
        return ubicaciones.findById(usuarioId)
                .map(e -> PerfilPersistenceMapper.aDominio(e, crypto.descifrar(e.getDireccionExactaCifrada())));
    }

    @Override
    public Ubicacion guardar(Ubicacion ubicacion) {
        var guardada = ubicaciones.save(PerfilPersistenceMapper.aEntidad(ubicacion,
                crypto.cifrar(ubicacion.direccionExacta())));
        return PerfilPersistenceMapper.aDominio(guardada, ubicacion.direccionExacta());
    }
}
