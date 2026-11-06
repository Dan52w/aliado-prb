package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.TokenRecuperacion;
import java.util.Optional;

public interface TokenRecuperacionRepository {
    Optional<TokenRecuperacion> porCodigoHash(String codigoHash);
    TokenRecuperacion guardar(TokenRecuperacion token);
}
