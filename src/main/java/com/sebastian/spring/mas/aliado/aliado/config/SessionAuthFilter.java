package com.sebastian.spring.mas.aliado.aliado.config;

import com.sebastian.spring.mas.aliado.aliado.common.web.CurrentUser;
import com.sebastian.spring.mas.aliado.aliado.modules.auth.application.SesionService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Autenticación por sesión opaca (plan general, sección 6.2; SPEC-01).
 * El cliente envía Authorization: Bearer <token>; en BD solo vive el hash SHA-256.
 * Las rutas públicas y la documentación quedan fuera del filtro.
 */
public class SessionAuthFilter extends OncePerRequestFilter {
    private final SesionService sesionService;

    public SessionAuthFilter(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = request.getRequestURI();
        return ruta.startsWith("/api/v1/auth/")
                || ruta.startsWith("/swagger-ui/")
                || ruta.startsWith("/v3/api-docs/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring("Bearer ".length()).trim();
            sesionService.validar(token).ifPresent(usuario -> {
                CurrentUser principal = new CurrentUser(usuario.id(), usuario.email(), usuario.rol().name());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }
        chain.doFilter(request, response);
    }
}
