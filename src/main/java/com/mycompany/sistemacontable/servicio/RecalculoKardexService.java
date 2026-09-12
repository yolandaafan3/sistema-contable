package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.KardexDAO;
import com.mycompany.sistemacontable.dao.OperacionInventarioDAO;
import com.mycompany.sistemacontable.dao.ProductoDAO;

import com.mycompany.sistemacontable.modelo.CapaPeps;
import com.mycompany.sistemacontable.modelo.MovimientoKardex;
import com.mycompany.sistemacontable.modelo.OperacionInventarioRecalculo;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.List;

public class RecalculoKardexService {

    private final ProductoDAO productoDAO;
    private final KardexDAO kardexDAO;
    private final OperacionInventarioDAO operacionInventarioDAO;

    private final CalculoIVAService ivaService;

    public RecalculoKardexService() {

        productoDAO =
                new ProductoDAO();

        kardexDAO =
                new KardexDAO();

        operacionInventarioDAO =
                new OperacionInventarioDAO();

        ivaService =
                new CalculoIVAService();
    }


    public void recalcularProducto(
            int idProducto,
            Connection conexion
    ) throws SQLException {

        Producto producto =
                productoDAO.buscarPorId(
                        idProducto,
                        conexion
                );


        if (producto == null) {

            throw new SQLException(
                    "No se encontro el producto para recalcular Kardex."
            );
        }


        // =====================================================
        // OPERACIONES ORDENADAS CRONOLOGICAMENTE
        // =====================================================

        List<OperacionInventarioRecalculo> operaciones =
                operacionInventarioDAO.listarPorProducto(
                        idProducto,
                        conexion
                );


        // =====================================================
        // LIMPIAR KARDEX Y CAPAS PEPS
        // =====================================================

        kardexDAO.eliminarCapasProducto(
                idProducto,
                conexion
        );


        kardexDAO.eliminarMovimientosProducto(
                idProducto,
                conexion
        );


        // =====================================================
        // VARIABLES DE CONTROL
        // =====================================================

        BigDecimal existencia =
                BigDecimal.ZERO;


        BigDecimal saldoInventario =
                BigDecimal.ZERO;


        BigDecimal comprasPendientesDevolucion =
                BigDecimal.ZERO;


        BigDecimal ventasPendientesDevolucion =
                BigDecimal.ZERO;


        BigDecimal ultimoCostoUnitarioVenta =
                null;


        // =====================================================
        // INVENTARIO INICIAL
        // =====================================================

        BigDecimal existenciaInicial =
                normalizar(
                        producto.getExistenciaInicial()
                );


        if (existenciaInicial.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            LocalDate fechaInicio =
                    operacionInventarioDAO
                            .obtenerFechaInicioSistema(
                                    conexion
                            );


            if (fechaInicio == null) {

                throw new SQLException(
                        "No existe un periodo contable para "
                        + "registrar el inventario inicial."
                );
            }


            DatosInventarioInicial datosInicial =
                    obtenerDatosInventarioInicial(
                            fechaInicio,
                            conexion
                    );


            BigDecimal montoContableInicial =
                    datosInicial.monto;


            if (montoContableInicial == null
                    ||
                montoContableInicial.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

                ResultadoIVA resultadoUnitario =
                        ivaService.calcular(
                                producto.getCostoCompra()
                        );


                BigDecimal costoUnitarioFallback =
                        resultadoUnitario
                                .getSubtotal()
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                );


                montoContableInicial =
                        costoUnitarioFallback
                                .multiply(
                                        existenciaInicial
                                )
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                );
            }


            /*
             * El Kardex trabaja el costo unitario redondeado a centavos.
             * El valor contable de apertura permanece exactamente como fue
             * registrado por el usuario; cualquier diferencia de redondeo
             * corresponde únicamente al cálculo auxiliar del Kardex.
             */

            BigDecimal costoUnitarioInicial =
                    montoContableInicial.divide(
                            existenciaInicial,
                            2,
                            RoundingMode.HALF_UP
                    );


            BigDecimal costoInicial =
                    costoUnitarioInicial
                            .multiply(
                                    existenciaInicial
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


            existencia =
                    existenciaInicial;


            saldoInventario =
                    costoInicial;


            MovimientoKardex movimientoInicial =
                    new MovimientoKardex();


            movimientoInicial.setIdProducto(
                    idProducto
            );


            movimientoInicial.setIdAsiento(
                    datosInicial.idAsiento
            );


            movimientoInicial.setFecha(
                    fechaInicio
            );


            movimientoInicial.setConcepto(
                    "Inventario inicial"
            );


            movimientoInicial.setUnidadesEntrada(
                    existenciaInicial
            );


            movimientoInicial.setUnidadesSalida(
                    BigDecimal.ZERO
            );


            movimientoInicial.setUnidadesExistencia(
                    existencia
            );


            movimientoInicial.setCostoUnitario(
                    costoUnitarioInicial
            );


            movimientoInicial.setCostoPeps(
                    costoUnitarioInicial
            );


            movimientoInicial.setSaldoDeudor(
                    costoInicial
            );


            movimientoInicial.setSaldoAcreedor(
                    BigDecimal.ZERO
            );


            movimientoInicial.setSaldo(
                    saldoInventario
            );


            int idKardexInicial =
                    kardexDAO.insertarMovimiento(
                            movimientoInicial,
                            conexion
                    );


            kardexDAO.insertarCapaPeps(
                    idProducto,
                    idKardexInicial,
                    fechaInicio,
                    existenciaInicial,
                    costoUnitarioInicial,
                    conexion
            );
        }


        // =====================================================
        // RECORRER OPERACIONES CRONOLOGICAMENTE
        // =====================================================

        for (
                OperacionInventarioRecalculo operacion
                : operaciones
        ) {

            BigDecimal cantidad =
                    normalizar(
                            operacion.getCantidad()
                    );


            if (cantidad.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new SQLException(
                        "La operacion "
                        + operacion.getIdOperacion()
                        + " tiene una cantidad invalida."
                );
            }


            switch (
                    operacion.getTipoOperacion()
            ) {

                // =================================================
                // COMPRA
                // =================================================

                case "COMPRA" -> {

                    comprasPendientesDevolucion =
                            comprasPendientesDevolucion.add(
                                    cantidad
                            );


                    /*
                     * El costo unitario del Kardex se redondea a 2 decimales.
                     * El valor de entrada se calcula utilizando ese costo
                     * unitario redondeado y la cantidad de la operación.
                     */

                    BigDecimal costoUnitario =
                            normalizar(
                                    operacion.getSubtotal()
                            )
                            .divide(
                                    cantidad,
                                    2,
                                    RoundingMode.HALF_UP
                            );


                    BigDecimal costoEntrada =
                            costoUnitario
                                    .multiply(
                                            cantidad
                                    )
                                    .setScale(
                                            2,
                                            RoundingMode.HALF_UP
                                    );


                    existencia =
                            existencia.add(
                                    cantidad
                            );


                    saldoInventario =
                            saldoInventario.add(
                                    costoEntrada
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


                    int idKardex =
                            registrarEntrada(
                                    operacion,
                                    idProducto,
                                    cantidad,
                                    existencia,
                                    costoUnitario,
                                    costoEntrada,
                                    saldoInventario,
                                    conexion
                            );


                    kardexDAO.insertarCapaPeps(
                            idProducto,
                            idKardex,
                            operacion.getFecha(),
                            cantidad,
                            costoUnitario,
                            conexion
                    );
                }


                // =================================================
                // DEVOLUCION SOBRE VENTA
                // =================================================

                case "DEVOLUCION_VENTA" -> {

                    if (ventasPendientesDevolucion.compareTo(
                            cantidad
                    ) < 0) {

                        throw new IllegalStateException(
                                "La devolucion sobre venta del "
                                + operacion.getFecha()
                                + " no es valida. "
                                + "Cantidad devuelta: "
                                + cantidad
                                + " | Cantidad vendida pendiente "
                                + "de devolucion: "
                                + ventasPendientesDevolucion
                        );
                    }


                    ventasPendientesDevolucion =
                            ventasPendientesDevolucion.subtract(
                                    cantidad
                            );


                    /*
                     * La devolución sobre venta regresa al inventario
                     * al costo unitario con el que salió la mercadería.
                     */

                    BigDecimal costoUnitario =
                            ultimoCostoUnitarioVenta;


                    if (costoUnitario == null
                            ||
                        costoUnitario.compareTo(
                                BigDecimal.ZERO
                        ) <= 0) {

                        ResultadoIVA resultadoUnitario =
                                ivaService.calcular(
                                        producto.getCostoCompra()
                                );


                        costoUnitario =
                                resultadoUnitario
                                        .getSubtotal()
                                        .setScale(
                                                2,
                                                RoundingMode.HALF_UP
                                        );
                    }


                    BigDecimal costoEntrada =
                            costoUnitario
                                    .multiply(
                                            cantidad
                                    )
                                    .setScale(
                                            2,
                                            RoundingMode.HALF_UP
                                    );


                    existencia =
                            existencia.add(
                                    cantidad
                            );


                    saldoInventario =
                            saldoInventario.add(
                                    costoEntrada
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


                    int idKardex =
                            registrarEntrada(
                                    operacion,
                                    idProducto,
                                    cantidad,
                                    existencia,
                                    costoUnitario,
                                    costoEntrada,
                                    saldoInventario,
                                    conexion
                            );


                    kardexDAO.insertarCapaPeps(
                            idProducto,
                            idKardex,
                            operacion.getFecha(),
                            cantidad,
                            costoUnitario,
                            conexion
                    );
                }


                // =================================================
                // VENTA / DEVOLUCION SOBRE COMPRA
                // =================================================

                case "VENTA",
                     "DEVOLUCION_COMPRA" -> {

                    // =============================================
                    // CONTROL DE DEVOLUCIONES
                    // =============================================

                    if ("VENTA".equals(
                            operacion.getTipoOperacion()
                    )) {

                        ventasPendientesDevolucion =
                                ventasPendientesDevolucion.add(
                                        cantidad
                                );

                    } else {

                        if (comprasPendientesDevolucion.compareTo(
                                cantidad
                        ) < 0) {

                            throw new IllegalStateException(
                                    "La devolucion sobre compra del "
                                    + operacion.getFecha()
                                    + " no es valida. "
                                    + "Cantidad devuelta: "
                                    + cantidad
                                    + " | Cantidad comprada pendiente "
                                    + "de devolucion: "
                                    + comprasPendientesDevolucion
                            );
                        }


                        comprasPendientesDevolucion =
                                comprasPendientesDevolucion.subtract(
                                        cantidad
                                );
                    }


                    // =============================================
                    // VALIDAR EXISTENCIA
                    // =============================================

                    if (existencia.compareTo(
                            cantidad
                    ) < 0) {

                        throw new IllegalStateException(
                                "Inventario insuficiente al procesar "
                                + operacion.getTipoOperacion()
                                + " del "
                                + operacion.getFecha()
                                + ". Existencia: "
                                + existencia
                                + " | Salida: "
                                + cantidad
                        );
                    }


                    // =============================================
                    // CONSUMIR PEPS
                    // =============================================

                    BigDecimal costoSalida =
                            consumirPeps(
                                    idProducto,
                                    operacion.getFecha(),
                                    cantidad,
                                    conexion
                            );


                    BigDecimal costoUnitarioSalida =
                            costoSalida.divide(
                                    cantidad,
                                    2,
                                    RoundingMode.HALF_UP
                            );


                    if ("VENTA".equals(
                            operacion.getTipoOperacion()
                    )) {

                        ultimoCostoUnitarioVenta =
                                costoUnitarioSalida;
                    }


                    // =============================================
                    // ACTUALIZAR EXISTENCIA
                    // =============================================

                    existencia =
                            existencia.subtract(
                                    cantidad
                            );


                    saldoInventario =
                            saldoInventario.subtract(
                                    costoSalida
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


                    if (saldoInventario.abs()
                            .compareTo(
                                    new BigDecimal("0.01")
                            ) < 0) {

                        saldoInventario =
                                BigDecimal.ZERO
                                        .setScale(
                                                2,
                                                RoundingMode.HALF_UP
                                        );
                    }


                    // =============================================
                    // REGISTRAR SALIDA
                    // =============================================

                    registrarSalida(
                            operacion,
                            idProducto,
                            cantidad,
                            existencia,
                            costoUnitarioSalida,
                            costoSalida,
                            saldoInventario,
                            conexion
                    );
                }


                default -> {

                    throw new SQLException(
                            "Tipo de operacion de inventario "
                            + "no reconocido: "
                            + operacion.getTipoOperacion()
                    );
                }
            }
        }


        // =====================================================
        // ACTUALIZAR EXISTENCIA ACTUAL DEL PRODUCTO
        // =====================================================

        productoDAO.actualizarExistencia(
                idProducto,
                existencia,
                conexion
        );
    }


    // =========================================================
    // REGISTRAR ENTRADA
    // =========================================================

    private int registrarEntrada(
            OperacionInventarioRecalculo operacion,
            int idProducto,
            BigDecimal cantidad,
            BigDecimal existencia,
            BigDecimal costoUnitario,
            BigDecimal costoEntrada,
            BigDecimal saldo,
            Connection conexion
    ) throws SQLException {

        MovimientoKardex movimiento =
                new MovimientoKardex();


        movimiento.setIdProducto(
                idProducto
        );


        movimiento.setIdAsiento(
                operacion.getIdAsiento()
        );


        movimiento.setFecha(
                operacion.getFecha()
        );


        movimiento.setConcepto(
                obtenerConcepto(
                        operacion
                )
        );


        movimiento.setUnidadesEntrada(
                cantidad
        );


        movimiento.setUnidadesSalida(
                BigDecimal.ZERO
        );


        movimiento.setUnidadesExistencia(
                existencia
        );


        movimiento.setCostoUnitario(
                costoUnitario
        );


        movimiento.setCostoPeps(
                costoUnitario
        );


        movimiento.setSaldoDeudor(
                costoEntrada
        );


        movimiento.setSaldoAcreedor(
                BigDecimal.ZERO
        );


        movimiento.setSaldo(
                saldo
        );


        return kardexDAO.insertarMovimiento(
                movimiento,
                conexion
        );
    }


    // =========================================================
    // REGISTRAR SALIDA
    // =========================================================

    private void registrarSalida(
            OperacionInventarioRecalculo operacion,
            int idProducto,
            BigDecimal cantidad,
            BigDecimal existencia,
            BigDecimal costoUnitario,
            BigDecimal costoSalida,
            BigDecimal saldo,
            Connection conexion
    ) throws SQLException {

        MovimientoKardex movimiento =
                new MovimientoKardex();


        movimiento.setIdProducto(
                idProducto
        );


        movimiento.setIdAsiento(
                operacion.getIdAsiento()
        );


        movimiento.setFecha(
                operacion.getFecha()
        );


        movimiento.setConcepto(
                obtenerConcepto(
                        operacion
                )
        );


        movimiento.setUnidadesEntrada(
                BigDecimal.ZERO
        );


        movimiento.setUnidadesSalida(
                cantidad
        );


        movimiento.setUnidadesExistencia(
                existencia
        );


        movimiento.setCostoUnitario(
                costoUnitario
        );


        movimiento.setCostoPeps(
                costoUnitario
        );


        movimiento.setSaldoDeudor(
                BigDecimal.ZERO
        );


        movimiento.setSaldoAcreedor(
                costoSalida
        );


        movimiento.setSaldo(
                saldo
        );


        kardexDAO.insertarMovimiento(
                movimiento,
                conexion
        );
    }


    // =========================================================
    // CONSUMIR CAPAS PEPS
    // =========================================================

    private BigDecimal consumirPeps(
            int idProducto,
            LocalDate fecha,
            BigDecimal cantidad,
            Connection conexion
    ) throws SQLException {

        List<CapaPeps> capas =
                kardexDAO.listarCapasDisponibles(
                        idProducto,
                        fecha,
                        conexion
                );


        BigDecimal pendiente =
                cantidad;


        BigDecimal costoSalida =
                BigDecimal.ZERO;


        for (CapaPeps capa : capas) {

            if (pendiente.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                break;
            }


            BigDecimal disponible =
                    capa.getCantidadDisponible();


            BigDecimal tomar =
                    disponible.min(
                            pendiente
                    );


            BigDecimal costo =
                    tomar.multiply(
                            capa.getCostoUnitario()
                    );


            costoSalida =
                    costoSalida.add(
                            costo
                    );


            BigDecimal restante =
                    disponible.subtract(
                            tomar
                    );


            kardexDAO.actualizarCantidadDisponible(
                    capa.getIdCapa(),
                    restante,
                    conexion
            );


            pendiente =
                    pendiente.subtract(
                            tomar
                    );
        }


        if (pendiente.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            throw new IllegalStateException(
                    "No existen suficientes capas PEPS "
                    + "para completar una salida de "
                    + cantidad
                    + " unidades en fecha "
                    + fecha
                    + "."
            );
        }


        return costoSalida.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =========================================================
    // DATOS DEL INVENTARIO INICIAL
    // =========================================================

    private DatosInventarioInicial obtenerDatosInventarioInicial(
            LocalDate fechaInicio,
            Connection conexion
    ) throws SQLException {

        String sql = """
                SELECT
                    ac.id_asiento,
                    da.debe AS monto

                FROM detalle_asientos da

                INNER JOIN asientos_contables ac
                    ON ac.id_asiento = da.id_asiento

                INNER JOIN catalogo_cuentas cc
                    ON cc.id_cuenta = da.id_cuenta

                WHERE cc.rol_reporte = 'INVENTARIO'
                  AND ac.estado = 'CONTABILIZADO'
                  AND ac.fecha = ?
                  AND da.debe > 0

                ORDER BY
                    ac.fecha ASC,
                    ac.numero_asiento ASC,
                    da.id_detalle ASC

                LIMIT 1
                """;


        try (
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql
                        )
        ) {

            ps.setObject(
                    1,
                    fechaInicio
            );


            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                if (rs.next()) {

                    return new DatosInventarioInicial(
                            rs.getInt(
                                    "id_asiento"
                            ),
                            rs.getBigDecimal(
                                    "monto"
                            )
                    );
                }
            }
        }


        return new DatosInventarioInicial(
                null,
                null
        );
    }


    private static class DatosInventarioInicial {

        private final Integer idAsiento;
        private final BigDecimal monto;

        private DatosInventarioInicial(
                Integer idAsiento,
                BigDecimal monto
        ) {

            this.idAsiento = idAsiento;
            this.monto = monto;
        }
    }


    // =========================================================
    // NORMALIZAR VALORES
    // =========================================================

    private BigDecimal normalizar(
            BigDecimal valor
    ) {

        if (valor == null) {

            return BigDecimal.ZERO
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }


        return valor.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =========================================================
    // CONCEPTO DEL MOVIMIENTO
    // =========================================================

    private String obtenerConcepto(
            OperacionInventarioRecalculo operacion
    ) {

        if (operacion.getConcepto() != null
                && !operacion.getConcepto().isBlank()) {

            return operacion
                    .getConcepto()
                    .trim();
        }


        return switch (
                operacion.getTipoOperacion()
        ) {

            case "COMPRA" ->
                "Compra de mercaderia";

            case "VENTA" ->
                "Venta de mercaderia";

            case "DEVOLUCION_COMPRA" ->
                "Devolucion sobre compra";

            case "DEVOLUCION_VENTA" ->
                "Devolucion sobre venta";

            default ->
                "Movimiento de inventario";
        };
    }
}