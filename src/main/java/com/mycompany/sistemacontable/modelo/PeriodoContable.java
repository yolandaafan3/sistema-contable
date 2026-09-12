package com.mycompany.sistemacontable.modelo;

import java.time.LocalDate;

public class PeriodoContable {

    private int idPeriodo;
    private int idEmpresa;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;

    public PeriodoContable() {
    }

    public PeriodoContable(int idPeriodo, int idEmpresa, String nombre,
                           LocalDate fechaInicio, LocalDate fechaFin,
                           String estado) {

        this.idPeriodo = idPeriodo;
        this.idEmpresa = idEmpresa;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}