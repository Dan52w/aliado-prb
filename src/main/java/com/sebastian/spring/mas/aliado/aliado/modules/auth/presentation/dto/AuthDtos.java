package com.sebastian.spring.mas.aliado.aliado.modules.auth.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/** Contratos HTTP de autenticación (SPEC-01). */
public final class AuthDtos {
    private AuthDtos() {}

    public record RegistroRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotNull String rol,
            boolean aceptaTerminos,
            @NotBlank String versionTerminos) {}

    public record RegistroResponse(UUID id, String email, String rol) {}

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}

    public record LoginResponse(String sessionToken, String rol) {}

    public record RecuperacionRequest(@Email @NotBlank String email) {}

    public record ConfirmarRecuperacionRequest(@NotBlank String token, @NotBlank String nuevaPassword) {}

    public record MensajeResponse(String mensaje) {}
}
