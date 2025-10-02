CREATE TABLE IF NOT EXISTS categorias (
  id_categoria SERIAL PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS productos (
  id_producto SERIAL PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  descripcion TEXT,
  imagen TEXT,
  precio INTEGER,
  stock INTEGER,
  unidad VARCHAR(50),
  estado BOOLEAN,
  fecha_vencimiento DATE,
  codigo VARCHAR(100),
  categoria_id INTEGER REFERENCES categorias(id_categoria) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_productos_codigo ON productos(codigo);

CREATE TABLE IF NOT EXISTS usuarios (
  id_usuario SERIAL PRIMARY KEY,
  nombre VARCHAR(255),
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  rol VARCHAR(50),
  rut VARCHAR(12) UNIQUE,
  telefono VARCHAR(50),
  activo BOOLEAN DEFAULT TRUE
);
