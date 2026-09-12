package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class ResultadoIVA {

    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    public ResultadoIVA() {
    }

    public ResultadoIVA(BigDecimal subtotal,
                        BigDecimal iva,
                        BigDecimal total) {

        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
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