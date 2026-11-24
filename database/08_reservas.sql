-- =====================================================================
-- 08_reservas.sql  |  Requiere: 04_clientes.sql, 03_libros.sql y 01_sedes.sql
-- Carga 4 reservas, una por estado: PENDIENTE, APROBADA, RECHAZADA, CANCELADA.
-- No ejecutar dos veces.
-- =====================================================================
USE bibliotecas_db;
SET @ahora = NOW(6);

INSERT INTO reservas (cliente_id, libro_id, sede_id, estado, motivo_rechazo, fecha_solicitud, fecha_respuesta, created_at, updated_at)
SELECT c.id, l.id, s.id, 'PENDIENTE', NULL, DATE_SUB(@ahora, INTERVAL 1 DAY), NULL, @ahora, @ahora
FROM clientes c, libros l, sedes s WHERE c.documento='1094000007' AND l.isbn='9788437604572' AND s.nombre='Sede Central';   -- Diego pide Rayuela

INSERT INTO reservas (cliente_id, libro_id, sede_id, estado, motivo_rechazo, fecha_solicitud, fecha_respuesta, created_at, updated_at)
SELECT c.id, l.id, s.id, 'APROBADA', NULL, DATE_SUB(@ahora, INTERVAL 3 DAY), DATE_SUB(@ahora, INTERVAL 2 DAY), @ahora, @ahora
FROM clientes c, libros l, sedes s WHERE c.documento='1094000008' AND l.isbn='9781617297571' AND s.nombre='Sede Norte';     -- Valentina: Spring in Action

INSERT INTO reservas (cliente_id, libro_id, sede_id, estado, motivo_rechazo, fecha_solicitud, fecha_respuesta, created_at, updated_at)
SELECT c.id, l.id, s.id, 'RECHAZADA', 'No hay ejemplares disponibles en la sede', DATE_SUB(@ahora, INTERVAL 4 DAY), DATE_SUB(@ahora, INTERVAL 4 DAY), @ahora, @ahora
FROM clientes c, libros l, sedes s WHERE c.documento='1094000002' AND l.isbn='9780142410370' AND s.nombre='Sede Calarca';  -- Juan: Matilda

INSERT INTO reservas (cliente_id, libro_id, sede_id, estado, motivo_rechazo, fecha_solicitud, fecha_respuesta, created_at, updated_at)
SELECT c.id, l.id, s.id, 'CANCELADA', NULL, DATE_SUB(@ahora, INTERVAL 6 DAY), NULL, @ahora, @ahora
FROM clientes c, libros l, sedes s WHERE c.documento='1094000003' AND l.isbn='9780062316097' AND s.nombre='Sede Central';   -- Carlos: Sapiens
