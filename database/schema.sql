-- ============================================================
-- SISTEMA CONTABLE - ESQUEMA DE BASE DE DATOS
-- Base de datos: sistema_contable
-- Motor: MySQL 8+
-- ============================================================

CREATE DATABASE IF NOT EXISTS sistema_contable
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sistema_contable;

CREATE TABLE IF NOT EXISTS empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    nit VARCHAR(30),
    nrc VARCHAR(30),
    giro_comercial VARCHAR(150),
    direccion VARCHAR(255),
    telefono VARCHAR(30),
    correo VARCHAR(120),
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS configuracion_contable (
    id_configuracion INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NOT NULL,
    porcentaje_iva DECIMAL(5,2) NOT NULL DEFAULT 13.00,
    tipo_iva ENUM('INCLUIDO','MAS_IVA') NOT NULL DEFAULT 'INCLUIDO',
    moneda VARCHAR(10) NOT NULL DEFAULT 'USD',
    CONSTRAINT fk_config_empresa
        FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS periodos_contables (
    id_periodo INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM('ABIERTO','CERRADO') NOT NULL DEFAULT 'ABIERTO',
    CONSTRAINT fk_periodo_empresa
        FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS catalogo_cuentas (
    id_cuenta INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    tipo ENUM('ACTIVO','PASIVO','PATRIMONIO','INGRESO','COSTO','GASTO') NOT NULL,
    clasificacion ENUM(
        'CORRIENTE','NO_CORRIENTE','PATRIMONIO','INGRESO','COSTO',
        'ADMINISTRATIVO','VENTA','FINANCIERO','OTRO'
    ) NOT NULL,
    naturaleza ENUM('DEUDORA','ACREEDORA') NOT NULL,
    rol_reporte ENUM(
        'NINGUNO','EFECTIVO','CUENTAS_COBRAR','INVENTARIO','IVA_CREDITO',
        'PROPIEDAD_PLANTA_EQUIPO','CUENTAS_PAGAR','IVA_DEBITO',
        'PRESTAMO_BANCARIO','CAPITAL_SOCIAL','VENTAS','DEVOLUCION_VENTAS',
        'COMPRAS','DEVOLUCION_COMPRAS','GASTOS_COMPRA','GASTOS_ADMIN',
        'GASTOS_VENTA','GASTOS_FINANCIEROS','UTILIDAD_EJERCICIO'
    ) NOT NULL DEFAULT 'NINGUNO',
    id_cuenta_padre INT NULL,
    permite_movimiento BOOLEAN NOT NULL DEFAULT TRUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_cuenta_padre
        FOREIGN KEY (id_cuenta_padre) REFERENCES catalogo_cuentas(id_cuenta)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255),
    costo_compra DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    precio_venta DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    costo_inicial DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    valor_inventario_inicial DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    existencia_inicial DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    existencia_actual DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS operaciones (
    id_operacion INT AUTO_INCREMENT PRIMARY KEY,
    id_periodo INT NOT NULL,
    fecha DATE NOT NULL,
    tipo_operacion ENUM(
        'COMPRA','VENTA','DEVOLUCION_COMPRA','DEVOLUCION_VENTA',
        'COBRO_CLIENTE','PAGO_PROVEEDOR','GASTO_ADMINISTRATIVO',
        'GASTO_VENTA','GASTO_FINANCIERO','COMPRA_ACTIVO','PRESTAMO',
        'APORTE_CAPITAL','OTRO'
    ) NOT NULL,
    concepto VARCHAR(255) NOT NULL,
    id_producto INT NULL,
    id_operacion_origen INT NULL,
    cantidad DECIMAL(18,6) NULL,
    precio_unitario DECIMAL(18,6) NULL,
    subtotal DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    iva DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    total DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    forma_pago ENUM('EFECTIVO','BANCO','CREDITO','MIXTO','NO_APLICA') NOT NULL DEFAULT 'NO_APLICA',
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_operacion_periodo
        FOREIGN KEY (id_periodo) REFERENCES periodos_contables(id_periodo),
    CONSTRAINT fk_operacion_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    CONSTRAINT fk_operacion_origen
        FOREIGN KEY (id_operacion_origen) REFERENCES operaciones(id_operacion)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS asientos_contables (
    id_asiento INT AUTO_INCREMENT PRIMARY KEY,
    id_periodo INT NOT NULL,
    id_operacion INT NULL,
    numero_asiento INT NOT NULL,
    fecha DATE NOT NULL,
    concepto VARCHAR(255) NOT NULL,
    tipo_asiento ENUM('AUTOMATICO','MANUAL') NOT NULL DEFAULT 'AUTOMATICO',
    estado ENUM('BORRADOR','CONTABILIZADO','ANULADO') NOT NULL DEFAULT 'CONTABILIZADO',
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_asiento_periodo_numero UNIQUE (id_periodo, numero_asiento),
    CONSTRAINT fk_asiento_periodo
        FOREIGN KEY (id_periodo) REFERENCES periodos_contables(id_periodo),
    CONSTRAINT fk_asiento_operacion
        FOREIGN KEY (id_operacion) REFERENCES operaciones(id_operacion)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS detalle_asientos (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_asiento INT NOT NULL,
    id_cuenta INT NOT NULL,
    descripcion VARCHAR(255),
    debe DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    haber DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT chk_detalle_no_negativo CHECK (debe >= 0 AND haber >= 0),
    CONSTRAINT chk_detalle_un_lado CHECK (NOT (debe > 0 AND haber > 0)),
    CONSTRAINT fk_detalle_asiento
        FOREIGN KEY (id_asiento) REFERENCES asientos_contables(id_asiento),
    CONSTRAINT fk_detalle_cuenta
        FOREIGN KEY (id_cuenta) REFERENCES catalogo_cuentas(id_cuenta)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS kardex (
    id_kardex INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_asiento INT NULL,
    fecha DATE NOT NULL,
    concepto VARCHAR(255) NOT NULL,
    unidades_entrada DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    unidades_salida DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    unidades_existencia DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    costo_unitario DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    costo_peps DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    saldo_deudor DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    saldo_acreedor DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    saldo DECIMAL(18,2) NOT NULL DEFAULT 0.00,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_kardex_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    CONSTRAINT fk_kardex_asiento
        FOREIGN KEY (id_asiento) REFERENCES asientos_contables(id_asiento)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS capas_peps (
    id_capa INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_kardex_entrada INT NOT NULL,
    fecha DATE NOT NULL,
    cantidad_original DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    cantidad_disponible DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    costo_unitario DECIMAL(18,6) NOT NULL DEFAULT 0.000000,
    CONSTRAINT fk_capa_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto),
    CONSTRAINT fk_capa_kardex
        FOREIGN KEY (id_kardex_entrada) REFERENCES kardex(id_kardex)
) ENGINE=InnoDB;

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

CREATE INDEX idx_operaciones_periodo_fecha
    ON operaciones(id_periodo, fecha);

CREATE INDEX idx_asientos_periodo_fecha
    ON asientos_contables(id_periodo, fecha);

CREATE INDEX idx_detalle_asiento
    ON detalle_asientos(id_asiento);

CREATE INDEX idx_detalle_cuenta
    ON detalle_asientos(id_cuenta);

CREATE INDEX idx_kardex_producto_fecha
    ON kardex(id_producto, fecha);


-- ============================================================
-- SEGURIDAD: LOGIN Y ROLES
-- ============================================================
-- ============================================================
-- MODULO DE LOGIN Y ROLES
-- Ejecutar sobre la base existente sistema_contable
-- ============================================================


CREATE TABLE IF NOT EXISTS roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(60) NOT NULL UNIQUE,
    nombre_completo VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
) ENGINE=InnoDB;

-- ============================================================
-- SOPORTE DE CIERRE / APERTURA ENTRE PERIODOS
-- ============================================================
CREATE TABLE IF NOT EXISTS inventario_apertura_lotes (
    id_apertura_lote BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_periodo INT NOT NULL,
    id_producto INT NOT NULL,
    fecha_origen DATE NOT NULL,
    cantidad DECIMAL(18,6) NOT NULL,
    costo_unitario DECIMAL(18,6) NOT NULL,
    orden_lote INT NOT NULL,
    INDEX idx_apertura_lotes_periodo_producto(id_periodo,id_producto,orden_lote),
    CONSTRAINT fk_apertura_lotes_periodo FOREIGN KEY(id_periodo) REFERENCES periodos_contables(id_periodo) ON DELETE CASCADE,
    CONSTRAINT fk_apertura_lotes_producto FOREIGN KEY(id_producto) REFERENCES productos(id_producto)
) ENGINE=InnoDB;
