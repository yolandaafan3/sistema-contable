package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;

import com.mycompany.sistemacontable.modelo.CuentaMayor;
import com.mycompany.sistemacontable.modelo.MovimientoMayor;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MayorizacionDAO {

    public List<CuentaMayor> obtenerMayorizacion(
            int idPeriodo
    ) {

        Map<Integer, CuentaMayor> cuentas =
                new LinkedHashMap<>();

        String sql = """
                SELECT
                    cc.id_cuenta,
                    cc.codigo,
                    cc.nombre,
                    cc.naturaleza,

                    ac.numero_asiento,
                    ac.fecha,
                    ac.concepto,

                    da.debe,
                    da.haber

                FROM detalle_asientos da

                INNER JOIN asientos_contables ac
                    ON ac.id_asiento = da.id_asiento

                INNER JOIN catalogo_cuentas cc
                    ON cc.id_cuenta = da.id_cuenta

                WHERE ac.id_periodo = ?
                  AND ac.estado = 'CONTABILIZADO'

                ORDER BY
                    cc.codigo ASC,
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

                    int idCuenta =
                            rs.getInt(
                                    "id_cuenta"
                            );

                    CuentaMayor cuentaMayor =
                            cuentas.get(
                                    idCuenta
                            );


                    if (cuentaMayor == null) {

                        cuentaMayor =
                                new CuentaMayor();

                        cuentaMayor.setIdCuenta(
                                idCuenta
                        );

                        cuentaMayor.setCodigo(
                                rs.getString(
                                        "codigo"
                                )
                        );

                        cuentaMayor.setNombre(
                                rs.getString(
                                        "nombre"
                                )
                        );

                        cuentaMayor.setNaturaleza(
                                rs.getString(
                                        "naturaleza"
                                )
                        );

                        cuentaMayor.setTotalDebe(
                                BigDecimal.ZERO
                        );

                        cuentaMayor.setTotalHaber(
                                BigDecimal.ZERO
                        );

                        cuentas.put(
                                idCuenta,
                                cuentaMayor
                        );
                    }


                    BigDecimal debe =
                            rs.getBigDecimal(
                                    "debe"
                            );

                    BigDecimal haber =
                            rs.getBigDecimal(
                                    "haber"
                            );


                    if (debe == null) {
                        debe = BigDecimal.ZERO;
                    }

                    if (haber == null) {
                        haber = BigDecimal.ZERO;
                    }


                    MovimientoMayor movimiento =
                            new MovimientoMayor();

                    movimiento.setNumeroAsiento(
                            rs.getInt(
                                    "numero_asiento"
                            )
                    );

                    movimiento.setFecha(
                            rs.getDate(
                                    "fecha"
                            ).toLocalDate()
                    );

                    movimiento.setConcepto(
                            rs.getString(
                                    "concepto"
                            )
                    );

                    movimiento.setDebe(
                            debe
                    );

                    movimiento.setHaber(
                            haber
                    );


                    cuentaMayor.agregarMovimiento(
                            movimiento
                    );


                    cuentaMayor.setTotalDebe(
                            cuentaMayor
                                    .getTotalDebe()
                                    .add(debe)
                    );

                    cuentaMayor.setTotalHaber(
                            cuentaMayor
                                    .getTotalHaber()
                                    .add(haber)
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al realizar la mayorizacion: "
                    + e.getMessage(),
                    e
            );
        }


        return new ArrayList<>(
                cuentas.values()
        );
    }
}