package com.sebastian.spring.mas.aliado.aliado.modules.auth.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Modelo de dominio: usuario sin dependencias de persistencia ni de framework. */
public record Usuario(UUID id, String email, String passwordHash, Rol rol,
                      EstadoCuenta estadoCuenta, OffsetDateTime fechaCreacion) {

    /** Política de contraseña del MVP (documentada en README): mínimo 8 caracteres, letra y dígito. */
    public static void validarPassword(String password) {
        if (password == null || password.length() < 8
                || !password.chars().anyMatch(Character::isLetter)
                || !password.chars().anyMatch(Character::isDigit)) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres, una letra y un dígito.");
        }
    }

    public static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public boolean bloqueado() {
        return estadoCuenta == EstadoCuenta.BLOQUEADA_PREVENTIVAMENTE;
    }
}
