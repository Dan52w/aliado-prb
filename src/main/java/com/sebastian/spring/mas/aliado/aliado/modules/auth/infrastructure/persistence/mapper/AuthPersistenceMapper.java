package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.mapper;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Consentimiento;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.EstadoCuenta;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.EstadoSesion;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Rol;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Sesion;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.TokenRecuperacion;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Usuario;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.ConsentimientoEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.SesionEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.TokenRecuperacionEntity;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.persistence.entity.UsuarioEntity;

/** Conversión explícita dominio <-> persistencia (sin filtrar entidades JPA hacia afuera). */
public final class AuthPersistenceMapper {
    private AuthPersistenceMapper() {}

    public static Usuario aDominio(UsuarioEntity e) {
        return new Usuario(e.getId(), e.getEmail(), e.getPasswordHash(), Rol.valueOf(e.getRol()),
                EstadoCuenta.valueOf(e.getEstadoCuenta()), e.getFechaCreacion());
    }

    public static UsuarioEntity aEntidad(Usuario u) {
        UsuarioEntity e = new UsuarioEntity();
        e.setId(u.id());
        e.setEmail(u.email());
        e.setPasswordHash(u.passwordHash());
        e.setRol(u.rol().name());
        e.setEstadoCuenta(u.estadoCuenta().name());
        e.setFechaCreacion(u.fechaCreacion());
        return e;
    }

    public static Sesion aDominio(SesionEntity e) {
        return new Sesion(e.getId(), e.getUsuarioId(), e.getTokenHash(), e.getFechaInicio(),
                e.getFechaExpiracion(), e.getFechaCierre(), EstadoSesion.valueOf(e.getEstado()));
    }

    public static SesionEntity aEntidad(Sesion s) {
        SesionEntity e = new SesionEntity();
        e.setId(s.id());
        e.setUsuarioId(s.usuarioId());
        e.setTokenHash(s.tokenHash());
        e.setFechaInicio(s.fechaInicio());
        e.setFechaExpiracion(s.fechaExpiracion());
        e.setFechaCierre(s.fechaCierre());
        e.setEstado(s.estado().name());
        return e;
    }

    public static TokenRecuperacion aDominio(TokenRecuperacionEntity e) {
        return new TokenRecuperacion(e.getId(), e.getUsuarioId(), e.getCodigoHash(),
                e.getFechaCreacion(), e.getFechaExpiracion(), e.isUsado());
    }

    public static TokenRecuperacionEntity aEntidad(TokenRecuperacion t) {
        TokenRecuperacionEntity e = new TokenRecuperacionEntity();
        e.setId(t.id());
        e.setUsuarioId(t.usuarioId());
        e.setCodigoHash(t.codigoHash());
        e.setFechaCreacion(t.fechaCreacion());
        e.setFechaExpiracion(t.fechaExpiracion());
        e.setUsado(t.usado());
        return e;
    }

    public static ConsentimientoEntity aEntidad(Consentimiento c) {
        ConsentimientoEntity e = new ConsentimientoEntity();
        e.setId(c.id());
        e.setUsuarioId(c.usuarioId());
        e.setVersionTerminos(c.versionTerminos());
        e.setFechaAceptacion(c.fechaAceptacion());
        return e;
    }
}
