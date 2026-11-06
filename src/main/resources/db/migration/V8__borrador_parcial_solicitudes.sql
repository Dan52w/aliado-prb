-- V8: el borrador parcial de SPEC-04 permite crear una solicitud sin categoría,
-- zona o descripción (se completan al editar o al publicar). PUBLICADA sigue
-- exigiendo los tres campos: la regla vive en aplicación y `publicar` la revalida.
-- Solo se relajan los NOT NULL; no se toca ninguna migración anterior.
ALTER TABLE solicitudes ALTER COLUMN categoria_id DROP NOT NULL;
ALTER TABLE solicitudes ALTER COLUMN zona_id DROP NOT NULL;
ALTER TABLE solicitudes ALTER COLUMN descripcion DROP NOT NULL;
