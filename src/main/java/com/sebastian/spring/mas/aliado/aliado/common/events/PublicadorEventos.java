package com.sebastian.spring.mas.aliado.aliado.common.events;

/** Puerto de publicación de eventos de dominio/aplicación (desacopla módulos MVP). */
public interface PublicadorEventos {
    void publicar(Object evento);
}
