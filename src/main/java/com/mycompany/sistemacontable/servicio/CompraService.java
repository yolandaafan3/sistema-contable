package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;

import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.dao.OperacionDAO;
import com.mycompany.sistemacontable.dao.ProductoDAO;

import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.DistribucionPago;
import com.mycompany.sistemacontable.modelo.Operacion;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.modelo.ResultadoCompra;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;

public class CompraService {

    private final ProductoDAO productoDAO;
    private final CuentaDAO cuentaDAO;
    private final OperacionDAO operacionDAO;
    private final AsientoDAO asientoDAO;

    private final PeriodoService periodoService;
    private final CalculoIVAService ivaService;
    private final RecalculoKardexService recalculoKardexService;

    public CompraService() {
        productoDAO = new ProductoDAO();
        cuentaDAO = new CuentaDAO();
        operacionDAO = new OperacionDAO();
        asientoDAO = new AsientoDAO();
        periodoService = new PeriodoService();
        ivaService = new CalculoIVAService();
        recalculoKardexService = new RecalculoKardexService();
    }

    /**
     * Registra una compra de mercadería usando cantidad física y costo unitario
     * neto. La cuenta contable afectada es COMPRAS; Kardex PEPS controla las
     * existencias por separado. El IVA nunca se incorpora al costo PEPS.
     */
    public ResultadoCompra registrarCompra(
            LocalDate fecha,
            int idProducto,
            BigDecimal cantidad,
            BigDecimal costoUnitario,
            DistribucionPago distribucion,
            String cuentaCredito,
            String concepto
    ) {

        validarDatos(fecha, idProducto, cantidad, costoUnitario, distribucion);
        periodoService.validarFecha(fecha);

        PeriodoContable periodo = periodoService.obtenerPeriodoActivo();
        if (periodo == null) {
            throw new IllegalStateException("No existe un período contable activo.");
        }

        BigDecimal cantidadNormalizada = cantidad.setScale(6, RoundingMode.HALF_UP);
        BigDecimal costoNormalizado = costoUnitario.setScale(6, RoundingMode.HALF_UP);
        BigDecimal baseCompra = cantidadNormalizada
                .multiply(costoNormalizado)
                .setScale(2, RoundingMode.HALF_UP);

        ResultadoIVA resultadoIVA = ivaService.calcularSobreBase(baseCompra);
        BigDecimal subtotal = resultadoIVA.getSubtotal().setScale(2, RoundingMode.HALF_UP);
        BigDecimal iva = resultadoIVA.getIva().setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = resultadoIVA.getTotal().setScale(2, RoundingMode.HALF_UP);

        if (distribucion.getTotal().compareTo(total) != 0) {
            throw new IllegalArgumentException(
                    "La distribución del pago ($" + distribucion.getTotal()
                    + ") no coincide con el total de la compra ($" + total + ")."
            );
        }

        Cuenta compras = cuentaDAO.buscarPorRol("COMPRAS");
        Cuenta ivaCredito = cuentaDAO.buscarPorRol("IVA_CREDITO");
        Cuenta caja = cuentaDAO.buscarPorCodigo("1.1.01.01");
        Cuenta banco = cuentaDAO.buscarPorCodigo("1.1.01.02");
        Cuenta proveedor = cuentaDAO.buscarPorCodigo(
                "ACREEDORES VARIOS".equalsIgnoreCase(cuentaCredito)
                        ? "2.1.01.02"
                        : "2.1.01.01"
        );

        validarCuenta(compras, "Compras");
        if (iva.compareTo(BigDecimal.ZERO) > 0) {
            validarCuenta(ivaCredito, "IVA Crédito Fiscal");
        }
        if (distribucion.getEfectivo().compareTo(BigDecimal.ZERO) > 0) {
            validarCuenta(caja, "Caja");
        }
        if (distribucion.getBanco().compareTo(BigDecimal.ZERO) > 0) {
            validarCuenta(banco, "Banco");
        }
        if (distribucion.getCredito().compareTo(BigDecimal.ZERO) > 0) {
            validarCuenta(proveedor, "cuenta por pagar");
        }

        Connection conexion = null;

        try {
            conexion = Conexion.conectar();
            if (conexion == null) {
                throw new SQLException("No se pudo conectar con MySQL.");
            }

            conexion.setAutoCommit(false);

            Producto producto = productoDAO.buscarPorId(idProducto, conexion);
            if (producto == null) {
                throw new IllegalStateException("No se encontró el producto seleccionado.");
            }
            if (!producto.isActivo()) {
                throw new IllegalStateException("El producto seleccionado está desactivado.");
            }

            Operacion operacion = new Operacion();
            operacion.setIdPeriodo(periodo.getIdPeriodo());
            operacion.setFecha(fecha);
            operacion.setTipoOperacion("COMPRA");
            operacion.setConcepto(prepararConcepto(concepto));
            operacion.setIdProducto(producto.getIdProducto());
            operacion.setCantidad(cantidadNormalizada);
            operacion.setPrecioUnitario(costoNormalizado);
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

            insertarDetalle(
                    idAsiento,
                    compras,
                    "Compra de mercadería - " + producto.getNombre(),
                    subtotal,
                    BigDecimal.ZERO,
                    conexion
            );

            if (iva.compareTo(BigDecimal.ZERO) > 0) {
                insertarDetalle(
                        idAsiento,
                        ivaCredito,
                        "IVA Crédito Fiscal",
                        iva,
                        BigDecimal.ZERO,
                        conexion
                );
            }

            if (distribucion.getEfectivo().compareTo(BigDecimal.ZERO) > 0) {
                insertarDetalle(
                        idAsiento,
                        caja,
                        "Pago de compra en efectivo",
                        BigDecimal.ZERO,
                        distribucion.getEfectivo(),
                        conexion
                );
            }

            if (distribucion.getBanco().compareTo(BigDecimal.ZERO) > 0) {
                insertarDetalle(
                        idAsiento,
                        banco,
                        "Pago de compra por banco/cheque",
                        BigDecimal.ZERO,
                        distribucion.getBanco(),
                        conexion
                );
            }

            if (distribucion.getCredito().compareTo(BigDecimal.ZERO) > 0) {
                insertarDetalle(
                        idAsiento,
                        proveedor,
                        "Saldo de compra al crédito",
                        BigDecimal.ZERO,
                        distribucion.getCredito(),
                        conexion
                );
            }

            // El costo de referencia se actualiza al último costo neto real,
            // pero esto no revaloriza capas PEPS anteriores.
            productoDAO.actualizarCostoCompra(
                    idProducto,
                    costoNormalizado.setScale(2, RoundingMode.HALF_UP),
                    conexion
            );

            recalculoKardexService.recalcularProducto(
                    producto.getIdProducto(), conexion
            );

            Producto productoActualizado = productoDAO.buscarPorId(
                    producto.getIdProducto(), conexion
            );

            if (productoActualizado == null) {
                throw new SQLException(
                        "No se pudo obtener el producto después del recálculo del Kardex."
                );
            }

            BigDecimal nuevaExistencia = productoActualizado
                    .getExistenciaActual()
                    .setScale(6, RoundingMode.HALF_UP);

            conexion.commit();

            return new ResultadoCompra(
                    idOperacion,
                    idAsiento,
                    numeroAsiento,
                    subtotal,
                    iva,
                    total,
                    nuevaExistencia
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
                    "No se pudo registrar la compra: " + e.getMessage(), e
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
        DetalleAsiento detalle = new DetalleAsiento();
        detalle.setIdAsiento(idAsiento);
        detalle.setIdCuenta(cuenta.getIdCuenta());
        detalle.setDescripcion(descripcion);
        detalle.setDebe(debe.setScale(2, RoundingMode.HALF_UP));
        detalle.setHaber(haber.setScale(2, RoundingMode.HALF_UP));
        asientoDAO.insertarDetalle(detalle, conexion);
    }

    private void validarDatos(
            LocalDate fecha,
            int idProducto,
            BigDecimal cantidad,
            BigDecimal costoUnitario,
            DistribucionPago distribucion
    ) {
        if (fecha == null) {
            throw new IllegalArgumentException("Debe seleccionar una fecha.");
        }
        if (idProducto <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un producto.");
        }
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (costoUnitario == null || costoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El costo unitario debe ser mayor que cero.");
        }
        if (distribucion == null || distribucion.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Debe indicar cómo se pagará la compra.");
        }
    }

    private void validarCuenta(Cuenta cuenta, String nombre) {
        if (cuenta == null) {
            throw new IllegalStateException("No se encontró la cuenta " + nombre + ".");
        }
        if (!cuenta.isActivo()) {
            throw new IllegalStateException(
                    "La cuenta " + cuenta.getNombre() + " está desactivada."
            );
        }
        if (!cuenta.isPermiteMovimiento()) {
            throw new IllegalStateException(
                    "La cuenta " + cuenta.getNombre() + " no permite movimientos."
            );
        }
    }

    private String prepararConcepto(String concepto) {
        if (concepto == null || concepto.isBlank()) {
            return "Compra de mercadería";
        }
        return concepto.trim();
    }
}
