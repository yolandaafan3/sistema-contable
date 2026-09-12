-- ============================================================
-- SISTEMA CONTABLE - DATOS BASE Y CATALOGO DE CUENTAS
-- Ejecutar DESPUES de schema.sql
-- ============================================================

USE sistema_contable;

-- ------------------------------------------------------------
-- EMPRESA / CONFIGURACION / PERIODO / PRODUCTO DE PRUEBA
-- ------------------------------------------------------------

INSERT INTO empresa (id_empresa, nombre, nit, nrc, direccion, telefono, correo, activo)
VALUES (1, 'Empresa de Electrodomésticos', NULL, NULL, NULL, NULL, NULL, TRUE)
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    activo = TRUE;

INSERT INTO configuracion_contable
    (id_configuracion, id_empresa, porcentaje_iva, tipo_iva, moneda)
VALUES
    (1, 1, 13.00, 'INCLUIDO', 'USD')
ON DUPLICATE KEY UPDATE
    porcentaje_iva = VALUES(porcentaje_iva),
    tipo_iva = VALUES(tipo_iva),
    moneda = VALUES(moneda);

INSERT INTO periodos_contables
    (id_periodo, id_empresa, nombre, fecha_inicio, fecha_fin, estado)
VALUES
    (1, 1, 'Periodo Contable 2026', '2026-01-01', '2026-12-31', 'ABIERTO')
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    fecha_inicio = VALUES(fecha_inicio),
    fecha_fin = VALUES(fecha_fin),
    estado = VALUES(estado);

INSERT INTO productos
    (id_producto, codigo, nombre, descripcion, costo_compra, precio_venta,
     existencia_inicial, existencia_actual, activo)
VALUES
    (1, 'PROD-001', 'Electrodoméstico de Prueba',
     'Producto base utilizado para compras, ventas y Kardex PEPS',
     10.00, 20.00, 0.000000, 0.000000, TRUE)
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    descripcion = VALUES(descripcion),
    costo_compra = VALUES(costo_compra),
    precio_venta = VALUES(precio_venta),
    activo = TRUE;

-- ------------------------------------------------------------
-- CATALOGO DE CUENTAS
-- ------------------------------------------------------------
-- Primero se insertan las cuentas padre y luego sus auxiliares.

INSERT INTO catalogo_cuentas
(codigo, nombre, tipo, clasificacion, naturaleza, rol_reporte,
 id_cuenta_padre, permite_movimiento, activo)
VALUES
('1.1.01', 'Efectivo y Equivalentes', 'ACTIVO', 'CORRIENTE', 'DEUDORA', 'EFECTIVO', NULL, FALSE, TRUE),
('1.1.02', 'Cuentas por Cobrar', 'ACTIVO', 'CORRIENTE', 'DEUDORA', 'CUENTAS_COBRAR', NULL, FALSE, TRUE),
('1.1.03', 'Inventario', 'ACTIVO', 'CORRIENTE', 'DEUDORA', 'INVENTARIO', NULL, TRUE, TRUE),
('1.1.04', 'IVA Crédito Fiscal', 'ACTIVO', 'CORRIENTE', 'DEUDORA', 'IVA_CREDITO', NULL, TRUE, TRUE),
('1.2.01', 'Propiedad, Planta y Equipo', 'ACTIVO', 'NO_CORRIENTE', 'DEUDORA', 'PROPIEDAD_PLANTA_EQUIPO', NULL, FALSE, TRUE),
('2.1.01', 'Cuentas por Pagar', 'PASIVO', 'CORRIENTE', 'ACREEDORA', 'CUENTAS_PAGAR', NULL, FALSE, TRUE),
('2.1.02', 'IVA Débito Fiscal', 'PASIVO', 'CORRIENTE', 'ACREEDORA', 'IVA_DEBITO', NULL, TRUE, TRUE),
('2.2.01', 'Préstamo Bancario', 'PASIVO', 'NO_CORRIENTE', 'ACREEDORA', 'PRESTAMO_BANCARIO', NULL, TRUE, TRUE),
('3.1.01', 'Capital Social', 'PATRIMONIO', 'PATRIMONIO', 'ACREEDORA', 'CAPITAL_SOCIAL', NULL, TRUE, TRUE),
('3.2.01', 'Utilidad del Ejercicio', 'PATRIMONIO', 'PATRIMONIO', 'ACREEDORA', 'UTILIDAD_EJERCICIO', NULL, FALSE, TRUE),
('4.1.01', 'Ventas', 'INGRESO', 'INGRESO', 'ACREEDORA', 'VENTAS', NULL, TRUE, TRUE),
('4.1.02', 'Devolución sobre Ventas', 'INGRESO', 'INGRESO', 'DEUDORA', 'DEVOLUCION_VENTAS', NULL, TRUE, TRUE),
('4.1.03', 'Descuentos sobre Ventas', 'INGRESO', 'INGRESO', 'DEUDORA', 'NINGUNO', NULL, TRUE, TRUE),
('4.2.01', 'Otros Productos / Productos Financieros', 'INGRESO', 'INGRESO', 'ACREEDORA', 'NINGUNO', NULL, TRUE, TRUE),
('5.1.01', 'Compras', 'COSTO', 'COSTO', 'DEUDORA', 'COMPRAS', NULL, TRUE, TRUE),
('5.1.02', 'Devolución sobre Compras', 'COSTO', 'COSTO', 'ACREEDORA', 'DEVOLUCION_COMPRAS', NULL, TRUE, TRUE),
('5.1.03', 'Gastos sobre Compras', 'COSTO', 'COSTO', 'DEUDORA', 'GASTOS_COMPRA', NULL, TRUE, TRUE),
('5.1.04', 'Descuentos sobre Compras', 'COSTO', 'COSTO', 'ACREEDORA', 'NINGUNO', NULL, TRUE, TRUE),
('6.1.01', 'Gastos Administrativos', 'GASTO', 'ADMINISTRATIVO', 'DEUDORA', 'GASTOS_ADMIN', NULL, TRUE, TRUE),
('6.2.01', 'Gastos de Venta', 'GASTO', 'VENTA', 'DEUDORA', 'GASTOS_VENTA', NULL, TRUE, TRUE),
('6.3.01', 'Gastos Financieros', 'GASTO', 'FINANCIERO', 'DEUDORA', 'GASTOS_FINANCIEROS', NULL, TRUE, TRUE)
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    tipo = VALUES(tipo),
    clasificacion = VALUES(clasificacion),
    naturaleza = VALUES(naturaleza),
    rol_reporte = VALUES(rol_reporte),
    permite_movimiento = VALUES(permite_movimiento),
    activo = VALUES(activo);

-- Auxiliares de Efectivo y Equivalentes
INSERT INTO catalogo_cuentas
(codigo, nombre, tipo, clasificacion, naturaleza, rol_reporte,
 id_cuenta_padre, permite_movimiento, activo)
SELECT '1.1.01.01', 'Caja', 'ACTIVO', 'CORRIENTE', 'DEUDORA', 'NINGUNO',
       id_cuenta, TRUE, TRUE
FROM catalogo_cuentas WHERE codigo = '1.1.01'
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), id_cuenta_padre = VALUES(id_cuenta_padre), activo = TRUE;

INSERT INTO catalogo_cuentas
(codigo, nombre, tipo, clasificacion, naturaleza, rol_reporte,
 id_cuenta_padre, permite_movimiento, activo)
SELECT '1.1.01.02', 'Banco', 'ACTIVO', 'CORRIENTE', 'DEUDORA', 'NINGUNO',
       id_cuenta, TRUE, TRUE
FROM catalogo_cuentas WHERE codigo = '1.1.01'
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), id_cuenta_padre = VALUES(id_cuenta_padre), activo = TRUE;

-- Auxiliar de Cuentas por Cobrar
INSERT INTO catalogo_cuentas
(codigo, nombre, tipo, clasificacion, naturaleza, rol_reporte,
 id_cuenta_padre, permite_movimiento, activo)
SELECT '1.1.02.01', 'Clientes', 'ACTIVO', 'CORRIENTE', 'DEUDORA', 'NINGUNO',
       id_cuenta, TRUE, TRUE
FROM catalogo_cuentas WHERE codigo = '1.1.02'
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), id_cuenta_padre = VALUES(id_cuenta_padre), activo = TRUE;

-- Auxiliares de Propiedad, Planta y Equipo
INSERT INTO catalogo_cuentas
(codigo, nombre, tipo, clasificacion, naturaleza, rol_reporte,
 id_cuenta_padre, permite_movimiento, activo)
SELECT '1.2.01.01', 'Mobiliario de Oficina', 'ACTIVO', 'NO_CORRIENTE', 'DEUDORA', 'NINGUNO',
       id_cuenta, TRUE, TRUE
FROM catalogo_cuentas WHERE codigo = '1.2.01'
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), id_cuenta_padre = VALUES(id_cuenta_padre), activo = TRUE;

INSERT INTO catalogo_cuentas
(codigo, nombre, tipo, clasificacion, naturaleza, rol_reporte,
 id_cuenta_padre, permite_movimiento, activo)
SELECT '1.2.01.02', 'Equipo de Cómputo', 'ACTIVO', 'NO_CORRIENTE', 'DEUDORA', 'NINGUNO',
       id_cuenta, TRUE, TRUE
FROM catalogo_cuentas WHERE codigo = '1.2.01'
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), id_cuenta_padre = VALUES(id_cuenta_padre), activo = TRUE;

INSERT INTO catalogo_cuentas
(codigo, nombre, tipo, clasificacion, naturaleza, rol_reporte,
 id_cuenta_padre, permite_movimiento, activo)
SELECT '1.2.01.03', 'Equipo de Transporte', 'ACTIVO', 'NO_CORRIENTE', 'DEUDORA', 'NINGUNO',
       id_cuenta, TRUE, TRUE
FROM catalogo_cuentas WHERE codigo = '1.2.01'
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), id_cuenta_padre = VALUES(id_cuenta_padre), activo = TRUE;

-- Auxiliares de Cuentas por Pagar
INSERT INTO catalogo_cuentas
(codigo, nombre, tipo, clasificacion, naturaleza, rol_reporte,
 id_cuenta_padre, permite_movimiento, activo)
SELECT '2.1.01.01', 'Proveedores', 'PASIVO', 'CORRIENTE', 'ACREEDORA', 'NINGUNO',
       id_cuenta, TRUE, TRUE
FROM catalogo_cuentas WHERE codigo = '2.1.01'
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), id_cuenta_padre = VALUES(id_cuenta_padre), activo = TRUE;

INSERT INTO catalogo_cuentas
(codigo, nombre, tipo, clasificacion, naturaleza, rol_reporte,
 id_cuenta_padre, permite_movimiento, activo)
SELECT '2.1.01.02', 'Acreedores Varios', 'PASIVO', 'CORRIENTE', 'ACREEDORA', 'NINGUNO',
       id_cuenta, TRUE, TRUE
FROM catalogo_cuentas WHERE codigo = '2.1.01'
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre), id_cuenta_padre = VALUES(id_cuenta_padre), activo = TRUE;

-- Consulta rápida para presentar el catálogo
SELECT
    codigo,
    nombre,
    tipo,
    clasificacion,
    naturaleza,
    rol_reporte,
    permite_movimiento,
    activo
FROM catalogo_cuentas
ORDER BY codigo;
