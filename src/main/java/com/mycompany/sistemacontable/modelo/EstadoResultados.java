package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class EstadoResultados {

    private BigDecimal ventas;
    private BigDecimal devolucionVentas;
    private BigDecimal descuentoVentas;
    private BigDecimal ventasNetas;

    private BigDecimal inventarioInicial;
    private BigDecimal inventarioFinal;

    private BigDecimal comprasBase;
    private BigDecimal compras;
    private BigDecimal gastosSobreCompras;
    private BigDecimal comprasTotales;
    private BigDecimal devolucionCompras;
    private BigDecimal descuentoCompras;
    private BigDecimal comprasNetas;
    private BigDecimal costoVentas;

    private BigDecimal utilidadBruta;

    private BigDecimal gastosAdministrativos;
    private BigDecimal gastosVenta;
    private BigDecimal gastosFinancieros;
    private BigDecimal totalGastosOperativos;
    private BigDecimal utilidadOperacionalAntesImpuesto;

    private BigDecimal otrosProductos;
    private BigDecimal utilidadEjercicio;

    public EstadoResultados() {
        ventas = BigDecimal.ZERO;
        devolucionVentas = BigDecimal.ZERO;
        descuentoVentas = BigDecimal.ZERO;
        ventasNetas = BigDecimal.ZERO;

        inventarioInicial = BigDecimal.ZERO;
        inventarioFinal = BigDecimal.ZERO;

        comprasBase = BigDecimal.ZERO;
        compras = BigDecimal.ZERO;
        gastosSobreCompras = BigDecimal.ZERO;
        comprasTotales = BigDecimal.ZERO;
        devolucionCompras = BigDecimal.ZERO;
        descuentoCompras = BigDecimal.ZERO;
        comprasNetas = BigDecimal.ZERO;
        costoVentas = BigDecimal.ZERO;

        utilidadBruta = BigDecimal.ZERO;

        gastosAdministrativos = BigDecimal.ZERO;
        gastosVenta = BigDecimal.ZERO;
        gastosFinancieros = BigDecimal.ZERO;
        totalGastosOperativos = BigDecimal.ZERO;
        utilidadOperacionalAntesImpuesto = BigDecimal.ZERO;

        otrosProductos = BigDecimal.ZERO;
        utilidadEjercicio = BigDecimal.ZERO;
    }

    public BigDecimal getVentas() { return ventas; }
    public void setVentas(BigDecimal ventas) { this.ventas = ventas; }

    public BigDecimal getDevolucionVentas() { return devolucionVentas; }
    public void setDevolucionVentas(BigDecimal devolucionVentas) { this.devolucionVentas = devolucionVentas; }

    public BigDecimal getDescuentoVentas() { return descuentoVentas; }
    public void setDescuentoVentas(BigDecimal descuentoVentas) { this.descuentoVentas = descuentoVentas; }

    public BigDecimal getVentasNetas() { return ventasNetas; }
    public void setVentasNetas(BigDecimal ventasNetas) { this.ventasNetas = ventasNetas; }

    public BigDecimal getInventarioInicial() { return inventarioInicial; }
    public void setInventarioInicial(BigDecimal inventarioInicial) { this.inventarioInicial = inventarioInicial; }

    public BigDecimal getInventarioFinal() { return inventarioFinal; }
    public void setInventarioFinal(BigDecimal inventarioFinal) { this.inventarioFinal = inventarioFinal; }

    public BigDecimal getComprasBase() { return comprasBase; }
    public void setComprasBase(BigDecimal comprasBase) { this.comprasBase = comprasBase; }

    public BigDecimal getCompras() { return compras; }
    public void setCompras(BigDecimal compras) { this.compras = compras; }

    public BigDecimal getGastosSobreCompras() { return gastosSobreCompras; }
    public void setGastosSobreCompras(BigDecimal gastosSobreCompras) { this.gastosSobreCompras = gastosSobreCompras; }

    public BigDecimal getComprasTotales() { return comprasTotales; }
    public void setComprasTotales(BigDecimal comprasTotales) { this.comprasTotales = comprasTotales; }

    public BigDecimal getDevolucionCompras() { return devolucionCompras; }
    public void setDevolucionCompras(BigDecimal devolucionCompras) { this.devolucionCompras = devolucionCompras; }

    public BigDecimal getDescuentoCompras() { return descuentoCompras; }
    public void setDescuentoCompras(BigDecimal descuentoCompras) { this.descuentoCompras = descuentoCompras; }

    public BigDecimal getComprasNetas() { return comprasNetas; }
    public void setComprasNetas(BigDecimal comprasNetas) { this.comprasNetas = comprasNetas; }

    public BigDecimal getCostoVentas() { return costoVentas; }
    public void setCostoVentas(BigDecimal costoVentas) { this.costoVentas = costoVentas; }

    public BigDecimal getMercanciaDisponible() { return comprasTotales; }
    public void setMercanciaDisponible(BigDecimal mercanciaDisponible) { this.comprasTotales = mercanciaDisponible; }

    public BigDecimal getUtilidadBruta() { return utilidadBruta; }
    public void setUtilidadBruta(BigDecimal utilidadBruta) { this.utilidadBruta = utilidadBruta; }

    public BigDecimal getGastosAdministrativos() { return gastosAdministrativos; }
    public void setGastosAdministrativos(BigDecimal gastosAdministrativos) { this.gastosAdministrativos = gastosAdministrativos; }

    public BigDecimal getGastosVenta() { return gastosVenta; }
    public void setGastosVenta(BigDecimal gastosVenta) { this.gastosVenta = gastosVenta; }

    public BigDecimal getGastosFinancieros() { return gastosFinancieros; }
    public void setGastosFinancieros(BigDecimal gastosFinancieros) { this.gastosFinancieros = gastosFinancieros; }

    public BigDecimal getTotalGastosOperativos() { return totalGastosOperativos; }
    public void setTotalGastosOperativos(BigDecimal totalGastosOperativos) { this.totalGastosOperativos = totalGastosOperativos; }

    public BigDecimal getUtilidadOperacionalAntesImpuesto() { return utilidadOperacionalAntesImpuesto; }
    public void setUtilidadOperacionalAntesImpuesto(BigDecimal utilidadOperacionalAntesImpuesto) { this.utilidadOperacionalAntesImpuesto = utilidadOperacionalAntesImpuesto; }

    public BigDecimal getOtrosProductos() { return otrosProductos; }
    public void setOtrosProductos(BigDecimal otrosProductos) { this.otrosProductos = otrosProductos; }

    public BigDecimal getUtilidadEjercicio() { return utilidadEjercicio; }
    public void setUtilidadEjercicio(BigDecimal utilidadEjercicio) { this.utilidadEjercicio = utilidadEjercicio; }
}
