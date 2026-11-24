-- =====================================================================
-- 03_libros.sql  |  Requiere: 02_categorias.sql
-- Carga 20 libros. El ISBN es unico: no ejecutar dos veces.
-- =====================================================================
USE bibliotecas_db;
SET @ahora = NOW(6);

INSERT INTO libros (titulo, autor, isbn, editorial, anio_publicacion, descripcion, categoria_id, created_at, updated_at) VALUES
 ('Cien anos de soledad', 'Gabriel Garcia Marquez', '9780307474728', 'Sudamericana', 1967, 'Historia de los Buendia en Macondo', (SELECT id FROM categorias WHERE nombre='Novela'), @ahora, @ahora),
 ('El coronel no tiene quien le escriba', 'Gabriel Garcia Marquez', '9780060737948', 'Sudamericana', 1961, 'Novela corta', (SELECT id FROM categorias WHERE nombre='Novela'), @ahora, @ahora),
 ('Clean Code', 'Robert C. Martin', '9780132350884', 'Prentice Hall', 2008, 'Buenas practicas de codigo', (SELECT id FROM categorias WHERE nombre='Informatica'), @ahora, @ahora),
 ('Cronica de una muerte anunciada', 'Gabriel Garcia Marquez', '9780307387349', 'Oveja Negra', 1981, 'Un crimen anunciado en un pueblo caribeno', (SELECT id FROM categorias WHERE nombre='Novela'), @ahora, @ahora),
 ('La voragine', 'Jose Eustasio Rivera', '9788437604947', 'Catedra', 1924, 'Novela de la selva colombiana', (SELECT id FROM categorias WHERE nombre='Novela'), @ahora, @ahora),
 ('Pedro Paramo', 'Juan Rulfo', '9788437604305', 'Catedra', 1955, 'Comala y sus fantasmas', (SELECT id FROM categorias WHERE nombre='Novela'), @ahora, @ahora),
 ('El amor en los tiempos del colera', 'Gabriel Garcia Marquez', '9780307389732', 'Oveja Negra', 1985, 'Historia de amor de medio siglo', (SELECT id FROM categorias WHERE nombre='Novela'), @ahora, @ahora),
 ('Rayuela', 'Julio Cortazar', '9788437604572', 'Catedra', 1963, 'Novela experimental', (SELECT id FROM categorias WHERE nombre='Novela'), @ahora, @ahora),
 ('La casa de los espiritus', 'Isabel Allende', '9780525433477', 'Plaza y Janes', 1982, 'Saga de la familia Trueba', (SELECT id FROM categorias WHERE nombre='Novela'), @ahora, @ahora),
 ('Effective Java', 'Joshua Bloch', '9780134685991', 'Addison-Wesley', 2018, 'Guia de buenas practicas en Java', (SELECT id FROM categorias WHERE nombre='Informatica'), @ahora, @ahora),
 ('Spring in Action', 'Craig Walls', '9781617297571', 'Manning', 2022, 'Desarrollo con Spring', (SELECT id FROM categorias WHERE nombre='Informatica'), @ahora, @ahora),
 ('Clean Architecture', 'Robert C. Martin', '9780134494166', 'Prentice Hall', 2017, 'Arquitectura de software', (SELECT id FROM categorias WHERE nombre='Informatica'), @ahora, @ahora),
 ('Introduction to Algorithms', 'Thomas H. Cormen', '9780262046305', 'MIT Press', 2022, 'Referencia de algoritmos', (SELECT id FROM categorias WHERE nombre='Informatica'), @ahora, @ahora),
 ('Cosmos', 'Carl Sagan', '9780345539434', 'Ballantine', 1980, 'Viaje por el universo', (SELECT id FROM categorias WHERE nombre='Fisica'), @ahora, @ahora),
 ('Breve historia del tiempo', 'Stephen Hawking', '9780553380163', 'Bantam', 1988, 'Del Big Bang a los agujeros negros', (SELECT id FROM categorias WHERE nombre='Fisica'), @ahora, @ahora),
 ('El gen egoista', 'Richard Dawkins', '9780198788607', 'Oxford', 1976, 'Evolucion vista desde los genes', (SELECT id FROM categorias WHERE nombre='Ciencias'), @ahora, @ahora),
 ('Sapiens', 'Yuval Noah Harari', '9780062316097', 'Harper', 2011, 'Breve historia de la humanidad', (SELECT id FROM categorias WHERE nombre='Historia'), @ahora, @ahora),
 ('Armas, germenes y acero', 'Jared Diamond', '9780393317558', 'Norton', 1997, 'Destinos de las sociedades humanas', (SELECT id FROM categorias WHERE nombre='Historia'), @ahora, @ahora),
 ('El principito', 'Antoine de Saint-Exupery', '9780156012195', 'Harcourt', 1943, 'Cuento clasico', (SELECT id FROM categorias WHERE nombre='Infantil'), @ahora, @ahora),
 ('Matilda', 'Roald Dahl', '9780142410370', 'Puffin', 1988, 'Una nina con poderes especiales', (SELECT id FROM categorias WHERE nombre='Infantil'), @ahora, @ahora);
