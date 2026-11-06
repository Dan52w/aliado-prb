package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.service;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto.RegistrarUsuarioCommand;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto.RegistrarConsentimientoCommand;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.dto.ResultadoRegistro;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.HasheadorPassword;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.PerfilBasePort;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.usecase.RegistrarUsuarioUseCase;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.usecase.RegistrarConsentimientoUseCase;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.exception.CorreoYaRegistradoException;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.exception.PasswordInvalidaException;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.exception.RolNoRegistrableException;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.EstadoCuenta;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Rol;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Usuario;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.repository.UsuarioRepository;
import java.time.OffsetDateTime;
import java.util.UUID;

public class RegistrarUsuarioService implements RegistrarUsuarioUseCase {
    private final UsuarioRepository usuarioRepository;
    private final RegistrarConsentimientoUseCase registrarConsentimientoUseCase;
    private final PerfilBasePort perfilBasePort;
    private final HasheadorPassword hasheadorPassword;

    public RegistrarUsuarioService(UsuarioRepository usuarioRepository,
                                   RegistrarConsentimientoUseCase registrarConsentimientoUseCase,
                                   PerfilBasePort perfilBasePort, HasheadorPassword hasheadorPassword) {
        this.usuarioRepository = usuarioRepository;
        this.registrarConsentimientoUseCase = registrarConsentimientoUseCase;
        this.perfilBasePort = perfilBasePort;
        this.hasheadorPassword = hasheadorPassword;
    }

    @Override
    public ResultadoRegistro ejecutar(RegistrarUsuarioCommand command) {
        String correo = Usuario.normalizarEmail(command.email());
        if (command.rol() == null || command.rol() == Rol.ADMINISTRADOR) {
            throw new RolNoRegistrableException();
        }
        try {
            Usuario.validarPassword(command.password());
        } catch (IllegalArgumentException ex) {
            throw new PasswordInvalidaException(ex.getMessage());
        }
        if (usuarioRepository.existeEmail(correo)) {
            throw new CorreoYaRegistradoException();
        }

        OffsetDateTime ahora = OffsetDateTime.now();
        Usuario usuario = new Usuario(UUID.randomUUID(), correo, hasheadorPassword.hashear(command.password()),
                command.rol(), EstadoCuenta.ACTIVA, ahora);
        usuarioRepository.guardar(usuario);
        registrarConsentimientoUseCase.ejecutar(new RegistrarConsentimientoCommand(usuario.id(),
                command.aceptaTerminos(), command.versionTerminos()));
        perfilBasePort.crearPerfilBase(usuario.id());
        return new ResultadoRegistro(usuario.id(), usuario.email(), usuario.rol().name());
    }
}
