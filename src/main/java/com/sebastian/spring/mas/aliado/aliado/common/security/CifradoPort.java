package com.sebastian.spring.mas.aliado.aliado.common.security;

/** Cifrado simétrico a nivel de aplicación (la implementación es AES-256-GCM). */
public interface CifradoPort {
    /** Cifra el texto plano; null devuelve null. */
    String cifrar(String textoPlano);

    /** Descifra el texto cifrado; null devuelve null. */
    String descifrar(String textoCifrado);
}
