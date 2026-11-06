package com.sebastian.spring.mas.aliado.aliado.modules.auth.infrastructure.adapter;

import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port.HasheadorPassword;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Adaptador técnico BCrypt del puerto de hash de contraseñas. */
@Component
public class BCryptHasheadorAdapter implements HasheadorPassword {
    private final PasswordEncoder passwordEncoder;

    public BCryptHasheadorAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hashear(String password) {
        return passwordEncoder.encode(password);
    }
}
