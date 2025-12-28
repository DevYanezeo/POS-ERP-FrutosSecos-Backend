-- Agrega columna costo a lotes (costo unitario del lote)
ALTER TABLE lotes
ADD COLUMN IF NOT EXISTS costo integer;

-- Agrega columnas en detalle_ventas para referenciar el lote usado y guardar costo unitario histórico
ALTER TABLE detalle_ventas
ADD COLUMN IF NOT EXISTS id_lote integer;

ALTER TABLE detalle_ventas
ADD COLUMN IF NOT EXISTS costo_unitario integer;

-- Opcional: agregar FK desde detalle_ventas.id_lote hacia lotes.id_lote
ALTER TABLE detalle_ventas
    ADD CONSTRAINT IF NOT EXISTS fk_detalle_ventas_lote
    FOREIGN KEY (id_lote) REFERENCES lotes(id_lote);

-- Nota: Para el uso de migraciones (flyway/liquibase) se deben integrar estas sentencias allí.

