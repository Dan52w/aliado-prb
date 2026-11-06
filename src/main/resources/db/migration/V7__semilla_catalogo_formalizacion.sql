-- V7__semilla_catalogo_formalizacion.sql
-- Semilla del catálogo y de la ruta de formalización (plan general §30).
-- Idempotente: los INSERT usan UUID fijos y ON CONFLICT DO NOTHING.
-- Las zonas son de Santa Marta (ciudad piloto, SPEC-03) y son ejemplos editables por el admin.

INSERT INTO categorias_servicio (id, nombre, estado) VALUES
  ('11111111-1111-1111-1111-111111111101', 'Aire acondicionado', 'ACTIVO'),
  ('11111111-1111-1111-1111-111111111102', 'Electricidad', 'ACTIVO'),
  ('11111111-1111-1111-1111-111111111103', 'Plomería', 'ACTIVO'),
  ('11111111-1111-1111-1111-111111111104', 'Pintura', 'ACTIVO'),
  ('11111111-1111-1111-1111-111111111105', 'Mantenimiento', 'ACTIVO'),
  ('11111111-1111-1111-1111-111111111106', 'Electrodomésticos', 'ACTIVO'),
  ('11111111-1111-1111-1111-111111111107', 'Instalación de equipos', 'ACTIVO')
ON CONFLICT (id) DO NOTHING;

INSERT INTO zonas (id, nombre, cobertura_geografica, estado) VALUES
  ('22222222-2222-2222-2222-222222222201', 'Centro Histórico', 'Santa Marta, Centro Histórico', 'ACTIVO'),
  ('22222222-2222-2222-2222-222222222202', 'El Rodadero', 'Santa Marta, El Rodadero', 'ACTIVO'),
  ('22222222-2222-2222-2222-222222222203', 'Taganga', 'Santa Marta, Taganga', 'ACTIVO'),
  ('22222222-2222-2222-2222-222222222204', 'Gaira', 'Santa Marta, Gaira', 'ACTIVO'),
  ('22222222-2222-2222-2222-222222222205', 'Pozos Colorados', 'Santa Marta, Pozos Colorados', 'ACTIVO')
ON CONFLICT (id) DO NOTHING;

INSERT INTO rutas_formalizacion (id, nombre, descripcion, activa) VALUES
  ('33333333-3333-3333-3333-333333333301', 'Ruta de formalización', 'Pasos orientativos para formalizar tu oficio como prestador independiente.', true)
ON CONFLICT (id) DO NOTHING;

INSERT INTO pasos_formalizacion (id, ruta_id, titulo, descripcion, orden) VALUES
  ('33333333-3333-3333-3333-333333333302', '33333333-3333-3333-3333-333333333301', 'Definir tu actividad', 'Identifica los servicios que ofreces y cómo los facturarás.', 1),
  ('33333333-3333-3333-3333-333333333303', '33333333-3333-3333-3333-333333333301', 'Registro tributario', 'Consulta los trámites vigentes para registrar tu actividad económica.', 2),
  ('33333333-3333-3333-3333-333333333304', '33333333-3333-3333-3333-333333333301', 'Seguridad social', 'Infórmate sobre afiliación a salud y pensión como independiente.', 3),
  ('33333333-3333-3333-3333-333333333305', '33333333-3333-3333-3333-333333333301', 'Facturación', 'Conoce cómo emitir facturas por tus servicios.', 4)
ON CONFLICT (id) DO NOTHING;

INSERT INTO enlaces_formalizacion (id, paso_id, nombre, url, estado) VALUES
  ('33333333-3333-3333-3333-333333333306', '33333333-3333-3333-3333-333333333302', 'DIAN — Registro Único Tributario', 'https://www.dian.gov.co/', 'DISPONIBLE'),
  ('33333333-3333-3333-3333-333333333307', '33333333-3333-3333-3333-333333333303', 'Cámara de Comercio de Santa Marta', 'https://www.ccsm.org.co/', 'DISPONIBLE'),
  ('33333333-3333-3333-3333-333333333308', '33333333-3333-3333-3333-333333333304', 'Portal de seguridad social', 'https://www.minsalud.gov.co/', 'DISPONIBLE')
ON CONFLICT (id) DO NOTHING;
