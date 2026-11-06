package com.sebastian.spring.mas.aliado.aliado.common.security;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Cifrado a nivel de aplicación (AES-256-GCM) para la dirección exacta (SPEC-02, RNF-004).
 * La clave se toma de ALIADO_AES_KEY (Base64, 32 bytes). Sin ella se usa una clave
 * de desarrollo fija: NUNCA usar ese valor por defecto fuera de un entorno local de práctica.
 */
@Service
public class CryptoService {
    private static final Logger log = LoggerFactory.getLogger(CryptoService.class);
    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;

    private final SecretKeySpec clave;

    public CryptoService(@Value("${aliado.crypto.key:}") String claveBase64) {
        byte[] bytes;
        if (claveBase64 == null || claveBase64.isBlank()) {
            log.warn("ALIADO_AES_KEY no configurada: se usa la clave de desarrollo (solo práctica local).");
            bytes = new byte[32];
        } else {
            bytes = Base64.getDecoder().decode(claveBase64);
        }
        if (bytes.length != 32) {
            throw new IllegalStateException("ALIADO_AES_KEY debe tener 32 bytes en Base64.");
        }
        this.clave = new SecretKeySpec(bytes, "AES");
    }

    public String cifrar(String textoPlano) {
        if (textoPlano == null) {
            return null;
        }
        try {
            byte[] iv = new byte[IV_BYTES];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, clave, new GCMParameterSpec(TAG_BITS, iv));
            byte[] cifrado = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));
            byte[] salida = new byte[IV_BYTES + cifrado.length];
            System.arraycopy(iv, 0, salida, 0, IV_BYTES);
            System.arraycopy(cifrado, 0, salida, IV_BYTES, cifrado.length);
            return Base64.getEncoder().encodeToString(salida);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo cifrar el dato.", ex);
        }
    }

    public String descifrar(String textoCifrado) {
        if (textoCifrado == null) {
            return null;
        }
        try {
            byte[] datos = Base64.getDecoder().decode(textoCifrado);
            byte[] iv = new byte[IV_BYTES];
            System.arraycopy(datos, 0, iv, 0, IV_BYTES);
            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, clave, new GCMParameterSpec(TAG_BITS, iv));
            byte[] plano = cipher.doFinal(datos, IV_BYTES, datos.length - IV_BYTES);
            return new String(plano, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo descifrar el dato.", ex);
        }
    }
}
