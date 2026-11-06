package com.sebastian.spring.mas.aliado.aliado.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "ALIADO API", version = "1.0.0",
                description = "Marketplace de servicios técnicos a domicilio en Santa Marta. MVP: SPEC-01 a SPEC-14."),
        security = @SecurityRequirement(name = "sesion"))
@SecurityScheme(name = "sesion", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "opaque")
public class OpenApiConfig {}
