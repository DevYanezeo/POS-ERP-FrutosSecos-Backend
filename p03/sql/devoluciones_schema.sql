-- Create devoluciones table
CREATE TABLE IF NOT EXISTS devoluciones (
    id_devolucion INT AUTO_INCREMENT PRIMARY KEY,
    venta_id INT NOT NULL,
    fecha_devolucion DATETIME NOT NULL,
    motivo VARCHAR(500),
    monto_devuelto INT NOT NULL,
    usuario_id INT,
    tipo VARCHAR(20) NOT NULL, -- COMPLETA, PARCIAL
    FOREIGN KEY (venta_id) REFERENCES ventas(id_venta),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario),
    INDEX idx_venta (venta_id),
    INDEX idx_fecha (fecha_devolucion)
);

-- Create detalle_devoluciones table
CREATE TABLE IF NOT EXISTS detalle_devoluciones (
    id_detalle_devolucion INT AUTO_INCREMENT PRIMARY KEY,
    devolucion_id INT NOT NULL,
    detalle_venta_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad_devuelta INT NOT NULL,
    id_lote INT,
    monto_devuelto INT NOT NULL,
    FOREIGN KEY (devolucion_id) REFERENCES devoluciones(id_devolucion),
    FOREIGN KEY (detalle_venta_id) REFERENCES detalle_ventas(id_detalle_venta),
    FOREIGN KEY (producto_id) REFERENCES productos(id_producto),
    FOREIGN KEY (id_lote) REFERENCES lotes(id_lote),
    INDEX idx_devolucion (devolucion_id),
    INDEX idx_producto (producto_id)
);
