-- SCRIPT DE INTEGRACIÓN DE UN 20% DE LA BD DE ERP FRUTOS SECOS MIL SABORES

CREATE TABLE categorias (nombre varchar, IdCategoria int);
CREATE TABLE productos (nombre varchar, categoria varchar);
-- ==============================================
-- TABLA: CATEGORIAS
-- ==============================================

INSERT INTO categorias (nombre, IdCategoria) VALUES
                                    ('Frutos Secos', 1),
                                    ('MIX FRUTOS SECOS NATURAL', 2),
                                    ('MANI CONFITADOS SABORES', 3),
                                    ('MANÍ Y MIX SALADOS', 4),
                                    ('CHOCOLATES Y BAÑADOS EN CHOCOLATE', 5),
                                    ('SEMILLAS - HARINAS Y/O OTROS', 6),
                                    ('TE INFUSIONES', 7),
                                    ('SUPERALIMENTOS BROTA', 8);

-- ==============================================
-- TABLA: PRODUCTOS
-- ==============================================
INSERT INTO productos (nombre, categoria) VALUES
                                              ('ALMENDRAS ENTERAS ORGÁNICAS', 'Frutos secos'),
                                              ('ALMENDRAS PARTIDAS', 'Frutos Secos'),
                                              ('ALMENDRAS LAMINADAS SIN PIEL', 'Frutos Secos'),
                                              ('NUEZ MARIPOSA ORGÁNICAS', 'Frutos Secos'),
                                              ('NUEZ PARTIDAS ORGÁNICAS', 'Frutos Secos'),
                                              ('AVELLANAS TOSTADAS CHILENAS', 'Frutos Secos'),
                                              ('AVELLANAS EUROPEAS', 'Frutos Secos'),
                                              ('MANI CON PASAS', 'MIX FRUTOS SECOS NATURAL'),
                                              ('MIX FRUTOS SECOS (MANÍ-PASAS-NUECES-ALMENDRAS)', 'MIX FRUTOS SECOS NATURAL'),
                                              ('MIX ACONCAGUA (MANÍ-COCO CHIPS-ALMENDRAS-ZAPALLO-CRANBERRY', 'MIX FRUTOS SECOS NATURAL'),
                                              ('MIX ANTIOXIDANTE (MANÍ-GOJI-CRANBERRY-ZAPALLO-MARAVILLA-ALMENDRAS)', 'MIX FRUTOS SECOS NATURAL'),
                                              ('MIX TROPICAL (CAJÚ-ALMENDRAS-PIÑA-COCO-CRANBERRY)', 'MIX FRUTOS SECOS NATURAL'),
                                              ('MANI CONFITADO SABOR NATURAL', 'MANI CONFITADO SABORES'),
                                              ('MANI CONFITADO SABOR ARÁNDANO', 'MANI CONFITADO SABORES'),
                                              ('MANI CONFITADO SABOR FRAMBUESA', 'MANI CONFITADO SABORES'),
                                              ('MANI CONFITADO SABOR FRUTILLA', 'MANI CONFITADO SABORES'),
                                              ('MANI CONFITADO SABOR LUCUMA', 'MANI CONFITADO SABORES'),
                                              ('MANI JAPONES PETTIZ TRADICIONAL','MANI Y MIX SALADOS'),
                                              ('MANI JAPONES PETTIZ PIMIENTA ROJA', 'MANI Y MIX SALADOS'),
                                              ('MANI JAPONES PETTIZ CIBOULETTE - PEREJIL', 'MANI Y MIX SALADOS'),
                                              ('MANI JAPONES PETTIZ MIX SABORES', 'MANI Y MIX SALADOS'),
                                              ('MANÍ CON CÁSCARA', 'MANI Y MIX SALADOS'),
                                              ('CHOLITOS BAÑADOS EN CHOCOLATE SUCEDÁNEO', 'CHOCOLATES Y BAÑADOS EN CHOCOLATE'),
                                              ('MANÍ BAÑADOS EN CHOCOLATE SUCEDÁNEO', 'CHOCOLATES Y BAÑADOS EN CHOCOLATE'),
                                              ('HUEVITOS DE ALMENDRAS CONFITADAS', 'CHOCOLATES Y BAÑADOS EN CHOCOLATE'),
                                              ('GOMITAS CHERRY SOUR', 'CHOCOLATES Y BAÑADOS EN CHOCOLATE'),
                                              ('CHUBI (LENTEJAS DE COLORES)', 'CHOCOLATES Y BAÑADOS EN CHOCOLATE'),
                                              ('SEMILLAS DE ZAPALLO', 'SEMILLAS - HARINAS Y/O OTROS'),
                                              ('SEMILLAS DE MARAVILLA', 'SEMILLAS - HARINAS Y/O OTROS'),
                                              ('SEMILLAS DE CHÍA', 'SEMILLAS - HARINAS Y/O OTROS'),
                                              ('SEMILLAS DE LINAZA', 'SEMILLAS - HARINAS Y/O OTROS'),
                                              ('SEMILLAS GOJI', 'SEMILLAS - HARINAS Y/O OTROS'),
                                              ('FLOR DE JAMÁICA HOJAS 100 Gr.', 'TE INFUSIONES'),
                                              ('TÉ VERDE HOJAS 100 Gr.', 'TE INFUSIONES'),
                                              ('TÉ VERDE INFUSIÓN 20 SOBRES', 'TE INFUSIONES'),
                                              ('TÉ MATCHA INFUSIÓN 25 SOBRES', 'TE INFUSIONES'),
                                              ('TE CHAI INFUSION 25 SOBRES', 'TE INFUSIONES'),
                                              ('CACAO EN POLVO ORGANICO BROTA 150 gr', 'SUPERALIMENTOS BROTA'),
                                              ('CURCUMA EN POLVO ORGANICO BROTA 230 gr', 'SUPERALIMENTOS BROTA'),
                                              ('JENGIBRE EN POLVO ORGANICO BROTA 150 gr', 'SUPERALIMENTOS BROTA'),
                                              ('MORINGA EN POLVO ORGANICO BROTA 100 gr', 'SUPERALIMENTOS BROTA'),
                                              ('CAMU CAMU EN POLVO ORGANICO BROTA 100 gr', 'SUPERALIMENTOS BROTA');