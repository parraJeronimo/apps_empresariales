-- =====================================================================
-- 07_prestamos.sql  |  Requiere: 04_clientes.sql y 06_ejemplares.sql
-- Carga 7 prestamos con su historial (movimientos PRESTAMO / DEVOLUCION):
--   ACTIVOS (4): Clean Code (Maria), Effective Java (Carlos), Sapiens (Laura, VENCIDO),
--                Clean Architecture (Maria)
--   FINALIZADOS (3): El principito (a tiempo), Cosmos (con 6 dias de retraso),
--                    Pedro Paramo (prestado en Central, devuelto en Norte)
-- Los ejemplares de los prestamos ACTIVOS quedan en estado PRESTADO.
-- Fechas relativas a hoy. No ejecutar dos veces.
-- =====================================================================
USE bibliotecas_db;
SET @ahora = NOW(6);

-- ---- ACTIVOS ----
INSERT INTO prestamos (ejemplar_id, cliente_id, sede_prestamo_id, fecha_inicio, fecha_limite, devuelto_con_retraso, dias_retraso, estado, created_at, updated_at)
SELECT e.id, c.id, e.sede_actual_id, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 0, 0, 'ACTIVO', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id JOIN clientes c ON c.documento='1094000001'
WHERE l.isbn='9780132350884' AND s.nombre='Sede Central';          -- Clean Code -> Maria

INSERT INTO prestamos (ejemplar_id, cliente_id, sede_prestamo_id, fecha_inicio, fecha_limite, devuelto_con_retraso, dias_retraso, estado, created_at, updated_at)
SELECT e.id, c.id, e.sede_actual_id, DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 9 DAY), 0, 0, 'ACTIVO', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id JOIN clientes c ON c.documento='1094000003'
WHERE l.isbn='9780134685991' AND s.nombre='Sede Central';          -- Effective Java -> Carlos

INSERT INTO prestamos (ejemplar_id, cliente_id, sede_prestamo_id, fecha_inicio, fecha_limite, devuelto_con_retraso, dias_retraso, estado, created_at, updated_at)
SELECT e.id, c.id, e.sede_actual_id, DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 16 DAY), 0, 0, 'ACTIVO', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id JOIN clientes c ON c.documento='1094000004'
WHERE l.isbn='9780062316097' AND s.nombre='Sede Norte';            -- Sapiens -> Laura (vencido)

INSERT INTO prestamos (ejemplar_id, cliente_id, sede_prestamo_id, fecha_inicio, fecha_limite, devuelto_con_retraso, dias_retraso, estado, created_at, updated_at)
SELECT e.id, c.id, e.sede_actual_id, DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), 0, 0, 'ACTIVO', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id JOIN clientes c ON c.documento='1094000001'
WHERE l.isbn='9780134494166' AND s.nombre='Sede Norte';            -- Clean Architecture -> Maria

-- ---- FINALIZADOS ----
INSERT INTO prestamos (ejemplar_id, cliente_id, sede_prestamo_id, sede_devolucion_id, fecha_inicio, fecha_limite, fecha_devolucion, devuelto_con_retraso, dias_retraso, estado, created_at, updated_at)
SELECT e.id, c.id, e.sede_actual_id, e.sede_actual_id, DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), DATE_SUB(CURDATE(), INTERVAL 8 DAY), 0, 0, 'FINALIZADO', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id JOIN clientes c ON c.documento='1094000005'
WHERE l.isbn='9780156012195' AND s.nombre='Sede Central';          -- El principito -> Andres (a tiempo)

INSERT INTO prestamos (ejemplar_id, cliente_id, sede_prestamo_id, sede_devolucion_id, fecha_inicio, fecha_limite, fecha_devolucion, devuelto_con_retraso, dias_retraso, estado, created_at, updated_at)
SELECT e.id, c.id, e.sede_actual_id, e.sede_actual_id, DATE_SUB(CURDATE(), INTERVAL 40 DAY), DATE_SUB(CURDATE(), INTERVAL 26 DAY), DATE_SUB(CURDATE(), INTERVAL 20 DAY), 1, 6, 'FINALIZADO', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id JOIN clientes c ON c.documento='1094000006'
WHERE l.isbn='9780345539434' AND s.nombre='Sede Norte';            -- Cosmos -> Sofia (6 dias de retraso)

INSERT INTO prestamos (ejemplar_id, cliente_id, sede_prestamo_id, sede_devolucion_id, fecha_inicio, fecha_limite, fecha_devolucion, devuelto_con_retraso, dias_retraso, estado, created_at, updated_at)
SELECT e.id, c.id, e.sede_actual_id, (SELECT id FROM sedes WHERE nombre='Sede Norte'), DATE_SUB(CURDATE(), INTERVAL 15 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_SUB(CURDATE(), INTERVAL 3 DAY), 0, 0, 'FINALIZADO', @ahora, @ahora
FROM ejemplares e JOIN libros l ON l.id=e.libro_id JOIN sedes s ON s.id=e.sede_actual_id JOIN clientes c ON c.documento='1094000002'
WHERE l.isbn='9788437604305' AND s.nombre='Sede Central';          -- Pedro Paramo -> Juan (devuelto en Norte)

-- ---- Estado de los ejemplares ----
UPDATE ejemplares e SET e.estado='PRESTADO'
WHERE e.id IN (SELECT ejemplar_id FROM (SELECT ejemplar_id FROM prestamos WHERE estado='ACTIVO') p);
-- El ejemplar devuelto en otra sede queda alli
UPDATE ejemplares e JOIN libros l ON l.id=e.libro_id
SET e.sede_actual_id=(SELECT id FROM sedes WHERE nombre='Sede Norte')
WHERE l.isbn='9788437604305' AND e.codigo=CONCAT('9788437604305-', LPAD((SELECT id FROM sedes WHERE nombre='Sede Central'),3,'0'));

-- ---- Historial ----
INSERT INTO movimientos_ejemplar (ejemplar_id, tipo, sede_origen_id, sede_destino_id, fecha_movimiento, detalle)
SELECT p.ejemplar_id, 'PRESTAMO', p.sede_prestamo_id, NULL, TIMESTAMP(p.fecha_inicio, '10:00:00'), 'Prestamo'
FROM prestamos p;
INSERT INTO movimientos_ejemplar (ejemplar_id, tipo, sede_origen_id, sede_destino_id, fecha_movimiento, detalle)
SELECT p.ejemplar_id, 'DEVOLUCION', p.sede_prestamo_id, p.sede_devolucion_id, TIMESTAMP(p.fecha_devolucion, '16:00:00'), 'Devolucion'
FROM prestamos p WHERE p.estado='FINALIZADO';
