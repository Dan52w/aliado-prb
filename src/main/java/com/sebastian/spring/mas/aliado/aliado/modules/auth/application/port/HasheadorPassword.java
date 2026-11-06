package com.sebastian.spring.mas.aliado.aliado.modules.auth.application.port;

/** Puerto para proteger contraseñas sin acoplar el caso de uso a BCrypt o Spring Security. */
public interface HasheadorPassword {
    String hashear(String password);
}
