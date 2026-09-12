package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.modelo.CapaPeps;
import com.mycompany.sistemacontable.modelo.MovimientoKardex;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class KardexDAO {

    public int insertarMovimiento(
            MovimientoKardex movimiento,
            Connection conexion
    ) throws SQLException {

        String sql = """
                INSERT INTO kardex (
                    id_producto,
                    id_asiento,
                    fecha,
                    concepto,
                    unidades_entrada,
                    unidades_salida,
                    unidades_existencia,
                    costo_unitario,
                    costo_peps,
                    saldo_deudor,
                    saldo_acreedor,
                    saldo
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
                    movimiento.getIdProducto()
            );

            if (movimiento.getIdAsiento() != null) {

    ps.setInt(
            2,
            movimiento.getIdAsiento()
    );

} else {

    ps.setNull(
            2,
            java.sql.Types.INTEGER
    );
}

            ps.setDate(
                    3,
                    Date.valueOf(
                            movimiento.getFecha()
                    )
            );

            ps.setString(
                    4,
                    movimiento.getConcepto()
            );

            ps.setBigDecimal(
                    5,
                    movimiento.getUnidadesEntrada()
            );

            ps.setBigDecimal(
                    6,
                    movimiento.getUnidadesSalida()
            );

            ps.setBigDecimal(
                    7,
                    movimiento.getUnidadesExistencia()
            );

            ps.setBigDecimal(
                    8,
                    movimiento.getCostoUnitario()
            );

            ps.setBigDecimal(
                    9,
                    movimiento.getCostoPeps()
            );

            ps.setBigDecimal(
                    10,
                    movimiento.getSaldoDeudor()
            );

            ps.setBigDecimal(
                    11,
                    movimiento.getSaldoAcreedor()
            );

            ps.setBigDecimal(
                    12,
                    movimiento.getSaldo()
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
                "No se pudo obtener el ID del movimiento Kardex."
        );
    }


    public void insertarCapaPeps(
            int idProducto,
            int idKardexEntrada,
            LocalDate fecha,
            BigDecimal cantidad,
            BigDecimal costoUnitario,
            Connection conexion
    ) throws SQLException {

        String sql = """
                INSERT INTO capas_peps (
                    id_producto,
                    id_kardex_entrada,
                    fecha,
                    cantidad_original,
                    cantidad_disponible,
                    costo_unitario
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idProducto
            );

            ps.setInt(
                    2,
                    idKardexEntrada
            );

            ps.setDate(
                    3,
                    Date.valueOf(fecha)
            );

            ps.setBigDecimal(
                    4,
                    cantidad
            );

            ps.setBigDecimal(
                    5,
                    cantidad
            );

            ps.setBigDecimal(
                    6,
                    costoUnitario
            );

            ps.executeUpdate();
        }
    }


    public List<CapaPeps> listarCapasDisponibles(
            int idProducto,
            LocalDate fechaMovimiento,
            Connection conexion
    ) throws SQLException {

        List<CapaPeps> capas =
                new ArrayList<>();

        String sql = """
                SELECT
                    id_capa,
                    id_producto,
                    id_kardex_entrada,
                    fecha,
                    cantidad_original,
                    cantidad_disponible,
                    costo_unitario
                FROM capas_peps
                WHERE id_producto = ?
                  AND cantidad_disponible > 0
                  AND fecha <= ?
                ORDER BY fecha ASC, id_capa ASC
                FOR UPDATE
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    idProducto
            );

            ps.setDate(
                    2,
                    Date.valueOf(fechaMovimiento)
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (rs.next()) {

                    CapaPeps capa =
                            new CapaPeps();

                    capa.setIdCapa(
                            rs.getInt("id_capa")
                    );

                    capa.setIdProducto(
                            rs.getInt("id_producto")
                    );

                    capa.setIdKardexEntrada(
                            rs.getInt("id_kardex_entrada")
                    );

                    capa.setFecha(
                            rs.getDate("fecha")
                                    .toLocalDate()
                    );

                    capa.setCantidadOriginal(
                            rs.getBigDecimal(
                                    "cantidad_original"
                            )
                    );

                    capa.setCantidadDisponible(
                            rs.getBigDecimal(
                                    "cantidad_disponible"
                            )
                    );

                    capa.setCostoUnitario(
                            rs.getBigDecimal(
                                    "costo_unitario"
                            )
                    );

                    capas.add(capa);
                }
            }
        }

        return capas;
    }


    public void actualizarCantidadDisponible(
            int idCapa,
            BigDecimal nuevaCantidad,
            Connection conexion
    ) throws SQLException {

        if (nuevaCantidad.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "La cantidad disponible de una capa PEPS no puede ser negativa."
            );
        }

        String sql = """
                UPDATE capas_peps
                SET cantidad_disponible = ?
                WHERE id_capa = ?
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            ps.setBigDecimal(
                    1,
                    nuevaCantidad
            );

            ps.setInt(
                    2,
                    idCapa
            );

            int filas =
                    ps.executeUpdate();

            if (filas != 1) {

                throw new SQLException(
                        "No se pudo actualizar la capa PEPS."
                );
            }
        }
    }


    public BigDecimal obtenerSaldoPeps(
            int idProducto,
            Connection conexion
    ) throws SQLException {

        String sql = """
                SELECT
                    COALESCE(
                        SUM(
                            cantidad_disponible
                            * costo_unitario
                        ),
                        0
                    ) AS saldo
                FROM capas_peps
                WHERE id_producto = ?
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

                if (rs.next()) {

                    BigDecimal saldo =
                            rs.getBigDecimal(
                                    "saldo"
                            );

                    if (saldo == null) {
                        return BigDecimal.ZERO;
                    }

                    return saldo.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
                }
            }
        }

        return BigDecimal.ZERO;
    }
    
    public BigDecimal obtenerSaldoPepsHastaFecha(
        int idProducto,
        LocalDate fecha,
        Connection conexion
) throws SQLException {

    String sql = """
            SELECT
                COALESCE(
                    SUM(
                        cantidad_disponible
                        * costo_unitario
                    ),
                    0
                ) AS saldo
            FROM capas_peps
            WHERE id_producto = ?
              AND fecha <= ?
            """;

    try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
    ) {

        ps.setInt(
                1,
                idProducto
        );

        ps.setDate(
                2,
                Date.valueOf(fecha)
        );

        try (
                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {

                BigDecimal saldo =
                        rs.getBigDecimal("saldo");

                if (saldo == null) {
                    return BigDecimal.ZERO;
                }

                return saldo.setScale(
                        2,
                        RoundingMode.HALF_UP
                );
            }
        }
    }

    return BigDecimal.ZERO;
}
    public void eliminarCapasProducto(
        int idProducto,
        Connection conexion
) throws SQLException {

    String sql = """
            DELETE FROM capas_peps
            WHERE id_producto = ?
            """;

    try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
    ) {

        ps.setInt(
                1,
                idProducto
        );

        ps.executeUpdate();
    }
}


public void eliminarMovimientosProducto(
        int idProducto,
        Connection conexion
) throws SQLException {

    String sql = """
            DELETE FROM kardex
            WHERE id_producto = ?
            """;

    try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
    ) {

        ps.setInt(
                1,
                idProducto
        );

        ps.executeUpdate();
    }
}
    
public MovimientoKardex buscarMovimientoPorAsiento(
        int idAsiento,
        Connection conexion
) throws SQLException {

    String sql = """
            SELECT
                id_kardex,
                id_producto,
                id_asiento,
                fecha,
                concepto,
                unidades_entrada,
                unidades_salida,
                unidades_existencia,
                costo_unitario,
                costo_peps,
                saldo_deudor,
                saldo_acreedor,
                saldo
            FROM kardex
            WHERE id_asiento = ?
            LIMIT 1
            """;

    try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
    ) {

        ps.setInt(
                1,
                idAsiento
        );

        try (
                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {

                MovimientoKardex movimiento =
                        new MovimientoKardex();

                movimiento.setIdKardex(
                        rs.getInt(
                                "id_kardex"
                        )
                );

                movimiento.setIdProducto(
                        rs.getInt(
                                "id_producto"
                        )
                );

                Object idAsientoDb =
                        rs.getObject(
                                "id_asiento"
                        );

                if (idAsientoDb != null) {

                    movimiento.setIdAsiento(
                            ((Number) idAsientoDb)
                                    .intValue()
                    );
                }

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

                movimiento.setUnidadesEntrada(
                        rs.getBigDecimal(
                                "unidades_entrada"
                        )
                );

                movimiento.setUnidadesSalida(
                        rs.getBigDecimal(
                                "unidades_salida"
                        )
                );

                movimiento.setUnidadesExistencia(
                        rs.getBigDecimal(
                                "unidades_existencia"
                        )
                );

                movimiento.setCostoUnitario(
                        rs.getBigDecimal(
                                "costo_unitario"
                        )
                );

                movimiento.setCostoPeps(
                        rs.getBigDecimal(
                                "costo_peps"
                        )
                );

                movimiento.setSaldoDeudor(
                        rs.getBigDecimal(
                                "saldo_deudor"
                        )
                );

                movimiento.setSaldoAcreedor(
                        rs.getBigDecimal(
                                "saldo_acreedor"
                        )
                );

                movimiento.setSaldo(
                        rs.getBigDecimal(
                                "saldo"
                        )
                );

                return movimiento;
            }
        }
    }

    return null;
}
    

public java.util.List<MovimientoKardex> listarMovimientos(
        int idProducto,
        Connection conexion
) throws SQLException {

    java.util.List<MovimientoKardex> movimientos =
            new java.util.ArrayList<>();

    String sql = """
            SELECT
                k.id_kardex,
                k.id_producto,
                k.id_asiento,
                a.numero_asiento,
                k.fecha,
                k.concepto,
                k.unidades_entrada,
                k.unidades_salida,
                k.unidades_existencia,
                k.costo_unitario,
                k.costo_peps,
                k.saldo_deudor,
                k.saldo_acreedor,
                k.saldo
            FROM kardex k
            LEFT JOIN asientos_contables a
                ON a.id_asiento = k.id_asiento
            WHERE k.id_producto = ?
            ORDER BY k.fecha ASC, k.id_kardex ASC
            """;

    try (PreparedStatement ps = conexion.prepareStatement(sql)) {
        ps.setInt(1, idProducto);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                MovimientoKardex movimiento = new MovimientoKardex();
                movimiento.setIdKardex(rs.getInt("id_kardex"));
                movimiento.setIdProducto(rs.getInt("id_producto"));

                Object idAsientoDb = rs.getObject("id_asiento");
                if (idAsientoDb != null) {
                    movimiento.setIdAsiento(((Number) idAsientoDb).intValue());
                }

                Object numeroAsientoDb = rs.getObject("numero_asiento");
                if (numeroAsientoDb != null) {
                    movimiento.setNumeroAsiento(((Number) numeroAsientoDb).intValue());
                }

                movimiento.setFecha(rs.getDate("fecha").toLocalDate());
                movimiento.setConcepto(rs.getString("concepto"));
                movimiento.setUnidadesEntrada(rs.getBigDecimal("unidades_entrada"));
                movimiento.setUnidadesSalida(rs.getBigDecimal("unidades_salida"));
                movimiento.setUnidadesExistencia(rs.getBigDecimal("unidades_existencia"));
                movimiento.setCostoUnitario(rs.getBigDecimal("costo_unitario"));
                movimiento.setCostoPeps(rs.getBigDecimal("costo_peps"));
                movimiento.setSaldoDeudor(rs.getBigDecimal("saldo_deudor"));
                movimiento.setSaldoAcreedor(rs.getBigDecimal("saldo_acreedor"));
                movimiento.setSaldo(rs.getBigDecimal("saldo"));
                movimientos.add(movimiento);
            }
        }
    }

    return movimientos;
}
}