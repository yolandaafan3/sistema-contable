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
import com.mycompany.sistemacontable.modelo.ResultadoDevolucionCompra;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;

public class DevolucionCompraService {

    private final ProductoDAO productoDAO;
    private final CuentaDAO cuentaDAO;
    private final OperacionDAO operacionDAO;
    private final AsientoDAO asientoDAO;
    private final KardexDAO kardexDAO;

    private final PeriodoService periodoService;
    private final CalculoIVAService ivaService;
    private final RecalculoKardexService recalculoKardexService;


    public DevolucionCompraService() {

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


    public ResultadoDevolucionCompra registrar(
            LocalDate fecha,
            BigDecimal montoDevolucion,
            String formaReintegro,
            String concepto
    ) {

        // =====================================================
        // VALIDACIONES
        // =====================================================

        validarDatos(
                fecha,
                montoDevolucion,
                formaReintegro
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
        // MONTO INTRODUCIDO POR EL USUARIO
        // =====================================================

        BigDecimal monto =
                montoDevolucion.setScale(
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
        // UNIDADES AUTOMATICAS PARA KARDEX
        // =====================================================
        //
        // Ejemplo de la guía:
        //
        // Devolución: $1,000
        // Precio de compra: $10
        //
        // 1,000 / 10 = 100 unidades
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
                    "No fue posible calcular las unidades "
                    + "de la devolución."
            );
        }


        // =====================================================
        // CUENTAS CONTABLES
        // =====================================================

        Cuenta devolucionCompras =
                cuentaDAO.buscarPorRol(
                        "DEVOLUCION_COMPRAS"
                );


        Cuenta ivaCredito =
                cuentaDAO.buscarPorRol(
                        "IVA_CREDITO"
                );


        Cuenta contrapartida =
                obtenerContrapartida(
                        formaReintegro
                );


        validarCuenta(
                devolucionCompras,
                "Devolución sobre Compras"
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
                "contrapartida de la devolución"
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
                    "DEVOLUCION_COMPRA"
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
                    formaReintegro
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
            // 4. PROVEEDORES / CAJA / BANCO - DEBE
            // =================================================
            //
            // Si la compra fue al crédito:
            //
            // Proveedores                 DEBE $1,000
            //
            // Si el proveedor reintegra dinero:
            //
            // Caja/Banco                  DEBE $1,000
            //
            // =================================================

            insertarDetalle(
                    idAsiento,
                    contrapartida,
                    descripcionContrapartida(
                            formaReintegro
                    ),
                    total,
                    BigDecimal.ZERO,
                    conexion
            );


            // =================================================
            // 5. DEVOLUCION SOBRE COMPRAS - HABER
            // =================================================

            insertarDetalle(
                    idAsiento,
                    devolucionCompras,
                    "Devolución sobre compras",
                    BigDecimal.ZERO,
                    subtotal,
                    conexion
            );


            // =================================================
            // 6. IVA CREDITO FISCAL - HABER
            // =================================================

            if (iva.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                insertarDetalle(
                        idAsiento,
                        ivaCredito,
                        "Disminución IVA Crédito Fiscal",
                        BigDecimal.ZERO,
                        iva,
                        conexion
                );
            }


            // =================================================
            // 7. RECALCULAR KARDEX PEPS
            // =================================================
            //
            // El recalculador también comprueba que no se
            // devuelvan más unidades de las que realmente
            // fueron compradas hasta esa fecha.
            //
            // =================================================

            recalculoKardexService.recalcularProducto(
                    producto.getIdProducto(),
                    conexion
            );


            // =================================================
            // 8. MOVIMIENTO KARDEX
            // =================================================

            MovimientoKardex movimiento =
                    kardexDAO.buscarMovimientoPorAsiento(
                            idAsiento,
                            conexion
                    );


            if (movimiento == null) {

                throw new SQLException(
                        "No se encontró el movimiento Kardex "
                        + "de la devolución sobre compra."
                );
            }


            // =================================================
            // 9. PRODUCTO ACTUALIZADO
            // =================================================

            Producto productoActualizado =
                    productoDAO.buscarPorId(
                            producto.getIdProducto(),
                            conexion
                    );


            if (productoActualizado == null) {

                throw new SQLException(
                        "No se pudo obtener el producto "
                        + "después del recálculo."
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


            return new ResultadoDevolucionCompra(
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
                    "No se pudo registrar la devolución sobre compra: "
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
            String formaReintegro
    ) {

        return switch (
                formaReintegro
        ) {

            case "CREDITO" ->
                cuentaDAO.buscarPorCodigo(
                        "2.1.01.01"
                );


            case "EFECTIVO" ->
                cuentaDAO.buscarPorCodigo(
                        "1.1.01.01"
                );


            case "BANCO" ->
                cuentaDAO.buscarPorCodigo(
                        "1.1.01.02"
                );


            default ->
                null;
        };
    }


    private String descripcionContrapartida(
            String formaReintegro
    ) {

        return switch (
                formaReintegro
        ) {

            case "CREDITO" ->
                "Disminución de cuenta con proveedor";


            case "EFECTIVO" ->
                "Reintegro recibido en efectivo";


            case "BANCO" ->
                "Reintegro recibido en banco";


            default ->
                "Devolución sobre compra";
        };
    }


    // =========================================================
    // VALIDACIONES
    // =========================================================

    private void validarDatos(
            LocalDate fecha,
            BigDecimal montoDevolucion,
            String formaReintegro
    ) {

        if (fecha == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha."
            );
        }


        if (montoDevolucion == null
                ||
            montoDevolucion.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

            throw new IllegalArgumentException(
                    "El monto de la devolución "
                    + "debe ser mayor que cero."
            );
        }


        if (!"CREDITO".equals(
                formaReintegro
        )
                &&
            !"EFECTIVO".equals(
                    formaReintegro
            )
                &&
            !"BANCO".equals(
                    formaReintegro
            )) {

            throw new IllegalArgumentException(
                    "Forma de reintegro no válida."
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

            return "Devolución sobre compra";
        }


        return concepto.trim();
    }
}