package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.modelo.OperacionInventarioRecalculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class OperacionInventarioDAO {

    public List<OperacionInventarioRecalculo> listarPorProducto(
            int idProducto,
            Connection conexion
    ) throws SQLException {

        List<OperacionInventarioRecalculo> operaciones =
                new ArrayList<>();

        String sql = """
                SELECT
                    o.id_operacion,
                    o.id_operacion_origen,
                    ac.id_asiento,
                    o.fecha,
                    o.tipo_operacion,
                    o.concepto,
                    o.cantidad,
                    o.subtotal,
                    o.iva,
                    o.total

                FROM operaciones o

                INNER JOIN asientos_contables ac
                    ON ac.id_operacion = o.id_operacion

                WHERE o.id_producto = ?
                  AND ac.estado = 'CONTABILIZADO'
                  AND ac.id_periodo = (
                      SELECT id_periodo FROM periodos_contables
                      WHERE estado='ABIERTO' ORDER BY id_periodo DESC LIMIT 1
                  )

                  AND o.tipo_operacion IN (
                      'COMPRA',
                      'VENTA',
                      'DEVOLUCION_COMPRA',
                      'DEVOLUCION_VENTA'
                  )
                  /*
                   * Protección adicional: una compra de activo no pertenece al
                   * Kardex de mercadería, aunque por datos históricos haya quedado
                   * guardada erróneamente con tipo COMPRA.
                   */
                  AND LOWER(TRIM(o.concepto)) NOT LIKE 'compra de activo%'

                ORDER BY
                    o.fecha ASC,
                    o.id_operacion ASC
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idProducto
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    OperacionInventarioRecalculo operacion =
                            new OperacionInventarioRecalculo();

                    operacion.setIdOperacion(
                            rs.getInt("id_operacion")
                    );

                    int origen = rs.getInt("id_operacion_origen");
                    operacion.setIdOperacionOrigen(rs.wasNull() ? null : origen);

                    operacion.setIdAsiento(
                            rs.getInt("id_asiento")
                    );

                    operacion.setFecha(
                            rs.getDate("fecha")
                                    .toLocalDate()
                    );

                    operacion.setTipoOperacion(
                            rs.getString("tipo_operacion")
                    );

                    operacion.setConcepto(
                            rs.getString("concepto")
                    );

                    operacion.setCantidad(
                            rs.getBigDecimal("cantidad")
                    );

                    operacion.setSubtotal(
                            rs.getBigDecimal("subtotal")
                    );

                    operacion.setIva(
                            rs.getBigDecimal("iva")
                    );

                    operacion.setTotal(
                            rs.getBigDecimal("total")
                    );

                    operaciones.add(
                            operacion
                    );
                }
            }
        }

        return operaciones;
    }


    public LocalDate obtenerFechaInicioSistema(
            Connection conexion
    ) throws SQLException {

        String sql = """
                SELECT MIN(fecha_inicio) AS fecha_inicio
                FROM periodos_contables
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()
                    && rs.getDate("fecha_inicio") != null) {

                return rs.getDate(
                        "fecha_inicio"
                ).toLocalDate();
            }
        }

        return null;
    }
}