-- =====================================================================
-- 06_ejemplares.sql  |  Requiere: 01_sedes.sql y 03_libros.sql
-- Crea las copias fisicas (codigo = ISBN-numero de sede) y su movimiento REGISTRO.
--   * Los 20 libros: una copia en Sede Central y otra en Sede Norte (40)
--   * 4 libros de informatica: copia extra en Sede Sur
--   * 2 libros infantiles: copia extra en Sede Calarca
-- Total 46 ejemplares. El codigo es unico: no ejecutar dos veces.
-- =====================================================================
USE bibliotecas_db;
SET @ahora = NOW(6);
SET @registro = DATE_SUB(@ahora, INTERVAL 60 DAY);   -- fecha de alta (historial cronologico)

INSERT INTO ejemplares (codigo, estado, libro_id, sede_propietaria_id, sede_actual_id, created_at, updated_at)
SELECT CONCAT(l.isbn,'-',LPAD(s.id,3,'0')), 'DISPONIBLE', l.id, s.id, s.id, @ahora, @ahora
FROM libros l JOIN sedes s ON s.nombre IN ('Sede Central','Sede Norte');

INSERT INTO ejemplares (codigo, estado, libro_id, sede_propietaria_id, sede_actual_id, created_at, updated_at)
SELECT CONCAT(l.isbn,'-',LPAD(s.id,3,'0')), 'DISPONIBLE', l.id, s.id, s.id, @ahora, @ahora
FROM libros l JOIN sedes s ON s.nombre = 'Sede Sur'
WHERE l.isbn IN ('9780134685991','9781617297571','9780134494166','9780262046305');

INSERT INTO ejemplares (codigo, estado, libro_id, sede_propietaria_id, sede_actual_id, created_at, updated_at)
SELECT CONCAT(l.isbn,'-',LPAD(s.id,3,'0')), 'DISPONIBLE', l.id, s.id, s.id, @ahora, @ahora
FROM libros l JOIN sedes s ON s.nombre = 'Sede Calarca'
WHERE l.isbn IN ('9780156012195','9780142410370');

-- Historial: movimiento de registro de cada ejemplar
INSERT INTO movimientos_ejemplar (ejemplar_id, tipo, sede_origen_id, sede_destino_id, fecha_movimiento, detalle)
SELECT e.id, 'REGISTRO', NULL, e.sede_actual_id, @registro, 'Registro inicial'
FROM ejemplares e;
