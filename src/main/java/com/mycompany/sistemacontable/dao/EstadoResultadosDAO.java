package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.SaldoRolContable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.math.BigDecimal;

import java.util.HashMap;
import java.util.Map;

public class EstadoResultadosDAO {

    public Map<String, SaldoRolContable> obtenerSaldosPorRol(
            int idPeriodo
    ) {

        Map<String, SaldoRolContable> saldos =
                new HashMap<>();

        String sql = """
                SELECT
                    cc.rol_reporte,

                    COALESCE(
                        SUM(da.debe),
                        0
                    ) AS total_debe,

                    COALESCE(
                        SUM(da.haber),
                        0
                    ) AS total_haber

                FROM detalle_asientos da

                INNER JOIN asientos_contables ac
                    ON ac.id_asiento = da.id_asiento

                INNER JOIN catalogo_cuentas cc
                    ON cc.id_cuenta = da.id_cuenta

                WHERE ac.id_periodo = ?
                  AND ac.estado = 'CONTABILIZADO'
                  AND cc.rol_reporte <> 'NINGUNO'

                GROUP BY cc.rol_reporte
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

                    SaldoRolContable saldo =
                            new SaldoRolContable();

                    saldo.setRolReporte(
                            rs.getString(
                                    "rol_reporte"
                            )
                    );

                    saldo.setTotalDebe(
                            rs.getBigDecimal(
                                    "total_debe"
                            )
                    );

                    saldo.setTotalHaber(
                            rs.getBigDecimal(
                                    "total_haber"
                            )
                    );

                    saldos.put(
                            saldo.getRolReporte(),
                            saldo
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al obtener datos del Estado de Resultados: "
                    + e.getMessage(),
                    e
            );
        }

        return saldos;
    }


    /**
     * Obtiene exclusivamente las compras de mercadería del período.
     * Excluye compras de activos y cualquier otra operación que haya usado
     * accidentalmente la cuenta Compras en datos históricos.
     */
    public BigDecimal obtenerComprasMercaderia(int idPeriodo) {

        String sql = """
                SELECT
                    COALESCE(SUM(da.debe), 0) AS total_debe,
                    COALESCE(SUM(da.haber), 0) AS total_haber
                FROM detalle_asientos da
                INNER JOIN asientos_contables ac
                    ON ac.id_asiento = da.id_asiento
                INNER JOIN catalogo_cuentas cc
                    ON cc.id_cuenta = da.id_cuenta
                INNER JOIN operaciones op
                    ON op.id_operacion = ac.id_operacion
                WHERE ac.id_periodo = ?
                  AND ac.estado = 'CONTABILIZADO'
                  AND cc.rol_reporte = 'COMPRAS'
                  AND op.tipo_operacion = 'COMPRA'
                """;

        try (
                Connection conexion = Conexion.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {
            ps.setInt(1, idPeriodo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal debe = rs.getBigDecimal("total_debe");
                    BigDecimal haber = rs.getBigDecimal("total_haber");
                    BigDecimal saldo = debe.subtract(haber);
                    return saldo.compareTo(BigDecimal.ZERO) < 0
                            ? BigDecimal.ZERO
                            : saldo;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al obtener compras de mercadería: " + e.getMessage(),
                    e
            );
        }

        return BigDecimal.ZERO;
    }

    public BigDecimal obtenerSaldoDeudorPorCodigo(
            int idPeriodo,
            String codigo
    ) {
        return obtenerSaldoPorCodigo(idPeriodo, codigo, true);
    }


    public BigDecimal obtenerSaldoAcreedorPorCodigo(
            int idPeriodo,
            String codigo
    ) {
        return obtenerSaldoPorCodigo(idPeriodo, codigo, false);
    }


    private BigDecimal obtenerSaldoPorCodigo(
            int idPeriodo,
            String codigo,
            boolean deudor
    ) {

        String sql = """
                SELECT
                    COALESCE(SUM(da.debe), 0) AS total_debe,
                    COALESCE(SUM(da.haber), 0) AS total_haber
                FROM detalle_asientos da
                INNER JOIN asientos_contables ac
                    ON ac.id_asiento = da.id_asiento
                INNER JOIN catalogo_cuentas cc
                    ON cc.id_cuenta = da.id_cuenta
                WHERE ac.id_periodo = ?
                  AND ac.estado = 'CONTABILIZADO'
                  AND cc.codigo = ?
                """;

        try (
                Connection conexion = Conexion.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, idPeriodo);
            ps.setString(2, codigo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal debe = rs.getBigDecimal("total_debe");
                    BigDecimal haber = rs.getBigDecimal("total_haber");
                    BigDecimal valor = deudor
                            ? debe.subtract(haber)
                            : haber.subtract(debe);

                    if (valor.compareTo(BigDecimal.ZERO) < 0) {
                        return BigDecimal.ZERO;
                    }

                    return valor;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Error al obtener saldo de la cuenta " + codigo + ": "
                    + e.getMessage(),
                    e
            );
        }

        return BigDecimal.ZERO;
    }

}