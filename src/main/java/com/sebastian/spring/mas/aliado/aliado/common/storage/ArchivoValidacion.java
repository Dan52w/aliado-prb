package com.sebastian.spring.mas.aliado.aliado.common.storage;

import com.sebastian.spring.mas.aliado.aliado.common.exception.BusinessException;
import java.util.Set;

/** Reglas de archivo del MVP (P-07 pendiente de cerrar por producto: MIME y tamaños). */
public final class ArchivoValidacion {
    public static final long MAX_BYTES = 5L * 1024 * 1024;
    public static final Set<String> IMAGENES = Set.of("image/jpeg", "image/png", "image/webp");
    public static final Set<String> DOCUMENTOS = Set.of("application/pdf", "image/jpeg", "image/png", "image/webp");

    private ArchivoValidacion() {}

    public static void validar(String nombreOriginal, String tipoContenido, byte[] contenido, Set<String> permitidos) {
        if (contenido == null || contenido.length == 0) {
            throw new BusinessException.BadRequest("ARCHIVO_VACIO", "El archivo está vacío.");
        }
        if (contenido.length > MAX_BYTES) {
            throw new BusinessException.PayloadTooLarge("ARCHIVO_MUY_GRANDE", "El archivo supera los 5 MB permitidos.");
        }
        if (tipoContenido == null || !permitidos.contains(tipoContenido.toLowerCase())) {
            throw new BusinessException.UnsupportedMediaType("TIPO_ARCHIVO_NO_PERMITIDO",
                    "Tipo de archivo no permitido: " + tipoContenido);
        }
        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            throw new BusinessException.BadRequest("ARCHIVO_SIN_NOMBRE", "El archivo debe tener un nombre.");
        }
    }

    public static String extensionSegura(String nombreOriginal) {
        int punto = nombreOriginal.lastIndexOf('.');
        if (punto < 0 || punto == nombreOriginal.length() - 1) {
            return "";
        }
        String ext = nombreOriginal.substring(punto).toLowerCase();
        return ext.matches("\\.[a-z0-9]+") ? ext : "";
    }
}
