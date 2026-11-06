package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Usuario;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Puerto de persistencia de usuarios (núcleo; sin detalles JPA). */
public interface UsuarioRepository {
    Optional<Usuario> porId(UUID id);
    Optional<Usuario> porEmail(String emailNormalizado);
    boolean existeEmail(String emailNormalizado);
    Usuario guardar(Usuario usuario);
    /** Búsqueda admin (SPEC-12, FR-075): por correo (contiene) o id exacto. */
    List<Usuario> buscarPorTexto(String texto);
}
