package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class ResultadoGastoActivo {

    private int idOperacion;
    private int idAsiento;
    private int numeroAsiento;

    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    public ResultadoGastoActivo() {
    }

    public ResultadoGastoActivo(
            int idOperacion,
            int idAsiento,
            int numeroAsiento,
            BigDecimal subtotal,
            BigDecimal iva,
            BigDecimal total
    ) {

        this.idOperacion = idOperacion;
        this.idAsiento = idAsiento;
        this.numeroAsiento = numeroAsiento;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
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
}
