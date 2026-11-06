package com.sebastian.spring.mas.aliado.aliado.modules.preferencias.domain.model;

import java.util.UUID;

/** Preferencia de notificación (SPEC-14): un aviso no configurable no puede deshabilitarse. */
public record PreferenciaNotificacion(UUID usuarioId, TipoAviso tipoAviso, String canal,
                                      boolean habilitada, boolean configurable) {}
