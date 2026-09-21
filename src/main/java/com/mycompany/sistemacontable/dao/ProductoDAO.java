package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    private Producto mapear(ResultSet rs) throws SQLException {
        Producto p = new Producto();
        p.setIdProducto(rs.getInt("id_producto"));
        p.setCodigo(rs.getString("codigo"));
        p.setNombre(rs.getString("nombre"));
        p.setDescripcion(rs.getString("descripcion"));
        p.setCostoCompra(rs.getBigDecimal("costo_compra"));
        p.setPrecioVenta(rs.getBigDecimal("precio_venta"));
        p.setCostoInicial(rs.getBigDecimal("costo_inicial"));
        p.setValorInventarioInicial(rs.getBigDecimal("valor_inventario_inicial"));
        p.setExistenciaInicial(rs.getBigDecimal("existencia_inicial"));
        p.setExistenciaActual(rs.getBigDecimal("existencia_actual"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }

    private String columnas() {
        return """
                id_producto, codigo, nombre, descripcion,
                costo_compra, precio_venta, costo_inicial,
                valor_inventario_inicial, existencia_inicial,
                existencia_actual, activo
                """;
    }

    public Producto obtenerProductoActivo() {
        String sql = "SELECT " + columnas() + " FROM productos WHERE activo=TRUE ORDER BY id_producto LIMIT 1";
        try (Connection c = Conexion.conectar(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? mapear(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo obtener el producto: " + e.getMessage(), e);
        }
    }

    public Producto buscarPorId(int idProducto, Connection c) throws SQLException {
        String sql = "SELECT " + columnas() + " FROM productos WHERE id_producto=? LIMIT 1";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1,idProducto);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? mapear(rs) : null; }
        }
    }

    /**
     * Obtiene y bloquea la fila del producto dentro de la transacción actual.
     * Se usa antes de cualquier salida de inventario para impedir que dos
     * ventas simultáneas consuman las mismas unidades. El bloqueo se libera
     * únicamente con COMMIT o ROLLBACK.
     */
    public Producto buscarPorIdParaActualizar(int idProducto, Connection c) throws SQLException {
        String sql = "SELECT " + columnas() + " FROM productos WHERE id_producto=? LIMIT 1 FOR UPDATE";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public List<Producto> listarProductosActivos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT " + columnas() + " FROM productos WHERE activo=TRUE ORDER BY codigo,nombre";
        try (Connection c=Conexion.conectar(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            while(rs.next()) lista.add(mapear(rs));
        } catch(SQLException e){ throw new RuntimeException("No se pudieron cargar los productos: "+e.getMessage(),e); }
        return lista;
    }

    /**
     * Lista el catálogo completo para administración. Los productos inactivos
     * se conservan porque pueden tener movimientos históricos asociados.
     */
    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT " + columnas() + " FROM productos ORDER BY activo DESC,codigo,nombre";
        try (Connection c=Conexion.conectar(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            while(rs.next()) lista.add(mapear(rs));
        } catch(SQLException e){
            throw new RuntimeException("No se pudieron cargar los productos: "+e.getMessage(),e);
        }
        return lista;
    }

    /**
     * Activa o desactiva un producto sin eliminarlo. Un producto con
     * existencias no puede desactivarse para evitar dejar stock inaccesible
     * para ventas, devoluciones y Kardex. El cambio queda auditado.
     */
    public void cambiarActivo(int idProducto, boolean nuevoEstado) {
        Connection c = null;
        try {
            c = Conexion.conectar();
            c.setAutoCommit(false);

            Producto actual = buscarPorIdParaActualizar(idProducto, c);
            if (actual == null) {
                throw new SQLException("No se encontró el producto seleccionado.");
            }

            if (actual.isActivo() == nuevoEstado) {
                c.commit();
                return;
            }

            BigDecimal existencia = actual.getExistenciaActual() == null
                    ? BigDecimal.ZERO : actual.getExistenciaActual();

            if (!nuevoEstado && existencia.compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalStateException(
                        "No se puede desactivar el producto porque todavía tiene "
                        + existencia.stripTrailingZeros().toPlainString()
                        + " unidad(es) en existencia. Deja la existencia en 0 antes de desactivarlo."
                );
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "UPDATE productos SET activo=? WHERE id_producto=?")) {
                ps.setBoolean(1, nuevoEstado);
                ps.setInt(2, idProducto);
                if (ps.executeUpdate() != 1) {
                    throw new SQLException("No se pudo cambiar el estado del producto.");
                }
            }

            new AuditoriaDAO().registrarSiFalta(
                    c,
                    "PRODUCTO",
                    idProducto,
                    "MODIFICO",
                    "Estado: " + (actual.isActivo() ? "ACTIVO" : "INACTIVO"),
                    "Estado: " + (nuevoEstado ? "ACTIVO" : "INACTIVO")
            );

            c.commit();
        } catch (Exception e) {
            if (c != null) {
                try { c.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException(
                    e.getMessage() == null ? "No se pudo cambiar el estado del producto." : e.getMessage(),
                    e
            );
        } finally {
            if (c != null) {
                try { c.setAutoCommit(true); } catch (SQLException ignored) {}
                try { c.close(); } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Un producto nuevo entra al catálogo sin inventario. La existencia y la
     * valorización nacen exclusivamente de compras registradas en el período y
     * del Kardex PEPS. Los campos de inventario inicial se conservan en la BD
     * solo por compatibilidad con instalaciones antiguas.
     */
    public void insertar(Producto p) {
        String sql="""
            INSERT INTO productos(
                codigo,nombre,descripcion,costo_compra,precio_venta,
                costo_inicial,valor_inventario_inicial,existencia_inicial,
                existencia_actual,activo
            ) VALUES(?,?,?,?,?,0,0,0,0,TRUE)
            """;
        try(Connection c=Conexion.conectar(); PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,p.getCodigo());
            ps.setString(2,p.getNombre());
            ps.setString(3,p.getDescripcion());
            ps.setBigDecimal(4,nvl(p.getCostoCompra()));
            ps.setBigDecimal(5,nvl(p.getPrecioVenta()));
            ps.executeUpdate();
        }catch(SQLException e){ throw new RuntimeException("No se pudo registrar el producto: "+e.getMessage(),e); }
    }

    /**
     * Editar la ficha del producto nunca revaloriza ni reinicia inventario.
     * Solo se modifican datos maestros y valores de referencia.
     */
    public void actualizar(Producto p) {
        String sql="""
            UPDATE productos
               SET codigo=?, nombre=?, descripcion=?, costo_compra=?, precio_venta=?
             WHERE id_producto=?
            """;
        try(Connection c=Conexion.conectar(); PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1,p.getCodigo());
            ps.setString(2,p.getNombre());
            ps.setString(3,p.getDescripcion());
            ps.setBigDecimal(4,nvl(p.getCostoCompra()));
            ps.setBigDecimal(5,nvl(p.getPrecioVenta()));
            ps.setInt(6,p.getIdProducto());
            if(ps.executeUpdate()!=1) throw new SQLException("No se encontró el producto que se desea actualizar.");
        }catch(SQLException e){ throw new RuntimeException("No se pudo actualizar el producto: "+e.getMessage(),e); }
    }

    private BigDecimal nvl(BigDecimal v){ return v==null?BigDecimal.ZERO:v; }

    public void actualizarExistencia(int idProducto, BigDecimal nueva, Connection c) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE productos SET existencia_actual=? WHERE id_producto=?")){
            ps.setBigDecimal(1,nueva); ps.setInt(2,idProducto);
            if(ps.executeUpdate()!=1) throw new SQLException("No se pudo actualizar la existencia del producto.");
        }
    }

    public void actualizarCostoCompra(int idProducto, BigDecimal nuevoCosto, Connection c) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE productos SET costo_compra=? WHERE id_producto=? AND activo=TRUE")){
            ps.setBigDecimal(1,nuevoCosto); ps.setInt(2,idProducto); ps.executeUpdate();
        }
    }

    public void actualizarPrecioVenta(int idProducto, BigDecimal nuevoPrecio, Connection c) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement("UPDATE productos SET precio_venta=? WHERE id_producto=? AND activo=TRUE")){
            ps.setBigDecimal(1,nuevoPrecio); ps.setInt(2,idProducto); ps.executeUpdate();
        }
    }

    public BigDecimal obtenerValorInventarioInicialTotal() {
        String sql="SELECT COALESCE(SUM(valor_inventario_inicial),0) total FROM productos WHERE activo=TRUE";
        try(Connection c=Conexion.conectar(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()){
            return rs.next()?rs.getBigDecimal("total"):BigDecimal.ZERO;
        }catch(SQLException e){ throw new RuntimeException("No se pudo calcular el inventario inicial: "+e.getMessage(),e); }
    }
}
