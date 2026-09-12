package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

public class AperturaPeriodoService {

    private final PeriodoService periodoService;
    private final AsientoDAO asientoDAO;
    private final CalculoIVAService ivaService;
    private final RecalculoKardexService recalculoKardexService;

    public AperturaPeriodoService() {

        periodoService =
                new PeriodoService();

        asientoDAO =
                new AsientoDAO();

        ivaService =
                new CalculoIVAService();

        recalculoKardexService =
                new RecalculoKardexService();
    }

    public String registrarApertura(
            LocalDate fecha,
            BigDecimal efectivoInicial,
            BigDecimal valorInventarioInicial
    ) {

        validarDatos(
                fecha,
                efectivoInicial,
                valorInventarioInicial
        );

        periodoService.validarFecha(
                fecha
        );

        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();

        if (!fecha.equals(
                periodo.getFechaInicio()
        )) {

            throw new IllegalArgumentException(
                    "La apertura debe registrarse en la fecha "
                    + "de inicio del período: "
                    + periodo.getFechaInicio()
                    + "."
            );
        }

        Connection conexion =
                null;

        try {

            conexion =
                    Conexion.conectar();

            if (conexion == null) {

                throw new SQLException(
                        "No se pudo conectar con la base de datos."
                );
            }

            conexion.setAutoCommit(
                    false
            );

            validarPeriodoSinMovimientos(
                    periodo.getIdPeriodo(),
                    conexion
            );

            int idCaja =
                    buscarCuenta(
                            conexion,
                            "1.1.01.01",
                            "Caja"
                    );

            int idInventario =
                    buscarCuenta(
                            conexion,
                            "1.1.03",
                            "Inventario"
                    );

            int idCapital =
                    buscarCuenta(
                            conexion,
                            "3.1.01",
                            "Capital Social"
                    );

            ProductoApertura producto =
                    obtenerProductoActivo(
                            conexion
                    );

            BigDecimal efectivo =
                    efectivoInicial.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            BigDecimal inventario =
                    valorInventarioInicial.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            BigDecimal capital =
                    efectivo
                            .add(
                                    inventario
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            /*
             * El inventario inicial ingresado por el usuario
             * YA ES EL VALOR CONTABLE.
             *
             * Por eso NO se calcula IVA sobre el valor inicial ingresado.
             *
             * El IVA solamente se utiliza aquí para conocer
             * el costo unitario contable del producto y poder
             * calcular automáticamente las unidades del Kardex.
             */

            ResultadoIVA resultadoCostoUnitario =
                    ivaService.calcular(
                            producto.costoCompra
                    );

            BigDecimal costoUnitarioContable =
                    resultadoCostoUnitario
                            .getSubtotal()
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            if (costoUnitarioContable.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new IllegalStateException(
                        "El costo unitario contable del producto "
                        + "debe ser mayor que cero."
                );
            }

            BigDecimal cantidadCalculada =
                    inventario.divide(
                            costoUnitarioContable,
                            0,
                            RoundingMode.HALF_UP
                    );

            int numeroAsiento =
                    asientoDAO.obtenerSiguienteNumero(
                            periodo.getIdPeriodo(),
                            conexion
                    );

            AsientoContable asiento =
                    new AsientoContable();

            asiento.setIdPeriodo(
                    periodo.getIdPeriodo()
            );

            asiento.setIdOperacion(
                    null
            );

            asiento.setNumeroAsiento(
                    numeroAsiento
            );

            asiento.setFecha(
                    fecha
            );

            asiento.setConcepto(
                    "Apertura del período contable"
            );

            asiento.setTipoAsiento(
                    "AUTOMATICO"
            );

            asiento.setEstado(
                    "CONTABILIZADO"
            );

            int idAsiento =
                    asientoDAO.insertarAsiento(
                            asiento,
                            conexion
                    );

            if (efectivo.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                insertarDetalle(
                        idAsiento,
                        idCaja,
                        "Efectivo inicial",
                        efectivo,
                        BigDecimal.ZERO,
                        conexion
                );
            }

            if (inventario.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                insertarDetalle(
                        idAsiento,
                        idInventario,
                        "Inventario inicial",
                        inventario,
                        BigDecimal.ZERO,
                        conexion
                );
            }

            insertarDetalle(
                    idAsiento,
                    idCapital,
                    "Capital Social",
                    BigDecimal.ZERO,
                    capital,
                    conexion
            );

            actualizarInventarioInicial(
                    producto.idProducto,
                    cantidadCalculada,
                    conexion
            );

            recalculoKardexService.recalcularProducto(
                    producto.idProducto,
                    conexion
            );

            conexion.commit();

            BigDecimal valorKardexEstimado =
                    cantidadCalculada
                            .multiply(
                                    costoUnitarioContable
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            return """
                   Apertura registrada correctamente.

                   Asiento N.º %d

                   CONTABILIDAD

                   Caja:
                   $%,.2f

                   Inventario Inicial:
                   $%,.2f

                   Capital Social:
                   $%,.2f

                   KARDEX

                   Costo unitario contable:
                   $%,.2f

                   Unidades calculadas:
                   %s

                   Valor aproximado según Kardex:
                   $%,.2f
                   """
                    .formatted(
                            numeroAsiento,
                            efectivo,
                            inventario,
                            capital,
                            costoUnitarioContable,
                            cantidadCalculada
                                    .stripTrailingZeros()
                                    .toPlainString(),
                            valorKardexEstimado
                    );

        } catch (Exception e) {

            if (conexion != null) {

                try {

                    conexion.rollback();

                } catch (SQLException ex) {

                    System.out.println(
                            "Error en rollback: "
                            + ex.getMessage()
                    );
                }
            }

            throw new RuntimeException(
                    obtenerMensajeError(
                            e
                    ),
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
                            "Error al cerrar conexión: "
                            + e.getMessage()
                    );
                }
            }
        }
    }

    private void validarDatos(
            LocalDate fecha,
            BigDecimal efectivoInicial,
            BigDecimal valorInventarioInicial
    ) {

        if (fecha == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar la fecha de apertura."
            );
        }

        if (efectivoInicial == null) {

            throw new IllegalArgumentException(
                    "Debe ingresar el efectivo inicial."
            );
        }

        if (valorInventarioInicial == null) {

            throw new IllegalArgumentException(
                    "Debe ingresar el valor monetario "
                    + "del inventario inicial."
            );
        }

        if (efectivoInicial.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "El efectivo inicial no puede ser negativo."
            );
        }

        if (valorInventarioInicial.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "El inventario inicial no puede ser negativo."
            );
        }

        if (efectivoInicial.compareTo(
                BigDecimal.ZERO
        ) == 0
                &&
            valorInventarioInicial.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

            throw new IllegalArgumentException(
                    "La apertura debe contener valores iniciales."
            );
        }
    }

    private void validarPeriodoSinMovimientos(
            int idPeriodo,
            Connection conexion
    ) throws SQLException {

        String sql = """
                SELECT COUNT(*) AS cantidad
                FROM asientos_contables
                WHERE id_periodo = ?
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql
                        )
        ) {

            ps.setInt(
                    1,
                    idPeriodo
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()
                        &&
                    rs.getInt(
                            "cantidad"
                    ) > 0) {

                    throw new IllegalStateException(
                            "El período ya contiene asientos contables. "
                            + "No se puede registrar nuevamente la apertura."
                    );
                }
            }
        }
    }

    private int buscarCuenta(
            Connection conexion,
            String codigo,
            String nombre
    ) throws SQLException {

        String sql = """
                SELECT id_cuenta
                FROM catalogo_cuentas
                WHERE codigo = ?
                  AND activo = TRUE
                  AND permite_movimiento = TRUE
                LIMIT 1
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql
                        )
        ) {

            ps.setString(
                    1,
                    codigo
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return rs.getInt(
                            "id_cuenta"
                    );
                }
            }
        }

        throw new SQLException(
                "No se encontró la cuenta "
                + nombre
                + " ("
                + codigo
                + ")."
        );
    }

    private ProductoApertura obtenerProductoActivo(
            Connection conexion
    ) throws SQLException {

        String sql = """
                SELECT
                    id_producto,
                    costo_compra
                FROM productos
                WHERE activo = TRUE
                ORDER BY id_producto
                LIMIT 1
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql
                        );

                ResultSet rs =
                        ps.executeQuery()
        ) {

            if (rs.next()) {

                return new ProductoApertura(
                        rs.getInt(
                                "id_producto"
                        ),
                        rs.getBigDecimal(
                                "costo_compra"
                        )
                );
            }
        }

        throw new SQLException(
                "No existe un producto activo."
        );
    }

    private void insertarDetalle(
            int idAsiento,
            int idCuenta,
            String descripcion,
            BigDecimal debe,
            BigDecimal haber,
            Connection conexion
    ) throws SQLException {

        DetalleAsiento detalle =
                new DetalleAsiento();

        detalle.setIdAsiento(
                idAsiento
        );

        detalle.setIdCuenta(
                idCuenta
        );

        detalle.setDescripcion(
                descripcion
        );

        detalle.setDebe(
                debe.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );

        detalle.setHaber(
                haber.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );

        asientoDAO.insertarDetalle(
                detalle,
                conexion
        );
    }

    private void actualizarInventarioInicial(
            int idProducto,
            BigDecimal cantidad,
            Connection conexion
    ) throws SQLException {

        String sql = """
                UPDATE productos
                SET
                    existencia_inicial = ?,
                    existencia_actual = ?
                WHERE id_producto = ?
                """;

        try (
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql
                        )
        ) {

            ps.setBigDecimal(
                    1,
                    cantidad
            );

            ps.setBigDecimal(
                    2,
                    cantidad
            );

            ps.setInt(
                    3,
                    idProducto
            );

            if (ps.executeUpdate() != 1) {

                throw new SQLException(
                        "No se pudo establecer el inventario inicial."
                );
            }
        }
    }

    private String obtenerMensajeError(
            Throwable error
    ) {

        Throwable actual =
                error;

        String mensaje =
                "Ocurrió un error al registrar la apertura.";

        while (actual != null) {

            if (actual.getMessage() != null
                    &&
                !actual.getMessage().isBlank()) {

                mensaje =
                        actual.getMessage();
            }

            actual =
                    actual.getCause();
        }

        return mensaje;
    }

    private static class ProductoApertura {

        private final int idProducto;
        private final BigDecimal costoCompra;

        private ProductoApertura(
                int idProducto,
                BigDecimal costoCompra
        ) {

            this.idProducto =
                    idProducto;

            this.costoCompra =
                    costoCompra;
        }
    }
}