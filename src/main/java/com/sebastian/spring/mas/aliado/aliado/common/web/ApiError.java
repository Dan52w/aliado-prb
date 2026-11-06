package com.sebastian.spring.mas.aliado.aliado.common.web;

import java.time.OffsetDateTime;

/** Formato único de error (plan general, sección 13.1). Nunca incluye trazas ni datos personales. */
public record ApiError(OffsetDateTime timestamp, int status, String code, String message, String path, String traceId) {}
