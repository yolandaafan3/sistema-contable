package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class LineaBalanceComprobacion {

    private int idCuenta;
    private String codigo;
    private String nombre;

    private BigDecimal movimientoDebe;
    private BigDecimal movimientoHaber;

    private BigDecimal saldoDeudor;
    private BigDecimal saldoAcreedor;

    public LineaBalanceComprobacion() {
        movimientoDebe = BigDecimal.ZERO;
        movimientoHaber = BigDecimal.ZERO;
        saldoDeudor = BigDecimal.ZERO;
        saldoAcreedor = BigDecimal.ZERO;
    }

    public LineaBalanceComprobacion(
            int idCuenta,
            String codigo,
            String nombre,
            BigDecimal movimientoDebe,
            BigDecimal movimientoHaber,
            BigDecimal saldoDeudor,
            BigDecimal saldoAcreedor
    ) {

        this.idCuenta = idCuenta;
        this.codigo = codigo;
        this.nombre = nombre;
        this.movimientoDebe = movimientoDebe;
        this.movimientoHaber = movimientoHaber;
        this.saldoDeudor = saldoDeudor;
        this.saldoAcreedor = saldoAcreedor;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getMovimientoDebe() {
        return movimientoDebe;
    }

    public void setMovimientoDebe(BigDecimal movimientoDebe) {
        this.movimientoDebe = movimientoDebe;
    }

    public BigDecimal getMovimientoHaber() {
        return movimientoHaber;
    }

    public void setMovimientoHaber(BigDecimal movimientoHaber) {
        this.movimientoHaber = movimientoHaber;
    }

    public BigDecimal getSaldoDeudor() {
        return saldoDeudor;
    }

    public void setSaldoDeudor(BigDecimal saldoDeudor) {
        this.saldoDeudor = saldoDeudor;
    }

    public BigDecimal getSaldoAcreedor() {
        return saldoAcreedor;
    }

    public void setSaldoAcreedor(BigDecimal saldoAcreedor) {
        this.saldoAcreedor = saldoAcreedor;
    }
}