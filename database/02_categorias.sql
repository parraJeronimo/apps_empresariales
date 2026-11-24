-- =====================================================================
-- 02_categorias.sql  |  Requiere: 01_schema.sql
-- Carga 4 categorias principales y 3 subcategorias. No ejecutar dos veces.
-- =====================================================================
USE bibliotecas_db;
SET @ahora = NOW(6);

INSERT INTO categorias (nombre, descripcion, categoria_padre_id, created_at, updated_at) VALUES
 ('Literatura', 'Novela, cuento y poesia',              NULL, @ahora, @ahora),
 ('Ciencias',   'Libros cientificos y de divulgacion',  NULL, @ahora, @ahora),
 ('Historia',   'Historia universal y divulgacion',     NULL, @ahora, @ahora),
 ('Infantil',   'Literatura infantil y juvenil',        NULL, @ahora, @ahora);

-- Subcategorias (apuntan a su categoria padre)
INSERT INTO categorias (nombre, descripcion, categoria_padre_id, created_at, updated_at) VALUES
 ('Novela',      'Novela latinoamericana',  (SELECT id FROM (SELECT id FROM categorias WHERE nombre='Literatura') c), @ahora, @ahora),
 ('Informatica', 'Programacion y sistemas', (SELECT id FROM (SELECT id FROM categorias WHERE nombre='Ciencias')   c), @ahora, @ahora),
 ('Fisica',      'Fisica y astronomia',     (SELECT id FROM (SELECT id FROM categorias WHERE nombre='Ciencias')   c), @ahora, @ahora);
