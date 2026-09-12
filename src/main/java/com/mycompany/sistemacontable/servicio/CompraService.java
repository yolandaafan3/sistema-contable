package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;

import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.dao.OperacionDAO;
import com.mycompany.sistemacontable.dao.ProductoDAO;

import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
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

        productoDAO =
                new ProductoDAO();

        cuentaDAO =
                new CuentaDAO();

        operacionDAO =
                new OperacionDAO();

        asientoDAO =
                new AsientoDAO();

        periodoService =
                new PeriodoService();

        ivaService =
                new CalculoIVAService();

        recalculoKardexService =
                new RecalculoKardexService();
    }


    public ResultadoCompra registrarCompra(
            LocalDate fecha,
            BigDecimal montoCompra,
            String formaPago,
            String concepto
    ) {

        // =====================================================
        // VALIDACIONES
        // =====================================================

        validarDatos(
                fecha,
                montoCompra,
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


        if (producto.getCostoCompra() == null
                ||
            producto.getCostoCompra().compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

            throw new IllegalStateException(
                    "El producto no tiene un precio de compra válido."
            );
        }


        // =====================================================
        // MONTO INGRESADO POR EL USUARIO
        // =====================================================

        BigDecimal monto =
                montoCompra.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        // =====================================================
        // CALCULAR IVA
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
        // EL USUARIO NO INGRESA UNIDADES.
        //
        // Ejemplo:
        //
        // Compra = $10,000
        // Precio configurado = $10
        //
        // 10,000 / 10 = 1,000 unidades
        //
        // =====================================================

        BigDecimal cantidad =
                monto.divide(
                        producto.getCostoCompra(),
                        6,
                        RoundingMode.HALF_UP
                );


        if (cantidad.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalStateException(
                    "No fue posible calcular las unidades de la compra."
            );
        }


        // =====================================================
        // CUENTAS CONTABLES
        // =====================================================

        Cuenta compras =
                cuentaDAO.buscarPorRol(
                        "COMPRAS"
                );


        Cuenta ivaCredito =
                cuentaDAO.buscarPorRol(
                        "IVA_CREDITO"
                );


        Cuenta contrapartida =
                obtenerContrapartida(
                        formaPago
                );


        validarCuenta(
                compras,
                "Compras"
        );


        if (iva.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            validarCuenta(
                    ivaCredito,
                    "IVA Crédito Fiscal"
            );
        }


        validarCuenta(
                contrapartida,
                "contrapartida de la compra"
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
                    "COMPRA"
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
                    producto.getCostoCompra()
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
            // 4. COMPRAS - DEBE
            // =================================================

            insertarDetalle(
                    idAsiento,
                    compras,
                    "Compra de mercadería",
                    subtotal,
                    BigDecimal.ZERO,
                    conexion
            );


            // =================================================
            // 5. IVA CREDITO FISCAL - DEBE
            // =================================================

            if (iva.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                insertarDetalle(
                        idAsiento,
                        ivaCredito,
                        "IVA Crédito Fiscal",
                        iva,
                        BigDecimal.ZERO,
                        conexion
                );
            }


            // =================================================
            // 6. CAJA / BANCO / PROVEEDORES - HABER
            // =================================================

            insertarDetalle(
                    idAsiento,
                    contrapartida,
                    descripcionContrapartida(
                            formaPago
                    ),
                    BigDecimal.ZERO,
                    total,
                    conexion
            );


            // =================================================
            // 7. RECALCULAR KARDEX PEPS
            // =================================================

            recalculoKardexService.recalcularProducto(
                    producto.getIdProducto(),
                    conexion
            );


            // =================================================
            // 8. OBTENER EXISTENCIA ACTUALIZADA
            // =================================================

            Producto productoActualizado =
                    productoDAO.buscarPorId(
                            producto.getIdProducto(),
                            conexion
                    );


            if (productoActualizado == null) {

                throw new SQLException(
                        "No se pudo obtener el producto "
                        + "después del recálculo del Kardex."
                );
            }


            BigDecimal nuevaExistencia =
                    productoActualizado
                            .getExistenciaActual()
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


            // =================================================
            // 9. COMMIT
            // =================================================

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

                    System.out.println(
                            "Error al hacer rollback: "
                            + ex.getMessage()
                    );
                }
            }


            throw new RuntimeException(
                    "No se pudo registrar la compra: "
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
                        "2.1.01.01"
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
                "Pago de compra en efectivo";


            case "BANCO" ->
                "Pago de compra por banco";


            case "CREDITO" ->
                "Compra de mercadería al crédito";


            default ->
                "Compra de mercadería";
        };
    }


    // =========================================================
    // VALIDACIONES
    // =========================================================

    private void validarDatos(
            LocalDate fecha,
            BigDecimal montoCompra,
            String formaPago
    ) {

        if (fecha == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha."
            );
        }


        if (montoCompra == null
                ||
            montoCompra.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

            throw new IllegalArgumentException(
                    "El monto de la compra debe ser mayor que cero."
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

            return "Compra de mercadería";
        }


        return concepto.trim();
    }
}