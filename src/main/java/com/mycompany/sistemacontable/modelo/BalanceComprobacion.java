package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BalanceComprobacion {

    private List<LineaBalanceComprobacion> lineas;

    private BigDecimal totalMovimientoDebe;
    private BigDecimal totalMovimientoHaber;

    private BigDecimal totalSaldoDeudor;
    private BigDecimal totalSaldoAcreedor;

    private boolean movimientosCuadrados;
    private boolean saldosCuadrados;

    public BalanceComprobacion() {

        lineas = new ArrayList<>();

        totalMovimientoDebe = BigDecimal.ZERO;
        totalMovimientoHaber = BigDecimal.ZERO;

        totalSaldoDeudor = BigDecimal.ZERO;
        totalSaldoAcreedor = BigDecimal.ZERO;

        movimientosCuadrados = false;
        saldosCuadrados = false;
    }

    public List<LineaBalanceComprobacion> getLineas() {
        return lineas;
    }

    public void setLineas(
            List<LineaBalanceComprobacion> lineas
    ) {
        this.lineas = lineas;
    }

    public BigDecimal getTotalMovimientoDebe() {
        return totalMovimientoDebe;
    }

    public void setTotalMovimientoDebe(
            BigDecimal totalMovimientoDebe
    ) {
        this.totalMovimientoDebe = totalMovimientoDebe;
    }

    public BigDecimal getTotalMovimientoHaber() {
        return totalMovimientoHaber;
    }

    public void setTotalMovimientoHaber(
            BigDecimal totalMovimientoHaber
    ) {
        this.totalMovimientoHaber = totalMovimientoHaber;
    }

    public BigDecimal getTotalSaldoDeudor() {
        return totalSaldoDeudor;
    }

    public void setTotalSaldoDeudor(
            BigDecimal totalSaldoDeudor
    ) {
        this.totalSaldoDeudor = totalSaldoDeudor;
    }

    public BigDecimal getTotalSaldoAcreedor() {
        return totalSaldoAcreedor;
    }

    public void setTotalSaldoAcreedor(
            BigDecimal totalSaldoAcreedor
    ) {
        this.totalSaldoAcreedor = totalSaldoAcreedor;
    }

    public boolean isMovimientosCuadrados() {
        return movimientosCuadrados;
    }

    public void setMovimientosCuadrados(
            boolean movimientosCuadrados
    ) {
        this.movimientosCuadrados = movimientosCuadrados;
    }

    public boolean isSaldosCuadrados() {
        return saldosCuadrados;
    }

    public void setSaldosCuadrados(
            boolean saldosCuadrados
    ) {
        this.saldosCuadrados = saldosCuadrados;
    }
}