package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;

import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.CuentaDAO;

import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.modelo.ResultadoAsientoManual;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Connection;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.List;

public class AsientoManualService {

    private final AsientoDAO asientoDAO;
    private final CuentaDAO cuentaDAO;
    private final PeriodoService periodoService;


    public AsientoManualService() {

        asientoDAO =
                new AsientoDAO();

        cuentaDAO =
                new CuentaDAO();

        periodoService =
                new PeriodoService();
    }


    // =========================================================
    // REGISTRAR ASIENTO MANUAL
    // =========================================================

    public ResultadoAsientoManual registrar(
            LocalDate fecha,
            String concepto,
            List<DetalleAsiento> detalles
    ) {

        // =====================================================
        // VALIDACIONES GENERALES
        // =====================================================

        if (fecha == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha."
            );
        }


        periodoService.validarFecha(
                fecha
        );


        if (concepto == null
                || concepto.isBlank()) {

            throw new IllegalArgumentException(
                    "El concepto del asiento es obligatorio."
            );
        }


        if (detalles == null
                || detalles.size() < 2) {

            throw new IllegalArgumentException(
                    "El asiento debe tener al menos dos lineas."
            );
        }


        // =====================================================
        // VALIDAR DETALLES Y TOTALES
        // =====================================================

        BigDecimal totalDebe =
                BigDecimal.ZERO;

        BigDecimal totalHaber =
                BigDecimal.ZERO;


        for (DetalleAsiento detalle : detalles) {

            validarDetalle(
                    detalle
            );


            BigDecimal debe =
                    normalizarMonto(
                            detalle.getDebe()
                    );


            BigDecimal haber =
                    normalizarMonto(
                            detalle.getHaber()
                    );


            detalle.setDebe(
                    debe
            );


            detalle.setHaber(
                    haber
            );


            totalDebe =
                    totalDebe.add(
                            debe
                    );


            totalHaber =
                    totalHaber.add(
                            haber
                    );
        }


        totalDebe =
                normalizarMonto(
                        totalDebe
                );


        totalHaber =
                normalizarMonto(
                        totalHaber
                );


        if (totalDebe.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "El asiento debe contener movimientos."
            );
        }


        if (totalDebe.compareTo(
                totalHaber
        ) != 0) {

            BigDecimal diferencia =
                    totalDebe.subtract(
                            totalHaber
                    ).abs();


            throw new IllegalArgumentException(
                    "El asiento no esta cuadrado. "
                    + "Debe: $"
                    + totalDebe
                    + " | Haber: $"
                    + totalHaber
                    + " | Diferencia: $"
                    + diferencia
            );
        }


        // =====================================================
        // PERIODO
        // =====================================================

        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();


        if (periodo == null) {

            throw new IllegalStateException(
                    "No existe un periodo contable abierto."
            );
        }


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
            // CABECERA
            // =================================================

            AsientoContable asiento =
                    new AsientoContable();


            asiento.setIdPeriodo(
                    periodo.getIdPeriodo()
            );


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
                    concepto.trim()
            );


            asiento.setTipoAsiento(
                    "MANUAL"
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
            // DETALLES
            // =================================================

            for (DetalleAsiento detalle : detalles) {

                detalle.setIdAsiento(
                        idAsiento
                );


                asientoDAO.insertarDetalle(
                        detalle,
                        conexion
                );
            }


            // =================================================
            // COMMIT
            // =================================================

            conexion.commit();


            return new ResultadoAsientoManual(
                    idAsiento,
                    numeroAsiento,
                    totalDebe,
                    totalHaber
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
                    "No se pudo registrar el asiento manual: "
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


    // =========================================================
    // VALIDAR DETALLE
    // =========================================================

    private void validarDetalle(
            DetalleAsiento detalle
    ) {

        if (detalle == null) {

            throw new IllegalArgumentException(
                    "Existe una linea de asiento vacia."
            );
        }


        Cuenta cuenta =
                cuentaDAO.buscarPorId(
                        detalle.getIdCuenta()
                );


        if (cuenta == null) {

            throw new IllegalArgumentException(
                    "Una de las cuentas seleccionadas no existe."
            );
        }


        if (!cuenta.isActivo()) {

            throw new IllegalArgumentException(
                    "La cuenta "
                    + cuenta.getCodigo()
                    + " - "
                    + cuenta.getNombre()
                    + " esta desactivada."
            );
        }


        if (!cuenta.isPermiteMovimiento()) {

            throw new IllegalArgumentException(
                    "La cuenta "
                    + cuenta.getCodigo()
                    + " - "
                    + cuenta.getNombre()
                    + " no permite movimientos directos."
            );
        }


        // =====================================================
        // PROTEGER INVENTARIO
        // =====================================================

        validarCuentaInventario(
                cuenta
        );


        // =====================================================
        // DEBE / HABER
        // =====================================================

        BigDecimal debe =
                normalizarMonto(
                        detalle.getDebe()
                );


        BigDecimal haber =
                normalizarMonto(
                        detalle.getHaber()
                );


        if (debe.compareTo(
                BigDecimal.ZERO
        ) < 0
                ||
            haber.compareTo(
                    BigDecimal.ZERO
            ) < 0) {

            throw new IllegalArgumentException(
                    "Los valores Debe y Haber no pueden ser negativos."
            );
        }


        if (debe.compareTo(
                BigDecimal.ZERO
        ) > 0
                &&
            haber.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

            throw new IllegalArgumentException(
                    "Una linea no puede contener valor "
                    + "en Debe y Haber al mismo tiempo."
            );
        }


        if (debe.compareTo(
                BigDecimal.ZERO
        ) == 0
                &&
            haber.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

            throw new IllegalArgumentException(
                    "Cada linea debe contener un valor "
                    + "en Debe o Haber."
            );
        }
    }


    // =========================================================
    // PROTEGER CUENTA INVENTARIO
    // =========================================================

    private void validarCuentaInventario(
            Cuenta cuenta
    ) {

        Cuenta cuentaInventario =
                cuentaDAO.buscarPorRol(
                        "INVENTARIO"
                );


        boolean esInventario =
                cuentaInventario != null
                &&
                cuentaInventario.getIdCuenta()
                == cuenta.getIdCuenta();


        if (!esInventario
                &&
            "1.1.03".equals(
                    cuenta.getCodigo()
            )) {

            esInventario =
                    true;
        }


        if (esInventario) {

            throw new IllegalArgumentException(
                    "La cuenta de Inventario no puede modificarse "
                    + "mediante un asiento manual. "
                    + "Utilice las operaciones de inventario para "
                    + "mantener sincronizados el Kardex y PEPS."
            );
        }
    }


    // =========================================================
    // NORMALIZAR MONTO
    // =========================================================

    private BigDecimal normalizarMonto(
            BigDecimal valor
    ) {

        if (valor == null) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }


        return valor.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}