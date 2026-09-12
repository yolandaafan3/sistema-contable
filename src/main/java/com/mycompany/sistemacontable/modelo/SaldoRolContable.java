package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class SaldoRolContable {

    private String rolReporte;
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;

    public SaldoRolContable() {
        totalDebe = BigDecimal.ZERO;
        totalHaber = BigDecimal.ZERO;
    }

    public SaldoRolContable(
            String rolReporte,
            BigDecimal totalDebe,
            BigDecimal totalHaber
    ) {
        this.rolReporte = rolReporte;
        this.totalDebe = totalDebe;
        this.totalHaber = totalHaber;
    }

    public String getRolReporte() {
        return rolReporte;
    }

    public void setRolReporte(String rolReporte) {
        this.rolReporte = rolReporte;
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