package com.sebastian.spring.mas.aliado.aliado.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean de Jackson 2 (com.fasterxml.jackson).
 * Spring Boot 4 auto-configura únicamente el ObjectMapper de Jackson 3 (tools.jackson);
 * el código de ALIADO usa la API de Jackson 2 para la idempotencia (JsonNode) y para
 * serializar datosGenerales (JSONB), así que el bean se declara explícitamente aquí.
 */
@Configuration
public class Jackson2Config {

    @Bean
    public ObjectMapper jackson2ObjectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
