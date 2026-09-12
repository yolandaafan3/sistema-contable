package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.PeriodoContable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeriodoContableDAO {

    public PeriodoContable obtenerPeriodoAbierto() {

        String sql = """
                SELECT
                    id_periodo,
                    id_empresa,
                    nombre,
                    fecha_inicio,
                    fecha_fin,
                    estado
                FROM periodos_contables
                WHERE estado = 'ABIERTO'
                ORDER BY id_periodo DESC
                LIMIT 1
                """;

        try (
                Connection conexion = Conexion.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                PeriodoContable periodo =
                        new PeriodoContable();

                periodo.setIdPeriodo(
                        rs.getInt("id_periodo")
                );

                periodo.setIdEmpresa(
                        rs.getInt("id_empresa")
                );

                periodo.setNombre(
                        rs.getString("nombre")
                );

                periodo.setFechaInicio(
                        rs.getDate("fecha_inicio")
                                .toLocalDate()
                );

                periodo.setFechaFin(
                        rs.getDate("fecha_fin")
                                .toLocalDate()
                );

                periodo.setEstado(
                        rs.getString("estado")
                );

                return periodo;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener periodo contable: "
                    + e.getMessage()
            );
        }

        return null;
    }
}