package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;

import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.dao.OperacionDAO;

import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.Operacion;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;
import com.mycompany.sistemacontable.modelo.ResultadoOperacionGeneral;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;

public class OperacionesGeneralesService {

    private final CuentaDAO cuentaDAO;
    private final OperacionDAO operacionDAO;
    private final AsientoDAO asientoDAO;

    private final PeriodoService periodoService;
    private final CalculoIVAService ivaService;

    public OperacionesGeneralesService() {

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
    }


    // =====================================================
    // COBRO A CLIENTE
    // =====================================================

    public ResultadoOperacionGeneral registrarCobroCliente(
            LocalDate fecha,
            BigDecimal monto,
            String medio,
            String concepto
    ) {

        validarOperacionSimple(
                fecha,
                monto,
                medio
        );


        Cuenta cuentaMedio =
                obtenerCuentaMedio(
                        medio
                );


        Cuenta clientes =
                cuentaDAO.buscarPorCodigo(
                        "1.1.02.01"
                );


        validarCuenta(
                cuentaMedio,
                "medio de cobro"
        );


        validarCuenta(
                clientes,
                "Clientes"
        );


        return registrarMovimientoSimple(
                fecha,
                monto,
                "COBRO_CLIENTE",
                prepararConcepto(
                        concepto,
                        "Cobro a cliente"
                ),
                medio,
                cuentaMedio,
                clientes
        );
    }


    // =====================================================
    // PAGO A PROVEEDOR
    // =====================================================

    public ResultadoOperacionGeneral registrarPagoProveedor(
            LocalDate fecha,
            BigDecimal monto,
            String medio,
            String concepto
    ) {

        validarOperacionSimple(
                fecha,
                monto,
                medio
        );


        Cuenta proveedores =
                cuentaDAO.buscarPorCodigo(
                        "2.1.01.01"
                );


        Cuenta cuentaMedio =
                obtenerCuentaMedio(
                        medio
                );


        validarCuenta(
                proveedores,
                "Proveedores"
        );


        validarCuenta(
                cuentaMedio,
                "medio de pago"
        );


        return registrarMovimientoSimple(
                fecha,
                monto,
                "PAGO_PROVEEDOR",
                prepararConcepto(
                        concepto,
                        "Pago a proveedor"
                ),
                medio,
                proveedores,
                cuentaMedio
        );
    }


    // =====================================================
    // APORTE DE CAPITAL
    // =====================================================

    public ResultadoOperacionGeneral registrarAporteCapital(
            LocalDate fecha,
            BigDecimal monto,
            String medio,
            String concepto
    ) {

        validarOperacionSimple(
                fecha,
                monto,
                medio
        );


        Cuenta cuentaMedio =
                obtenerCuentaMedio(
                        medio
                );


        Cuenta capital =
                cuentaDAO.buscarPorRol(
                        "CAPITAL_SOCIAL"
                );


        validarCuenta(
                cuentaMedio,
                "medio del aporte"
        );


        validarCuenta(
                capital,
                "Capital Social"
        );


        return registrarMovimientoSimple(
                fecha,
                monto,
                "APORTE_CAPITAL",
                prepararConcepto(
                        concepto,
                        "Aporte de capital"
                ),
                medio,
                cuentaMedio,
                capital
        );
    }


    // =====================================================
    // PRESTAMO BANCARIO
    // =====================================================

    public ResultadoOperacionGeneral registrarPrestamo(
            LocalDate fecha,
            BigDecimal montoPrestamo,
            BigDecimal porcentajeComision,
            String medio,
            String concepto
    ) {

        validarOperacionSimple(
                fecha,
                montoPrestamo,
                medio
        );


        if (porcentajeComision == null) {

            porcentajeComision =
                    BigDecimal.ZERO;
        }


        if (porcentajeComision.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "El porcentaje de comision no puede ser negativo."
            );
        }


        if (porcentajeComision.compareTo(
                new BigDecimal("100")
        ) >= 0) {

            throw new IllegalArgumentException(
                    "El porcentaje de comision debe ser menor al 100%."
            );
        }


        periodoService.validarFecha(
                fecha
        );


        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();


        Cuenta cuentaMedio =
                obtenerCuentaMedio(
                        medio
                );


        Cuenta cuentaPrestamo =
                cuentaDAO.buscarPorRol(
                        "PRESTAMO_BANCARIO"
                );


        Cuenta gastosFinancieros =
                cuentaDAO.buscarPorRol(
                        "GASTOS_FINANCIEROS"
                );


        Cuenta ivaCredito =
                cuentaDAO.buscarPorRol(
                        "IVA_CREDITO"
                );


        validarCuenta(
                cuentaMedio,
                "cuenta donde se recibe el prestamo"
        );


        validarCuenta(
                cuentaPrestamo,
                "Prestamo Bancario"
        );


        // =================================================
        // COMISION
        // =================================================

        BigDecimal porcentajeDecimal =
                porcentajeComision.divide(
                        new BigDecimal("100"),
                        10,
                        RoundingMode.HALF_UP
                );


        BigDecimal comision =
                montoPrestamo
                        .multiply(
                                porcentajeDecimal
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        BigDecimal ivaComision =
                BigDecimal.ZERO;


        if (comision.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            validarCuenta(
                    gastosFinancieros,
                    "Gastos Financieros"
            );


            validarCuenta(
                    ivaCredito,
                    "IVA Credito Fiscal"
            );


            ResultadoIVA resultadoComision =
                    ivaService.calcularSobreBase(
                            comision
                    );


            ivaComision =
                    resultadoComision
                            .getIva();
        }


        // =================================================
        // MONTO NETO RECIBIDO
        // =================================================

        BigDecimal montoNeto =
                montoPrestamo
                        .subtract(
                                comision
                        )
                        .subtract(
                                ivaComision
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


        if (montoNeto.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "La comision y el IVA superan el monto del prestamo."
            );
        }


        Connection conexion = null;


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
            // OPERACION
            // =================================================

            Operacion operacion =
                    crearOperacionBase(
                            periodo.getIdPeriodo(),
                            fecha,
                            "PRESTAMO",
                            prepararConcepto(
                                    concepto,
                                    "Prestamo bancario"
                            ),
                            montoPrestamo,
                            ivaComision,
                            montoPrestamo,
                            medio
                    );


            int idOperacion =
                    operacionDAO.insertar(
                            operacion,
                            conexion
                    );


            // =================================================
            // ASIENTO
            // =================================================

            int numeroAsiento =
                    asientoDAO.obtenerSiguienteNumero(
                            periodo.getIdPeriodo(),
                            conexion
                    );


            AsientoContable asiento =
                    crearAsiento(
                            periodo.getIdPeriodo(),
                            idOperacion,
                            numeroAsiento,
                            fecha,
                            prepararConcepto(
                                    concepto,
                                    "Prestamo bancario"
                            )
                    );


            int idAsiento =
                    asientoDAO.insertarAsiento(
                            asiento,
                            conexion
                    );


            // =================================================
            // EFECTIVO / BANCO - DEBE
            // =================================================

            insertarDetalle(
                    idAsiento,
                    cuentaMedio,
                    "Monto neto recibido del prestamo",
                    montoNeto,
                    BigDecimal.ZERO,
                    conexion
            );


            // =================================================
            // GASTO FINANCIERO - DEBE
            // =================================================

            if (comision.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                insertarDetalle(
                        idAsiento,
                        gastosFinancieros,
                        "Comision bancaria",
                        comision,
                        BigDecimal.ZERO,
                        conexion
                );
            }


            // =================================================
            // IVA CREDITO - DEBE
            // =================================================

            if (ivaComision.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                insertarDetalle(
                        idAsiento,
                        ivaCredito,
                        "IVA sobre comision bancaria",
                        ivaComision,
                        BigDecimal.ZERO,
                        conexion
                );
            }


            // =================================================
            // PRESTAMO - HABER
            // =================================================

            insertarDetalle(
                    idAsiento,
                    cuentaPrestamo,
                    "Obligacion por prestamo bancario",
                    BigDecimal.ZERO,
                    montoPrestamo,
                    conexion
            );


            conexion.commit();


            return new ResultadoOperacionGeneral(
                    idOperacion,
                    idAsiento,
                    numeroAsiento,
                    montoPrestamo,
                    comision,
                    ivaComision,
                    montoNeto
            );


        } catch (Exception e) {

            rollback(
                    conexion
            );


            throw new RuntimeException(
                    "No se pudo registrar el prestamo: "
                    + e.getMessage(),
                    e
            );


        } finally {

            cerrarConexion(
                    conexion
            );
        }
    }


    // =====================================================
    // REGISTRO SIMPLE
    // =====================================================

    private ResultadoOperacionGeneral registrarMovimientoSimple(
            LocalDate fecha,
            BigDecimal monto,
            String tipoOperacion,
            String concepto,
            String medio,
            Cuenta cuentaDebe,
            Cuenta cuentaHaber
    ) {

        periodoService.validarFecha(
                fecha
        );


        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();


        Connection conexion = null;


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


            Operacion operacion =
                    crearOperacionBase(
                            periodo.getIdPeriodo(),
                            fecha,
                            tipoOperacion,
                            concepto,
                            monto,
                            BigDecimal.ZERO,
                            monto,
                            medio
                    );


            int idOperacion =
                    operacionDAO.insertar(
                            operacion,
                            conexion
                    );


            int numeroAsiento =
                    asientoDAO.obtenerSiguienteNumero(
                            periodo.getIdPeriodo(),
                            conexion
                    );


            AsientoContable asiento =
                    crearAsiento(
                            periodo.getIdPeriodo(),
                            idOperacion,
                            numeroAsiento,
                            fecha,
                            concepto
                    );


            int idAsiento =
                    asientoDAO.insertarAsiento(
                            asiento,
                            conexion
                    );


            insertarDetalle(
                    idAsiento,
                    cuentaDebe,
                    concepto,
                    monto,
                    BigDecimal.ZERO,
                    conexion
            );


            insertarDetalle(
                    idAsiento,
                    cuentaHaber,
                    concepto,
                    BigDecimal.ZERO,
                    monto,
                    conexion
            );


            conexion.commit();


            return new ResultadoOperacionGeneral(
                    idOperacion,
                    idAsiento,
                    numeroAsiento,
                    monto,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    monto
            );


        } catch (Exception e) {

            rollback(
                    conexion
            );


            throw new RuntimeException(
                    "No se pudo registrar la operacion: "
                    + e.getMessage(),
                    e
            );


        } finally {

            cerrarConexion(
                    conexion
            );
        }
    }


    // =====================================================
    // CREAR OPERACION
    // =====================================================

    private Operacion crearOperacionBase(
            int idPeriodo,
            LocalDate fecha,
            String tipoOperacion,
            String concepto,
            BigDecimal subtotal,
            BigDecimal iva,
            BigDecimal total,
            String medio
    ) {

        Operacion operacion =
                new Operacion();


        operacion.setIdPeriodo(
                idPeriodo
        );


        operacion.setFecha(
                fecha
        );


        operacion.setTipoOperacion(
                tipoOperacion
        );


        operacion.setConcepto(
                concepto
        );


        operacion.setIdProducto(
                null
        );


        operacion.setCantidad(
                null
        );


        operacion.setPrecioUnitario(
                null
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
                medio
        );


        return operacion;
    }


    // =====================================================
    // CREAR ASIENTO
    // =====================================================

    private AsientoContable crearAsiento(
            int idPeriodo,
            int idOperacion,
            int numeroAsiento,
            LocalDate fecha,
            String concepto
    ) {

        AsientoContable asiento =
                new AsientoContable();


        asiento.setIdPeriodo(
                idPeriodo
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
                concepto
        );


        asiento.setTipoAsiento(
                "AUTOMATICO"
        );


        asiento.setEstado(
                "CONTABILIZADO"
        );


        return asiento;
    }


    // =====================================================
    // INSERTAR DETALLE
    // =====================================================

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


    // =====================================================
    // CUENTA CAJA / BANCO
    // =====================================================

    private Cuenta obtenerCuentaMedio(
            String medio
    ) {

        return switch (medio) {

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


    // =====================================================
    // VALIDACIONES
    // =====================================================

    private void validarOperacionSimple(
            LocalDate fecha,
            BigDecimal monto,
            String medio
    ) {

        if (fecha == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha."
            );
        }


        if (monto == null
                || monto.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero."
            );
        }


        if (!"EFECTIVO".equals(
                medio
        )
                &&
            !"BANCO".equals(
                medio
        )) {

            throw new IllegalArgumentException(
                    "El medio debe ser EFECTIVO o BANCO."
            );
        }
    }


    private void validarCuenta(
            Cuenta cuenta,
            String descripcion
    ) {

        if (cuenta == null) {

            throw new IllegalStateException(
                    "No se encontro la cuenta: "
                    + descripcion
                    + "."
            );
        }


        if (!cuenta.isActivo()) {

            throw new IllegalStateException(
                    "La cuenta "
                    + cuenta.getNombre()
                    + " esta desactivada."
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
            String concepto,
            String valorDefecto
    ) {

        if (concepto == null
                || concepto.isBlank()) {

            return valorDefecto;
        }


        return concepto.trim();
    }


    // =====================================================
    // TRANSACCIONES
    // =====================================================

    private void rollback(
            Connection conexion
    ) {

        if (conexion != null) {

            try {

                conexion.rollback();

            } catch (SQLException e) {

                System.out.println(
                        "Error en rollback: "
                        + e.getMessage()
                );
            }
        }
    }


    private void cerrarConexion(
            Connection conexion
    ) {

        if (conexion != null) {

            try {

                conexion.setAutoCommit(
                        true
                );

                conexion.close();

            } catch (SQLException e) {

                System.out.println(
                        "Error al cerrar conexion: "
                        + e.getMessage()
                );
            }
        }
    }
}