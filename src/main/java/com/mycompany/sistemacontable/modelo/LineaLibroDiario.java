package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LineaLibroDiario {

    private int idAsiento;
    private int numeroAsiento;
    private LocalDate fecha;

    private String concepto;
    private String codigoCuenta;
    private String nombreCuenta;
    private String descripcion;

    private BigDecimal debe;
    private BigDecimal haber;

    public LineaLibroDiario() {
    }

    public LineaLibroDiario(
            int idAsiento,
            int numeroAsiento,
            LocalDate fecha,
            String concepto,
            String codigoCuenta,
            String nombreCuenta,
            String descripcion,
            BigDecimal debe,
            BigDecimal haber
    ) {

        this.idAsiento = idAsiento;
        this.numeroAsiento = numeroAsiento;
        this.fecha = fecha;
        this.concepto = concepto;
        this.codigoCuenta = codigoCuenta;
        this.nombreCuenta = nombreCuenta;
        this.descripcion = descripcion;
        this.debe = debe;
        this.haber = haber;
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

    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    public void setCodigoCuenta(String codigoCuenta) {
        this.codigoCuenta = codigoCuenta;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
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