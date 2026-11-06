package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Usuario;
import java.util.Optional;
import java.util.UUID;

/** Contrato público del módulo auth para otros módulos (nunca expone persistencia). */
public interface UsuarioConsultaPort {
    Optional<Usuario> porId(UUID id);
    boolean existe(UUID id);
}
