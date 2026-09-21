-- ============================================================
-- MODULO DE LOGIN Y ROLES
-- Ejecutar sobre la base existente sistema_contable
-- ============================================================
USE sistema_contable;

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

INSERT INTO roles (codigo,nombre,descripcion) VALUES
('ADMINISTRADOR','Administrador','Acceso total al sistema, catálogo, IVA, usuarios y edición de asientos manuales'),
('CONTABLE','Contable','Registra operaciones y asientos y consulta reportes'),
('CONSULTA','Consulta / Auditor','Acceso de solo lectura al Libro Diario y reportes')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), descripcion=VALUES(descripcion), activo=TRUE;

INSERT IGNORE INTO usuarios(usuario,nombre_completo,password_hash,id_rol,activo)
SELECT 'admin','Administrador del Sistema','PBKDF2$120000$YWRtaW4tc2VlZC0yMDI2$MG09z74/MtjIXNaCO663CBMEEcmg23UQ7ytQWBlXA/0=',id_rol,TRUE FROM roles WHERE codigo='ADMINISTRADOR';
INSERT IGNORE INTO usuarios(usuario,nombre_completo,password_hash,id_rol,activo)
SELECT 'contable','Usuario Contable','PBKDF2$120000$Y29udGFibGUtc2VlZC0yMDI2$MJyIiKCZ9hNRMtjCdJ59WZywt0+K9CYgqqTC0CSssv4=',id_rol,TRUE FROM roles WHERE codigo='CONTABLE';
INSERT IGNORE INTO usuarios(usuario,nombre_completo,password_hash,id_rol,activo)
SELECT 'consulta','Usuario de Consulta','PBKDF2$120000$Y29uc3VsdGEtc2VlZC0yMDI2$QLIX85qxKHz7ZxDArRA9dV0eplfhs+yVNLBfob2m8fo=',id_rol,TRUE FROM roles WHERE codigo='CONSULTA';
