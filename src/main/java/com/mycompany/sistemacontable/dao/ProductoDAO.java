package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductoDAO {

    public Producto obtenerProductoActivo() {

        String sql = """
                SELECT
                    id_producto,
                    codigo,
                    nombre,
                    descripcion,
                    costo_compra,
                    precio_venta,
                    existencia_inicial,
                    existencia_actual,
                    activo
                FROM productos
                WHERE activo = TRUE
                ORDER BY id_producto
                LIMIT 1
                """;

        try (
                Connection conexion = Conexion.conectar();
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            if (rs.next()) {

                Producto producto = new Producto();

                producto.setIdProducto(
                        rs.getInt("id_producto")
                );

                producto.setCodigo(
                        rs.getString("codigo")
                );

                producto.setNombre(
                        rs.getString("nombre")
                );

                producto.setDescripcion(
                        rs.getString("descripcion")
                );

                producto.setCostoCompra(
                        rs.getBigDecimal("costo_compra")
                );

                producto.setPrecioVenta(
                        rs.getBigDecimal("precio_venta")
                );

                producto.setExistenciaInicial(
                        rs.getBigDecimal("existencia_inicial")
                );

                producto.setExistenciaActual(
                        rs.getBigDecimal("existencia_actual")
                );

                producto.setActivo(
                        rs.getBoolean("activo")
                );

                return producto;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener producto: "
                    + e.getMessage()
            );
        }

        return null;
    }
    
    public Producto buscarPorId(
        int idProducto,
        Connection conexion
) throws SQLException {

    String sql = """
            SELECT
                id_producto,
                codigo,
                nombre,
                descripcion,
                costo_compra,
                precio_venta,
                existencia_inicial,
                existencia_actual,
                activo
            FROM productos
            WHERE id_producto = ?
            LIMIT 1
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

                Producto producto =
                        new Producto();

                producto.setIdProducto(
                        rs.getInt("id_producto")
                );

                producto.setCodigo(
                        rs.getString("codigo")
                );

                producto.setNombre(
                        rs.getString("nombre")
                );

                producto.setDescripcion(
                        rs.getString("descripcion")
                );

                producto.setCostoCompra(
                        rs.getBigDecimal("costo_compra")
                );

                producto.setPrecioVenta(
                        rs.getBigDecimal("precio_venta")
                );

                producto.setExistenciaInicial(
                        rs.getBigDecimal("existencia_inicial")
                );

                producto.setExistenciaActual(
                        rs.getBigDecimal("existencia_actual")
                );

                producto.setActivo(
                        rs.getBoolean("activo")
                );

                return producto;
            }
        }
    }

    return null;
}


public void actualizarExistencia(
        int idProducto,
        java.math.BigDecimal nuevaExistencia,
        Connection conexion
) throws SQLException {

    String sql = """
            UPDATE productos
            SET existencia_actual = ?
            WHERE id_producto = ?
            """;

    try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
    ) {

        ps.setBigDecimal(
                1,
                nuevaExistencia
        );

        ps.setInt(
                2,
                idProducto
        );

        int filas =
                ps.executeUpdate();

        if (filas != 1) {

            throw new SQLException(
                    "No se pudo actualizar la existencia del producto."
            );
        }
    }
}
    
}