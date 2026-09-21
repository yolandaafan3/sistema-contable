package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Operacion {

    private int idOperacion;
    private int idPeriodo;
    private LocalDate fecha;

    private String tipoOperacion;
    private String concepto;

    private Integer idProducto;
    private Integer idOperacionOrigen;

    private BigDecimal cantidad;
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;

    private String formaPago;

    public Operacion() {
    }

    public Operacion(int idOperacion, int idPeriodo, LocalDate fecha,
                     String tipoOperacion, String concepto,
                     Integer idProducto, BigDecimal cantidad,
                     BigDecimal precioUnitario, BigDecimal subtotal,
                     BigDecimal iva, BigDecimal total,
                     String formaPago) {

        this.idOperacion = idOperacion;
        this.idPeriodo = idPeriodo;
        this.fecha = fecha;
        this.tipoOperacion = tipoOperacion;
        this.concepto = concepto;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.formaPago = formaPago;
    }

    public int getIdOperacion() {
        return idOperacion;
    }

    public void setIdOperacion(int idOperacion) {
        this.idOperacion = idOperacion;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Integer getIdOperacionOrigen() { return idOperacionOrigen; }
    public void setIdOperacionOrigen(Integer v) { idOperacionOrigen = v; }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }
}