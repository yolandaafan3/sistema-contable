package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class LineaBalanceGeneral {

    private int idCuenta;

    private String codigo;
    private String nombre;

    private String tipo;
    private String clasificacion;
    private String naturaleza;
    private String rolReporte;

    private BigDecimal totalDebe;
    private BigDecimal totalHaber;

    private BigDecimal saldo;

    public LineaBalanceGeneral() {

        totalDebe = BigDecimal.ZERO;
        totalHaber = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}