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
import com.mycompany.sistemacontable.modelo.ResultadoDevolucionVenta;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;

public class DevolucionVentaService {

    private final ProductoDAO productoDAO;
    private final CuentaDAO cuentaDAO;
    private final OperacionDAO operacionDAO;
    private final AsientoDAO asientoDAO;
    private final KardexDAO kardexDAO;

    private final PeriodoService periodoService;
    private final CalculoIVAService ivaService;

    private final RecalculoKardexService
            recalculoKardexService;


    public DevolucionVentaService() {

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


    public ResultadoDevolucionVenta registrar(
            LocalDate fecha,
            BigDecimal montoDevolucion,
            String formaDevolucion,
            String concepto
    ) {

        // =====================================================
        // VALIDACIONES
        // =====================================================

        validarDatos(
                fecha,
                montoDevolucion,
                formaDevolucion
        );


        periodoService.validarFecha(
                fecha
        );


        PeriodoContable periodo =
                periodoService
                        .obtenerPeriodoActivo();


        Producto producto =
                productoDAO
                        .obtenerProductoActivo();


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
        //
        // El usuario introduce DINERO.
        //
        // Ejemplo:
        //
        // Devolución = $100
        //
        // =====================================================

        BigDecimal monto =
                montoDevolucion
                        .setScale(
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
        // CALCULAR UNIDADES PARA EL KARDEX
        // =====================================================
        //
        // Ejemplo de la guía:
        //
        // Devolución = $100
        // Precio de venta = $20
        //
        // 100 / 20 = 5 unidades
        //
        // Estas 5 unidades regresarán automáticamente
        // al inventario mediante el Kardex.
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
                    "No fue posible calcular las unidades "
                    + "de la devolución."
            );
        }


        // =====================================================
        // CUENTAS CONTABLES
        // =====================================================

        Cuenta devolucionVentas =
                cuentaDAO.buscarPorRol(
                        "DEVOLUCION_VENTAS"
                );


        Cuenta ivaDebito =
                cuentaDAO.buscarPorRol(
                        "IVA_DEBITO"
                );


        Cuenta contrapartida =
                obtenerContrapartida(
                        formaDevolucion
                );


        validarCuenta(
                devolucionVentas,
                "Devolución sobre Ventas"
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
                    "DEVOLUCION_VENTA"
            );


            operacion.setConcepto(
                    prepararConcepto(
                            concepto
                    )
            );


            operacion.setIdProducto(
                    producto.getIdProducto()
            );


            // Las unidades fueron calculadas automáticamente.

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
                    formaDevolucion
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
                    asientoDAO
                            .obtenerSiguienteNumero(
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
            // 4. DEVOLUCION SOBRE VENTAS - DEBE
            // =================================================

            insertarDetalle(
                    idAsiento,
                    devolucionVentas,
                    "Devolución sobre ventas",
                    subtotal,
                    BigDecimal.ZERO,
                    conexion
            );


            // =================================================
            // 5. IVA DEBITO FISCAL - DEBE
            // =================================================

            if (iva.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                insertarDetalle(
                        idAsiento,
                        ivaDebito,
                        "Disminución IVA Débito Fiscal",
                        iva,
                        BigDecimal.ZERO,
                        conexion
                );
            }


            // =================================================
            // 6. CLIENTES / CAJA / BANCO - HABER
            // =================================================

            insertarDetalle(
                    idAsiento,
                    contrapartida,
                    descripcionContrapartida(
                            formaDevolucion
                    ),
                    BigDecimal.ZERO,
                    total,
                    conexion
            );


            // =================================================
            // 7. RECALCULAR KARDEX PEPS
            // =================================================
            //
            // Las unidades devueltas regresan al inventario.
            //
            // El RecalculoKardexService también valida
            // cronológicamente las devoluciones.
            //
            // =================================================

            recalculoKardexService
                    .recalcularProducto(
                            producto.getIdProducto(),
                            conexion
                    );


            // =================================================
            // 8. MOVIMIENTO KARDEX GENERADO
            // =================================================

            MovimientoKardex movimiento =
                    kardexDAO
                            .buscarMovimientoPorAsiento(
                                    idAsiento,
                                    conexion
                            );


            if (movimiento == null) {

                throw new SQLException(
                        "No se encontró el movimiento Kardex "
                        + "de la devolución sobre venta."
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


            // =================================================
            // 10. RESULTADOS DEL KARDEX
            // =================================================

            BigDecimal costoInventario =
                    movimiento
                            .getSaldoDeudor()
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


            BigDecimal saldoKardex =
                    movimiento
                            .getSaldo()
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


            // =================================================
            // 11. COMMIT
            // =================================================

            conexion.commit();


            return new ResultadoDevolucionVenta(
                    idOperacion,
                    idAsiento,
                    numeroAsiento,
                    subtotal,
                    iva,
                    total,
                    costoInventario,
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
                    "No se pudo registrar la devolución sobre venta: "
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
            String formaDevolucion
    ) {

        return switch (
                formaDevolucion
        ) {

            case "CREDITO" ->
                cuentaDAO.buscarPorCodigo(
                        "1.1.02.01"
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
            String formaDevolucion
    ) {

        return switch (
                formaDevolucion
        ) {

            case "CREDITO" ->
                "Disminución de cuenta por cobrar al cliente";


            case "EFECTIVO" ->
                "Reintegro al cliente en efectivo";


            case "BANCO" ->
                "Reintegro al cliente por banco";


            default ->
                "Devolución sobre venta";
        };
    }


    // =========================================================
    // VALIDACIONES
    // =========================================================

    private void validarDatos(
            LocalDate fecha,
            BigDecimal montoDevolucion,
            String formaDevolucion
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
                formaDevolucion
        )
                &&
            !"EFECTIVO".equals(
                    formaDevolucion
            )
                &&
            !"BANCO".equals(
                    formaDevolucion
            )) {

            throw new IllegalArgumentException(
                    "Forma de devolución no válida."
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

            return "Devolución sobre venta";
        }


        return concepto.trim();
    }
}