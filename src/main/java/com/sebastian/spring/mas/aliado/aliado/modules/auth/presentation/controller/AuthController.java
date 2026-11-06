package com.sebastian.spring.mas.aliado.aliado.modules.auth.presentation.controller;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import com.sebastian.spring.mas.aliado.aliado.common.web.BaseController;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.AuthService;
import com.sebastian.spring.mas.aliado.aliado.common.idempotencia.IdempotencyService;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.SesionPort;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model.Rol;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.presentation.dto.AuthDtos;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Autenticación y control de acceso (SPEC-01). Sin GET /auth/me ni CRUD de usuarios (F01). */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController extends BaseController {
    private final AuthService authService;
    private final SesionPort sesionService;

    public AuthController(AuthService authService, SesionPort sesionService,
                          ObjectMapper objectMapper, IdempotencyService idempotencyService) {
        super(objectMapper, idempotencyService);
        this.authService = authService;
        this.sesionService = sesionService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody AuthDtos.RegistroRequest req,
                                     @RequestHeader(value = "Idempotency-Key", required = false) String claveHeader) {
        Rol rol;
        try {
            rol = Rol.valueOf(req.rol());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException.BadRequest("ROL_INVALIDO", "Rol inválido.");
        }
        return ejecutarIdempotente(claveHeader, "auth.registro", AuthDtos.RegistroResponse::id, 201, () -> {
            AuthService.RegistroResultado resultado = authService.registrar(
                    req.email(), req.password(), rol, req.aceptaTerminos(), req.versionTerminos());
            return new AuthDtos.RegistroResponse(resultado.id(), resultado.email(), resultado.rol());
        });
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.LoginResponse> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        AuthService.LoginResultado resultado = authService.login(req.email(), req.password());
        return ResponseEntity.ok(new AuthDtos.LoginResponse(resultado.sessionToken(), resultado.rol()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            sesionService.cerrarSesion(auth.substring("Bearer ".length()).trim());
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recuperacion")
    public ResponseEntity<AuthDtos.MensajeResponse> recuperacion(
            @Valid @RequestBody AuthDtos.RecuperacionRequest req) {
        authService.solicitarRecuperacion(req.email());
        return ResponseEntity.accepted().body(new AuthDtos.MensajeResponse(
                "Si la cuenta existe, recibirás las instrucciones de recuperación."));
    }

    @PostMapping("/recuperacion/confirmar")
    public ResponseEntity<AuthDtos.MensajeResponse> confirmar(
            @Valid @RequestBody AuthDtos.ConfirmarRecuperacionRequest req) {
        authService.confirmarRecuperacion(req.token(), req.nuevaPassword());
        return ResponseEntity.ok(new AuthDtos.MensajeResponse("Contraseña actualizada."));
    }
}
