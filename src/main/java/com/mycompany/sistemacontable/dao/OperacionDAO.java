package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.modelo.Operacion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OperacionDAO {

    public int insertar(
            Operacion operacion,
            Connection conexion
    ) throws SQLException {

        String sql = """
                INSERT INTO operaciones (
                    id_periodo,
                    fecha,
                    tipo_operacion,
                    concepto,
                    id_producto,
                    id_operacion_origen,
                    cantidad,
                    precio_unitario,
                    subtotal,
                    iva,
                    total,
                    forma_pago
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
                    operacion.getIdPeriodo()
            );

            ps.setDate(
                    2,
                    Date.valueOf(
                            operacion.getFecha()
                    )
            );

            ps.setString(
                    3,
                    operacion.getTipoOperacion()
            );

            ps.setString(
                    4,
                    operacion.getConcepto()
            );

            if (operacion.getIdProducto() != null) {

                ps.setInt(
                        5,
                        operacion.getIdProducto()
                );

            } else {

                ps.setNull(
                        5,
                        java.sql.Types.INTEGER
                );
            }

            if (operacion.getIdOperacionOrigen() != null) {
                ps.setInt(6, operacion.getIdOperacionOrigen());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            if (operacion.getCantidad() != null) {

                ps.setBigDecimal(
                        7,
                        operacion.getCantidad()
                );

            } else {

                ps.setNull(
                        7,
                        java.sql.Types.DECIMAL
                );
            }

            if (operacion.getPrecioUnitario() != null) {

                ps.setBigDecimal(
                        8,
                        operacion.getPrecioUnitario()
                );

            } else {

                ps.setNull(
                        8,
                        java.sql.Types.DECIMAL
                );
            }

            ps.setBigDecimal(
                    9,
                    operacion.getSubtotal()
            );

            ps.setBigDecimal(
                    10,
                    operacion.getIva()
            );

            ps.setBigDecimal(
                    11,
                    operacion.getTotal()
            );

            ps.setString(
                    12,
                    operacion.getFormaPago()
            );

            ps.executeUpdate();

            try (
                    ResultSet rs =
                            ps.getGeneratedKeys()
            ) {

                if (rs.next()) {
                    int id = rs.getInt(1);
                    String nuevo = operacion.getTipoOperacion() + " | fecha=" + operacion.getFecha()
                            + " | concepto=" + operacion.getConcepto() + " | total=" + operacion.getTotal();
                    new AuditoriaDAO().registrarSiFalta(conexion, "OPERACION", id, "CREO", null, nuevo);
                    return id;
                }
            }
        }

        throw new SQLException(
                "No se pudo obtener el ID de la operacion."
        );
    }
}