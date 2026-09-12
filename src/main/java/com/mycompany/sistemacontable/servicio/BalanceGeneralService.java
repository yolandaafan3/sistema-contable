package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.BalanceGeneralDAO;

import com.mycompany.sistemacontable.modelo.BalanceGeneral;
import com.mycompany.sistemacontable.modelo.EstadoResultados;
import com.mycompany.sistemacontable.modelo.LineaBalanceGeneral;
import com.mycompany.sistemacontable.modelo.PeriodoContable;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.List;

public class BalanceGeneralService {

    private final BalanceGeneralDAO balanceGeneralDAO;
    private final PeriodoService periodoService;
    private final EstadoResultadosService estadoResultadosService;

    public BalanceGeneralService() {

        balanceGeneralDAO =
                new BalanceGeneralDAO();

        periodoService =
                new PeriodoService();

        estadoResultadosService =
                new EstadoResultadosService();
    }


    public BalanceGeneral generar() {

        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();

        EstadoResultados estadoResultados =
                estadoResultadosService.generar();

        List<LineaBalanceGeneral> cuentas =
                balanceGeneralDAO.obtenerCuentas(
                        periodo.getIdPeriodo()
                );


        BalanceGeneral balance =
                new BalanceGeneral();


        BigDecimal totalActivoCorriente =
                BigDecimal.ZERO;

        BigDecimal totalActivoNoCorriente =
                BigDecimal.ZERO;

        BigDecimal totalPasivoCorriente =
                BigDecimal.ZERO;

        BigDecimal totalPasivoNoCorriente =
                BigDecimal.ZERO;

        BigDecimal totalPatrimonio =
                BigDecimal.ZERO;


        for (LineaBalanceGeneral cuenta : cuentas) {

            BigDecimal saldo =
                    calcularSaldo(
                            cuenta
                    );


            // ==========================================
            // INVENTARIO FINAL
            // ==========================================

            if ("INVENTARIO".equals(
                    cuenta.getRolReporte()
            )) {

                saldo =
                        estadoResultados
                                .getInventarioFinal();
            }


            // ==========================================
            // UTILIDAD / PERDIDA DEL EJERCICIO
            // ==========================================

            if ("UTILIDAD_EJERCICIO".equals(
                    cuenta.getRolReporte()
            )) {

                saldo =
                        estadoResultados
                                .getUtilidadEjercicio();
            }


            saldo =
                    saldo.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );


            cuenta.setSaldo(
                    saldo
            );


            // Las cuentas padre sin saldo no necesitan
            // aparecer en los valores finales.

            if (saldo.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

                continue;
            }


            // ==========================================
            // ACTIVOS
            // ==========================================

            if ("ACTIVO".equals(
                    cuenta.getTipo()
            )) {

                if ("CORRIENTE".equals(
                        cuenta.getClasificacion()
                )) {

                    balance.getActivosCorrientes()
                            .add(cuenta);

                    totalActivoCorriente =
                            totalActivoCorriente.add(
                                    saldo
                            );

                } else if ("NO_CORRIENTE".equals(
                        cuenta.getClasificacion()
                )) {

                    balance.getActivosNoCorrientes()
                            .add(cuenta);

                    totalActivoNoCorriente =
                            totalActivoNoCorriente.add(
                                    saldo
                            );
                }
            }


            // ==========================================
            // PASIVOS
            // ==========================================

            else if ("PASIVO".equals(
                    cuenta.getTipo()
            )) {

                if ("CORRIENTE".equals(
                        cuenta.getClasificacion()
                )) {

                    balance.getPasivosCorrientes()
                            .add(cuenta);

                    totalPasivoCorriente =
                            totalPasivoCorriente.add(
                                    saldo
                            );

                } else if ("NO_CORRIENTE".equals(
                        cuenta.getClasificacion()
                )) {

                    balance.getPasivosNoCorrientes()
                            .add(cuenta);

                    totalPasivoNoCorriente =
                            totalPasivoNoCorriente.add(
                                    saldo
                            );
                }
            }


            // ==========================================
            // PATRIMONIO
            // ==========================================

            else if ("PATRIMONIO".equals(
                    cuenta.getTipo()
            )) {

                balance.getPatrimonio()
                        .add(cuenta);

                totalPatrimonio =
                        totalPatrimonio.add(
                                saldo
                        );
            }
        }


        // ==============================================
        // TOTALES
        // ==============================================

        BigDecimal totalActivo =
                totalActivoCorriente.add(
                        totalActivoNoCorriente
                );


        BigDecimal totalPasivo =
                totalPasivoCorriente.add(
                        totalPasivoNoCorriente
                );


        BigDecimal totalPasivoPatrimonio =
                totalPasivo.add(
                        totalPatrimonio
                );


        BigDecimal diferencia =
                totalActivo.subtract(
                        totalPasivoPatrimonio
                );


        totalActivoCorriente =
                escalar(totalActivoCorriente);

        totalActivoNoCorriente =
                escalar(totalActivoNoCorriente);

        totalActivo =
                escalar(totalActivo);

        totalPasivoCorriente =
                escalar(totalPasivoCorriente);

        totalPasivoNoCorriente =
                escalar(totalPasivoNoCorriente);

        totalPasivo =
                escalar(totalPasivo);

        totalPatrimonio =
                escalar(totalPatrimonio);

        totalPasivoPatrimonio =
                escalar(totalPasivoPatrimonio);

        diferencia =
                escalar(diferencia);


        balance.setTotalActivoCorriente(
                totalActivoCorriente
        );

        balance.setTotalActivoNoCorriente(
                totalActivoNoCorriente
        );

        balance.setTotalActivo(
                totalActivo
        );


        balance.setTotalPasivoCorriente(
                totalPasivoCorriente
        );

        balance.setTotalPasivoNoCorriente(
                totalPasivoNoCorriente
        );

        balance.setTotalPasivo(
                totalPasivo
        );


        balance.setTotalPatrimonio(
                totalPatrimonio
        );


        balance.setTotalPasivoPatrimonio(
                totalPasivoPatrimonio
        );


        balance.setDiferencia(
                diferencia
        );


        balance.setCuadrado(
                diferencia.abs().compareTo(
                        new BigDecimal("0.01")
                ) <= 0
        );


        return balance;
    }


    private BigDecimal calcularSaldo(
            LineaBalanceGeneral cuenta
    ) {

        BigDecimal debe =
                cuenta.getTotalDebe();

        BigDecimal haber =
                cuenta.getTotalHaber();


        if ("DEUDORA".equals(
                cuenta.getNaturaleza()
        )) {

            return debe.subtract(
                    haber
            );
        }


        return haber.subtract(
                debe
        );
    }


    private BigDecimal escalar(
            BigDecimal valor
    ) {

        return valor.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}