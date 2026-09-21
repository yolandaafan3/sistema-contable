USE sistema_contable;

-- ============================================================
-- MIGRACION 004 - MULTIPRODUCTO + LOTES PEPS + DEVOLUCIONES
-- Ejecutar UNA VEZ sobre una base existente.
-- No elimina asientos ni operaciones existentes.
-- ============================================================

ALTER TABLE productos
    ADD COLUMN IF NOT EXISTS costo_inicial DECIMAL(18,6) NOT NULL DEFAULT 0.000000 AFTER precio_venta,
    ADD COLUMN IF NOT EXISTS valor_inventario_inicial DECIMAL(18,2) NOT NULL DEFAULT 0.00 AFTER costo_inicial;

ALTER TABLE operaciones
    ADD COLUMN IF NOT EXISTS id_operacion_origen INT NULL AFTER id_producto;

-- Agrega la FK solo si no existe.
SET @fk_origen := (
    SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'operaciones'
      AND CONSTRAINT_NAME = 'fk_operacion_origen'
);
SET @sql_fk := IF(@fk_origen = 0,
    'ALTER TABLE operaciones ADD CONSTRAINT fk_operacion_origen FOREIGN KEY (id_operacion_origen) REFERENCES operaciones(id_operacion)',
    'SELECT 1');
PREPARE stmt FROM @sql_fk; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Sincroniza el nombre de la columna de existencia del Kardex con el código Java.
SET @tiene_existencia := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='kardex' AND COLUMN_NAME='existencia'
);
SET @tiene_unidades := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME='kardex' AND COLUMN_NAME='unidades_existencia'
);
SET @sql_kardex := IF(@tiene_existencia=1 AND @tiene_unidades=0,
    'ALTER TABLE kardex CHANGE COLUMN existencia unidades_existencia DECIMAL(18,6) NOT NULL DEFAULT 0.000000',
    'SELECT 1');
PREPARE stmt FROM @sql_kardex; EXECUTE stmt; DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS detalle_consumo_peps (
    id_consumo BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_operacion_salida INT NOT NULL,
    id_capa INT NOT NULL,
    cantidad DECIMAL(18,6) NOT NULL,
    costo_unitario DECIMAL(18,6) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_consumo_producto FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    CONSTRAINT fk_consumo_operacion FOREIGN KEY (id_operacion_salida) REFERENCES operaciones(id_operacion),
    CONSTRAINT fk_consumo_capa FOREIGN KEY (id_capa) REFERENCES capas_peps(id_capa)
) ENGINE=InnoDB;

SET @idx1 := (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='operaciones' AND INDEX_NAME='idx_operacion_origen');
SET @sql_idx1 := IF(@idx1=0,'CREATE INDEX idx_operacion_origen ON operaciones(id_operacion_origen)','SELECT 1');
PREPARE stmt FROM @sql_idx1; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx2 := (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='detalle_consumo_peps' AND INDEX_NAME='idx_consumo_operacion');
SET @sql_idx2 := IF(@idx2=0,'CREATE INDEX idx_consumo_operacion ON detalle_consumo_peps(id_operacion_salida)','SELECT 1');
PREPARE stmt FROM @sql_idx2; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- Compatibilidad con el proyecto anterior:
-- si existe UN solo producto activo y ya había inventario inicial contable,
-- conserva ese valor y calcula su costo unitario. Con varios productos no se
-- reparte automáticamente porque sería ambiguo: se configura cada producto desde la interfaz.
SET @productos_activos := (SELECT COUNT(*) FROM productos WHERE activo=TRUE);
SET @valor_apertura := (
    SELECT COALESCE(SUM(da.debe),0)
    FROM detalle_asientos da
    JOIN asientos_contables a ON a.id_asiento=da.id_asiento
    JOIN catalogo_cuentas cc ON cc.id_cuenta=da.id_cuenta
    WHERE cc.rol_reporte='INVENTARIO' AND a.estado='CONTABILIZADO'
);
UPDATE productos
SET valor_inventario_inicial = CASE
        WHEN @productos_activos=1 AND existencia_inicial>0 AND @valor_apertura>0 THEN @valor_apertura
        ELSE valor_inventario_inicial END,
    costo_inicial = CASE
        WHEN @productos_activos=1 AND existencia_inicial>0 AND @valor_apertura>0 THEN ROUND(@valor_apertura/existencia_inicial,6)
        WHEN costo_inicial=0 AND existencia_inicial>0 THEN costo_compra
        ELSE costo_inicial END;
