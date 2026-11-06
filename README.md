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
- **Visibilidad (SPEC-02/03/06):** `PRIVADO` nunca aparece en descubrimiento ni
  como compatible en oportunidades; `SOLO_ZONA` solo aparece en búsquedas
  acotadas a su zona; `PUBLICO` siempre visible. La lectura vive en el servicio
  dedicado `PreferenciaVisibilidadService` (sin dependencias de otros servicios,
  para no cerrar un ciclo entre módulos).
- **Campos públicos de `datosGenerales` (SPEC-03):** el SPEC no define los campos
  ("a definir por UI"); la lista blanca `PerfilProfesional.DATOS_PUBLICOS`
  permite solo claves de presentación (descripción, experiencia, especialidades,
  formación, idiomas, sitio web, etc.). Teléfono, dirección, correo o documento
  nunca salen del perfil propio aunque estén guardados ahí.
- **Borrador parcial (SPEC-04):** crear en `BORRADOR` admite categoría, zona y
  descripción vacías (V8 relajó sus `NOT NULL`); `publicar` revalida que esté
  completo y que categoría y zona sigan activas.
- **Indicadores (SPEC-14, T007):** el SPEC no fija las fórmulas. `TERMINADO`
  (trabajo terminado por el prestador, pendiente de confirmación del cliente)
  cuenta como finalizado, no como en curso. `CANCELADO` no cuenta en ningún
  subtotal.
- **Eliminación de cuenta (SPEC-02, F02-QA-03):** confirmar la eliminación
  bloquea la cuenta (login rechazado) y cierra sus sesiones; la anonimización
  de los datos personales sigue pendiente de decisión de producto.
- **SOLICITAR_CORRECCION (SPEC-12):** oculta el contenido igual que
  `OCULTAR_CONTENIDO` (así lo exige el test de aceptación de SPEC-12); la
  diferencia queda en la acción registrada y en la auditoría. Un servicio no
  tiene contenido ocultable: para él, alto riesgo o bloqueo de cuenta.
- **Vinculación institucional (SPEC-13, UC080):** sigue sin implementación.
  Con el flag apagado responde 404; con el flag encendido responde 501.
- **Errores de subida:** un fallo al leer el multipart es 400
  (`ARCHIVO_INVALIDO`), no 500; un archivo cuyo contenido no corresponde al
  tipo declarado es 415 (`TIPO_ARCHIVO_NO_PERMITIDO`, verificado por magic bytes).

### Contradicciones entre plan y regla (se sigue la regla)

- **SPEC-11, consolidación de reportes:** el plan pide 409 al consolidar un
  reporte duplicado y a la vez documenta 201 con el id existente. Se deja el
  **201** (la regla: la consolidación devuelve el reporte existente; el
  controlador siempre responde 201) y se documenta aquí la contradicción.
- **SPEC-04, borrador parcial y migraciones:** el borrador parcial exige
  relajar los `NOT NULL` de `solicitudes`; se hizo con una migración nueva
  **V8** (aditiva, no modifica ninguna migración existente) porque ninguna BD
  real ha aplicado V2 todavía y es el mecanismo estándar de Flyway.

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
política de contraseña, compatibilidad y reputación, un test de contexto
con los feature flags apagados y `CorreccionesVerificacionTest` (magic bytes,
lista blanca de datos públicos, preferencias de notificación, duplicado de
catálogo, recuperación 400/410 y verificación sin límite de documentos).
