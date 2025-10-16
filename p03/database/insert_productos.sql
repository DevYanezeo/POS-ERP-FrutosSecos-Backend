-- Script para insertar productos masivamente
-- Este archivo contiene todos los productos del catálogo

INSERT INTO productos (nombre, descripcion, imagen, precio, stock, unidad, estado, categoria_id, codigo, peso)
VALUES 
-- ALMENDRAS ENTERAS
('ALMENDRAS ENTERAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Almendras%20Org%C3%A1nica.png', 1500, 0, '100gr', true, 1, 'ALME-100', 100),
('ALMENDRAS ENTERAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Almendras%20Org%C3%A1nica.png', 3500, 0, '250gr', true, 1, 'ALME-250', 250),
('ALMENDRAS ENTERAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Almendras%20Org%C3%A1nica.png', 6500, 0, '500gr', true, 1, 'ALME-500', 500),
('ALMENDRAS ENTERAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Almendras%20Org%C3%A1nica.png', 12700, 0, '1000gr', true, 1, 'ALME-1000', 1000),

-- NUEZ MARIPOSA ORGÁNICAS
('NUEZ MARIPOSA ORGÁNICAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Nuez%20Mariposa%20Organica.png', 1300, 0, '100gr', true, 1, 'NUEZ-100', 100),
('NUEZ MARIPOSA ORGÁNICAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Nuez%20Mariposa%20Organica.png', 3000, 0, '250gr', true, 1, 'NUEZ-250', 250),
('NUEZ MARIPOSA ORGÁNICAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Nuez%20Mariposa%20Organica.png', 5700, 0, '500gr', true, 1, 'NUEZ-500', 500),
('NUEZ MARIPOSA ORGÁNICAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Nuez%20Mariposa%20Organica.png', 10900, 0, '1000gr', true, 1, 'NUEZ-1000', 1000),

-- MANÍ JAPONÉS PETTIZ PIMIENTA ROJA
('MANI JAPONES PETTIZ PIMIENTA ROJA', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20japon%C3%A9s%20PETTIZ%20%20Pimienta%20Roja.png', 1800, 0, '250gr', true, 1, 'MANI-PR-250', 250),
('MANI JAPONES PETTIZ PIMIENTA ROJA', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20japon%C3%A9s%20PETTIZ%20%20Pimienta%20Roja.png', 3300, 0, '500gr', true, 1, 'MANI-PR-500', 500),
('MANI JAPONES PETTIZ PIMIENTA ROJA', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20japon%C3%A9s%20PETTIZ%20%20Pimienta%20Roja.png', 6300, 0, '1000gr', true, 1, 'MANI-PR-1000', 1000),

-- PISTACHOS C/S SAL
('PISTACHOS C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pistacho%20Natural%20Salado.png', 2200, 0, '100gr', true, 1, 'PIST-100', 100),
('PISTACHOS C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pistacho%20Natural%20Salado.png', 4900, 0, '250gr', true, 1, 'PIST-250', 250),
('PISTACHOS C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pistacho%20Natural%20Salado.png', 9500, 0, '500gr', true, 1, 'PIST-500', 500),
('PISTACHOS C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pistacho%20Natural%20Salado.png', 18990, 0, '1000gr', true, 1, 'PIST-1000', 1000),

-- CASTAÑAS DE CAJÚ C/S SAL
('CASTAÑAS DE CAJÚ C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Casta%C3%B1as%20de%20Caj%C3%BA%20Natural%20o%20Saladas.png', 2300, 0, '100gr', true, 1, 'CAST-100', 100),
('CASTAÑAS DE CAJÚ C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Casta%C3%B1as%20de%20Caj%C3%BA%20Natural%20o%20Saladas.png', 5500, 0, '250gr', true, 1, 'CAST-250', 250),
('CASTAÑAS DE CAJÚ C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Casta%C3%B1as%20de%20Caj%C3%BA%20Natural%20o%20Saladas.png', 10500, 0, '500gr', true, 1, 'CAST-500', 500),
('CASTAÑAS DE CAJÚ C/S SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Casta%C3%B1as%20de%20Caj%C3%BA%20Natural%20o%20Saladas.png', 20900, 0, '1000gr', true, 1, 'CAST-1000', 1000),

-- BANANA CHIPS
('BANANA CHIPS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Banana%20Chips%20Deshidratada.png', 2000, 0, '250gr', true, 1, 'BANA-250', 250),
('BANANA CHIPS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Banana%20Chips%20Deshidratada.png', 3800, 0, '500gr', true, 1, 'BANA-500', 500),
('BANANA CHIPS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Banana%20Chips%20Deshidratada.png', 7500, 0, '1000gr', true, 1, 'BANA-1000', 1000),

-- MANGO LONJAS
('MANGO LONJAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mango%20Lonja%20Deshidratado.png', 2500, 0, '250gr', true, 1, 'MANG-250', 250),
('MANGO LONJAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mango%20Lonja%20Deshidratado.png', 4800, 0, '500gr', true, 1, 'MANG-500', 500),
('MANGO LONJAS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mango%20Lonja%20Deshidratado.png', 9200, 0, '1000gr', true, 1, 'MANG-1000', 1000),

-- MANÍ JAPONÉS PETTIZ CIBOULETTE - PEREJIL
('MANI JAPONES PETTIZ CIBOULETTE - PEREJIL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Perejil%20Ciboulette.png', 1800, 0, '250gr', true, 2, 'MANI-CP-250', 250),
('MANI JAPONES PETTIZ CIBOULETTE - PEREJIL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Perejil%20Ciboulette.png', 3300, 0, '500gr', true, 2, 'MANI-CP-500', 500),
('MANI JAPONES PETTIZ CIBOULETTE - PEREJIL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Perejil%20Ciboulette.png', 6300, 0, '1000gr', true, 2, 'MANI-CP-1000', 1000),

-- MIX PREMIUM CON SAL
('MIX PREMIUM CON SAL (MANÍ SALADO - ALMENDRAS - CASTAÑAS DE CAJÚ Y PISTACHOS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mani%20Tostado%20Salado.png', 3000, 0, '250gr', true, 2, 'MIX-PREM-250', 250),
('MIX PREMIUM CON SAL (MANÍ SALADO - ALMENDRAS - CASTAÑAS DE CAJÚ Y PISTACHOS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mani%20Tostado%20Salado.png', 5900, 0, '500gr', true, 2, 'MIX-PREM-500', 500),
('MIX PREMIUM CON SAL (MANÍ SALADO - ALMENDRAS - CASTAÑAS DE CAJÚ Y PISTACHOS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mani%20Tostado%20Salado.png', 11500, 0, '1000gr', true, 2, 'MIX-PREM-1000', 1000),

-- MANÍ JAPONÉS PETTIZ MIX SABORES
('MANI JAPONES PETTIZ MIX SABORES', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Mix.png', 1800, 0, '250gr', true, 2, 'MANI-MIX-250', 250),
('MANI JAPONES PETTIZ MIX SABORES', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Mix.png', 3300, 0, '500gr', true, 2, 'MANI-MIX-500', 500),
('MANI JAPONES PETTIZ MIX SABORES', '', 'https://storage.googleapis.com/msm-imagenes-productos/Man%C3%AD%20Japon%C3%A9s%20Mix.png', 6300, 0, '1000gr', true, 2, 'MANI-MIX-1000', 1000),

-- MIX ACONCAGUA
('MIX ACONCAGUA ( MANÍ- COCO CHIPS- ALMENDRAS ZAPALLO - CRANBERRY )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Aconcagua.png', 2000, 0, '250gr', true, 3, 'MIX-ACON-250', 250),
('MIX ACONCAGUA ( MANÍ- COCO CHIPS- ALMENDRAS ZAPALLO - CRANBERRY )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Aconcagua.png', 3900, 0, '500gr', true, 3, 'MIX-ACON-500', 500),
('MIX ACONCAGUA ( MANÍ- COCO CHIPS- ALMENDRAS ZAPALLO - CRANBERRY )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Aconcagua.png', 7500, 0, '1000gr', true, 3, 'MIX-ACON-1000', 1000),

-- POMELO VERDE
('POMELO VERDE', '', 'https://storage.googleapis.com/msm-imagenes-productos/POMELO%20VERDE.png', 2500, 0, '250gr', true, 1, 'POME-250', 250),
('POMELO VERDE', '', 'https://storage.googleapis.com/msm-imagenes-productos/POMELO%20VERDE.png', 4800, 0, '500gr', true, 1, 'POME-500', 500),
('POMELO VERDE', '', 'https://storage.googleapis.com/msm-imagenes-productos/POMELO%20VERDE.png', 9200, 0, '1000gr', true, 1, 'POME-1000', 1000),

-- PIÑA CUBOS
('PIÑA CUBOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pi%C3%B1a%20Cubo%20con%20Az%C3%BAcar.png', 2500, 0, '250gr', true, 1, 'PINA-250', 250),
('PIÑA CUBOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pi%C3%B1a%20Cubo%20con%20Az%C3%BAcar.png', 4700, 0, '500gr', true, 1, 'PINA-500', 500),
('PIÑA CUBOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Pi%C3%B1a%20Cubo%20con%20Az%C3%BAcar.png', 8900, 0, '1000gr', true, 1, 'PINA-1000', 1000),

-- COCO CUBOS CON AZÚCAR
('COCO CUBOS CON AZUCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Coco%20Cubo%20Con%20Az%C3%BAcar.png', 3500, 0, '250gr', true, 1, 'COCO-250', 250),
('COCO CUBOS CON AZUCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Coco%20Cubo%20Con%20Az%C3%BAcar.png', 6500, 0, '500gr', true, 1, 'COCO-500', 500),
('COCO CUBOS CON AZUCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Coco%20Cubo%20Con%20Az%C3%BAcar.png', 12500, 0, '1000gr', true, 1, 'COCO-1000', 1000),

-- MIX ANTIOXIDANTE
('MIX ANTIOXIDANTE ( MANI - GOJI - CRANBERRY ZAPALLO - MARAVILLA - ALMENDRAS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Antioxidante.png', 2200, 0, '250gr', true, 3, 'MIX-ANTI-250', 250),
('MIX ANTIOXIDANTE ( MANI - GOJI - CRANBERRY ZAPALLO - MARAVILLA - ALMENDRAS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Antioxidante.png', 4000, 0, '500gr', true, 3, 'MIX-ANTI-500', 500),
('MIX ANTIOXIDANTE ( MANI - GOJI - CRANBERRY ZAPALLO - MARAVILLA - ALMENDRAS )', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Antioxidante.png', 7800, 0, '1000gr', true, 3, 'MIX-ANTI-1000', 1000),

-- MIX TROPICAL
('MIX TROPICAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20tropical%201%20kilo.png', 3300, 0, '250gr', true, 3, 'MIX-TROP-250', 250),
('MIX TROPICAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20tropical%201%20kilo.png', 6500, 0, '500gr', true, 3, 'MIX-TROP-500', 500),
('MIX TROPICAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20tropical%201%20kilo.png', 2000, 0, '250gr', true, 3, 'MIX-TROP2-250', 250),

-- MIX FRUTOS SECOS
('MIX FRUTOS SECOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 3700, 0, '500gr', true, 3, 'MIX-FS-500', 500),
('MIX FRUTOS SECOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 7200, 0, '1000gr', true, 3, 'MIX-FS-1000', 1000),
('MIX FRUTOS SECOS', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 2000, 0, '250gr', true, 3, 'MIX-FS-250', 250),

-- MIX FRUTOS SECOS CON SAL
('MIX FRUTOS SECOS CON SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 7200, 0, '1000gr', true, 3, 'MIX-FSSAL-1000', 1000),
('MIX FRUTOS SECOS CON SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 3700, 0, '500gr', true, 3, 'MIX-FSSAL-500', 500),
('MIX FRUTOS SECOS CON SAL', '', 'https://storage.googleapis.com/msm-imagenes-productos/Mix%20Frutos%20Secos%20Natural.png', 2000, 0, '250gr', true, 3, 'MIX-FSSAL-250', 250),

-- CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR
('CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056_%20Cacao%20SIN%20AZ%C3%9ACAR.png', 3000, 0, '100gr', true, 4, 'CHOCO-56SA-100', 100),
('CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056_%20Cacao%20SIN%20AZ%C3%9ACAR.png', 7300, 0, '250gr', true, 4, 'CHOCO-56SA-250', 250),
('CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056_%20Cacao%20SIN%20AZ%C3%9ACAR.png', 14500, 0, '500gr', true, 4, 'CHOCO-56SA-500', 500),
('CHIPS CHOCOLATE 56% CACAO SIN AZÚCAR', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056_%20Cacao%20SIN%20AZ%C3%9ACAR.png', 28500, 0, '1000gr', true, 4, 'CHOCO-56SA-1000', 1000),

-- CHIPS CHOCOLATE 56% CACAO
('CHIPS CHOCOLATE 56 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056%20_%20Cacao%20Chocono.png', 2700, 0, '100gr', true, 4, 'CHOCO-56-100', 100),
('CHIPS CHOCOLATE 56 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056%20_%20Cacao%20Chocono.png', 6500, 0, '250gr', true, 4, 'CHOCO-56-250', 250),
('CHIPS CHOCOLATE 56 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056%20_%20Cacao%20Chocono.png', 12900, 0, '500gr', true, 4, 'CHOCO-56-500', 500),
('CHIPS CHOCOLATE 56 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2056%20_%20Cacao%20Chocono.png', 25000, 0, '1000gr', true, 4, 'CHOCO-56-1000', 1000),

-- CHIPS CHOCOLATE 63% CACAO
('CHIPS CHOCOLATE 63 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2063_%20Cacao%20Chocono.png', 2800, 0, '100gr', true, 4, 'CHOCO-63-100', 100),
('CHIPS CHOCOLATE 63 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2063_%20Cacao%20Chocono.png', 6900, 0, '250gr', true, 4, 'CHOCO-63-250', 250),
('CHIPS CHOCOLATE 63 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2063_%20Cacao%20Chocono.png', 13500, 0, '500gr', true, 4, 'CHOCO-63-500', 500),
('CHIPS CHOCOLATE 63 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20Chocolate%2063_%20Cacao%20Chocono.png', 26500, 0, '1000gr', true, 4, 'CHOCO-63-1000', 1000),

-- CHIPS CHOCOLATE 72% CACAO
('CHIPS CHOCOLATE 72 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20chocolate%2072_%20Cacao%20Chocono.png', 3000, 0, '100gr', true, 4, 'CHOCO-72-100', 100),
('CHIPS CHOCOLATE 72 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20chocolate%2072_%20Cacao%20Chocono.png', 7300, 0, '250gr', true, 4, 'CHOCO-72-250', 250),
('CHIPS CHOCOLATE 72 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20chocolate%2072_%20Cacao%20Chocono.png', 14500, 0, '500gr', true, 4, 'CHOCO-72-500', 500),
('CHIPS CHOCOLATE 72 % CACAO', '', 'https://storage.googleapis.com/msm-imagenes-productos/Chips%20chocolate%2072_%20Cacao%20Chocono.png', 27900, 0, '1000gr', true, 4, 'CHOCO-72-1000', 1000),

-- DÁTILES SIN CAROZO
('DATILES SIN CAROZO', '', 'https://storage.googleapis.com/msm-imagenes-productos/D%C3%A1tiles%20Sin%20Carozo.png', 1500, 0, '250gr', true, 1, 'DATE-250', 250),
('DATILES SIN CAROZO', '', 'https://storage.googleapis.com/msm-imagenes-productos/D%C3%A1tiles%20Sin%20Carozo.png', 2600, 0, '500gr', true, 1, 'DATE-500', 500),
('DATILES SIN CAROZO', '', 'https://storage.googleapis.com/msm-imagenes-productos/D%C3%A1tiles%20Sin%20Carozo.png', 4900, 0, '1000gr', true, 1, 'DATE-1000', 1000)
ON CONFLICT (codigo) DO NOTHING;
