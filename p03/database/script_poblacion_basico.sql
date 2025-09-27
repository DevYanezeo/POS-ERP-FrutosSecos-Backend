-- Script de población básico - Solo para pruebas rápidas
-- Tienda de frutos secos - Chile

-- Categorías básicas
INSERT INTO categorias (nombre) VALUES 
('Frutos Secos'),
('Frutas Deshidratadas'),
('Semillas'),
('Mix y Combinados');

-- Productos básicos
INSERT INTO productos (nombre, descripcion, precio, stock, unidad, estado, codigo, categoria_id, fecha_vencimiento) VALUES 
-- Frutos Secos
('Almendras', 'Almendras premium sin piel', 8500, 50, 'kg', true, '7804567890201', 1, '2025-06-15'),
('Nueces', 'Nueces frescas chilenas', 12000, 30, 'kg', true, '7804567890202', 1, '2025-07-20'),
('Castañas de Cajú', 'Cajú sin sal', 15000, 25, 'kg', true, '7804567890203', 1, '2025-09-25'),

-- Frutas Deshidratadas  
('Pasas', 'Pasas sin azúcar agregada', 4500, 60, 'kg', true, '7804567890204', 2, '2025-12-31'),
('Higos Secos', 'Higos naturales', 7500, 35, 'kg', true, '7804567890205', 2, '2025-10-15'),
('Damascos', 'Damascos sin conservantes', 9500, 30, 'kg', true, '7804567890206', 2, '2025-09-15'),

-- Semillas
('Semillas de Girasol', 'Semillas naturales', 3500, 80, 'kg', true, '7804567890207', 3, '2025-12-15'),
('Semillas de Chía', 'Chía orgánica', 8500, 40, 'kg', true, '7804567890208', 3, '2025-10-20'),

-- Mix
('Mix Estudiante', 'Frutos secos y pasas', 7800, 35, 'kg', true, '7804567890209', 4, '2025-08-30'),
('Trail Mix', 'Mix premium', 11500, 20, 'kg', true, '7804567890210', 4, '2025-09-20');