package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AsientoDAO {

    public int obtenerSiguienteNumero(
            int idPeriodo,
            Connection conexion
    ) throws SQLException {

        String sql = """
                SELECT
                    COALESCE(MAX(numero_asiento), 0) + 1
                    AS siguiente
                FROM asientos_contables
                WHERE id_periodo = ?
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idPeriodo
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(
                            "siguiente"
                    );
                }
            }
        }

        return 1;
    }


    public int insertarAsiento(
            AsientoContable asiento,
            Connection conexion
    ) throws SQLException {

        String sql = """
                INSERT INTO asientos_contables (
                    id_periodo,
                    id_operacion,
                    numero_asiento,
                    fecha,
                    concepto,
                    tipo_asiento,
                    estado
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            ps.setInt(
                    1,
                    asiento.getIdPeriodo()
            );

            if (asiento.getIdOperacion() != null) {

                ps.setInt(
                        2,
                        asiento.getIdOperacion()
                );

            } else {

                ps.setNull(
                        2,
                        java.sql.Types.INTEGER
                );
            }

            ps.setInt(
                    3,
                    asiento.getNumeroAsiento()
            );

            ps.setDate(
                    4,
                    Date.valueOf(
                            asiento.getFecha()
                    )
            );

            ps.setString(
                    5,
                    asiento.getConcepto()
            );

            ps.setString(
                    6,
                    asiento.getTipoAsiento()
            );

            ps.setString(
                    7,
                    asiento.getEstado()
            );

            ps.executeUpdate();

            try (
                    ResultSet rs =
                            ps.getGeneratedKeys()
            ) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException(
                "No se pudo obtener el ID del asiento."
        );
    }


    public void insertarDetalle(
            DetalleAsiento detalle,
            Connection conexion
    ) throws SQLException {

        String sql = """
                INSERT INTO detalle_asientos (
                    id_asiento,
                    id_cuenta,
                    descripcion,
                    debe,
                    haber
                )
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    detalle.getIdAsiento()
            );

            ps.setInt(
                    2,
                    detalle.getIdCuenta()
            );

            ps.setString(
                    3,
                    detalle.getDescripcion()
            );

            ps.setBigDecimal(
                    4,
                    detalle.getDebe()
            );

            ps.setBigDecimal(
                    5,
                    detalle.getHaber()
            );

            ps.executeUpdate();
        }
    }
}