package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class Producto {
    private int idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal costoCompra;
    private BigDecimal precioVenta;
    private BigDecimal costoInicial;
    private BigDecimal valorInventarioInicial;
    private BigDecimal existenciaInicial;
    private BigDecimal existenciaActual;
    private boolean activo;

    public Producto() {}

    public int getIdProducto(){ return idProducto; }
    public void setIdProducto(int v){ idProducto=v; }
    public String getCodigo(){ return codigo; }
    public void setCodigo(String v){ codigo=v; }
    public String getNombre(){ return nombre; }
    public void setNombre(String v){ nombre=v; }
    public String getDescripcion(){ return descripcion; }
    public void setDescripcion(String v){ descripcion=v; }
    public BigDecimal getCostoCompra(){ return costoCompra; }
    public void setCostoCompra(BigDecimal v){ costoCompra=v; }
    public BigDecimal getPrecioVenta(){ return precioVenta; }
    public void setPrecioVenta(BigDecimal v){ precioVenta=v; }
    public BigDecimal getCostoInicial(){ return costoInicial; }
    public void setCostoInicial(BigDecimal v){ costoInicial=v; }
    public BigDecimal getValorInventarioInicial(){ return valorInventarioInicial; }
    public void setValorInventarioInicial(BigDecimal v){ valorInventarioInicial=v; }
    public BigDecimal getExistenciaInicial(){ return existenciaInicial; }
    public void setExistenciaInicial(BigDecimal v){ existenciaInicial=v; }
    public BigDecimal getExistenciaActual(){ return existenciaActual; }
    public void setExistenciaActual(BigDecimal v){ existenciaActual=v; }
    public boolean isActivo(){ return activo; }
    public void setActivo(boolean v){ activo=v; }
    @Override public String toString(){ return codigo + " - " + nombre; }
}
