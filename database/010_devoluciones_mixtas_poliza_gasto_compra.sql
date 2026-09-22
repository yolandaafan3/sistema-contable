-- ContaProMax - ajuste 010
-- Ejecutar UNA sola vez sobre la base actual antes de probar
-- 1) habilita tipos de operación para Gasto sobre compra y Póliza de seguro
-- 2) asegura las cuentas necesarias si no existen

ALTER TABLE operaciones
MODIFY COLUMN tipo_operacion ENUM(
    'COMPRA','VENTA','DEVOLUCION_COMPRA','DEVOLUCION_VENTA',
    'COBRO_CLIENTE','PAGO_PROVEEDOR','GASTO_ADMINISTRATIVO',
    'GASTO_VENTA','GASTO_FINANCIERO','GASTO_COMPRA','POLIZA_SEGURO',
    'COMPRA_ACTIVO','PRESTAMO','APORTE_CAPITAL','OTRO'
) NOT NULL;

-- Gastos sobre Compras (normalmente ya existe)
INSERT INTO catalogo_cuentas
(codigo,nombre,tipo,clasificacion,naturaleza,rol_reporte,id_cuenta_padre,permite_movimiento,activo)
SELECT '5.1.03','Gastos sobre Compras','COSTO','COSTO','DEUDORA','GASTOS_COMPRA',NULL,1,1
WHERE NOT EXISTS (SELECT 1 FROM catalogo_cuentas WHERE codigo='5.1.03');

-- Grupo de pagos anticipados
INSERT INTO catalogo_cuentas
(codigo,nombre,tipo,clasificacion,naturaleza,rol_reporte,id_cuenta_padre,permite_movimiento,activo)
SELECT '1.1.05','Gastos Pagados por Anticipado','ACTIVO','CORRIENTE','DEUDORA','NINGUNO',NULL,0,1
WHERE NOT EXISTS (SELECT 1 FROM catalogo_cuentas WHERE codigo='1.1.05');

-- Póliza/seguro pagado por anticipado
INSERT INTO catalogo_cuentas
(codigo,nombre,tipo,clasificacion,naturaleza,rol_reporte,id_cuenta_padre,permite_movimiento,activo)
SELECT '1.1.05.01','Seguros Pagados por Anticipado','ACTIVO','CORRIENTE','DEUDORA','NINGUNO',p.id_cuenta,1,1
FROM catalogo_cuentas p
WHERE p.codigo='1.1.05'
  AND NOT EXISTS (SELECT 1 FROM catalogo_cuentas WHERE codigo='1.1.05.01');

SELECT codigo,nombre,tipo,clasificacion,rol_reporte,activo
FROM catalogo_cuentas
WHERE codigo IN ('5.1.03','1.1.05','1.1.05.01')
ORDER BY codigo;
