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


    public ResultadoVenta registrarVenta(
            LocalDate fecha,
            BigDecimal montoVenta,
            String formaPago,
            String concepto
    ) {

        // =====================================================
        // VALIDACIONES
        // =====================================================

        validarDatos(
                fecha,
                montoVenta,
                formaPago
        );


        periodoService.validarFecha(
                fecha
        );


        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();


        Producto producto =
                productoDAO.obtenerProductoActivo();


        if (producto == null) {

            throw new IllegalStateException(
                    "No existe un producto activo."
            );
        }


        if (producto.getPrecioVenta() == null
                ||
            producto.getPrecioVenta().compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

            throw new IllegalStateException(
                    "El producto no tiene un precio de venta válido."
            );
        }


        // =====================================================
        // MONTO INTRODUCIDO POR EL USUARIO
        // =====================================================

        BigDecimal monto =
                montoVenta.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        // =====================================================
        // IVA
        // =====================================================

        ResultadoIVA resultadoIVA =
                ivaService.calcular(
                        monto
                );


        BigDecimal subtotal =
                resultadoIVA
                        .getSubtotal()
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        BigDecimal iva =
                resultadoIVA
                        .getIva()
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        BigDecimal total =
                resultadoIVA
                        .getTotal()
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        // =====================================================
        // CALCULAR UNIDADES PARA KARDEX
        // =====================================================
        //
        // El usuario introduce DINERO.
        //
        // Ejemplo:
        //
        // Venta: $12,000
        // Precio de venta: $20
        //
        // 12,000 / 20 = 600 unidades
        //
        // =====================================================

        BigDecimal cantidad =
                monto.divide(
                        producto.getPrecioVenta(),
                        6,
                        RoundingMode.HALF_UP
                );


        if (cantidad.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalStateException(
                    "No fue posible calcular las unidades de la venta."
            );
        }


        // =====================================================
        // CUENTAS CONTABLES
        // =====================================================

        Cuenta ventas =
                cuentaDAO.buscarPorRol(
                        "VENTAS"
                );


        Cuenta ivaDebito =
                cuentaDAO.buscarPorRol(
                        "IVA_DEBITO"
                );


        Cuenta contrapartida =
                obtenerContrapartida(
                        formaPago
                );


        validarCuenta(
                ventas,
                "Ventas"
        );


        if (iva.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            validarCuenta(
                    ivaDebito,
                    "IVA Débito Fiscal"
            );
        }


        validarCuenta(
                contrapartida,
                "contrapartida de la venta"
        );


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


            // =================================================
            // 1. OPERACION
            // =================================================

            Operacion operacion =
                    new Operacion();


            operacion.setIdPeriodo(
                    periodo.getIdPeriodo()
            );


            operacion.setFecha(
                    fecha
            );


            operacion.setTipoOperacion(
                    "VENTA"
            );


            operacion.setConcepto(
                    prepararConcepto(
                            concepto
                    )
            );


            operacion.setIdProducto(
                    producto.getIdProducto()
            );


            // Las unidades son calculadas automáticamente.

            operacion.setCantidad(
                    cantidad
            );


            operacion.setPrecioUnitario(
                    producto.getPrecioVenta()
            );


            operacion.setSubtotal(
                    subtotal
            );


            operacion.setIva(
                    iva
            );


            operacion.setTotal(
                    total
            );


            operacion.setFormaPago(
                    formaPago
            );


            int idOperacion =
                    operacionDAO.insertar(
                            operacion,
                            conexion
                    );


            // =================================================
            // 2. NUMERO DE ASIENTO
            // =================================================

            int numeroAsiento =
                    asientoDAO.obtenerSiguienteNumero(
                            periodo.getIdPeriodo(),
                            conexion
                    );


            // =================================================
            // 3. ASIENTO CONTABLE
            // =================================================

            AsientoContable asiento =
                    new AsientoContable();


            asiento.setIdPeriodo(
                    periodo.getIdPeriodo()
            );


            asiento.setIdOperacion(
                    idOperacion
            );


            asiento.setNumeroAsiento(
                    numeroAsiento
            );


            asiento.setFecha(
                    fecha
            );


            asiento.setConcepto(
                    prepararConcepto(
                            concepto
                    )
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


            // =================================================
            // 4. CAJA / BANCO / CLIENTES - DEBE
            // =================================================

            insertarDetalle(
                    idAsiento,
                    contrapartida,
                    descripcionContrapartida(
                            formaPago
                    ),
                    total,
                    BigDecimal.ZERO,
                    conexion
            );


            // =================================================
            // 5. VENTAS - HABER
            // =================================================

            insertarDetalle(
                    idAsiento,
                    ventas,
                    "Venta de mercadería",
                    BigDecimal.ZERO,
                    subtotal,
                    conexion
            );


            // =================================================
            // 6. IVA DEBITO FISCAL - HABER
            // =================================================

            if (iva.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                insertarDetalle(
                        idAsiento,
                        ivaDebito,
                        "IVA Débito Fiscal",
                        BigDecimal.ZERO,
                        iva,
                        conexion
                );
            }


            // =================================================
            // 7. RECALCULAR KARDEX PEPS
            // =================================================

            recalculoKardexService.recalcularProducto(
                    producto.getIdProducto(),
                    conexion
            );


            /*
             * El recalculador valida también que el inventario
             * no quede negativo cronológicamente.
             */


            // =================================================
            // 8. MOVIMIENTO KARDEX GENERADO
            // =================================================

            MovimientoKardex movimiento =
                    kardexDAO.buscarMovimientoPorAsiento(
                            idAsiento,
                            conexion
                    );


            if (movimiento == null) {

                throw new SQLException(
                        "No se encontró el movimiento Kardex "
                        + "generado para la venta."
                );
            }


            // =================================================
            // 9. EXISTENCIA ACTUALIZADA
            // =================================================

            Producto productoActualizado =
                    productoDAO.buscarPorId(
                            producto.getIdProducto(),
                            conexion
                    );


            if (productoActualizado == null) {

                throw new SQLException(
                        "No se pudo obtener la existencia "
                        + "actualizada del producto."
                );
            }


            BigDecimal costoPeps =
                    movimiento
                            .getSaldoAcreedor()
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


            BigDecimal saldoKardex =
                    movimiento
                            .getSaldo()
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


            BigDecimal nuevaExistencia =
                    productoActualizado
                            .getExistenciaActual()
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


            // =================================================
            // 10. COMMIT
            // =================================================

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

                    System.out.println(
                            "Error al hacer rollback: "
                            + ex.getMessage()
                    );
                }
            }


            throw new RuntimeException(
                    "No se pudo registrar la venta: "
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
                            "Error al cerrar conexión: "
                            + e.getMessage()
                    );
                }
            }
        }
    }


    // =========================================================
    // INSERTAR DETALLE
    // =========================================================

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


    // =========================================================
    // CONTRAPARTIDA
    // =========================================================

    private Cuenta obtenerContrapartida(
            String formaPago
    ) {

        return switch (
                formaPago
        ) {

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

        return switch (
                formaPago
        ) {

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


    // =========================================================
    // VALIDACIONES
    // =========================================================

    private void validarDatos(
            LocalDate fecha,
            BigDecimal montoVenta,
            String formaPago
    ) {

        if (fecha == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha."
            );
        }


        if (montoVenta == null
                ||
            montoVenta.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

            throw new IllegalArgumentException(
                    "El monto de la venta debe ser mayor que cero."
            );
        }


        if (!"EFECTIVO".equals(
                formaPago
        )
                &&
            !"BANCO".equals(
                formaPago
        )
                &&
            !"CREDITO".equals(
                formaPago
        )) {

            throw new IllegalArgumentException(
                    "Forma de pago no válida."
            );
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
                ||
            concepto.isBlank()) {

            return "Venta de mercadería";
        }


        return concepto.trim();
    }
}