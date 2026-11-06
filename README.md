# ALIADO

Plataforma piloto que conecta clientes con prestadores de servicios para el hogar
en Santa Marta (Magdalena, Colombia). Proyecto universitario de práctica:
monolito modular con Spring Boot 4.1.2, Java 21, PostgreSQL y Flyway.

## Alcance del MVP

Implementado según `src/docs/plans/` y `src/docs/specs/`:

| SPEC | Módulo | Estado |
|---|---|---|
| 01 | Autenticación, registro y control de acceso | Implementado |
| 02 | Perfil y privacidad (incluye eliminación con confirmación a 24 h) | Implementado |
| 03 | Descubrimiento de prestadores (solo CLIENTE) | Implementado |
| 04 | Solicitudes de servicio | Implementado |
| 05 | Perfil profesional | Implementado |
| 06 | Compatibilidad y oportunidades (sin puntaje: booleana) | Implementado |
| 07 | Propuestas | Implementado |
| 08 | Contratación y ciclo de vida del servicio | Implementado |
| 09 | Notificaciones de propuestas y servicios | Implementado |
| 10 | Calificaciones, reseñas y verificación | Implementado |
| 11 | Reportes y evidencias | Implementado |
| 12 | Administración, moderación y riesgos | Implementado |
| 13 | Formalización (contenido orientativo) | Implementado |
| 14 | Preferencias, historial e indicadores | Implementado |

### No implementado (futuro, apagado por feature flags)

SPEC-15 (chat interno / WhatsApp), 16 (onboarding asistido), 17 (mapa),
18 (insignias), 19 (referidos / idiomas) y 20 (pagos), más la vinculación
institucional UC080 de SPEC-13. Los endpoints futuros responden
`FUNCIONALIDAD_DESHABILITADA`, nunca como disponibles.

## Cómo correrlo

```bash
docker compose up -d            # solo PostgreSQL 17
./mvnw spring-boot:run          # Java 21, puerto 8080
```

Variables de entorno principales:

| Variable | Uso |
|---|---|
| `DATABASE_URL` / `DATABASE_USERNAME` / `DATABASE_PASSWORD` | Conexión PostgreSQL |
| `ALIADO_AES_KEY` | Clave AES-256-GCM en Base64 (32 bytes) para cifrar la dirección exacta |
| `ALIADO_ADMIN_EMAIL` / `ALIADO_ADMIN_PASSWORD` | Crea el ADMINISTRADOR inicial al arrancar |
| `ALIADO_STORAGE_DIR` | Directorio local de archivos subidos |

La API se documenta en `/swagger-ui.html`. El correo de recuperación y la
entrega push son `Log` en esta versión (P-05): el token de recuperación se
registra en el log solo para desarrollo.

## Decisiones ante vacíos de los SPEC (`NEEDS CLARIFICATION`)

- **Sesión opaca revocable** (no JWT): token aleatorio, solo su hash SHA-256
  en BD, TTL 24 h; al iniciar sesión se cierran las anteriores.
- **Contraseña:** mínimo 8 caracteres, con letra y dígito.
- **Urgencia:** `BAJA / MEDIA / ALTA / URGENTE`.
- **Visibilidad:** `PUBLICO / SOLO_ZONA / PRIVADO` (defecto `SOLO_ZONA`).
- **Exposición pública del perfil:** `PUBLICO / OCULTO`.
- **Reputación:** media aritmética de calificaciones (1–5), 2 decimales.
- **Nivel de verificación derivado:** 0 aprobadas = `SIN_VERIFICAR`, ≥1 = `BASICO`;
  `AMPLIADO` reservado. Nunca se presenta como certificación legal.
- **Compatibilidad:** booleana por categoría + zona + disponibilidad declarada.
- **Servicio:** secuencia lineal estricta; `TERMINADO` exige `EN_EJECUCION`.
- **Storage:** adaptador local en filesystem (S3/MinIO es P-04, pendiente);
  máximo 5 MB; imágenes y PDF según el caso.
- **Notificaciones:** registro idempotente en la transacción de negocio y
  entrega asíncrona cada 60 s por scheduler (canal Log), máximo 3 intentos.
- **Idempotencia:** header `Idempotency-Key` en registro, crear solicitud,
  crear propuesta y aceptar propuesta.

## Arquitectura

Monolito modular por SPEC, con Clean Architecture por módulo (`domain`,
`application`, `infrastructure`, `presentation`) según `src/AGENTS.md`.
Comunicación entre módulos solo por puertos/fachadas públicas. Entidades JPA
planas (FK como UUID, sin asociaciones entre módulos). Sin Lombok en el código
generado: constructores y getters explícitos.

## Tests

```bash
./mvnw test   # H2 en memoria, sin Flyway, sin red
```

Incluye tests de máquinas de estados (solicitud, propuesta, servicio),
política de contraseña, compatibilidad y reputación, y un test de contexto
con los feature flags apagados.
