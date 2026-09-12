package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.LineaBalanceGeneral;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class BalanceGeneralDAO {

    public List<LineaBalanceGeneral> obtenerCuentas(
            int idPeriodo
    ) {

        List<LineaBalanceGeneral> cuentas =
                new ArrayList<>();

        String sql = """
                SELECT
                    cc.id_cuenta,
                    cc.codigo,
                    cc.nombre,
                    cc.tipo,
                    cc.clasificacion,
                    cc.naturaleza,
                    cc.rol_reporte,

                    COALESCE(
                        mov.total_debe,
                        0
                    ) AS total_debe,

                    COALESCE(
                        mov.total_haber,
                        0
                    ) AS total_haber

                FROM catalogo_cuentas cc

                LEFT JOIN (

                    SELECT
                        da.id_cuenta,

                        SUM(da.debe) AS total_debe,
                        SUM(da.haber) AS total_haber

                    FROM detalle_asientos da

                    INNER JOIN asientos_contables ac
                        ON ac.id_asiento = da.id_asiento

                    WHERE ac.id_periodo = ?
                      AND ac.estado = 'CONTABILIZADO'

                    GROUP BY da.id_cuenta

                ) mov
                    ON mov.id_cuenta = cc.id_cuenta

               WHERE cc.tipo IN (
                      'ACTIVO',
                      'PASIVO',
                      'PATRIMONIO'
                  )
                  AND (
                      cc.activo = TRUE
                      OR mov.id_cuenta IS NOT NULL
                  )

                ORDER BY cc.codigo
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

                    LineaBalanceGeneral linea =
                            new LineaBalanceGeneral();

                    linea.setIdCuenta(
                            rs.getInt(
                                    "id_cuenta"
                            )
                    );

                    linea.setCodigo(
                            rs.getString(
                                    "codigo"
                            )
                    );

                    linea.setNombre(
                            rs.getString(
                                    "nombre"
                            )
                    );

                    linea.setTipo(
                            rs.getString(
                                    "tipo"
                            )
                    );

                    linea.setClasificacion(
                            rs.getString(
                                    "clasificacion"
                            )
                    );

                    linea.setNaturaleza(
                            rs.getString(
                                    "naturaleza"
                            )
                    );

                    linea.setRolReporte(
                            rs.getString(
                                    "rol_reporte"
                            )
                    );

                    linea.setTotalDebe(
                            rs.getBigDecimal(
                                    "total_debe"
                            )
                    );

                    linea.setTotalHaber(
                            rs.getBigDecimal(
                                    "total_haber"
                            )
                    );

                    cuentas.add(
                            linea
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al obtener cuentas del Balance General: "
                    + e.getMessage(),
                    e
            );
        }

        return cuentas;
    }
}