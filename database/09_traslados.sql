-- =====================================================================
-- 09_traslados.sql  |  Requiere: 06_ejemplares.sql (y 07_prestamos.sql ya ejecutado)
-- Carga 3 traslados con su historial:
--   EN_TRANSITO : Armas, germenes y acero (Norte -> Central)  -> ejemplar EN_TRASLADO
--   RECIBIDO    : La voragine (Central -> Sur)                -> ejemplar queda en Sur
--   CANCELADO   : El gen egoista (Central -> Norte)           -> ejemplar sigue en Central
-- No ejecutar dos veces.
-- =====================================================================
USE bibliotecas_db;
SET @ahora = NOW(6);

-- A) EN_TRANSITO
INSERT INTO traslados (ejemplar_id, sede_origen_id, sede_destino_id, estado, fecha_solicitud, fecha_recepcion, observaciones, created_at, updated_at)
SELECT e.id, e.sede_actual_id, (SELECT id FROM sedes WHERE nombre='Sede Central'), 'EN_TRANSITO', DATE_SUB(@ahora, INTERVAL 1 DAY), NULL, 'Solicitado por demanda en la sede central', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id
WHERE l.isbn='9780393317558' AND s.nombre='Sede Norte';
UPDATE ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id
SET e.estado='EN_TRASLADO' WHERE l.isbn='9780393317558' AND s.nombre='Sede Norte';

-- B) RECIBIDO
INSERT INTO traslados (ejemplar_id, sede_origen_id, sede_destino_id, estado, fecha_solicitud, fecha_recepcion, observaciones, created_at, updated_at)
SELECT e.id, e.sede_actual_id, (SELECT id FROM sedes WHERE nombre='Sede Sur'), 'RECIBIDO', DATE_SUB(@ahora, INTERVAL 10 DAY), DATE_SUB(@ahora, INTERVAL 8 DAY), 'Recibido sin novedades', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id
WHERE l.isbn='9788437604947' AND s.nombre='Sede Central';
UPDATE ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id
SET e.sede_actual_id=(SELECT id FROM sedes WHERE nombre='Sede Sur')
WHERE l.isbn='9788437604947' AND s.nombre='Sede Central';

-- C) CANCELADO
INSERT INTO traslados (ejemplar_id, sede_origen_id, sede_destino_id, estado, fecha_solicitud, fecha_recepcion, observaciones, created_at, updated_at)
SELECT e.id, e.sede_actual_id, (SELECT id FROM sedes WHERE nombre='Sede Norte'), 'CANCELADO', DATE_SUB(@ahora, INTERVAL 5 DAY), NULL, 'Cancelado: ya no se necesita en la sede destino', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id
WHERE l.isbn='9780198788607' AND s.nombre='Sede Central';

-- Historial
INSERT INTO movimientos_ejemplar (ejemplar_id, tipo, sede_origen_id, sede_destino_id, fecha_movimiento, detalle)
SELECT t.ejemplar_id, 'TRASLADO_SALIDA', t.sede_origen_id, t.sede_destino_id, t.fecha_solicitud, 'Salida por traslado' FROM traslados t;
INSERT INTO movimientos_ejemplar (ejemplar_id, tipo, sede_origen_id, sede_destino_id, fecha_movimiento, detalle)
SELECT t.ejemplar_id, 'TRASLADO_RECEPCION', t.sede_origen_id, t.sede_destino_id, t.fecha_recepcion, 'Recepcion de traslado' FROM traslados t WHERE t.estado='RECIBIDO';
INSERT INTO movimientos_ejemplar (ejemplar_id, tipo, sede_origen_id, sede_destino_id, fecha_movimiento, detalle)
SELECT t.ejemplar_id, 'TRASLADO_CANCELADO', t.sede_origen_id, t.sede_destino_id, DATE_SUB(@ahora, INTERVAL 4 DAY), 'Traslado cancelado' FROM traslados t WHERE t.estado='CANCELADO';
