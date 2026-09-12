package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class ResultadoOperacionGeneral {

    private int idOperacion;
    private int idAsiento;
    private int numeroAsiento;

    private BigDecimal monto;
    private BigDecimal comision;
    private BigDecimal iva;
    private BigDecimal montoNeto;

    public ResultadoOperacionGeneral() {
    }

    public ResultadoOperacionGeneral(
            int idOperacion,
            int idAsiento,
            int numeroAsiento,
            BigDecimal monto,
            BigDecimal comision,
            BigDecimal iva,
            BigDecimal montoNeto
    ) {

        this.idOperacion = idOperacion;
        this.idAsiento = idAsiento;
        this.numeroAsiento = numeroAsiento;
        this.monto = monto;
        this.comision = comision;
        this.iva = iva;
        this.montoNeto = montoNeto;
    }

    public int getIdOperacion() {
        return idOperacion;
    }

    public void setIdOperacion(int idOperacion) {
        this.idOperacion = idOperacion;
    }

    public int getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public int getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(int numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public BigDecimal getComision() {
        return comision;
    }

    public void setComision(BigDecimal comision) {
        this.comision = comision;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getMontoNeto() {
        return montoNeto;
    }

    public void setMontoNeto(BigDecimal montoNeto) {
        this.montoNeto = montoNeto;
    }
}