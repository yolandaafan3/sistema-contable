package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CapaPeps {

    private int idCapa;
    private int idProducto;
    private int idKardexEntrada;

    private LocalDate fecha;

    private BigDecimal cantidadOriginal;
    private BigDecimal cantidadDisponible;
    private BigDecimal costoUnitario;

    public CapaPeps() {
    }

    public CapaPeps(int idCapa,
                    int idProducto,
                    int idKardexEntrada,
                    LocalDate fecha,
                    BigDecimal cantidadOriginal,
                    BigDecimal cantidadDisponible,
                    BigDecimal costoUnitario) {

        this.idCapa = idCapa;
        this.idProducto = idProducto;
        this.idKardexEntrada = idKardexEntrada;
        this.fecha = fecha;
        this.cantidadOriginal = cantidadOriginal;
        this.cantidadDisponible = cantidadDisponible;
        this.costoUnitario = costoUnitario;
    }

    public int getIdCapa() {
        return idCapa;
    }

    public void setIdCapa(int idCapa) {
        this.idCapa = idCapa;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdKardexEntrada() {
        return idKardexEntrada;
    }

    public void setIdKardexEntrada(int idKardexEntrada) {
        this.idKardexEntrada = idKardexEntrada;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getCantidadOriginal() {
        return cantidadOriginal;
    }

    public void setCantidadOriginal(BigDecimal cantidadOriginal) {
        this.cantidadOriginal = cantidadOriginal;
    }

    public BigDecimal getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(BigDecimal cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }
}