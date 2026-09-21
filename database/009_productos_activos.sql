USE sistema_contable;

-- ============================================================
-- MIGRACION 009 - ACTIVAR / DESACTIVAR PRODUCTOS
-- Seguro para ejecutar más de una vez en MySQL 8+.
-- No borra productos ni movimientos históricos.
-- ============================================================

ALTER TABLE productos
    ADD COLUMN IF NOT EXISTS activo BOOLEAN NOT NULL DEFAULT TRUE;

-- Cualquier instalación antigua queda con sus productos habilitados.
UPDATE productos
SET activo = TRUE
WHERE activo IS NULL;

-- Verificación.
SELECT
    id_producto,
    codigo,
    nombre,
    existencia_actual,
    activo
FROM productos
ORDER BY activo DESC, codigo, nombre;
