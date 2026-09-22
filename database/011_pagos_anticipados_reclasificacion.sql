USE sistema_contable;

-- 011: Pagos por anticipado y reclasificacion del ejercicio actual
-- No borra asientos: solo cambia la cuenta contable de las lineas ya registradas.

ALTER TABLE operaciones MODIFY tipo_operacion ENUM(
 'COMPRA','VENTA','DEVOLUCION_COMPRA','DEVOLUCION_VENTA',
 'COBRO_CLIENTE','PAGO_PROVEEDOR','GASTO_ADMINISTRATIVO',
 'GASTO_VENTA','GASTO_FINANCIERO','GASTO_COMPRA','PAGO_ANTICIPADO','POLIZA_SEGURO',
 'COMPRA_ACTIVO','PRESTAMO','APORTE_CAPITAL','OTRO'
) NOT NULL;

INSERT INTO catalogo_cuentas
(codigo,nombre,tipo,clasificacion,naturaleza,rol_reporte,id_cuenta_padre,permite_movimiento,activo)
SELECT '1.1.05','Gastos Pagados por Anticipado','ACTIVO','CORRIENTE','DEUDORA','NINGUNO',NULL,0,1
WHERE NOT EXISTS (SELECT 1 FROM catalogo_cuentas WHERE codigo='1.1.05');

INSERT INTO catalogo_cuentas
(codigo,nombre,tipo,clasificacion,naturaleza,rol_reporte,id_cuenta_padre,permite_movimiento,activo)
SELECT '1.1.05.02','Alquileres Pagados por Anticipado','ACTIVO','CORRIENTE','DEUDORA','NINGUNO',p.id_cuenta,1,1
FROM catalogo_cuentas p WHERE p.codigo='1.1.05'
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), activo=1;

INSERT INTO catalogo_cuentas
(codigo,nombre,tipo,clasificacion,naturaleza,rol_reporte,id_cuenta_padre,permite_movimiento,activo)
SELECT '1.1.05.03','Papelería y Útiles Pagados por Anticipado','ACTIVO','CORRIENTE','DEUDORA','NINGUNO',p.id_cuenta,1,1
FROM catalogo_cuentas p WHERE p.codigo='1.1.05'
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), activo=1;

INSERT INTO catalogo_cuentas
(codigo,nombre,tipo,clasificacion,naturaleza,rol_reporte,id_cuenta_padre,permite_movimiento,activo)
SELECT '1.1.05.04','Servicios Pagados por Anticipado','ACTIVO','CORRIENTE','DEUDORA','NINGUNO',p.id_cuenta,1,1
FROM catalogo_cuentas p WHERE p.codigo='1.1.05'
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), activo=1;

-- Reclasifica SOLO los asientos identificados en tu ejercicio actual.
-- Asiento 3: Alquiler $1,600 -> Alquiler pagado por anticipado
UPDATE detalle_asientos da
JOIN asientos_contables ac ON ac.id_asiento=da.id_asiento
JOIN catalogo_cuentas origen ON origen.id_cuenta=da.id_cuenta
JOIN catalogo_cuentas destino ON destino.codigo='1.1.05.02'
SET da.id_cuenta=destino.id_cuenta,
    da.descripcion='Alquiler pagado por anticipado'
WHERE ac.estado='CONTABILIZADO'
  AND ac.numero_asiento=3
  AND origen.codigo='6.1.01.02'
  AND da.debe=1600.00;

-- Asiento 6: Papeleria $450 -> Papeleria pagada por anticipado
UPDATE detalle_asientos da
JOIN asientos_contables ac ON ac.id_asiento=da.id_asiento
JOIN catalogo_cuentas origen ON origen.id_cuenta=da.id_cuenta
JOIN catalogo_cuentas destino ON destino.codigo='1.1.05.03'
SET da.id_cuenta=destino.id_cuenta,
    da.descripcion='Papelería pagada por anticipado'
WHERE ac.estado='CONTABILIZADO'
  AND ac.numero_asiento=6
  AND origen.codigo='6.1.01.03'
  AND da.debe=450.00;

-- Verificacion esperada: 2050 reclasificados a Activo Corriente.
SELECT ac.numero_asiento, cc.codigo, cc.nombre, da.debe, da.haber
FROM detalle_asientos da
JOIN asientos_contables ac ON ac.id_asiento=da.id_asiento
JOIN catalogo_cuentas cc ON cc.id_cuenta=da.id_cuenta
WHERE ac.numero_asiento IN (3,6)
  AND cc.codigo IN ('1.1.05.02','1.1.05.03')
ORDER BY ac.numero_asiento;
