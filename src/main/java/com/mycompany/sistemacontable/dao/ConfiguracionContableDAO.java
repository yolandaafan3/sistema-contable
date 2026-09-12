package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.ConfiguracionContable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracionContableDAO {

    public ConfiguracionContable obtenerConfiguracion() {

        String sql = """
                SELECT
                    id_configuracion,
                    id_empresa,
                    porcentaje_iva,
                    tipo_iva,
                    moneda
                FROM configuracion_contable
                ORDER BY id_configuracion
                LIMIT 1
                """;

        try (
                Connection conexion = Conexion.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                ConfiguracionContable configuracion =
                        new ConfiguracionContable();

                configuracion.setIdConfiguracion(
                        rs.getInt("id_configuracion")
                );

                configuracion.setIdEmpresa(
                        rs.getInt("id_empresa")
                );

                configuracion.setPorcentajeIva(
                        rs.getBigDecimal("porcentaje_iva")
                );

                configuracion.setTipoIva(
                        rs.getString("tipo_iva")
                );

                configuracion.setMoneda(
                        rs.getString("moneda")
                );

                return configuracion;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener configuracion contable: "
                    + e.getMessage()
            );
        }

        return null;
    }

    public void actualizarIva(
            int idConfiguracion,
            java.math.BigDecimal porcentajeIva,
            String tipoIva
    ) {

        String sql = """
                UPDATE configuracion_contable
                SET porcentaje_iva = ?,
                    tipo_iva = ?
                WHERE id_configuracion = ?
                """;

        try (
                Connection conexion = Conexion.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setBigDecimal(1, porcentajeIva);
            ps.setString(2, tipoIva);
            ps.setInt(3, idConfiguracion);

            if (ps.executeUpdate() == 0) {
                throw new SQLException("No se encontró la configuración contable.");
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo actualizar la configuración de IVA: "
                    + e.getMessage(),
                    e
            );
        }
    }
}