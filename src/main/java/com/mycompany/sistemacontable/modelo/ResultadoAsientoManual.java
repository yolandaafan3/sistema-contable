package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class ResultadoAsientoManual {

    private int idAsiento;
    private int numeroAsiento;

    private BigDecimal totalDebe;
    private BigDecimal totalHaber;

    public ResultadoAsientoManual() {
    }

    public ResultadoAsientoManual(
            int idAsiento,
            int numeroAsiento,
            BigDecimal totalDebe,
            BigDecimal totalHaber
    ) {

        this.idAsiento = idAsiento;
        this.numeroAsiento = numeroAsiento;
        this.totalDebe = totalDebe;
        this.totalHaber = totalHaber;
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

    public BigDecimal getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(BigDecimal totalDebe) {
        this.totalDebe = totalDebe;
    }

    public BigDecimal getTotalHaber() {
        return totalHaber;
    }

    public void setTotalHaber(BigDecimal totalHaber) {
        this.totalHaber = totalHaber;
    }
}