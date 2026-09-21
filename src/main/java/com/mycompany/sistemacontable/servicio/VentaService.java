package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;

import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.dao.KardexDAO;
import com.mycompany.sistemacontable.dao.OperacionDAO;
import com.mycompany.sistemacontable.dao.ProductoDAO;

import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.DistribucionPago;
import com.mycompany.sistemacontable.modelo.MovimientoKardex;
import com.mycompany.sistemacontable.modelo.Operacion;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;
import com.mycompany.sistemacontable.modelo.ResultadoVenta;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;

public class VentaService {

    private final ProductoDAO productoDAO;
    private final CuentaDAO cuentaDAO;
    private final OperacionDAO operacionDAO;
    private final AsientoDAO asientoDAO;
    private final KardexDAO kardexDAO;

    private final PeriodoService periodoService;
    private final CalculoIVAService ivaService;
    private final RecalculoKardexService recalculoKardexService;

    public VentaService() {

        productoDAO =
                new ProductoDAO();

        cuentaDAO =
                new CuentaDAO();

        operacionDAO =
                new OperacionDAO();

        asientoDAO =
                new AsientoDAO();

        kardexDAO =
                new KardexDAO();

        periodoService =
                new PeriodoService();

        ivaService =
                new CalculoIVAService();

        recalculoKardexService =
                new RecalculoKardexService();
    }

    /**
     * API compatible con las versiones anteriores. Convierte la forma simple
     * de cobro en una distribución y delega a la lógica única de ventas.
     */
    public ResultadoVenta registrarVenta(
            LocalDate fecha,
            int idProducto,
            BigDecimal montoOperacion,
            BigDecimal precioUnitario,
            String formaPago,
            String concepto
    ) {
        if (montoOperacion == null || precioUnitario == null) {
            throw new IllegalArgumentException("Monto y precio son obligatorios.");
        }

        BigDecimal precio = precioUnitario.setScale(6, RoundingMode.HALF_UP);
        BigDecimal monto = montoOperacion.setScale(2, RoundingMode.HALF_UP);
        BigDecimal cantidad = monto.divide(precio, 6, RoundingMode.HALF_UP);
        ResultadoIVA calculo = ivaService.calcular(monto);
        BigDecimal total = calculo.getTotal().setScale(2, RoundingMode.HALF_UP);

        DistribucionPago distribucion = switch (formaPago) {
            case "EFECTIVO" -> new DistribucionPago(total, BigDecimal.ZERO, BigDecimal.ZERO);
            case "BANCO" -> new DistribucionPago(BigDecimal.ZERO, total, BigDecimal.ZERO);
            case "CREDITO" -> new DistribucionPago(BigDecimal.ZERO, BigDecimal.ZERO, total);
            default -> throw new IllegalArgumentException(
                    "La venta mixta requiere indicar cuánto se cobra ahora y cuánto queda a crédito."
            );
        };

        return registrarVenta(
                fecha,
                idProducto,
                cantidad,
                precio,
                distribucion,
                concepto
        );
    }

    /**
     * Registra una venta con cantidad física, precio unitario y distribución
     * del cobro. La distribución puede ser efectivo, banco, crédito o MIXTO.
     * El saldo a crédito siempre se registra contra Clientes.
     */
    public ResultadoVenta registrarVenta(
            LocalDate fecha,
            int idProducto,
            BigDecimal cantidad,
            BigDecimal precioUnitario,
            DistribucionPago distribucion,
            String concepto
    ) {

        validarDatos(fecha, idProducto, cantidad, precioUnitario, distribucion);
        periodoService.validarFecha(fecha);

        PeriodoContable periodo = periodoService.obtenerPeriodoActivo();
        if (periodo == null) {
            throw new IllegalStateException("No existe un período contable activo.");
        }

        BigDecimal cantidadNormalizada = cantidad.setScale(6, RoundingMode.HALF_UP);
        BigDecimal precioNormalizado = precioUnitario.setScale(6, RoundingMode.HALF_UP);
        BigDecimal monto = cantidadNormalizada
                .multiply(precioNormalizado)
                .setScale(2, RoundingMode.HALF_UP);

        ResultadoIVA resultadoIVA = ivaService.calcular(monto);
        BigDecimal subtotal = resultadoIVA.getSubtotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal iva = resultadoIVA.getIva().setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = resultadoIVA.getTotal().setScale(2, RoundingMode.HALF_UP);

        if (distribucion.getTotal().compareTo(total) != 0) {
            throw new IllegalArgumentException(
                    "La distribución del cobro ($" + distribucion.getTotal()
                    + ") no coincide con el total de la venta ($" + total + ")."
            );
        }

        Cuenta ventas = cuentaDAO.buscarPorRol("VENTAS");
        Cuenta ivaDebito = cuentaDAO.buscarPorRol("IVA_DEBITO");
        Cuenta caja = cuentaDAO.buscarPorCodigo("1.1.01.01");
        Cuenta banco = cuentaDAO.buscarPorCodigo("1.1.01.02");
        Cuenta clientes = cuentaDAO.buscarPorCodigo("1.1.02.01");

        validarCuenta(ventas, "Ventas");
        if (iva.compareTo(BigDecimal.ZERO) > 0) {
            validarCuenta(ivaDebito, "IVA Débito Fiscal");
        }
        if (distribucion.getEfectivo().compareTo(BigDecimal.ZERO) > 0) {
            validarCuenta(caja, "Caja");
        }
        if (distribucion.getBanco().compareTo(BigDecimal.ZERO) > 0) {
            validarCuenta(banco, "Banco");
        }
        if (distribucion.getCredito().compareTo(BigDecimal.ZERO) > 0) {
            validarCuenta(clientes, "Clientes");
        }

        Connection conexion = null;

        try {
            conexion = Conexion.conectar();
            if (conexion == null) {
                throw new SQLException("No se pudo conectar con MySQL.");
            }

            conexion.setAutoCommit(false);

            Producto producto = productoDAO.buscarPorIdParaActualizar(idProducto, conexion);
            if (producto == null) {
                throw new IllegalStateException("No se encontró el producto seleccionado.");
            }
            if (!producto.isActivo()) {
                throw new IllegalStateException("El producto seleccionado está desactivado.");
            }

            BigDecimal existenciaDisponible = producto.getExistenciaActual() == null
                    ? BigDecimal.ZERO
                    : producto.getExistenciaActual();

            if (cantidadNormalizada.compareTo(existenciaDisponible) > 0) {
                throw new IllegalArgumentException(
                        "Inventario insuficiente. Se intentan vender "
                        + cantidadNormalizada.stripTrailingZeros().toPlainString()
                        + " unidades, pero solo hay "
                        + existenciaDisponible.stripTrailingZeros().toPlainString()
                        + " disponibles. No se guardó ninguna operación ni asiento."
                );
            }

            Operacion operacion = new Operacion();
            operacion.setIdPeriodo(periodo.getIdPeriodo());
            operacion.setFecha(fecha);
            operacion.setTipoOperacion("VENTA");
            operacion.setConcepto(prepararConcepto(concepto));
            operacion.setIdProducto(producto.getIdProducto());
            operacion.setCantidad(cantidadNormalizada);
            operacion.setPrecioUnitario(precioNormalizado);
            operacion.setSubtotal(subtotal);
            operacion.setIva(iva);
            operacion.setTotal(total);
            operacion.setFormaPago(distribucion.obtenerFormaPago());

            int idOperacion = operacionDAO.insertar(operacion, conexion);

            int numeroAsiento = asientoDAO.obtenerSiguienteNumero(
                    periodo.getIdPeriodo(), conexion
            );

            AsientoContable asiento = new AsientoContable();
            asiento.setIdPeriodo(periodo.getIdPeriodo());
            asiento.setIdOperacion(idOperacion);
            asiento.setNumeroAsiento(numeroAsiento);
            asiento.setFecha(fecha);
            asiento.setConcepto(prepararConcepto(concepto));
            asiento.setTipoAsiento("AUTOMATICO");
            asiento.setEstado("CONTABILIZADO");

            int idAsiento = asientoDAO.insertarAsiento(asiento, conexion);

            if (distribucion.getEfectivo().compareTo(BigDecimal.ZERO) > 0) {
                insertarDetalle(
                        idAsiento,
                        caja,
                        "Cobro de venta en efectivo",
                        distribucion.getEfectivo(),
                        BigDecimal.ZERO,
                        conexion
                );
            }

            if (distribucion.getBanco().compareTo(BigDecimal.ZERO) > 0) {
                insertarDetalle(
                        idAsiento,
                        banco,
                        "Cobro de venta por banco/cheque",
                        distribucion.getBanco(),
                        BigDecimal.ZERO,
                        conexion
                );
            }

            if (distribucion.getCredito().compareTo(BigDecimal.ZERO) > 0) {
                insertarDetalle(
                        idAsiento,
                        clientes,
                        "Saldo de venta al crédito",
                        distribucion.getCredito(),
                        BigDecimal.ZERO,
                        conexion
                );
            }

            insertarDetalle(
                    idAsiento,
                    ventas,
                    "Venta de mercadería - " + producto.getNombre(),
                    BigDecimal.ZERO,
                    subtotal,
                    conexion
            );

            if (iva.compareTo(BigDecimal.ZERO) > 0) {
                insertarDetalle(
                        idAsiento,
                        ivaDebito,
                        "IVA Débito Fiscal",
                        BigDecimal.ZERO,
                        iva,
                        conexion
                );
            }

            productoDAO.actualizarPrecioVenta(
                    producto.getIdProducto(),
                    precioNormalizado.setScale(2, RoundingMode.HALF_UP),
                    conexion
            );

            recalculoKardexService.recalcularProducto(
                    producto.getIdProducto(), conexion
            );

            MovimientoKardex movimiento = kardexDAO.buscarMovimientoPorAsiento(
                    idAsiento, conexion
            );

            if (movimiento == null) {
                throw new SQLException(
                        "No se encontró el movimiento Kardex generado para la venta."
                );
            }

            Producto productoActualizado = productoDAO.buscarPorId(
                    producto.getIdProducto(), conexion
            );

            if (productoActualizado == null) {
                throw new SQLException(
                        "No se pudo obtener la existencia actualizada del producto."
                );
            }

            BigDecimal costoPeps = movimiento.getSaldoAcreedor()
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal saldoKardex = movimiento.getSaldo()
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal nuevaExistencia = productoActualizado.getExistenciaActual()
                    .setScale(6, RoundingMode.HALF_UP);

            conexion.commit();

            return new ResultadoVenta(
                    idOperacion,
                    idAsiento,
                    numeroAsiento,
                    subtotal,
                    iva,
                    total,
                    costoPeps,
                    nuevaExistencia,
                    saldoKardex
            );

        } catch (Exception e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error al hacer rollback: " + ex.getMessage());
                }
            }

            throw new RuntimeException(
                    "No se pudo registrar la venta: " + e.getMessage(), e
            );

        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    private void insertarDetalle(
            int idAsiento,
            Cuenta cuenta,
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
                cuenta.getIdCuenta()
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

    private Cuenta obtenerContrapartida(
            String formaPago
    ) {

        return switch (formaPago) {

            case "EFECTIVO" ->
                cuentaDAO.buscarPorCodigo(
                        "1.1.01.01"
                );

            case "BANCO" ->
                cuentaDAO.buscarPorCodigo(
                        "1.1.01.02"
                );

            case "CREDITO" ->
                cuentaDAO.buscarPorCodigo(
                        "1.1.02.01"
                );

            default ->
                null;
        };
    }

    private String descripcionContrapartida(
            String formaPago
    ) {

        return switch (formaPago) {

            case "EFECTIVO" ->
                "Venta de mercadería al contado";

            case "BANCO" ->
                "Venta de mercadería recibida en banco";

            case "CREDITO" ->
                "Venta de mercadería al crédito";

            default ->
                "Venta de mercadería";
        };
    }

    private void validarDatos(
            LocalDate fecha,
            int idProducto,
            BigDecimal cantidad,
            BigDecimal precioUnitario,
            DistribucionPago distribucion
    ) {
        if (fecha == null) {
            throw new IllegalArgumentException("Debe seleccionar una fecha.");
        }
        if (idProducto <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un producto.");
        }
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad vendida debe ser mayor que cero.");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio unitario de venta debe ser mayor que cero."
            );
        }
        if (distribucion == null || distribucion.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debe indicar cómo se cobrará la venta.");
        }
    }

    private void validarCuenta(
            Cuenta cuenta,
            String nombre
    ) {

        if (cuenta == null) {

            throw new IllegalStateException(
                    "No se encontró la cuenta "
                            + nombre
                            + "."
            );
        }

        if (!cuenta.isActivo()) {

            throw new IllegalStateException(
                    "La cuenta "
                            + cuenta.getNombre()
                            + " está desactivada."
            );
        }

        if (!cuenta.isPermiteMovimiento()) {

            throw new IllegalStateException(
                    "La cuenta "
                            + cuenta.getNombre()
                            + " no permite movimientos."
            );
        }
    }

    private String prepararConcepto(
            String concepto
    ) {

        if (concepto == null
                || concepto.isBlank()) {

            return "Venta de mercadería";
        }

        return concepto.trim();
    }
}