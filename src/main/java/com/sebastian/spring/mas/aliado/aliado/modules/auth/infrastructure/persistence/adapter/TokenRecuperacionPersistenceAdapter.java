package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.TokenRecuperacion;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository.TokenRecuperacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.jpa.TokenRecuperacionJpaRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.mapper.AuthPersistenceMapper;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TokenRecuperacionPersistenceAdapter implements TokenRecuperacionRepository {
    private final TokenRecuperacionJpaRepository jpa;

    public TokenRecuperacionPersistenceAdapter(TokenRecuperacionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<TokenRecuperacion> porCodigoHash(String codigoHash) {
        return jpa.findByCodigoHash(codigoHash).map(AuthPersistenceMapper::aDominio);
    }

    @Override
    public TokenRecuperacion guardar(TokenRecuperacion token) {
        return AuthPersistenceMapper.aDominio(jpa.save(AuthPersistenceMapper.aEntidad(token)));
    }
}
