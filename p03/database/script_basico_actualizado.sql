ROLLBACK;

DROP TABLE IF EXISTS lotes;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS categorias;

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

INSERT INTO categorias (nombre) VALUES
('Frutos secos'),
('Maní y mix salados'),
('Mix Frutos Secos Natural'),
('Chocolates y bañados en chocolate');

INSERT INTO productos (nombre, descripcion, imagen, precio, stock, unidad, estado, codigo, categoria_id) VALUES
-- ALMENDRAS ENTERAS
('ALMENDRAS ENTERAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Almendras%20Org%C3%A1nica.png', 1500, 0, '100gr', true, 'ALME-100', 1),
('ALMENDRAS ENTERAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Almendras%20Org%C3%A1nica.png', 3500, 0, '250gr', true, 'ALME-250', 1),
('ALMENDRAS ENTERAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Almendras%20Org%C3%A1nica.png', 6500, 0, '500gr', true, 'ALME-500', 1),
('ALMENDRAS ENTERAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Almendras%20Org%C3%A1nica.png', 12700, 0, '1000gr', true, 'ALME-1000', 1),

-- NUEZ MARIPOSA ORGÁNICAS
('NUEZ MARIPOSA ORGÁNICAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Nuez%20Mariposa%20Organica.png', 1300, 0, '100gr', true, 'NUEZ-100', 1),
('NUEZ MARIPOSA ORGÁNICAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Nuez%20Mariposa%20Organica.png', 3000, 0, '250gr', true, 'NUEZ-250', 1),
('NUEZ MARIPOSA ORGÁNICAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Nuez%20Mariposa%20Organica.png', 5700, 0, '500gr', true, 'NUEZ-500', 1),
('NUEZ MARIPOSA ORGÁNICAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Nuez%20Mariposa%20Organica.png', 10900, 0, '1000gr', true, 'NUEZ-1000', 1),

-- MANÍ JAPONÉS PETTIZ PIMIENTA ROJA
('MANI JAPONES PETTIZ PIMIENTA ROJA', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20japon%C3%A9s%20PETTIZ%20%20Pimienta%20Roja.png', 1800, 0, '250gr', true, 'MANI-PR-250', 1),
('MANI JAPONES PETTIZ PIMIENTA ROJA', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20japon%C3%A9s%20PETTIZ%20%20Pimienta%20Roja.png', 3300, 0, '500gr', true, 'MANI-PR-500', 1),
('MANI JAPONES PETTIZ PIMIENTA ROJA', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20japon%C3%A9s%20PETTIZ%20%20Pimienta%20Roja.png', 6300, 0, '1000gr', true, 'MANI-PR-1000', 1),

-- PISTACHOS C/S SAL
('PISTACHOS C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pistacho%20Natural%20Salado.png', 2200, 0, '100gr', true, 'PIST-100', 1),
('PISTACHOS C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pistacho%20Natural%20Salado.png', 4900, 0, '250gr', true, 'PIST-250', 1),
('PISTACHOS C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pistacho%20Natural%20Salado.png', 9500, 0, '500gr', true, 'PIST-500', 1),
('PISTACHOS C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pistacho%20Natural%20Salado.png', 18990, 0, '1000gr', true, 'PIST-1000', 1),

-- CASTAÑAS DE CAJÚ C/S SAL
('CASTAÑAS DE CAJÚ C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Casta%C3%B1as%20de%20Caj%C3%BA%20Natural%20o%20Saladas.png', 2300, 0, '100gr', true, 'CAST-100', 1),
('CASTAÑAS DE CAJÚ C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Casta%C3%B1as%20de%20Caj%C3%BA%20Natural%20o%20Saladas.png', 5500, 0, '250gr', true, 'CAST-250', 1),
('CASTAÑAS DE CAJÚ C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Casta%C3%B1as%20de%20Caj%C3%BA%20Natural%20o%20Saladas.png', 10500, 0, '500gr', true, 'CAST-500', 1),
('CASTAÑAS DE CAJÚ C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Casta%C3%B1as%20de%20Caj%C3%BA%20Natural%20o%20Saladas.png', 20900, 0, '1000gr', true, 'CAST-1000', 1),

-- BANANA CHIPS
('BANANA CHIPS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Banana%20Chips%20Deshidratada.png', 2000, 0, '250gr', true, 'BANA-250', 1),
('BANANA CHIPS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Banana%20Chips%20Deshidratada.png', 3800, 0, '500gr', true, 'BANA-500', 1),
('BANANA CHIPS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Banana%20Chips%20Deshidratada.png', 7500, 0, '1000gr', true, 'BANA-1000', 1),

-- MANGO LONJAS
('MANGO LONJAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mango%20Lonja%20Deshidratado.png', 2500, 0, '250gr', true, 'MANG-250', 1),
('MANGO LONJAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mango%20Lonja%20Deshidratado.png', 4800, 0, '500gr', true, 'MANG-500', 1),
('MANGO LONJAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mango%20Lonja%20Deshidratado.png', 9200, 0, '1000gr', true, 'MANG-1000', 1),

-- MANÍ JAPONÉS PETTIZ CIBOULETTE - PEREJIL
('MANI JAPONES PETTIZ CIBOULETTE - PEREJIL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Perejil%20Ciboulette.png', 1800, 0, '250gr', true, 'MANI-CP-250', 2),
('MANI JAPONES PETTIZ CIBOULETTE - PEREJIL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Perejil%20Ciboulette.png', 3300, 0, '500gr', true, 'MANI-CP-500', 2),
('MANI JAPONES PETTIZ CIBOULETTE - PEREJIL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Perejil%20Ciboulette.png', 6300, 0, '1000gr', true, 'MANI-CP-1000', 2),

-- MIX PREMIUM CON SAL
('MIX PREMIUM CON SAL (MANÍ SALADO - ALMENDRAS - CASTAÑAS DE CAJÚ Y PISTACHOS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mani%20Tostado%20Salado.png', 3000, 0, '250gr', true, 'MIX-PREM-250', 2),
('MIX PREMIUM CON SAL (MANÍ SALADO - ALMENDRAS - CASTAÑAS DE CAJÚ Y PISTACHOS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mani%20Tostado%20Salado.png', 5900, 0, '500gr', true, 'MIX-PREM-500', 2),
('MIX PREMIUM CON SAL (MANÍ SALADO - ALMENDRAS - CASTAÑAS DE CAJÚ Y PISTACHOS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mani%20Tostado%20Salado.png', 11500, 0, '1000gr', true, 'MIX-PREM-1000', 2),

-- MANÍ JAPONÉS PETTIZ MIX SABORES
('MANI JAPONES PETTIZ MIX SABORES', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Mix.png', 1800, 0, '250gr', true, 'MANI-MIX-250', 2),
('MANI JAPONES PETTIZ MIX SABORES', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Mix.png', 3300, 0, '500gr', true, 'MANI-MIX-500', 2),
('MANI JAPONES PETTIZ MIX SABORES', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Mix.png', 6300, 0, '1000gr', true, 'MANI-MIX-1000', 2),

-- MIX ACONCAGUA
('MIX ACONCAGUA ( MANÍ- COCO CHIPS- ALMENDRAS ZAPALLO - CRANBERRY )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Aconcagua.png', 2000, 0, '250gr', true, 'MIX-ACON-250', 3),
('MIX ACONCAGUA ( MANÍ- COCO CHIPS- ALMENDRAS ZAPALLO - CRANBERRY )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Aconcagua.png', 3900, 0, '500gr', true, 'MIX-ACON-500', 3),
('MIX ACONCAGUA ( MANÍ- COCO CHIPS- ALMENDRAS ZAPALLO - CRANBERRY )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Aconcagua.png', 7500, 0, '1000gr', true, 'MIX-ACON-1000', 3),

-- POMELO VERDE
('POMELO VERDE', '', 'https://storage.googleapis.com/msm-imagenes-productos/POMELO%20VERDE.png', 2500, 0, '250gr', true, 'POME-250', 1),
('POMELO VERDE', '', 'https://storage.googleapis.com/msm-imagenes-productos/POMELO%20VERDE.png', 4800, 0, '500gr', true, 'POME-500', 1),
('POMELO VERDE', '', 'https://storage.googleapis.com/msm-imagenes-productos/POMELO%20VERDE.png', 9200, 0, '1000gr', true, 'POME-1000', 1),

-- PIÑA CUBOS
('PIÑA CUBOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pi%C3%B1a%20Cubo%20con%20Az%C3%BAcar.png', 2500, 0, '250gr', true, 'PINA-250', 1),
('PIÑA CUBOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pi%C3%B1a%20Cubo%20con%20Az%C3%BAcar.png', 4700, 0, '500gr', true, 'PINA-500', 1),
('PIÑA CUBOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pi%C3%B1a%20Cubo%20con%20Az%C3%BAcar.png', 8900, 0, '1000gr', true, 'PINA-1000', 1),

-- COCO CUBOS CON AZÚCAR
('COCO CUBOS CON AZUCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Coco%20Cubo%20Con%20Az%C3%BAcar.png', 3500, 0, '250gr', true, 'COCO-250', 1),
('COCO CUBOS CON AZUCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Coco%20Cubo%20Con%20Az%C3%BAcar.png', 6500, 0, '500gr', true, 'COCO-500', 1),
('COCO CUBOS CON AZUCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Coco%20Cubo%20Con%20Az%C3%BAcar.png', 12500, 0, '1000gr', true, 'COCO-1000', 1),

-- MIX ANTIOXIDANTE
('MIX ANTIOXIDANTE ( MANI - GOJI - CRANBERRY ZAPALLO - MARAVILLA - ALMENDRAS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Antioxidante.png', 2200, 0, '250gr', true, 'MIX-ANTI-250', 3),
('MIX ANTIOXIDANTE ( MANI - GOJI - CRANBERRY ZAPALLO - MARAVILLA - ALMENDRAS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Antioxidante.png', 4000, 0, '500gr', true, 'MIX-ANTI-500', 3),
('MIX ANTIOXIDANTE ( MANI - GOJI - CRANBERRY ZAPALLO - MARAVILLA - ALMENDRAS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Antioxidante.png', 7800, 0, '1000gr', true, 'MIX-ANTI-1000', 3),

-- MIX TROPICAL
('MIX TROPICAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20tropical%201%20kilo.png', 3300, 0, '250gr', true, 'MIX-TROP-250', 3),
('MIX TROPICAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20tropical%201%20kilo.png', 6500, 0, '500gr', true, 'MIX-TROP-500', 3),
('MIX TROPICAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20tropical%201%20kilo.png', 2000, 0, '250gr', true, 'MIX-TROP2-250', 3),

-- MIX FRUTOS SECOS
('MIX FRUTOS SECOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 3700, 0, '500gr', true, 'MIX-FS-500', 3),
('MIX FRUTOS SECOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 7200, 0, '1000gr', true, 'MIX-FS-1000', 3),
('MIX FRUTOS SECOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 2000, 0, '250gr', true, 'MIX-FS-250', 3),

-- MIX FRUTOS SECOS CON SAL
('MIX FRUTOS SECOS CON SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 7200, 0, '1000gr', true, 'MIX-FSSAL-1000', 3),
('MIX FRUTOS SECOS CON SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 3700, 0, '500gr', true, 'MIX-FSSAL-500', 3),
('MIX FRUTOS SECOS CON SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 2000, 0, '250gr', true, 'MIX-FSSAL-250', 3),

-- CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR
('CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056_%20Cacao%20SIN%20AZ%C3%9ACAR.png', 3000, 0, '100gr', true, 'CHOCO-56SA-100', 4),
('CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056_%20Cacao%20SIN%20AZ%C3%9ACAR.png', 7300, 0, '250gr', true, 'CHOCO-56SA-250', 4),
('CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056_%20Cacao%20SIN%20AZ%C3%9ACAR.png', 14500, 0, '500gr', true, 'CHOCO-56SA-500', 4),
('CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056_%20Cacao%20SIN%20AZ%C3%9ACAR.png', 28500, 0, '1000gr', true, 'CHOCO-56SA-1000', 4),

-- CHIPS CHOCOLATE 56% CACAO
('CHIPS CHOCOLATE 56 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056%20_%20Cacao%20Chocono.png', 2700, 0, '100gr', true, 'CHOCO-56-100', 4),
('CHIPS CHOCOLATE 56 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056%20_%20Cacao%20Chocono.png', 6500, 0, '250gr', true, 'CHOCO-56-250', 4),
('CHIPS CHOCOLATE 56 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056%20_%20Cacao%20Chocono.png', 12900, 0, '500gr', true, 'CHOCO-56-500', 4),
('CHIPS CHOCOLATE 56 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056%20_%20Cacao%20Chocono.png', 25000, 0, '1000gr', true, 'CHOCO-56-1000', 4),

-- CHIPS CHOCOLATE 63% CACAO
('CHIPS CHOCOLATE 63 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2063_%20Cacao%20Chocono.png', 2800, 0, '100gr', true, 'CHOCO-63-100', 4),
('CHIPS CHOCOLATE 63 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2063_%20Cacao%20Chocono.png', 6900, 0, '250gr', true, 'CHOCO-63-250', 4),
('CHIPS CHOCOLATE 63 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2063_%20Cacao%20Chocono.png', 13500, 0, '500gr', true, 'CHOCO-63-500', 4),
('CHIPS CHOCOLATE 63 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2063_%20Cacao%20Chocono.png', 26500, 0, '1000gr', true, 'CHOCO-63-1000', 4),

-- CHIPS CHOCOLATE 72% CACAO
('CHIPS CHOCOLATE 72 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20chocolate%2072_%20Cacao%20Chocono.png', 3000, 0, '100gr', true, 'CHOCO-72-100', 4),
('CHIPS CHOCOLATE 72 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20chocolate%2072_%20Cacao%20Chocono.png', 7300, 0, '250gr', true, 'CHOCO-72-250', 4),
('CHIPS CHOCOLATE 72 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20chocolate%2072_%20Cacao%20Chocono.png', 14500, 0, '500gr', true, 'CHOCO-72-500', 4),
('CHIPS CHOCOLATE 72 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20chocolate%2072_%20Cacao%20Chocono.png', 27900, 0, '1000gr', true, 'CHOCO-72-1000', 4),

-- DÁTILES SIN CAROZO
('DATILES SIN CAROZO', '', 'https://storage.googleapis.com/msm-imagenes-productos/D%C3%A1tiles%20Sin%20Carozo.png', 1500, 0, '250gr', true, 'DATE-250', 1),
('DATILES SIN CAROZO', '', 'https://storage.googleapis.com/msm-imagenes-productos/D%C3%A1tiles%20Sin%20Carozo.png', 2600, 0, '500gr', true, 'DATE-500', 1),
('DATILES SIN CAROZO', '', 'https://storage.googleapis.com/msm-imagenes-productos/D%C3%A1tiles%20Sin%20Carozo.png', 4900, 0, '1000gr', true, 'DATE-1000', 1)
ON CONFLICT (codigo) DO NOTHING;
