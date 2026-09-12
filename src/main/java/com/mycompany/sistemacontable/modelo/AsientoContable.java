package com.mycompany.sistemacontable.modelo;

import java.time.LocalDate;

public class AsientoContable {

    private int idAsiento;
    private int idPeriodo;
    private Integer idOperacion;

    private int numeroAsiento;

    private LocalDate fecha;

    private String concepto;
    private String tipoAsiento;
    private String estado;

    public AsientoContable() {
    }

    public AsientoContable(int idAsiento, int idPeriodo,
                           Integer idOperacion, int numeroAsiento,
                           LocalDate fecha, String concepto,
                           String tipoAsiento, String estado) {

        this.idAsiento = idAsiento;
        this.idPeriodo = idPeriodo;
        this.idOperacion = idOperacion;
        this.numeroAsiento = numeroAsiento;
        this.fecha = fecha;
        this.concepto = concepto;
        this.tipoAsiento = tipoAsiento;
        this.estado = estado;
    }

    public int getIdAsiento() {
        return idAsiento;
    }

    public void setIdAsiento(int idAsiento) {
        this.idAsiento = idAsiento;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public Integer getIdOperacion() {
        return idOperacion;
    }

    public void setIdOperacion(Integer idOperacion) {
        this.idOperacion = idOperacion;
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

    public String getTipoAsiento() {
        return tipoAsiento;
    }

    public void setTipoAsiento(String tipoAsiento) {
        this.tipoAsiento = tipoAsiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}