package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.LineaLibroDiario;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class LibroDiarioDAO {

    public List<LineaLibroDiario> listar(
            int idPeriodo
    ) {

        List<LineaLibroDiario> lineas =
                new ArrayList<>();

        String sql = """
                SELECT
                    ac.id_asiento,
                    ac.numero_asiento,
                    ac.fecha,
                    ac.concepto,

                    cc.codigo AS codigo_cuenta,
                    cc.nombre AS nombre_cuenta,

                    da.descripcion,
                    da.debe,
                    da.haber

                FROM asientos_contables ac

                INNER JOIN detalle_asientos da
                    ON da.id_asiento = ac.id_asiento

                INNER JOIN catalogo_cuentas cc
                    ON cc.id_cuenta = da.id_cuenta

                WHERE ac.id_periodo = ?
                  AND ac.estado = 'CONTABILIZADO'

                ORDER BY
                    ac.fecha ASC,
                    ac.numero_asiento ASC,
                    da.id_detalle ASC
                """;

        try (
                Connection conexion =
                        Conexion.conectar();
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

                while (rs.next()) {

                    LineaLibroDiario linea =
                            new LineaLibroDiario();

                    linea.setIdAsiento(
                            rs.getInt(
                                    "id_asiento"
                            )
                    );

                    linea.setNumeroAsiento(
                            rs.getInt(
                                    "numero_asiento"
                            )
                    );

                    linea.setFecha(
                            rs.getDate(
                                    "fecha"
                            ).toLocalDate()
                    );

                    linea.setConcepto(
                            rs.getString(
                                    "concepto"
                            )
                    );

                    linea.setCodigoCuenta(
                            rs.getString(
                                    "codigo_cuenta"
                            )
                    );

                    linea.setNombreCuenta(
                            rs.getString(
                                    "nombre_cuenta"
                            )
                    );

                    linea.setDescripcion(
                            rs.getString(
                                    "descripcion"
                            )
                    );

                    linea.setDebe(
                            rs.getBigDecimal(
                                    "debe"
                            )
                    );

                    linea.setHaber(
                            rs.getBigDecimal(
                                    "haber"
                            )
                    );

                    lineas.add(linea);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al obtener el Libro Diario: "
                    + e.getMessage(),
                    e
            );
        }

        return lineas;
    }


    public BigDecimal obtenerTotalDebe(
            int idPeriodo
    ) {

        return obtenerTotal(
                idPeriodo,
                "debe"
        );
    }


    public BigDecimal obtenerTotalHaber(
            int idPeriodo
    ) {

        return obtenerTotal(
                idPeriodo,
                "haber"
        );
    }


    private BigDecimal obtenerTotal(
            int idPeriodo,
            String columna
    ) {

        if (!columna.equals("debe")
                && !columna.equals("haber")) {

            throw new IllegalArgumentException(
                    "Columna contable no valida."
            );
        }

        String sql = """
                SELECT
                    COALESCE(
                        SUM(da.%s),
                        0
                    ) AS total

                FROM detalle_asientos da

                INNER JOIN asientos_contables ac
                    ON ac.id_asiento = da.id_asiento

                WHERE ac.id_periodo = ?
                  AND ac.estado = 'CONTABILIZADO'
                """.formatted(columna);

        try (
                Connection conexion =
                        Conexion.conectar();
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

                    return rs.getBigDecimal(
                            "total"
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al calcular total del Libro Diario: "
                    + e.getMessage(),
                    e
            );
        }

        return BigDecimal.ZERO;
    }
}