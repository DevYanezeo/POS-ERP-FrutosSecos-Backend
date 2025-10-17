-- Script de población para la base de datos ERP Frutos Secos
-- Tienda de frutos secos - Chile

-- ==============================================
-- TABLA: CATEGORIAS
-- ==============================================

INSERT INTO categorias (nombre) VALUES 
('Frutos Secos'),
('Frutas Deshidratadas'),
('Semillas'),
('Cereales y Granolas'),
('Legumbres'),
('Especias y Condimentos'),
('Snacks Saludables'),
('Aceites y Vinagres'),
('Superalimentos'),
('Mix y Combinados');

-- ==============================================
-- TABLA: PRODUCTOS
-- ==============================================

-- Frutos Secos (categoria_id = 1)
INSERT INTO productos (nombre, descripcion, precio, stock, unidad, estado, codigo, categoria_id, fecha_vencimiento) VALUES 
('Almendras Repeladas', 'Almendras sin piel, ideales para repostería y consumo directo', 8500, 50, 'kg', true, '7804567890123', 1, '2025-06-15'),
('Nueces de Nogal', 'Nueces frescas y crujientes, ricas en omega 3', 12000, 30, 'kg', true, '7804567890124', 1, '2025-07-20'),
('Avellanas Tostadas', 'Avellanas tostadas con sal marina', 9500, 40, 'kg', true, '7804567890125', 1, '2025-08-10'),
('Castañas de Cajú', 'Castañas de cajú premium, sin sal', 15000, 25, 'kg', true, '7804567890126', 1, '2025-09-25'),
('Pistachos Salados', 'Pistachos tostados con sal de mar', 18000, 20, 'kg', true, '7804567890127', 1, '2025-05-30'),
('Almendras con Sal', 'Almendras tostadas con sal marina', 9000, 45, 'kg', true, '7804567890128', 1, '2025-06-12'),
('Nueces Brasileñas', 'Nueces de Brasil, fuente natural de selenio', 16000, 15, 'kg', true, '7804567890129', 1, '2025-08-15'),
('Macadamias', 'Macadamias premium importadas', 25000, 10, 'kg', true, '7804567890130', 1, '2025-07-05'),

-- Frutas Deshidratadas (categoria_id = 2)
('Pasas Rubias', 'Pasas de uva rubia sin azúcar agregada', 4500, 60, 'kg', true, '7804567890131', 2, '2025-12-31'),
('Higos Secos', 'Higos deshidratados naturales', 7500, 35, 'kg', true, '7804567890132', 2, '2025-10-15'),
('Ciruelas Deshidratadas', 'Ciruelas sin carozo, secas naturalmente', 6000, 40, 'kg', true, '7804567890133', 2, '2025-11-20'),
('Dátiles Medjool', 'Dátiles premium, dulces y jugosos', 12500, 20, 'kg', true, '7804567890134', 2, '2025-09-30'),
('Mango Deshidratado', 'Trozos de mango seco sin azúcar', 8000, 25, 'kg', true, '7804567890135', 2, '2025-08-25'),
('Damascos Secos', 'Damascos deshidratados sin conservantes', 9500, 30, 'kg', true, '7804567890136', 2, '2025-09-15'),
('Arándanos Deshidratados', 'Arándanos secos endulzados naturalmente', 11000, 22, 'kg', true, '7804567890137', 2, '2025-07-30'),

-- Semillas (categoria_id = 3)
('Semillas de Girasol', 'Semillas de girasol sin sal, naturales', 3500, 80, 'kg', true, '7804567890138', 3, '2025-12-15'),
('Semillas de Calabaza', 'Pepitas de calabaza tostadas', 5500, 50, 'kg', true, '7804567890139', 3, '2025-11-10'),
('Semillas de Chía', 'Chía orgánica, rica en omega 3', 8500, 40, 'kg', true, '7804567890140', 3, '2025-10-20'),
('Semillas de Sésamo', 'Ajonjolí blanco tostado', 4800, 60, 'kg', true, '7804567890141', 3, '2026-01-15'),
('Semillas de Amapola', 'Semillas de amapola para panadería', 12000, 15, 'kg', true, '7804567890142', 3, '2025-12-05'),
('Linaza Dorada', 'Semillas de lino doradas, molidas', 4200, 70, 'kg', true, '7804567890143', 3, '2025-11-25'),

-- Cereales y Granolas (categoria_id = 4)
('Avena Integral', 'Hojuelas de avena integral', 2800, 100, 'kg', true, '7804567890144', 4, '2026-02-28'),
('Granola Artesanal', 'Mix de avena, frutos secos y miel', 6500, 45, 'kg', true, '7804567890145', 4, '2025-10-30'),
('Quinoa Blanca', 'Quinoa real boliviana, grano entero', 7800, 35, 'kg', true, '7804567890146', 4, '2026-01-20'),
('Amaranto Inflado', 'Amaranto expandido, libre de gluten', 5200, 30, 'kg', true, '7804567890147', 4, '2025-12-10'),
('Muesli Premium', 'Mezcla de cereales, frutas y frutos secos', 7200, 25, 'kg', true, '7804567890148', 4, '2025-11-15'),

-- Legumbres (categoria_id = 5)
('Garbanzos Secos', 'Garbanzos nacionales, calibre grande', 3200, 120, 'kg', true, '7804567890149', 5, '2026-03-15'),
('Lentejas Rojas', 'Lentejas turcas sin piel', 3800, 80, 'kg', true, '7804567890150', 5, '2026-02-20'),
('Porotos Negros', 'Porotos negros chilenos', 3500, 90, 'kg', true, '7804567890151', 5, '2026-04-10'),
('Arvejas Partidas', 'Arvejas amarillas partidas', 2900, 100, 'kg', true, '7804567890152', 5, '2026-01-30'),

-- Especias y Condimentos (categoria_id = 6)
('Pimentón Dulce', 'Pimentón ahumado español', 15000, 10, 'kg', true, '7804567890153', 6, '2025-08-20'),
('Cúrcuma en Polvo', 'Cúrcuma orgánica molida', 18000, 8, 'kg', true, '7804567890154', 6, '2025-09-10'),
('Canela en Polvo', 'Canela de Ceilán molida', 22000, 6, 'kg', true, '7804567890155', 6, '2025-07-15'),
('Jengibre Deshidratado', 'Jengibre en cubitos secos', 14000, 12, 'kg', true, '7804567890156', 6, '2025-10-05'),

-- Snacks Saludables (categoria_id = 7)
('Mix Estudiante', 'Mezcla de frutos secos y pasas', 7800, 35, 'kg', true, '7804567890157', 7, '2025-08-30'),
('Trail Mix Premium', 'Mix de nueces, almendras y arándanos', 11500, 20, 'kg', true, '7804567890158', 7, '2025-09-20'),
('Chips de Manzana', 'Láminas de manzana deshidratada', 6800, 40, 'kg', true, '7804567890159', 7, '2025-11-05'),

-- Aceites y Vinagres (categoria_id = 8)
('Aceite de Coco Virgen', 'Aceite de coco orgánico prensado en frío', 12000, 25, 'lt', true, '7804567890160', 8, '2026-05-15'),
('Aceite de Palta', 'Aceite de palta chilena extra virgen', 18000, 15, 'lt', true, '7804567890161', 8, '2026-03-20'),
('Vinagre de Manzana', 'Vinagre orgánico con madre', 8500, 30, 'lt', true, '7804567890162', 8, '2026-06-10'),

-- Superalimentos (categoria_id = 9)
('Spirulina en Polvo', 'Alga spirulina deshidratada', 28000, 5, 'kg', true, '7804567890163', 9, '2025-12-20'),
('Polvo de Açaí', 'Açaí liofilizado sin azúcar', 32000, 3, 'kg', true, '7804567890164', 9, '2025-10-25'),
('Cacao Crudo', 'Nibs de cacao sin tostar', 15000, 12, 'kg', true, '7804567890165', 9, '2025-11-30'),
('Goji Berries', 'Bayas de goji deshidratadas', 24000, 8, 'kg', true, '7804567890166', 9, '2025-09-05'),

-- Mix y Combinados (categoria_id = 10)
('Mix Tropical', 'Frutas tropicales deshidratadas', 9500, 28, 'kg', true, '7804567890167', 10, '2025-08-18'),
('Mix Energético', 'Frutos secos y semillas para deportistas', 13500, 18, 'kg', true, '7804567890168', 10, '2025-09-12'),
('Mix Antioxidante', 'Berries y frutos ricos en antioxidantes', 16500, 12, 'kg', true, '7804567890169', 10, '2025-07-22'),
('Mix Proteico', 'Legumbres y frutos secos altos en proteína', 8800, 25, 'kg', true, '7804567890170', 10, '2025-10-08');

-- ==============================================
-- VERIFICACIÓN
-- ==============================================

-- Consultas para verificar la inserción
-- SELECT COUNT(*) as total_categorias FROM categorias;
-- SELECT COUNT(*) as total_productos FROM productos;
-- SELECT c.nombre as categoria, COUNT(p.id_producto) as cantidad_productos 
-- FROM categorias c 
-- LEFT JOIN productos p ON c.id_categoria = p.categoria_id 
-- GROUP BY c.id_categoria, c.nombre 
-- ORDER BY c.id_categoria;