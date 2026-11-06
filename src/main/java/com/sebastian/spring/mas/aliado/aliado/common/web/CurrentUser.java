package com.sebastian.spring.mas.aliado.aliado.common.web;

import java.util.UUID;

/** Identidad autenticada que viaja en el SecurityContext (sesión opaca, SPEC-01). */
public record CurrentUser(UUID id, String email, String rol) {
    public boolean es(String rolEsperado) { return rol != null && rol.equals(rolEsperado); }
    public boolean esCliente() { return es("CLIENTE"); }
    public boolean esPrestador() { return es("PRESTADOR"); }
    public boolean esAdministrador() { return es("ADMINISTRADOR"); }
}
