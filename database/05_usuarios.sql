-- =====================================================================
-- 05_usuarios.sql  |  Requiere: 01_sedes.sql
-- Carga 7 usuarios administrativos. TODOS usan la clave: Admin12345
--   admin@biblioteca.com            ADMINISTRADOR_GENERAL (sin sede)
--   admin.central / admin.norte     ADMINISTRADOR_SEDE
--   biblio.norte / .central / .sur  BIBLIOTECARIO
--   biblio.inactivo                 BIBLIOTECARIO (inactivo)
-- El email es unico: no ejecutar dos veces. NO usar estas claves en produccion.
-- =====================================================================
USE bibliotecas_db;
SET @ahora = NOW(6);
SET @hash = '$2a$10$OHSuPyXbCLFWvAmCvPFw2OAQjDGCh2ucqeLboBtN.WHSkuAjENKae';  -- BCrypt de "Admin12345"

INSERT INTO usuarios_admin (nombre, email, password, rol, sede_id, activo, created_at, updated_at) VALUES
 ('Administrador General',  'admin@biblioteca.com',          @hash, 'ADMINISTRADOR_GENERAL', NULL, 1, @ahora, @ahora),
 ('Admin Sede Central',     'admin.central@biblioteca.com',  @hash, 'ADMINISTRADOR_SEDE', (SELECT id FROM sedes WHERE nombre='Sede Central'), 1, @ahora, @ahora),
 ('Admin Sede Norte',       'admin.norte@biblioteca.com',    @hash, 'ADMINISTRADOR_SEDE', (SELECT id FROM sedes WHERE nombre='Sede Norte'),   1, @ahora, @ahora),
 ('Bibliotecario Norte',    'biblio.norte@biblioteca.com',   @hash, 'BIBLIOTECARIO',      (SELECT id FROM sedes WHERE nombre='Sede Norte'),   1, @ahora, @ahora),
 ('Bibliotecario Central',  'biblio.central@biblioteca.com', @hash, 'BIBLIOTECARIO',      (SELECT id FROM sedes WHERE nombre='Sede Central'), 1, @ahora, @ahora),
 ('Bibliotecario Sur',      'biblio.sur@biblioteca.com',     @hash, 'BIBLIOTECARIO',      (SELECT id FROM sedes WHERE nombre='Sede Sur'),     1, @ahora, @ahora),
 ('Bibliotecario Inactivo', 'biblio.inactivo@biblioteca.com',@hash, 'BIBLIOTECARIO',      (SELECT id FROM sedes WHERE nombre='Sede Calarca'), 0, @ahora, @ahora);
