package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.KardexDAO;
import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.MovimientoKardex;
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


    /**
     * Reconstruye Kardex y capas PEPS con las reglas actuales del proyecto.
     * Se usa al abrir/actualizar Kardex y antes de calcular estados financieros,
     * para que los saldos reflejen el costo unitario redondeado a centavos.
     */
    public void recalcular() {

        Producto producto =
                obtenerProductoActivo();

        if (producto == null) {
            return;
        }


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
                    producto.getIdProducto(),
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
                    producto.getIdProducto(),
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
                    producto.getIdProducto(),
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

        recalcular();

        return obtenerSaldoPeps();
    }
}
