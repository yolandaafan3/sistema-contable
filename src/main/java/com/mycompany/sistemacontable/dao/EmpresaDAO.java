package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.Empresa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmpresaDAO {

    public Empresa buscarPorId(int idEmpresa) {

        String sql = """
                SELECT
                    id_empresa,
                    nombre,
                    nit,
                    nrc,
                    giro_comercial,
                    direccion,
                    telefono,
                    correo,
                    activo
                FROM empresa
                WHERE id_empresa = ?
                """;

        try (
                Connection conexion = Conexion.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Empresa empresa = new Empresa();

                    empresa.setIdEmpresa(rs.getInt("id_empresa"));
                    empresa.setNombre(rs.getString("nombre"));
                    empresa.setNit(rs.getString("nit"));
                    empresa.setNrc(rs.getString("nrc"));
                    empresa.setGiroComercial(rs.getString("giro_comercial"));
                    empresa.setDireccion(rs.getString("direccion"));
                    empresa.setTelefono(rs.getString("telefono"));
                    empresa.setCorreo(rs.getString("correo"));
                    empresa.setActivo(rs.getBoolean("activo"));

                    return empresa;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo obtener la información de la empresa: "
                    + e.getMessage(),
                    e
            );
        }

        return null;
    }

    public void actualizar(Empresa empresa) {

        String sql = """
                UPDATE empresa
                SET nombre = ?,
                    nit = ?,
                    nrc = ?,
                    giro_comercial = ?,
                    direccion = ?,
                    telefono = ?,
                    correo = ?
                WHERE id_empresa = ?
                """;

        try (
                Connection conexion = Conexion.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getNit());
            ps.setString(3, empresa.getNrc());
            ps.setString(4, empresa.getGiroComercial());
            ps.setString(5, empresa.getDireccion());
            ps.setString(6, empresa.getTelefono());
            ps.setString(7, empresa.getCorreo());
            ps.setInt(8, empresa.getIdEmpresa());

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró la empresa.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo actualizar la empresa: "
                    + e.getMessage(),
                    e
            );
        }
    }
}