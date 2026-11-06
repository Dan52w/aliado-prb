package com.sebastian.spring.mas.aliado.aliado.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Interruptores de capacidades Could/Future (plan general, sección 3.2).
 * Todas en false por defecto: apagadas no alteran el MVP ni se presentan como disponibles.
 */
@ConfigurationProperties(prefix = "aliado.features")
public record FeatureFlags(
        boolean chatInterno,
        boolean soporteWhatsapp,
        boolean onboardingAsistido,
        boolean mapaPrestadores,
        boolean insigniaFormalizacion,
        boolean referidos,
        boolean idiomasAdicionales,
        boolean integracionInstitucional,
        Pagos pagos) {

    public FeatureFlags {
        if (pagos == null) {
            pagos = new Pagos(false, false);
        }
    }

    /** Pasarela de pagos (SPEC-20, Future): requiere flag Y aprobación explícita de producto. */
    public record Pagos(boolean pilotoEnabled, boolean pilotoApproved) {
        public boolean activo() { return pilotoEnabled && pilotoApproved; }
    }
}
