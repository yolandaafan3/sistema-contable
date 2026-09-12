package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class Producto {

    private int idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;

    private BigDecimal costoCompra;
    private BigDecimal precioVenta;

    private BigDecimal existenciaInicial;
    private BigDecimal existenciaActual;

    private boolean activo;

    public Producto() {
    }

    public Producto(int idProducto, String codigo, String nombre,
                    String descripcion, BigDecimal costoCompra,
                    BigDecimal precioVenta,
                    BigDecimal existenciaInicial,
                    BigDecimal existenciaActual,
                    boolean activo) {

        this.idProducto = idProducto;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.costoCompra = costoCompra;
        this.precioVenta = precioVenta;
        this.existenciaInicial = existenciaInicial;
        this.existenciaActual = existenciaActual;
        this.activo = activo;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getCostoCompra() {
        return costoCompra;
    }

    public void setCostoCompra(BigDecimal costoCompra) {
        this.costoCompra = costoCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getExistenciaInicial() {
        return existenciaInicial;
    }

    public void setExistenciaInicial(BigDecimal existenciaInicial) {
        this.existenciaInicial = existenciaInicial;
    }

    public BigDecimal getExistenciaActual() {
        return existenciaActual;
    }

    public void setExistenciaActual(BigDecimal existenciaActual) {
        this.existenciaActual = existenciaActual;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}