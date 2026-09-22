USE sistema_contable;

-- Soporte para conservar las capas PEPS al cerrar un periodo y abrir el siguiente.
CREATE TABLE IF NOT EXISTS inventario_apertura_lotes (
    id_apertura_lote BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_periodo INT NOT NULL,
    id_producto INT NOT NULL,
    fecha_origen DATE NOT NULL,
    cantidad DECIMAL(18,6) NOT NULL,
    costo_unitario DECIMAL(18,6) NOT NULL,
    orden_lote INT NOT NULL,
    INDEX idx_apertura_lotes_periodo_producto(id_periodo,id_producto,orden_lote),
    CONSTRAINT fk_apertura_lotes_periodo
        FOREIGN KEY(id_periodo) REFERENCES periodos_contables(id_periodo) ON DELETE CASCADE,
    CONSTRAINT fk_apertura_lotes_producto
        FOREIGN KEY(id_producto) REFERENCES productos(id_producto)
) ENGINE=InnoDB;

-- La utilidad de periodos cerrados pasa a Resultados acumulados.
INSERT INTO catalogo_cuentas
(codigo,nombre,tipo,clasificacion,naturaleza,rol_reporte,id_cuenta_padre,permite_movimiento,activo)
SELECT '3.2.02','Resultados acumulados','PATRIMONIO','PATRIMONIO','ACREEDORA','NINGUNO',NULL,TRUE,TRUE
WHERE NOT EXISTS (SELECT 1 FROM catalogo_cuentas WHERE codigo='3.2.02');
