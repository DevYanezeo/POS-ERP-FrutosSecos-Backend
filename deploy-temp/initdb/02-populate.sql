-- sql
-- escript para poblar productos y lotes
-- Ejecuta primero si estás en una transacción abortada:
ROLLBACK;

-- Script corregido: database/script_poblacion_basico.sql
-- No usa BEGIN/COMMIT para evitar abortar todo el bloque si hay un error.

-- 1) Eliminar tablas si existen (orden seguro)
DROP TABLE IF EXISTS lotes;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS categorias;

-- 2) Crear tablas
CREATE TABLE categorias (
                            id_categoria SERIAL PRIMARY KEY,
                            nombre VARCHAR(150) NOT NULL
);

CREATE TABLE productos (
                           id_producto SERIAL PRIMARY KEY,
                           nombre VARCHAR(250) NOT NULL,
                           descripcion TEXT,
                           imagen VARCHAR(500),
                           precio INTEGER DEFAULT 0,
                           stock INTEGER DEFAULT 0,
                           unidad VARCHAR(20),
                           estado BOOLEAN DEFAULT true,
                           codigo VARCHAR(80) UNIQUE,
                           categoria_id INTEGER REFERENCES categorias(id_categoria) ON DELETE SET NULL,
                           fecha_vencimiento DATE
);

CREATE TABLE lotes (
                       id_lote SERIAL PRIMARY KEY,
                       producto_id INTEGER NOT NULL REFERENCES productos(id_producto) ON DELETE CASCADE,
                       cantidad INTEGER NOT NULL,
                       fecha_vencimiento DATE,
                       fecha_ingreso DATE NOT NULL DEFAULT CURRENT_DATE,
                       codigo_lote VARCHAR(150),
                       estado BOOLEAN DEFAULT true
);

-- 3) Insertar categorías
INSERT INTO categorias (nombre) VALUES
                                    ('Frutos Secos'),
                                    ('Frutas Deshidratadas'),
                                    ('Semillas'),
                                    ('Mix y Combinados');

-- 4) Insertar 10 productos con presentaciones en gramos
INSERT INTO productos (nombre, descripcion, precio, stock, unidad, estado, codigo, categoria_id, fecha_vencimiento) VALUES
                                                                                                                        ('Almendras', 'Almendras premium sin piel', 8500, 50, '500gr', true, '7804567890201', 1, '2025-06-15'),
                                                                                                                        ('Nueces', 'Nueces frescas chilenas', 12000, 30, '500gr', true, '7804567890202', 1, '2025-07-20'),
                                                                                                                        ('Castañas de Cajú', 'Cajú sin sal', 15000, 25, '250gr', true, '7804567890203', 1, '2025-09-25'),
                                                                                                                        ('Pasas', 'Pasas sin azúcar agregada', 4500, 60, '250gr', true, '7804567890204', 2, '2025-12-31'),
                                                                                                                        ('Higos Secos', 'Higos naturales', 7500, 35, '500gr', true, '7804567890205', 2, '2025-10-15'),
                                                                                                                        ('Damascos', 'Damascos sin conservantes', 9500, 30, '250gr', true, '7804567890206', 2, '2025-09-15'),
                                                                                                                        ('Semillas de Girasol', 'Semillas naturales', 3500, 80, '100gr', true, '7804567890207', 3, '2025-12-15'),
                                                                                                                        ('Semillas de Chía', 'Chía orgánica', 8500, 40, '250gr', true, '7804567890208', 3, '2025-10-20'),
                                                                                                                        ('Mix Estudiante', 'Frutos secos y pasas', 7800, 35, '500gr', true, '7804567890209', 4, '2025-08-30'),
                                                                                                                        ('Trail Mix', 'Mix premium', 11500, 20, '1000gr', true, '7804567890210', 4, '2025-09-20');

-- 5) Insertar 2 lotes por producto (A ~= 60% con ingreso 30 días atrás, B = resto con ingreso hoy)
-- Producto 1 (codigo 7804567890201)
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto,
       GREATEST(CEIL(p.stock * 0.6)::int, 1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A',
       true
FROM productos p WHERE p.codigo = '7804567890201';

INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto,
       (p.stock - GREATEST(CEIL(p.stock * 0.6)::int, 1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date,
       'L-'||p.codigo||'-B',
       true
FROM productos p WHERE p.codigo = '7804567890201';

-- Producto 2
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, GREATEST(CEIL(p.stock * 0.6)::int,1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A', true
FROM productos p WHERE p.codigo = '7804567890202';
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, (p.stock - GREATEST(CEIL(p.stock * 0.6)::int,1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date, 'L-'||p.codigo||'-B', true
FROM productos p WHERE p.codigo = '7804567890202';

-- Producto 3
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, GREATEST(CEIL(p.stock * 0.6)::int,1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A', true
FROM productos p WHERE p.codigo = '7804567890203';
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, (p.stock - GREATEST(CEIL(p.stock * 0.6)::int,1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date, 'L-'||p.codigo||'-B', true
FROM productos p WHERE p.codigo = '7804567890203';

-- Producto 4
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, GREATEST(CEIL(p.stock * 0.6)::int,1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A', true
FROM productos p WHERE p.codigo = '7804567890204';
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, (p.stock - GREATEST(CEIL(p.stock * 0.6)::int,1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date, 'L-'||p.codigo||'-B', true
FROM productos p WHERE p.codigo = '7804567890204';

-- Producto 5
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, GREATEST(CEIL(p.stock * 0.6)::int,1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A', true
FROM productos p WHERE p.codigo = '7804567890205';
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, (p.stock - GREATEST(CEIL(p.stock * 0.6)::int,1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date, 'L-'||p.codigo||'-B', true
FROM productos p WHERE p.codigo = '7804567890205';

-- Producto 6
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, GREATEST(CEIL(p.stock * 0.6)::int,1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A', true
FROM productos p WHERE p.codigo = '7804567890206';
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, (p.stock - GREATEST(CEIL(p.stock * 0.6)::int,1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date, 'L-'||p.codigo||'-B', true
FROM productos p WHERE p.codigo = '7804567890206';

-- Producto 7
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, GREATEST(CEIL(p.stock * 0.6)::int,1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A', true
FROM productos p WHERE p.codigo = '7804567890207';
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, (p.stock - GREATEST(CEIL(p.stock * 0.6)::int,1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date, 'L-'||p.codigo||'-B', true
FROM productos p WHERE p.codigo = '7804567890207';

-- Producto 8
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, GREATEST(CEIL(p.stock * 0.6)::int,1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A', true
FROM productos p WHERE p.codigo = '7804567890208';
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, (p.stock - GREATEST(CEIL(p.stock * 0.6)::int,1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date, 'L-'||p.codigo||'-B', true
FROM productos p WHERE p.codigo = '7804567890208';

-- Producto 9
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, GREATEST(CEIL(p.stock * 0.6)::int,1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A', true
FROM productos p WHERE p.codigo = '7804567890209';
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, (p.stock - GREATEST(CEIL(p.stock * 0.6)::int,1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date, 'L-'||p.codigo||'-B', true
FROM productos p WHERE p.codigo = '7804567890209';

-- Producto 10
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, GREATEST(CEIL(p.stock * 0.6)::int,1),
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       (CURRENT_DATE - INTERVAL '30 days')::date,
       'L-'||p.codigo||'-A', true
FROM productos p WHERE p.codigo = '7804567890210';
INSERT INTO lotes (producto_id, cantidad, fecha_vencimiento, fecha_ingreso, codigo_lote, estado)
SELECT p.id_producto, (p.stock - GREATEST(CEIL(p.stock * 0.6)::int,1))::int,
       COALESCE(p.fecha_vencimiento, (CURRENT_DATE + INTERVAL '180 days')::date),
       CURRENT_DATE::date, 'L-'||p.codigo||'-B', true
FROM productos p WHERE p.codigo = '7804567890210';

-- 6) Recalcular stock de productos según lotes activos
UPDATE productos p
SET stock = COALESCE((SELECT SUM(l.cantidad) FROM lotes l WHERE l.producto_id = p.id_producto AND l.estado = true), 0);
