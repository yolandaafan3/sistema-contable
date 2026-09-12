package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DistribucionPago {

    private BigDecimal efectivo;
    private BigDecimal banco;
    private BigDecimal credito;

    public DistribucionPago() {

        efectivo = BigDecimal.ZERO;
        banco = BigDecimal.ZERO;
        credito = BigDecimal.ZERO;
    }

    public DistribucionPago(
            BigDecimal efectivo,
            BigDecimal banco,
            BigDecimal credito
    ) {

        this.efectivo = normalizar(efectivo);
        this.banco = normalizar(banco);
        this.credito = normalizar(credito);
    }

    public BigDecimal getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(BigDecimal efectivo) {
        this.efectivo = normalizar(efectivo);
    }

    public BigDecimal getBanco() {
        return banco;
    }

    public void setBanco(BigDecimal banco) {
        this.banco = normalizar(banco);
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = normalizar(credito);
    }

    public BigDecimal getTotal() {

        return normalizar(
                efectivo
                        .add(banco)
                        .add(credito)
        );
    }

    public String obtenerFormaPago() {

        int usados = 0;

        if (efectivo.compareTo(BigDecimal.ZERO) > 0) {
            usados++;
        }

        if (banco.compareTo(BigDecimal.ZERO) > 0) {
            usados++;
        }

        if (credito.compareTo(BigDecimal.ZERO) > 0) {
            usados++;
        }

        if (usados > 1) {
            return "MIXTO";
        }

        if (efectivo.compareTo(BigDecimal.ZERO) > 0) {
            return "EFECTIVO";
        }

        if (banco.compareTo(BigDecimal.ZERO) > 0) {
            return "BANCO";
        }

        if (credito.compareTo(BigDecimal.ZERO) > 0) {
            return "CREDITO";
        }

        return "NO_APLICA";
    }

    private BigDecimal normalizar(
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