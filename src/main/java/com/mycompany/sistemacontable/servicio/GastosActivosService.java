package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;

import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.dao.OperacionDAO;

import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.DistribucionPago;
import com.mycompany.sistemacontable.modelo.Operacion;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.modelo.ResultadoGastoActivo;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;

public class GastosActivosService {

    private final CuentaDAO cuentaDAO;
    private final OperacionDAO operacionDAO;
    private final AsientoDAO asientoDAO;

    private final PeriodoService periodoService;
    private final CalculoIVAService ivaService;

    public GastosActivosService() {

        cuentaDAO = new CuentaDAO();
        operacionDAO = new OperacionDAO();
        asientoDAO = new AsientoDAO();

        periodoService = new PeriodoService();
        ivaService = new CalculoIVAService();
    }


    // =====================================================
    // REGISTRAR GASTO
    // =====================================================

    public ResultadoGastoActivo registrarGasto(
            LocalDate fecha,
            BigDecimal monto,
            boolean aplicaIva,
            int idCuentaGasto,
            DistribucionPago distribucion,
            String concepto
    ) {

        Cuenta cuenta =
                cuentaDAO.buscarPorId(
                        idCuentaGasto
                );

        validarCuentaGasto(
                cuenta
        );

        String tipoOperacion =
                obtenerTipoOperacionGasto(
                        cuenta
                );

        return registrar(
                fecha,
                monto,
                aplicaIva,
                cuenta,
                distribucion,
                concepto,
                tipoOperacion
        );
    }


    // =====================================================
    // REGISTRAR COMPRA DE ACTIVO
    // =====================================================

    public ResultadoGastoActivo registrarCompraActivo(
            LocalDate fecha,
            BigDecimal monto,
            boolean aplicaIva,
            int idCuentaActivo,
            DistribucionPago distribucion,
            String concepto
    ) {

        Cuenta cuenta =
                cuentaDAO.buscarPorId(
                        idCuentaActivo
                );

        validarCuentaActivo(
                cuenta
        );

        return registrar(
                fecha,
                monto,
                aplicaIva,
                cuenta,
                distribucion,
                concepto,
                "COMPRA_ACTIVO"
        );
    }


    // =====================================================
    // REGISTRO GENERAL
    // =====================================================

    private ResultadoGastoActivo registrar(
            LocalDate fecha,
            BigDecimal monto,
            boolean aplicaIva,
            Cuenta cuentaPrincipal,
            DistribucionPago distribucion,
            String concepto,
            String tipoOperacion
    ) {

        validarDatos(
                fecha,
                monto,
                distribucion
        );

        periodoService.validarFecha(
                fecha
        );

        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();


        // =================================================
        // IVA
        // =================================================

        BigDecimal subtotal;
        BigDecimal iva;
        BigDecimal total;


        if (aplicaIva) {

            ResultadoIVA resultadoIVA =
                    ivaService.calcular(
                            monto
                    );

            subtotal =
                    resultadoIVA
                            .getSubtotal();

            iva =
                    resultadoIVA
                            .getIva();

            total =
                    resultadoIVA
                            .getTotal();

        } else {

            subtotal =
                    monto.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            iva =
                    BigDecimal.ZERO.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            total =
                    subtotal;
        }


        // =================================================
        // DISTRIBUCION DEL PAGO
        // =================================================

        BigDecimal totalDistribuido =
                distribucion.getTotal();


        if (totalDistribuido.compareTo(
                total
        ) != 0) {

            throw new IllegalArgumentException(
                    "La distribucion del pago no coincide con el total. "
                    + "Total de la operacion: $"
                    + total
                    + " | Distribuido: $"
                    + totalDistribuido
            );
        }


        Cuenta ivaCredito =
                null;


        if (iva.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            ivaCredito =
                    cuentaDAO.buscarPorRol(
                            "IVA_CREDITO"
                    );


            validarCuenta(
                    ivaCredito,
                    "IVA Credito Fiscal"
            );
        }


        Cuenta caja =
                null;

        Cuenta banco =
                null;

        Cuenta acreedores =
                null;


        if (distribucion
                .getEfectivo()
                .compareTo(
                        BigDecimal.ZERO
                ) > 0) {

            caja =
                    cuentaDAO.buscarPorCodigo(
                            "1.1.01.01"
                    );

            validarCuenta(
                    caja,
                    "Caja"
            );
        }


        if (distribucion
                .getBanco()
                .compareTo(
                        BigDecimal.ZERO
                ) > 0) {

            banco =
                    cuentaDAO.buscarPorCodigo(
                            "1.1.01.02"
                    );

            validarCuenta(
                    banco,
                    "Banco"
            );
        }


        if (distribucion
                .getCredito()
                .compareTo(
                        BigDecimal.ZERO
                ) > 0) {

            acreedores =
                    cuentaDAO.buscarPorCodigo(
                            "2.1.01.02"
                    );

            validarCuenta(
                    acreedores,
                    "Acreedores Varios"
            );
        }


        String conceptoFinal =
                prepararConcepto(
                        concepto,
                        cuentaPrincipal.getNombre()
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
            // OPERACION
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
                    tipoOperacion
            );


            operacion.setConcepto(
                    conceptoFinal
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
                    distribucion.obtenerFormaPago()
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
                    conceptoFinal
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
            // CUENTA PRINCIPAL - DEBE
            // =================================================

            insertarDetalle(
                    idAsiento,
                    cuentaPrincipal,
                    conceptoFinal,
                    subtotal,
                    BigDecimal.ZERO,
                    conexion
            );


            // =================================================
            // IVA CREDITO - DEBE
            // =================================================

            if (iva.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                insertarDetalle(
                        idAsiento,
                        ivaCredito,
                        "IVA Credito Fiscal",
                        iva,
                        BigDecimal.ZERO,
                        conexion
                );
            }


            // =================================================
            // EFECTIVO - HABER
            // =================================================

            if (distribucion
                    .getEfectivo()
                    .compareTo(
                            BigDecimal.ZERO
                    ) > 0) {

                insertarDetalle(
                        idAsiento,
                        caja,
                        "Pago en efectivo",
                        BigDecimal.ZERO,
                        distribucion.getEfectivo(),
                        conexion
                );
            }


            // =================================================
            // BANCO - HABER
            // =================================================

            if (distribucion
                    .getBanco()
                    .compareTo(
                            BigDecimal.ZERO
                    ) > 0) {

                insertarDetalle(
                        idAsiento,
                        banco,
                        "Pago por banco",
                        BigDecimal.ZERO,
                        distribucion.getBanco(),
                        conexion
                );
            }


            // =================================================
            // CREDITO - HABER
            // =================================================

            if (distribucion
                    .getCredito()
                    .compareTo(
                            BigDecimal.ZERO
                    ) > 0) {

                insertarDetalle(
                        idAsiento,
                        acreedores,
                        "Compra al credito",
                        BigDecimal.ZERO,
                        distribucion.getCredito(),
                        conexion
                );
            }


            // =================================================
            // COMMIT
            // =================================================

            conexion.commit();


            return new ResultadoGastoActivo(
                    idOperacion,
                    idAsiento,
                    numeroAsiento,
                    subtotal,
                    iva,
                    total
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
                    "No se pudo registrar la operacion: "
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
                            "Error al cerrar conexion: "
                            + e.getMessage()
                    );
                }
            }
        }
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
    // VALIDAR GASTO
    // =====================================================

    private void validarCuentaGasto(
            Cuenta cuenta
    ) {

        validarCuenta(
                cuenta,
                "cuenta de gasto"
        );


        if (!"GASTO".equals(
                cuenta.getTipo()
        )) {

            throw new IllegalArgumentException(
                    "La cuenta seleccionada no pertenece a GASTOS."
            );
        }


        String clasificacion =
                cuenta.getClasificacion();


        boolean valida =
                "ADMINISTRATIVO".equals(
                        clasificacion
                )
                ||
                "VENTA".equals(
                        clasificacion
                )
                ||
                "FINANCIERO".equals(
                        clasificacion
                )
                ||
                "OTRO".equals(
                        clasificacion
                );


        if (!valida) {

            throw new IllegalArgumentException(
                    "La clasificacion de la cuenta de gasto no es valida."
            );
        }
    }


    // =====================================================
    // VALIDAR ACTIVO
    // =====================================================

    private void validarCuentaActivo(
            Cuenta cuenta
    ) {

        validarCuenta(
                cuenta,
                "cuenta de activo"
        );


        if (!"ACTIVO".equals(
                cuenta.getTipo()
        )) {

            throw new IllegalArgumentException(
                    "La cuenta seleccionada no pertenece a ACTIVOS."
            );
        }


        if (!"NO_CORRIENTE".equals(
                cuenta.getClasificacion()
        )) {

            throw new IllegalArgumentException(
                    "La compra de activo debe utilizar una cuenta "
                    + "de Activo No Corriente."
            );
        }
    }


    // =====================================================
    // TIPO DE OPERACION DEL GASTO
    // =====================================================

    private String obtenerTipoOperacionGasto(
            Cuenta cuenta
    ) {

        return switch (
                cuenta.getClasificacion()
        ) {

            case "ADMINISTRATIVO" ->
                "GASTO_ADMINISTRATIVO";

            case "VENTA" ->
                "GASTO_VENTA";

            case "FINANCIERO" ->
                "GASTO_FINANCIERO";

            default ->
                "OTRO";
        };
    }


    // =====================================================
    // VALIDACIONES GENERALES
    // =====================================================

    private void validarDatos(
            LocalDate fecha,
            BigDecimal monto,
            DistribucionPago distribucion
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


        if (distribucion == null) {

            throw new IllegalArgumentException(
                    "Debe indicar la distribucion del pago."
            );
        }


        if (distribucion.getEfectivo()
                .compareTo(
                        BigDecimal.ZERO
                ) < 0
                ||
            distribucion.getBanco()
                .compareTo(
                        BigDecimal.ZERO
                ) < 0
                ||
            distribucion.getCredito()
                .compareTo(
                        BigDecimal.ZERO
                ) < 0) {

            throw new IllegalArgumentException(
                    "Los valores de pago no pueden ser negativos."
            );
        }


        if (distribucion.getTotal()
                .compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            throw new IllegalArgumentException(
                    "Debe existir al menos una forma de pago."
            );
        }
    }


    private void validarCuenta(
            Cuenta cuenta,
            String nombre
    ) {

        if (cuenta == null) {

            throw new IllegalStateException(
                    "No se encontro "
                    + nombre
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
}