package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimientoMayor {

    private int numeroAsiento;
    private LocalDate fecha;
    private String concepto;

    private BigDecimal debe;
    private BigDecimal haber;

    public MovimientoMayor() {
    }

    public MovimientoMayor(
            int numeroAsiento,
            LocalDate fecha,
            String concepto,
            BigDecimal debe,
            BigDecimal haber
    ) {

        this.numeroAsiento = numeroAsiento;
        this.fecha = fecha;
        this.concepto = concepto;
        this.debe = debe;
        this.haber = haber;
    }

    public int getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(int numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getDebe() {
        return debe;
    }

    public void setDebe(BigDecimal debe) {
        this.debe = debe;
    }

    public BigDecimal getHaber() {
        return haber;
    }

    public void setHaber(BigDecimal haber) {
        this.haber = haber;
    }
}