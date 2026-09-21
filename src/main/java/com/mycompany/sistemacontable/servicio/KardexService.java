package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.KardexDAO;
import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.MovimientoKardex;
import com.mycompany.sistemacontable.modelo.CapaPeps;
import com.mycompany.sistemacontable.modelo.Producto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class KardexService {

    private final KardexDAO kardexDAO;
    private final ProductoDAO productoDAO;
    private final RecalculoKardexService recalculoKardexService;

    public KardexService() {

        kardexDAO =
                new KardexDAO();

        productoDAO =
                new ProductoDAO();

        recalculoKardexService =
                new RecalculoKardexService();
    }

    public Producto obtenerProductoActivo() {

        return productoDAO.obtenerProductoActivo();
    }

    public List<Producto> listarProductosActivos() {

        return productoDAO.listarProductosActivos();
    }

    public void recalcular() {

        Producto producto =
                obtenerProductoActivo();

        if (producto == null) {
            return;
        }

        recalcular(
                producto.getIdProducto()
        );
    }

    public void recalcular(
            int idProducto
    ) {

        Connection conexion =
                null;

        try {

            conexion =
                    Conexion.conectar();

            if (conexion == null) {

                throw new SQLException(
                        "No se pudo conectar con MySQL."
                );
            }

            conexion.setAutoCommit(
                    false
            );

            recalculoKardexService.recalcularProducto(
                    idProducto,
                    conexion
            );

            conexion.commit();

        } catch (Exception e) {

            if (conexion != null) {

                try {

                    conexion.rollback();

                } catch (SQLException ex) {

                    System.out.println(
                            "Error al hacer rollback del Kardex: "
                            + ex.getMessage()
                    );
                }
            }

            throw new RuntimeException(
                    "No se pudo recalcular el Kardex: "
                    + e.getMessage(),
                    e
            );

        } finally {

            if (conexion != null) {

                try {

                    conexion.setAutoCommit(
                            true
                    );

                    conexion.close();

                } catch (SQLException e) {

                    System.out.println(
                            "Error al cerrar conexión del Kardex: "
                            + e.getMessage()
                    );
                }
            }
        }
    }

    public List<MovimientoKardex> obtenerMovimientos() {

        Producto producto =
                obtenerProductoActivo();

        if (producto == null) {

            return Collections.emptyList();
        }

        return obtenerMovimientos(
                producto.getIdProducto()
        );
    }

    public List<MovimientoKardex> obtenerMovimientos(
            int idProducto
    ) {

        try (
                Connection conexion =
                        Conexion.conectar()
        ) {

            if (conexion == null) {

                throw new SQLException(
                        "No se pudo conectar con MySQL."
                );
            }

            return kardexDAO.listarMovimientos(
                    idProducto,
                    conexion
            );

        } catch (SQLException e) {

            throw new RuntimeException(
                    "No se pudo consultar el Kardex: "
                    + e.getMessage(),
                    e
            );
        }
    }

    public BigDecimal obtenerSaldoPeps() {

        Producto producto =
                obtenerProductoActivo();

        if (producto == null) {

            return BigDecimal.ZERO;
        }

        return obtenerSaldoPeps(
                producto.getIdProducto()
        );
    }

    public BigDecimal obtenerSaldoPeps(
            int idProducto
    ) {

        try (
                Connection conexion =
                        Conexion.conectar()
        ) {

            if (conexion == null) {

                throw new SQLException(
                        "No se pudo conectar con MySQL."
                );
            }

            return kardexDAO.obtenerSaldoPeps(
                    idProducto,
                    conexion
            );

        } catch (SQLException e) {

            throw new RuntimeException(
                    "No se pudo consultar el saldo PEPS: "
                    + e.getMessage(),
                    e
            );
        }
    }

    public BigDecimal obtenerSaldoPepsActualizado() {
        BigDecimal total = BigDecimal.ZERO;
        for (Producto producto : listarProductosActivos()) {
            recalcular(producto.getIdProducto());
            total = total.add(obtenerSaldoPeps(producto.getIdProducto()));
        }
        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal obtenerSaldoPepsActualizado(
            int idProducto
    ) {

        recalcular(
                idProducto
        );

        return obtenerSaldoPeps(
                idProducto
        );
    }

    public List<CapaPeps> obtenerCapas(int idProducto) {
        try (Connection conexion = Conexion.conectar()) {
            if (conexion == null) throw new SQLException("No se pudo conectar con MySQL.");
            return kardexDAO.listarCapasTodas(idProducto, conexion);
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron consultar los lotes PEPS: " + e.getMessage(), e);
        }
    }
}