package com.sebastian.spring.mas.aliado.aliado.common.storage;

/**
 * Contrato de almacenamiento de archivos (plan general, sección 6.2).
 * El sistema persiste únicamente la clave del objeto, nunca el binario en PostgreSQL.
 * La implementación del MVP es local; un backend S3-compatible la reemplaza sin tocar el dominio.
 */
public interface ArchivoStorageService {
    /** Guarda el contenido y devuelve su clave opaca. */
    String guardar(String nombreOriginal, String tipoContenido, byte[] contenido);

    /** Lee el contenido de una clave. */
    byte[] leer(String clave);

    /** Elimina el objeto si existe. */
    void eliminar(String clave);
}
