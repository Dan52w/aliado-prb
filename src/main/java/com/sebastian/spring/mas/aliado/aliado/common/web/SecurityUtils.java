package com.sebastian.spring.mas.aliado.aliado.common.web;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Acceso a la identidad autenticada. Nunca devuelve null: falla con 401 si no hay sesión. */
public final class SecurityUtils {
    private SecurityUtils() {}

    public static CurrentUser actual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CurrentUser user)) {
            throw new BusinessException.Unauthorized("SIN_SESION", "Se requiere una sesión activa.");
        }
        return user;
    }

    public static void exigirRol(String... roles) {
        CurrentUser user = actual();
        for (String rol : roles) {
            if (user.es(rol)) {
                return;
            }
        }
        throw new BusinessException.Forbidden("ROL_NO_AUTORIZADO", "El rol autenticado no puede realizar esta operación.");
    }
}
