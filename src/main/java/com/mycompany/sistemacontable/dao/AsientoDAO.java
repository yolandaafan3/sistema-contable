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


    public AsientoContable buscarPorId(int idAsiento) {
        String sql = "SELECT id_asiento,id_periodo,id_operacion,numero_asiento,fecha,concepto,tipo_asiento,estado FROM asientos_contables WHERE id_asiento=?";
        try (Connection cn = com.mycompany.sistemacontable.Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAsiento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AsientoContable a = new AsientoContable();
                    a.setIdAsiento(rs.getInt("id_asiento"));
                    a.setIdPeriodo(rs.getInt("id_periodo"));
                    int op = rs.getInt("id_operacion");
                    a.setIdOperacion(rs.wasNull() ? null : op);
                    a.setNumeroAsiento(rs.getInt("numero_asiento"));
                    a.setFecha(rs.getDate("fecha").toLocalDate());
                    a.setConcepto(rs.getString("concepto"));
                    a.setTipoAsiento(rs.getString("tipo_asiento"));
                    a.setEstado(rs.getString("estado"));
                    return a;
                }
            }
        } catch (SQLException e) { throw new RuntimeException("No se pudo consultar el asiento: " + e.getMessage(), e); }
        return null;
    }

    public java.util.List<DetalleAsiento> listarDetalles(int idAsiento) {
        java.util.List<DetalleAsiento> lista = new java.util.ArrayList<>();
        String sql = "SELECT id_detalle,id_asiento,id_cuenta,descripcion,debe,haber FROM detalle_asientos WHERE id_asiento=? ORDER BY id_detalle";
        try (Connection cn = com.mycompany.sistemacontable.Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idAsiento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DetalleAsiento(rs.getInt("id_detalle"), rs.getInt("id_asiento"), rs.getInt("id_cuenta"),
                            rs.getString("descripcion"), rs.getBigDecimal("debe"), rs.getBigDecimal("haber")));
                }
            }
        } catch (SQLException e) { throw new RuntimeException("No se pudieron consultar los detalles del asiento: " + e.getMessage(), e); }
        return lista;
    }

    public void actualizarCabecera(int idAsiento, java.time.LocalDate fecha, String concepto, Connection conexion) throws SQLException {
        String sql = "UPDATE asientos_contables SET fecha=?, concepto=? WHERE id_asiento=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha)); ps.setString(2, concepto); ps.setInt(3, idAsiento); ps.executeUpdate();
        }
    }

    public void eliminarDetalles(int idAsiento, Connection conexion) throws SQLException {
        try (PreparedStatement ps = conexion.prepareStatement("DELETE FROM detalle_asientos WHERE id_asiento=?")) {
            ps.setInt(1, idAsiento); ps.executeUpdate();
        }
    }

}