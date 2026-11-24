-- =====================================================================
-- Esquema MySQL 8 - Sistema de Gestion de Multiples Bibliotecas
-- Equivale a las entidades JPA (src/main/java/.../persistenceLayer/entity)
-- Uso:  mysql -u root -p < database/01_schema.sql
-- =====================================================================
CREATE DATABASE IF NOT EXISTS bibliotecas_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bibliotecas_db;

CREATE TABLE IF NOT EXISTS sedes (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(150) NOT NULL,
  direccion   VARCHAR(200) NOT NULL,
  ciudad      VARCHAR(100),
  telefono    VARCHAR(20),
  activa      BIT          NOT NULL,
  created_at  DATETIME(6),
  updated_at  DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_sedes_nombre UNIQUE (nombre)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS categorias (
  id                  BIGINT       NOT NULL AUTO_INCREMENT,
  nombre              VARCHAR(100) NOT NULL,
  descripcion         VARCHAR(500),
  categoria_padre_id  BIGINT,
  created_at          DATETIME(6),
  updated_at          DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_categorias_padre FOREIGN KEY (categoria_padre_id) REFERENCES categorias (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS libros (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  titulo            VARCHAR(200) NOT NULL,
  autor             VARCHAR(150) NOT NULL,
  isbn              VARCHAR(20)  NOT NULL,
  editorial         VARCHAR(150),
  anio_publicacion  INT,
  descripcion       VARCHAR(2000),
  categoria_id      BIGINT,
  created_at        DATETIME(6),
  updated_at        DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_libros_isbn UNIQUE (isbn),
  CONSTRAINT fk_libros_categoria FOREIGN KEY (categoria_id) REFERENCES categorias (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS ejemplares (
  id                   BIGINT      NOT NULL AUTO_INCREMENT,
  codigo               VARCHAR(50) NOT NULL,
  estado               ENUM('DISPONIBLE','PRESTADO','EN_TRASLADO') NOT NULL,
  libro_id             BIGINT      NOT NULL,
  sede_propietaria_id  BIGINT      NOT NULL,
  sede_actual_id       BIGINT      NOT NULL,
  created_at           DATETIME(6),
  updated_at           DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_ejemplares_codigo UNIQUE (codigo),
  CONSTRAINT fk_ejemplares_libro FOREIGN KEY (libro_id) REFERENCES libros (id),
  CONSTRAINT fk_ejemplares_sede_prop FOREIGN KEY (sede_propietaria_id) REFERENCES sedes (id),
  CONSTRAINT fk_ejemplares_sede_act FOREIGN KEY (sede_actual_id) REFERENCES sedes (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS clientes (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(150) NOT NULL,
  documento   VARCHAR(30)  NOT NULL,
  email       VARCHAR(150) NOT NULL,
  telefono    VARCHAR(20),
  direccion   VARCHAR(200),
  created_at  DATETIME(6),
  updated_at  DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_clientes_documento UNIQUE (documento),
  CONSTRAINT uk_clientes_email UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS usuarios_admin (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(150) NOT NULL,
  email       VARCHAR(150) NOT NULL,
  password    VARCHAR(100),
  rol         ENUM('ADMINISTRADOR_GENERAL','ADMINISTRADOR_SEDE','BIBLIOTECARIO')  NOT NULL,
  sede_id     BIGINT,
  activo      BIT          NOT NULL,
  created_at  DATETIME(6),
  updated_at  DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT uk_usuarios_email UNIQUE (email),
  CONSTRAINT fk_usuarios_sede FOREIGN KEY (sede_id) REFERENCES sedes (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS prestamos (
  id                   BIGINT      NOT NULL AUTO_INCREMENT,
  ejemplar_id          BIGINT      NOT NULL,
  cliente_id           BIGINT      NOT NULL,
  sede_prestamo_id     BIGINT      NOT NULL,
  sede_devolucion_id   BIGINT,
  fecha_inicio         DATE        NOT NULL,
  fecha_limite         DATE        NOT NULL,
  fecha_devolucion     DATE,
  devuelto_con_retraso BIT,
  dias_retraso         INT,
  estado               ENUM('ACTIVO','FINALIZADO') NOT NULL,
  created_at           DATETIME(6),
  updated_at           DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_prestamos_ejemplar FOREIGN KEY (ejemplar_id) REFERENCES ejemplares (id),
  CONSTRAINT fk_prestamos_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id),
  CONSTRAINT fk_prestamos_sede_pres FOREIGN KEY (sede_prestamo_id) REFERENCES sedes (id),
  CONSTRAINT fk_prestamos_sede_dev FOREIGN KEY (sede_devolucion_id) REFERENCES sedes (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reservas (
  id               BIGINT      NOT NULL AUTO_INCREMENT,
  cliente_id       BIGINT      NOT NULL,
  libro_id         BIGINT      NOT NULL,
  sede_id          BIGINT      NOT NULL,
  estado           ENUM('PENDIENTE','APROBADA','RECHAZADA','CANCELADA') NOT NULL,
  motivo_rechazo   VARCHAR(500),
  fecha_solicitud  DATETIME(6) NOT NULL,
  fecha_respuesta  DATETIME(6),
  created_at       DATETIME(6),
  updated_at       DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_reservas_cliente FOREIGN KEY (cliente_id) REFERENCES clientes (id),
  CONSTRAINT fk_reservas_libro FOREIGN KEY (libro_id) REFERENCES libros (id),
  CONSTRAINT fk_reservas_sede FOREIGN KEY (sede_id) REFERENCES sedes (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS traslados (
  id               BIGINT      NOT NULL AUTO_INCREMENT,
  ejemplar_id      BIGINT      NOT NULL,
  sede_origen_id   BIGINT      NOT NULL,
  sede_destino_id  BIGINT      NOT NULL,
  estado           ENUM('EN_TRANSITO','RECIBIDO','CANCELADO') NOT NULL,
  fecha_solicitud  DATETIME(6) NOT NULL,
  fecha_recepcion  DATETIME(6),
  observaciones    VARCHAR(500),
  created_at       DATETIME(6),
  updated_at       DATETIME(6),
  PRIMARY KEY (id),
  CONSTRAINT fk_traslados_ejemplar FOREIGN KEY (ejemplar_id) REFERENCES ejemplares (id),
  CONSTRAINT fk_traslados_origen FOREIGN KEY (sede_origen_id) REFERENCES sedes (id),
  CONSTRAINT fk_traslados_destino FOREIGN KEY (sede_destino_id) REFERENCES sedes (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS movimientos_ejemplar (
  id                BIGINT      NOT NULL AUTO_INCREMENT,
  ejemplar_id       BIGINT      NOT NULL,
  tipo              ENUM('REGISTRO','PRESTAMO','DEVOLUCION','TRASLADO_SALIDA','TRASLADO_RECEPCION','TRASLADO_CANCELADO') NOT NULL,
  sede_origen_id    BIGINT,
  sede_destino_id   BIGINT,
  fecha_movimiento  DATETIME(6) NOT NULL,
  detalle           VARCHAR(500),
  PRIMARY KEY (id),
  CONSTRAINT fk_mov_ejemplar FOREIGN KEY (ejemplar_id) REFERENCES ejemplares (id),
  CONSTRAINT fk_mov_origen FOREIGN KEY (sede_origen_id) REFERENCES sedes (id),
  CONSTRAINT fk_mov_destino FOREIGN KEY (sede_destino_id) REFERENCES sedes (id)
) ENGINE=InnoDB;

-- Indices para las consultas mas frecuentes (prestamos por cliente/estado, reservas por sede, etc.)
CREATE INDEX idx_ejemplares_libro_sede ON ejemplares (libro_id, sede_actual_id, estado);
CREATE INDEX idx_prestamos_cliente ON prestamos (cliente_id, estado);
CREATE INDEX idx_prestamos_ejemplar ON prestamos (ejemplar_id, estado);
CREATE INDEX idx_reservas_sede_estado ON reservas (sede_id, estado);
CREATE INDEX idx_mov_ejemplar_fecha ON movimientos_ejemplar (ejemplar_id, fecha_movimiento);
