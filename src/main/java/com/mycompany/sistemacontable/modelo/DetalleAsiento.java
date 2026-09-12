package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class DetalleAsiento {

    private int idDetalle;
    private int idAsiento;
    private int idCuenta;

    private String descripcion;

    private BigDecimal debe;
    private BigDecimal haber;

    public DetalleAsiento() {
    }

    public DetalleAsiento(int idDetalle, int idAsiento,
                          int idCuenta, String descripcion,
                          BigDecimal debe, BigDecimal haber) {

        this.idDetalle = idDetalle;
        this.idAsiento = idAsiento;
        this.idCuenta = idCuenta;
        this.descripcion = descripcion;
        this.debe = debe;
        this.haber = haber;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public int getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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