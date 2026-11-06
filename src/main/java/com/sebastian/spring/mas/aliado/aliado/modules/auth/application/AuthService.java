package com.sebastian.spring.mas.aliado.aliado.modules.auth.application;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.CuentaBloqueoPort;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.PerfilBasePort;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.RolConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.SesionPort;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.UsuarioConsultaPort;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Consentimiento;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.EstadoCuenta;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Rol;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.TokenRecuperacion;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Usuario;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository.ConsentimientoRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository.TokenRecuperacionRepository;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository.UsuarioRepository;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso de autenticación (SPEC-01, UC001–UC007).
 * Registro atómico: usuario + consentimiento + perfil base, o nada (FR-001/FR-002).
 * La recuperación no confirma ni niega la existencia de la cuenta (FR-006).
 */
@Service
public class AuthService implements UsuarioConsultaPort, RolConsultaPort, CuentaBloqueoPort {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarios;
    private final ConsentimientoRepository consentimientos;
    private final TokenRecuperacionRepository tokens;
    private final SesionPort sesionService;
    private final PerfilBasePort perfilService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarios, ConsentimientoRepository consentimientos,
                       TokenRecuperacionRepository tokens, SesionPort sesionService,
                       PerfilBasePort perfilService, PasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.consentimientos = consentimientos;
        this.tokens = tokens;
        this.sesionService = sesionService;
        this.perfilService = perfilService;
        this.passwordEncoder = passwordEncoder;
    }

    public record RegistroResultado(UUID id, String email, String rol) {}
    public record LoginResultado(String sessionToken, String rol) {}

    @Transactional
    public RegistroResultado registrar(String email, String password, Rol rol,
                                       boolean aceptaTerminos, String versionTerminos) {
        String correo = Usuario.normalizarEmail(email);
        if (!aceptaTerminos || versionTerminos == null || versionTerminos.isBlank()) {
            throw new BusinessException.BadRequest("TERMINOS_NO_ACEPTADOS",
                    "Debe aceptar los términos y el tratamiento de datos.");
        }
        if (rol == Rol.ADMINISTRADOR) {
            throw new BusinessException.BadRequest("ROL_NO_REGISTRABLE",
                    "El rol ADMINISTRADOR no se registra por la API pública.");
        }
        try {
            Usuario.validarPassword(password);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException.BadRequest("PASSWORD_INVALIDA", ex.getMessage());
        }
        // FR-003: sin revelar el rol de la cuenta existente.
        if (usuarios.existeEmail(correo)) {
            throw new BusinessException.Conflict("CORREO_YA_REGISTRADO", "El correo ya está registrado.");
        }
        OffsetDateTime ahora = OffsetDateTime.now();
        Usuario usuario = new Usuario(UUID.randomUUID(), correo, passwordEncoder.encode(password),
                rol, EstadoCuenta.ACTIVA, ahora);
        usuarios.guardar(usuario);
        consentimientos.guardar(new Consentimiento(UUID.randomUUID(), usuario.id(), versionTerminos, ahora));
        perfilService.crearPerfilBase(usuario.id());
        return new RegistroResultado(usuario.id(), usuario.email(), usuario.rol().name());
    }

    @Transactional
    public LoginResultado login(String email, String password) {
        String correo = Usuario.normalizarEmail(email);
        // FR-004: mensaje genérico, sin revelar si falló el correo o la contraseña.
        Usuario usuario = usuarios.porEmail(correo)
                .filter(u -> passwordEncoder.matches(password, u.passwordHash()))
                .orElseThrow(() -> new BusinessException.Unauthorized(
                        "CREDENCIALES_INVALIDAS", "Credenciales inválidas."));
        if (usuario.bloqueado()) {
            throw new BusinessException.Forbidden("CUENTA_BLOQUEADA",
                    "La cuenta está bloqueada preventivamente.");
        }
        String token = sesionService.crearSesion(usuario.id());
        return new LoginResultado(token, usuario.rol().name());
    }

    @Transactional
    public void solicitarRecuperacion(String email) {
        String correo = Usuario.normalizarEmail(email);
        // FR-006: la respuesta observable es idéntica exista o no la cuenta.
        usuarios.porEmail(correo).ifPresent(usuario -> {
            byte[] aleatorio = new byte[24];
            new SecureRandom().nextBytes(aleatorio);
            String codigo = HexFormat.of().formatHex(aleatorio);
            OffsetDateTime ahora = OffsetDateTime.now();
            tokens.guardar(new TokenRecuperacion(UUID.randomUUID(), usuario.id(),
                    SesionService.hashToken(codigo), ahora, ahora.plusMinutes(30), false));
            // Sin proveedor de correo configurado en el MVP (P-05): el código queda en el log
            // del servidor para la práctica. En piloto debe enviarse por correo, nunca en la API.
            log.warn("Código de recuperación (solo desarrollo, no exponer en API): {}", codigo);
        });
    }

    @Transactional
    public void confirmarRecuperacion(String codigo, String nuevaPassword) {
        OffsetDateTime ahora = OffsetDateTime.now();
        // Plan SPEC-01: 400 si el código no existe, 410 si fue usado o expiró.
        TokenRecuperacion token = tokens.porCodigoHash(SesionService.hashToken(codigo))
                .orElseThrow(() -> new BusinessException.BadRequest("TOKEN_INVALIDO",
                        "El código es inválido."));
        if (!token.vigente(ahora)) {
            throw new BusinessException.Gone("TOKEN_EXPIRADO",
                    "El código ya fue usado o expiró.");
        }
        try {
            Usuario.validarPassword(nuevaPassword);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException.BadRequest("PASSWORD_INVALIDA", ex.getMessage());
        }
        Usuario usuario = usuarios.porId(token.usuarioId())
                .orElseThrow(() -> new BusinessException.NotFound("USUARIO_NO_ENCONTRADO", "Usuario no encontrado."));
        tokens.guardar(new TokenRecuperacion(token.id(), token.usuarioId(), token.codigoHash(),
                token.fechaCreacion(), token.fechaExpiracion(), true));
        usuarios.guardar(new Usuario(usuario.id(), usuario.email(), passwordEncoder.encode(nuevaPassword),
                usuario.rol(), usuario.estadoCuenta(), usuario.fechaCreacion()));
        sesionService.cerrarSesionesDeUsuario(usuario.id());
    }

    /** Creación de administrador por bootstrap (variables de entorno). No es un endpoint. */
    @Transactional
    public void crearAdministrador(String email, String password) {
        String correo = Usuario.normalizarEmail(email);
        if (usuarios.existeEmail(correo)) {
            throw new BusinessException.Conflict("CORREO_YA_REGISTRADO",
                    "La cuenta administradora ya existe.");
        }
        try {
            Usuario.validarPassword(password);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException.BadRequest("PASSWORD_INVALIDA", ex.getMessage());
        }
        OffsetDateTime ahora = OffsetDateTime.now();
        Usuario admin = new Usuario(UUID.randomUUID(), correo, passwordEncoder.encode(password),
                Rol.ADMINISTRADOR, EstadoCuenta.ACTIVA, ahora);
        usuarios.guardar(admin);
        consentimientos.guardar(new Consentimiento(UUID.randomUUID(), admin.id(), "admin-bootstrap", ahora));
        perfilService.crearPerfilBase(admin.id());
    }

    @Override
    @Transactional
    public void bloquearCuenta(UUID usuarioId) {
        Usuario usuario = usuarios.porId(usuarioId)
                .orElseThrow(() -> new BusinessException.NotFound("USUARIO_NO_ENCONTRADO", "Usuario no encontrado."));
        usuarios.guardar(new Usuario(usuario.id(), usuario.email(), usuario.passwordHash(), usuario.rol(),
                EstadoCuenta.BLOQUEADA_PREVENTIVAMENTE, usuario.fechaCreacion()));
        sesionService.cerrarSesionesDeUsuario(usuarioId);
    }

    @Override
    @Transactional
    public void desbloquearCuenta(UUID usuarioId) {
        Usuario usuario = usuarios.porId(usuarioId)
                .orElseThrow(() -> new BusinessException.NotFound("USUARIO_NO_ENCONTRADO", "Usuario no encontrado."));
        usuarios.guardar(new Usuario(usuario.id(), usuario.email(), usuario.passwordHash(), usuario.rol(),
                EstadoCuenta.ACTIVA, usuario.fechaCreacion()));
    }

    @Override
    public Optional<Usuario> porId(UUID id) {
        return usuarios.porId(id);
    }

    @Override
    public boolean existe(UUID id) {
        return usuarios.porId(id).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorTexto(String texto) {
        return usuarios.buscarPorTexto(texto);
    }

    @Override
    public boolean esPrestador(UUID usuarioId) {
        return usuarios.porId(usuarioId).map(u -> u.rol() == Rol.PRESTADOR).orElse(false);
    }
}
