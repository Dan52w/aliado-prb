package com.sebastian.spring.mas.aliado.aliado.modules.confianza.domain.model;

/**
 * Nivel de verificación derivado de solicitudes APROBADAS (SPEC-10):
 * 0 = SIN_VERIFICAR, >=1 = BASICO; AMPLIADO queda reservado para decisión de producto.
 * Ningún nivel se presenta como certificación legal (FR-062).
 */
public enum NivelVerificacion {
    SIN_VERIFICAR, BASICO, AMPLIADO
}
