package com.sebastian.spring.mas.aliado.aliado.modules.reportes.application;

/** Contenido de una evidencia de reporte leído del storage (SPEC-11). */
public record EvidenciaContenido(byte[] contenido, String tipoContenido) {}
