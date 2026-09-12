package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class ResultadoDevolucionVenta {

    private int idOperacion;
    private int idAsiento;
    private int numeroAsiento;

    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    private BigDecimal costoInventario;
    private BigDecimal nuevaExistencia;
    private BigDecimal saldoKardex;

    public ResultadoDevolucionVenta() {
    }

    public ResultadoDevolucionVenta(
            int idOperacion,
            int idAsiento,
            int numeroAsiento,
            BigDecimal subtotal,
            BigDecimal iva,
            BigDecimal total,
            BigDecimal costoInventario,
            BigDecimal nuevaExistencia,
            BigDecimal saldoKardex
    ) {

        this.idOperacion = idOperacion;
        this.idAsiento = idAsiento;
        this.numeroAsiento = numeroAsiento;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.costoInventario = costoInventario;
        this.nuevaExistencia = nuevaExistencia;
        this.saldoKardex = saldoKardex;
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

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getCostoInventario() {
        return costoInventario;
    }

    public void setCostoInventario(BigDecimal costoInventario) {
        this.costoInventario = costoInventario;
    }

    public BigDecimal getNuevaExistencia() {
        return nuevaExistencia;
    }

    public void setNuevaExistencia(BigDecimal nuevaExistencia) {
        this.nuevaExistencia = nuevaExistencia;
    }

    public BigDecimal getSaldoKardex() {
        return saldoKardex;
    }

    public void setSaldoKardex(BigDecimal saldoKardex) {
        this.saldoKardex = saldoKardex;
    }
}