-- =====================================================================
-- 01_sedes.sql  |  Requiere: 01_schema.sql ya ejecutado (carpeta database/)
-- Carga 5 sedes (la 5ta esta inactiva para probar). No ejecutar dos veces.
-- =====================================================================
USE bibliotecas_db;
SET @ahora = NOW(6);

INSERT INTO sedes (nombre, direccion, ciudad, telefono, activa, created_at, updated_at) VALUES
 ('Sede Central',    'Calle 10 # 5-20',   'Armenia',    '6067410001', 1, @ahora, @ahora),
 ('Sede Norte',      'Carrera 14 # 30-8', 'Armenia',    '6067410002', 1, @ahora, @ahora),
 ('Sede Calarca',    'Calle 40 # 24-15',  'Calarca',    '6067410003', 1, @ahora, @ahora),
 ('Sede Sur',        'Carrera 19 # 2-45', 'Armenia',    '6067410004', 1, @ahora, @ahora),
 ('Sede Montenegro', 'Calle 20 # 8-12',   'Montenegro', '6067410005', 0, @ahora, @ahora);
