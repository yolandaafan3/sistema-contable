package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;

public class ConfiguracionContable {

    private int idConfiguracion;
    private int idEmpresa;
    private BigDecimal porcentajeIva;
    private String tipoIva;
    private String moneda;

    public ConfiguracionContable() {
    }

    public ConfiguracionContable(int idConfiguracion,
                                 int idEmpresa,
                                 BigDecimal porcentajeIva,
                                 String tipoIva,
                                 String moneda) {

        this.idConfiguracion = idConfiguracion;
        this.idEmpresa = idEmpresa;
        this.porcentajeIva = porcentajeIva;
        this.tipoIva = tipoIva;
        this.moneda = moneda;
    }

    public int getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(int idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public BigDecimal getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(BigDecimal porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    public String getTipoIva() {
        return tipoIva;
    }

    public void setTipoIva(String tipoIva) {
        this.tipoIva = tipoIva;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
}