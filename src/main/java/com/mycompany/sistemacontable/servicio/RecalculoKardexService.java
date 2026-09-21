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
import java.util.ArrayList;

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

        kardexDAO.eliminarConsumosProducto(idProducto, conexion);

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
        // INVENTARIO INICIAL DEL PERIODO
        // =====================================================
        // Si el período fue creado por cierre/arrastre, reconstruimos cada
        // capa PEPS histórica por separado. Si no existen capas de apertura,
        // usamos el inventario inicial manual configurado en el producto.

        Integer idAsientoApertura = null;
        LocalDate fechaApertura = null;
        String sqlApertura = """
                SELECT a.id_asiento, a.fecha
                  FROM asientos_contables a
                 WHERE a.id_periodo=(SELECT id_periodo FROM periodos_contables WHERE estado='ABIERTO' ORDER BY id_periodo DESC LIMIT 1)
                   AND a.estado='CONTABILIZADO'
                   AND LOWER(a.concepto) LIKE 'apertura%'
                 ORDER BY a.numero_asiento ASC
                 LIMIT 1
                """;
        try (PreparedStatement ps = conexion.prepareStatement(sqlApertura); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) { idAsientoApertura=rs.getInt(1); fechaApertura=rs.getDate(2).toLocalDate(); }
        }

        boolean reconstruyoPorLotes = false;
        try {
            String q = """
                    SELECT cantidad,costo_unitario
                      FROM inventario_apertura_lotes
                     WHERE id_periodo=(SELECT id_periodo FROM periodos_contables WHERE estado='ABIERTO' ORDER BY id_periodo DESC LIMIT 1)
                       AND id_producto=?
                     ORDER BY orden_lote,id_apertura_lote
                    """;
            try (PreparedStatement ps=conexion.prepareStatement(q)) {
                ps.setInt(1,idProducto);
                try (ResultSet rs=ps.executeQuery()) {
                    while(rs.next()) {
                        reconstruyoPorLotes=true;
                        BigDecimal cantidad=normalizar(rs.getBigDecimal("cantidad"));
                        BigDecimal costo=rs.getBigDecimal("costo_unitario").setScale(6,RoundingMode.HALF_UP);
                        if(cantidad.signum()<=0 || costo.signum()<=0) continue;
                        BigDecimal valor=cantidad.multiply(costo).setScale(2,RoundingMode.HALF_UP);
                        existencia=existencia.add(cantidad);
                        saldoInventario=saldoInventario.add(valor);
                        if(idAsientoApertura!=null && fechaApertura!=null) {
                            MovimientoKardex inicial=new MovimientoKardex();
                            inicial.setIdProducto(idProducto); inicial.setIdAsiento(idAsientoApertura); inicial.setFecha(fechaApertura);
                            inicial.setConcepto("Inventario inicial arrastrado - lote PEPS");
                            inicial.setUnidadesEntrada(cantidad); inicial.setUnidadesSalida(BigDecimal.ZERO); inicial.setUnidadesExistencia(existencia);
                            inicial.setCostoUnitario(costo); inicial.setCostoPeps(costo); inicial.setSaldoDeudor(valor); inicial.setSaldoAcreedor(BigDecimal.ZERO); inicial.setSaldo(saldoInventario);
                            int idK=kardexDAO.insertarMovimiento(inicial,conexion);
                            kardexDAO.insertarCapaPeps(idProducto,idK,fechaApertura,cantidad,costo,conexion);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            // Compatibilidad con instalaciones que todavía no han ejecutado la migración.
            if (!ex.getMessage().toLowerCase().contains("inventario_apertura_lotes")) throw ex;
        }

        if(!reconstruyoPorLotes) {
            BigDecimal cantidadInicial = normalizar(producto.getExistenciaInicial());
            BigDecimal costoInicial = producto.getCostoInicial() == null ? BigDecimal.ZERO : producto.getCostoInicial().setScale(6, RoundingMode.HALF_UP);
            if (cantidadInicial.compareTo(BigDecimal.ZERO) > 0 && costoInicial.compareTo(BigDecimal.ZERO) > 0 && idAsientoApertura!=null && fechaApertura!=null) {
                BigDecimal valorKardexInicial = costoInicial.multiply(cantidadInicial).setScale(2, RoundingMode.HALF_UP);
                existencia = cantidadInicial; saldoInventario = valorKardexInicial;
                MovimientoKardex inicial = new MovimientoKardex();
                inicial.setIdProducto(idProducto); inicial.setIdAsiento(idAsientoApertura); inicial.setFecha(fechaApertura); inicial.setConcepto("Inventario inicial");
                inicial.setUnidadesEntrada(cantidadInicial); inicial.setUnidadesSalida(BigDecimal.ZERO); inicial.setUnidadesExistencia(cantidadInicial);
                inicial.setCostoUnitario(costoInicial); inicial.setCostoPeps(costoInicial); inicial.setSaldoDeudor(valorKardexInicial); inicial.setSaldoAcreedor(BigDecimal.ZERO); inicial.setSaldo(valorKardexInicial);
                int idKardexInicial = kardexDAO.insertarMovimiento(inicial, conexion);
                kardexDAO.insertarCapaPeps(idProducto,idKardexInicial,fechaApertura,cantidadInicial,costoInicial,conexion);
            }
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
                     * La devolución sobre venta es una ENTRADA al inventario.
                     * Para conservar PEPS correctamente, se reconstruyen las
                     * capas consumidas por la venta original, empezando por la
                     * última capa que esa venta consumió. Así una devolución
                     * parcial recupera exactamente el costo histórico que salió.
                     */

                    if (operacion.getIdOperacionOrigen() == null) {
                        throw new IllegalStateException(
                                "La devolución sobre venta debe estar vinculada a una venta de origen."
                        );
                    }

                    List<RetornoPeps> retornos = obtenerRetornosVenta(
                            idProducto,
                            operacion.getIdOperacionOrigen(),
                            operacion.getIdOperacion(),
                            cantidad,
                            conexion
                    );

                    BigDecimal costoEntrada = BigDecimal.ZERO;
                    for (RetornoPeps retorno : retornos) {
                        costoEntrada = costoEntrada.add(
                                retorno.cantidad().multiply(retorno.costoUnitario())
                        );
                    }
                    costoEntrada = costoEntrada.setScale(2, RoundingMode.HALF_UP);

                    BigDecimal costoUnitario = costoEntrada
                            .divide(cantidad, 2, RoundingMode.HALF_UP);

                    existencia = existencia.add(cantidad);

                    saldoInventario = saldoInventario
                            .add(costoEntrada)
                            .setScale(2, RoundingMode.HALF_UP);

                    int idKardex = registrarEntrada(
                            operacion,
                            idProducto,
                            cantidad,
                            existencia,
                            costoUnitario,
                            costoEntrada,
                            saldoInventario,
                            conexion
                    );

                    // Una devolución puede abarcar más de una capa original.
                    // El movimiento se muestra agregado, pero las capas se
                    // restituyen separadas con su costo histórico exacto.
                    for (RetornoPeps retorno : retornos) {
                        kardexDAO.insertarCapaPeps(
                                idProducto,
                                idKardex,
                                operacion.getFecha(),
                                retorno.cantidad(),
                                retorno.costoUnitario(),
                                conexion
                        );
                    }
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
                                + " (operación #" + operacion.getIdOperacion() + ", " + operacion.getConcepto() + ")"
                                + ". Existencia: "
                                + existencia
                                + " | Salida: "
                                + cantidad
                        );
                    }


                    // =============================================
                    // CONSUMIR PEPS
                    // =============================================

                    BigDecimal costoSalida;
                    if ("DEVOLUCION_COMPRA".equals(operacion.getTipoOperacion())
                            && operacion.getIdOperacionOrigen() != null) {
                        costoSalida = consumirCapaCompraOrigen(
                                idProducto, operacion.getIdOperacion(), operacion.getIdOperacionOrigen(), cantidad, conexion
                        );
                    } else {
                        costoSalida = consumirPeps(
                                idProducto, operacion.getIdOperacion(), operacion.getFecha(), cantidad, conexion
                        );
                    }


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



    private List<RetornoPeps> obtenerRetornosVenta(
            int idProducto,
            int idOperacionVenta,
            int idOperacionDevolucionActual,
            BigDecimal cantidadDevolver,
            Connection conexion
    ) throws SQLException {

        BigDecimal yaDevuelto = BigDecimal.ZERO;
        String sqlDevuelto = """
                SELECT COALESCE(SUM(cantidad), 0) AS cantidad_devuelta
                FROM operaciones
                WHERE id_producto = ?
                  AND tipo_operacion = 'DEVOLUCION_VENTA'
                  AND id_operacion_origen = ?
                  AND id_operacion < ?
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sqlDevuelto)) {
            ps.setInt(1, idProducto);
            ps.setInt(2, idOperacionVenta);
            ps.setInt(3, idOperacionDevolucionActual);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal valor = rs.getBigDecimal("cantidad_devuelta");
                    if (valor != null) {
                        yaDevuelto = valor;
                    }
                }
            }
        }

        String sqlConsumos = """
                SELECT id_consumo, cantidad, costo_unitario
                FROM detalle_consumo_peps
                WHERE id_producto = ?
                  AND id_operacion_salida = ?
                ORDER BY id_consumo DESC
                """;

        List<RetornoPeps> resultado = new ArrayList<>();
        BigDecimal omitir = yaDevuelto;
        BigDecimal pendiente = cantidadDevolver;

        try (PreparedStatement ps = conexion.prepareStatement(sqlConsumos)) {
            ps.setInt(1, idProducto);
            ps.setInt(2, idOperacionVenta);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next() && pendiente.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal consumida = rs.getBigDecimal("cantidad");
                    BigDecimal costo = rs.getBigDecimal("costo_unitario")
                            .setScale(2, RoundingMode.HALF_UP);

                    if (omitir.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal salto = consumida.min(omitir);
                        consumida = consumida.subtract(salto);
                        omitir = omitir.subtract(salto);
                    }

                    if (consumida.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }

                    BigDecimal devolver = consumida.min(pendiente);
                    resultado.add(new RetornoPeps(devolver, costo));
                    pendiente = pendiente.subtract(devolver);
                }
            }
        }

        if (pendiente.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException(
                    "La devolución supera las unidades pendientes de la venta de origen. "
                    + "Pendiente sin asociar: " + pendiente.stripTrailingZeros().toPlainString()
            );
        }

        return resultado;
    }

    private record RetornoPeps(BigDecimal cantidad, BigDecimal costoUnitario) {}

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
            int idOperacionSalida,
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

            kardexDAO.insertarConsumoPeps(
                    idProducto, idOperacionSalida, capa.getIdCapa(), tomar, capa.getCostoUnitario(), conexion
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


    private BigDecimal consumirCapaCompraOrigen(
            int idProducto, int idOperacionSalida, int idOperacionCompra, BigDecimal cantidad, Connection conexion
    ) throws SQLException {
        CapaPeps capa = kardexDAO.buscarCapaPorOperacionEntrada(idProducto, idOperacionCompra, conexion);
        if (capa == null) {
            throw new IllegalStateException("No se encontró el lote PEPS de la compra seleccionada.");
        }
        if (capa.getCantidadDisponible().compareTo(cantidad) < 0) {
            throw new IllegalStateException(
                    "La devolución supera las unidades todavía disponibles del lote de compra. "
                    + "Disponibles: " + capa.getCantidadDisponible() + " | Devolución: " + cantidad
            );
        }
        BigDecimal restante = capa.getCantidadDisponible().subtract(cantidad);
        kardexDAO.actualizarCantidadDisponible(capa.getIdCapa(), restante, conexion);
        kardexDAO.insertarConsumoPeps(idProducto, idOperacionSalida, capa.getIdCapa(), cantidad, capa.getCostoUnitario(), conexion);
        return cantidad.multiply(capa.getCostoUnitario()).setScale(2, RoundingMode.HALF_UP);
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