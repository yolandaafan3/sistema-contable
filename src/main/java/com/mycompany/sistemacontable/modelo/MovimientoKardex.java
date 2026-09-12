package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MovimientoKardex {

    private int idKardex;
    private int idProducto;

    private Integer idAsiento;
    private Integer numeroAsiento;

    private LocalDate fecha;
    private String concepto;

    private BigDecimal unidadesEntrada;
    private BigDecimal unidadesSalida;
    private BigDecimal unidadesExistencia;

    private BigDecimal costoUnitario;
    private BigDecimal costoPeps;

    private BigDecimal saldoDeudor;
    private BigDecimal saldoAcreedor;
    private BigDecimal saldo;

    public MovimientoKardex() {
    }

    public MovimientoKardex(int idKardex,
                            int idProducto,
                            Integer idAsiento,
                            LocalDate fecha,
                            String concepto,
                            BigDecimal unidadesEntrada,
                            BigDecimal unidadesSalida,
                            BigDecimal unidadesExistencia,
                            BigDecimal costoUnitario,
                            BigDecimal costoPeps,
                            BigDecimal saldoDeudor,
                            BigDecimal saldoAcreedor,
                            BigDecimal saldo) {

        this.idKardex = idKardex;
        this.idProducto = idProducto;
        this.idAsiento = idAsiento;
        this.fecha = fecha;
        this.concepto = concepto;
        this.unidadesEntrada = unidadesEntrada;
        this.unidadesSalida = unidadesSalida;
        this.unidadesExistencia = unidadesExistencia;
        this.costoUnitario = costoUnitario;
        this.costoPeps = costoPeps;
        this.saldoDeudor = saldoDeudor;
        this.saldoAcreedor = saldoAcreedor;
        this.saldo = saldo;
    }

    public int getIdKardex() {
        return idKardex;
    }

    public void setIdKardex(int idKardex) {
        this.idKardex = idKardex;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(Integer idAsiento) {
        this.idAsiento = idAsiento;
    }

    public Integer getNumeroAsiento() {
        return numeroAsiento;
    }

    public void setNumeroAsiento(Integer numeroAsiento) {
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

    public BigDecimal getUnidadesEntrada() {
        return unidadesEntrada;
    }

    public void setUnidadesEntrada(BigDecimal unidadesEntrada) {
        this.unidadesEntrada = unidadesEntrada;
    }

    public BigDecimal getUnidadesSalida() {
        return unidadesSalida;
    }

    public void setUnidadesSalida(BigDecimal unidadesSalida) {
        this.unidadesSalida = unidadesSalida;
    }

    public BigDecimal getUnidadesExistencia() {
        return unidadesExistencia;
    }

    public void setUnidadesExistencia(BigDecimal unidadesExistencia) {
        this.unidadesExistencia = unidadesExistencia;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public BigDecimal getCostoPeps() {
        return costoPeps;
    }

    public void setCostoPeps(BigDecimal costoPeps) {
        this.costoPeps = costoPeps;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}