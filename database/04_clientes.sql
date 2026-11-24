-- =====================================================================
-- 04_clientes.sql  |  Requiere: 01_schema.sql
-- Carga 8 clientes. Documento y email son unicos: no ejecutar dos veces.
-- =====================================================================
USE bibliotecas_db;
SET @ahora = NOW(6);

INSERT INTO clientes (nombre, documento, email, telefono, direccion, created_at, updated_at) VALUES
 ('Maria Lopez',    '1094000001', 'maria.lopez@example.com',    '3001112233', 'Cra 15 # 20-10', @ahora, @ahora),
 ('Juan Perez',     '1094000002', 'juan.perez@example.com',     '3004445566', 'Cll 21 # 14-30', @ahora, @ahora),
 ('Carlos Ramirez', '1094000003', 'carlos.ramirez@example.com', '3101230001', 'Cra 14 # 5-20',  @ahora, @ahora),
 ('Laura Gomez',    '1094000004', 'laura.gomez@example.com',    '3101230002', 'Cll 12 # 18-40', @ahora, @ahora),
 ('Andres Torres',  '1094000005', 'andres.torres@example.com',  '3101230003', 'Cra 20 # 9-15',  @ahora, @ahora),
 ('Sofia Herrera',  '1094000006', 'sofia.herrera@example.com',  '3101230004', 'Cll 8 # 13-22',  @ahora, @ahora),
 ('Diego Castro',   '1094000007', 'diego.castro@example.com',   '3101230005', 'Cra 17 # 3-30',  @ahora, @ahora),
 ('Valentina Ruiz', '1094000008', 'valentina.ruiz@example.com', '3101230006', 'Cll 25 # 15-8',  @ahora, @ahora);
