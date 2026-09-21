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

        BalanceGeneral balance = new BalanceGeneral();

        BigDecimal totalActivoCorriente = BigDecimal.ZERO;
        BigDecimal totalActivoNoCorriente = BigDecimal.ZERO;
        BigDecimal totalPasivoCorriente = BigDecimal.ZERO;
        BigDecimal totalPasivoNoCorriente = BigDecimal.ZERO;
        BigDecimal totalPatrimonio = BigDecimal.ZERO;

        // IVA se presenta NETEADO: si IVA Crédito > IVA Débito aparece
        // como "IVA remanente a favor" en Activo Corriente. Si ocurre
        // lo contrario aparece "Impuesto a pagar por IVA" en Pasivo
        // Corriente. De esta forma no se muestran ambos saldos a la vez.
        BigDecimal ivaCredito = BigDecimal.ZERO;
        BigDecimal ivaDebito = BigDecimal.ZERO;

        for (LineaBalanceGeneral cuenta : cuentas) {
            if ("IVA_CREDITO".equals(cuenta.getRolReporte())) {
                ivaCredito = positivoOZero(calcularSaldo(cuenta));
            } else if ("IVA_DEBITO".equals(cuenta.getRolReporte())) {
                ivaDebito = positivoOZero(calcularSaldo(cuenta));
            }
        }

        for (LineaBalanceGeneral cuenta : cuentas) {

            // Las cuentas IVA individuales no se agregan directamente al
            // Balance General porque se presentarán neteadas más abajo.
            if ("IVA_CREDITO".equals(cuenta.getRolReporte())
                    || "IVA_DEBITO".equals(cuenta.getRolReporte())) {
                continue;
            }

            BigDecimal saldo = calcularSaldo(cuenta);

            if ("INVENTARIO".equals(cuenta.getRolReporte())) {
                saldo = estadoResultados.getInventarioFinal();
            }

            if ("UTILIDAD_EJERCICIO".equals(cuenta.getRolReporte())) {
                saldo = estadoResultados.getUtilidadEjercicio();
            }

            saldo = escalar(saldo);
            cuenta.setSaldo(saldo);

            if (saldo.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            if ("ACTIVO".equals(cuenta.getTipo())) {
                if ("CORRIENTE".equals(cuenta.getClasificacion())) {
                    balance.getActivosCorrientes().add(cuenta);
                    totalActivoCorriente = totalActivoCorriente.add(saldo);
                } else if ("NO_CORRIENTE".equals(cuenta.getClasificacion())) {
                    balance.getActivosNoCorrientes().add(cuenta);
                    totalActivoNoCorriente = totalActivoNoCorriente.add(saldo);
                }
            } else if ("PASIVO".equals(cuenta.getTipo())) {
                if ("CORRIENTE".equals(cuenta.getClasificacion())) {
                    balance.getPasivosCorrientes().add(cuenta);
                    totalPasivoCorriente = totalPasivoCorriente.add(saldo);
                } else if ("NO_CORRIENTE".equals(cuenta.getClasificacion())) {
                    balance.getPasivosNoCorrientes().add(cuenta);
                    totalPasivoNoCorriente = totalPasivoNoCorriente.add(saldo);
                }
            } else if ("PATRIMONIO".equals(cuenta.getTipo())) {
                balance.getPatrimonio().add(cuenta);
                totalPatrimonio = totalPatrimonio.add(saldo);
            }
        }

        // Presentación neta del IVA.
        BigDecimal diferenciaIva = ivaCredito.subtract(ivaDebito);

        if (diferenciaIva.compareTo(BigDecimal.ZERO) > 0) {
            LineaBalanceGeneral remanente = crearLineaCalculada(
                    "IVA-REM",
                    "IVA remanente a favor",
                    "ACTIVO",
                    "CORRIENTE",
                    escalar(diferenciaIva)
            );
            balance.getActivosCorrientes().add(remanente);
            totalActivoCorriente = totalActivoCorriente.add(remanente.getSaldo());
        } else if (diferenciaIva.compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal impuestoPagar = diferenciaIva.abs();
            LineaBalanceGeneral impuesto = crearLineaCalculada(
                    "IVA-PAG",
                    "Impuesto a pagar por IVA",
                    "PASIVO",
                    "CORRIENTE",
                    escalar(impuestoPagar)
            );
            balance.getPasivosCorrientes().add(impuesto);
            totalPasivoCorriente = totalPasivoCorriente.add(impuesto.getSaldo());
        }

        BigDecimal totalActivo = totalActivoCorriente.add(totalActivoNoCorriente);
        BigDecimal totalPasivo = totalPasivoCorriente.add(totalPasivoNoCorriente);
        BigDecimal totalPasivoPatrimonio = totalPasivo.add(totalPatrimonio);
        BigDecimal diferencia = totalActivo.subtract(totalPasivoPatrimonio);

        totalActivoCorriente = escalar(totalActivoCorriente);
        totalActivoNoCorriente = escalar(totalActivoNoCorriente);
        totalActivo = escalar(totalActivo);
        totalPasivoCorriente = escalar(totalPasivoCorriente);
        totalPasivoNoCorriente = escalar(totalPasivoNoCorriente);
        totalPasivo = escalar(totalPasivo);
        totalPatrimonio = escalar(totalPatrimonio);
        totalPasivoPatrimonio = escalar(totalPasivoPatrimonio);
        diferencia = escalar(diferencia);

        balance.setTotalActivoCorriente(totalActivoCorriente);
        balance.setTotalActivoNoCorriente(totalActivoNoCorriente);
        balance.setTotalActivo(totalActivo);
        balance.setTotalPasivoCorriente(totalPasivoCorriente);
        balance.setTotalPasivoNoCorriente(totalPasivoNoCorriente);
        balance.setTotalPasivo(totalPasivo);
        balance.setTotalPatrimonio(totalPatrimonio);
        balance.setTotalPasivoPatrimonio(totalPasivoPatrimonio);
        balance.setDiferencia(diferencia);
        balance.setCuadrado(
                diferencia.abs().compareTo(new BigDecimal("0.01")) <= 0
        );

        return balance;
    }

    private LineaBalanceGeneral crearLineaCalculada(
            String codigo,
            String nombre,
            String tipo,
            String clasificacion,
            BigDecimal saldo
    ) {
        LineaBalanceGeneral linea = new LineaBalanceGeneral();
        linea.setCodigo(codigo);
        linea.setNombre(nombre);
        linea.setTipo(tipo);
        linea.setClasificacion(clasificacion);
        linea.setNaturaleza("DEUDORA");
        linea.setRolReporte("NINGUNO");
        linea.setSaldo(saldo);
        return linea;
    }

    private BigDecimal positivoOZero(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return valor;
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