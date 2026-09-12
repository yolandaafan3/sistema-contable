package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;

import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.CuentaDAO;

import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.PeriodoContable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;

public class TransferenciaEfectivoService {

    private final CuentaDAO cuentaDAO;
    private final AsientoDAO asientoDAO;
    private final PeriodoService periodoService;


    public TransferenciaEfectivoService() {

        cuentaDAO =
                new CuentaDAO();

        asientoDAO =
                new AsientoDAO();

        periodoService =
                new PeriodoService();
    }


    public String registrarTransferenciaCajaBanco(
            LocalDate fecha,
            BigDecimal montoTransferencia,
            String concepto
    ) {

        // =====================================================
        // VALIDACIONES
        // =====================================================

        validarDatos(
                fecha,
                montoTransferencia
        );


        periodoService.validarFecha(
                fecha
        );


        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();


        // =====================================================
        // CUENTAS
        // =====================================================

        Cuenta caja =
                cuentaDAO.buscarPorCodigo(
                        "1.1.01.01"
                );


        Cuenta banco =
                cuentaDAO.buscarPorCodigo(
                        "1.1.01.02"
                );


        validarCuenta(
                caja,
                "Caja"
        );


        validarCuenta(
                banco,
                "Banco"
        );


        BigDecimal monto =
                montoTransferencia
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
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
            // NUMERO DE ASIENTO
            // =================================================

            int numeroAsiento =
                    asientoDAO.obtenerSiguienteNumero(
                            periodo.getIdPeriodo(),
                            conexion
                    );


            // =================================================
            // ASIENTO
            // =================================================

            AsientoContable asiento =
                    new AsientoContable();


            asiento.setIdPeriodo(
                    periodo.getIdPeriodo()
            );


            /*
             * Esta transferencia no pertenece a una compra,
             * venta o movimiento de inventario.
             *
             * Por eso puede existir como asiento automático
             * sin una operación comercial asociada.
             */

            asiento.setIdOperacion(
                    null
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
            // BANCO - DEBE
            // =================================================
            //
            // El dinero entra al Banco.
            //
            // =================================================

            insertarDetalle(
                    idAsiento,
                    banco,
                    "Transferencia recibida desde Caja",
                    monto,
                    BigDecimal.ZERO,
                    conexion
            );


            // =================================================
            // CAJA - HABER
            // =================================================
            //
            // El dinero sale de Caja.
            //
            // =================================================

            insertarDetalle(
                    idAsiento,
                    caja,
                    "Transferencia enviada a Banco",
                    BigDecimal.ZERO,
                    monto,
                    conexion
            );


            conexion.commit();


            return """
                   Transferencia registrada correctamente.

                   Asiento N.º %d

                   Banco:
                   DEBE $%,.2f

                   Caja:
                   HABER $%,.2f
                   """
                    .formatted(
                            numeroAsiento,
                            monto,
                            monto
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
                    "No se pudo registrar la transferencia: "
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
    // VALIDACIONES
    // =========================================================

    private void validarDatos(
            LocalDate fecha,
            BigDecimal monto
    ) {

        if (fecha == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha."
            );
        }


        if (monto == null
                ||
            monto.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

            throw new IllegalArgumentException(
                    "El monto de la transferencia "
                    + "debe ser mayor que cero."
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


    // =========================================================
    // CONCEPTO
    // =========================================================

    private String prepararConcepto(
            String concepto
    ) {

        if (concepto == null
                ||
            concepto.isBlank()) {

            return "Transferencia de Caja a Banco";
        }


        return concepto.trim();
    }
}