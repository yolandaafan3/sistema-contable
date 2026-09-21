package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class InicializacionSeguridadService {
    public void prepararSeguridad() {
        try (Connection cn = Conexion.conectar(); Statement st = cn.createStatement()) {
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS roles (
                    id_rol INT AUTO_INCREMENT PRIMARY KEY,
                    codigo VARCHAR(30) NOT NULL UNIQUE,
                    nombre VARCHAR(80) NOT NULL,
                    descripcion VARCHAR(255),
                    activo BOOLEAN NOT NULL DEFAULT TRUE
                ) ENGINE=InnoDB
                """);
            st.executeUpdate("""
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
                ) ENGINE=InnoDB
                """);
            st.executeUpdate("INSERT IGNORE INTO roles(codigo,nombre,descripcion) VALUES "
                    + "('ADMINISTRADOR','Administrador','Acceso total al sistema, catálogo, IVA, usuarios y edición de asientos manuales'),"
                    + "('CONTABLE','Contable','Registra operaciones y asientos, y consulta todos los reportes'),"
                    + "('CONSULTA','Consulta / Auditor','Acceso de solo lectura a libro diario y reportes')");
            insertarUsuario(cn, "admin", "Administrador del Sistema", "PBKDF2$120000$YWRtaW4tc2VlZC0yMDI2$MG09z74/MtjIXNaCO663CBMEEcmg23UQ7ytQWBlXA/0=", "ADMINISTRADOR");
            insertarUsuario(cn, "contable", "Usuario Contable", "PBKDF2$120000$Y29udGFibGUtc2VlZC0yMDI2$MJyIiKCZ9hNRMtjCdJ59WZywt0+K9CYgqqTC0CSssv4=", "CONTABLE");
            insertarUsuario(cn, "consulta", "Usuario de Consulta", "PBKDF2$120000$Y29uc3VsdGEtc2VlZC0yMDI2$QLIX85qxKHz7ZxDArRA9dV0eplfhs+yVNLBfob2m8fo=", "CONSULTA");
        } catch (Exception e) {
            throw new RuntimeException("No se pudo preparar el módulo de seguridad: " + e.getMessage(), e);
        }
    }

    private void insertarUsuario(Connection cn, String usuario, String nombre, String hash, String rol) throws Exception {
        String sql = """
            INSERT IGNORE INTO usuarios(usuario,nombre_completo,password_hash,id_rol,activo)
            SELECT ?,?,?,id_rol,TRUE FROM roles WHERE codigo=?
            """;
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario); ps.setString(2, nombre); ps.setString(3, hash); ps.setString(4, rol); ps.executeUpdate();
        }
    }
}
