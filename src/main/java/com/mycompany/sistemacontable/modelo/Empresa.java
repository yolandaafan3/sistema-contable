package com.mycompany.sistemacontable.modelo;

public class Empresa {

    private int idEmpresa;
    private String nombre;
    private String nit;
    private String nrc;
    private String giroComercial;
    private String direccion;
    private String telefono;
    private String correo;
    private boolean activo;

    public Empresa() {
    }

    public Empresa(int idEmpresa, String nombre, String nit, String nrc,
               String giroComercial, String direccion, String telefono,
               String correo, boolean activo) {

    this.idEmpresa = idEmpresa;
    this.nombre = nombre;
    this.nit = nit;
    this.nrc = nrc;
    this.giroComercial = giroComercial;
    this.direccion = direccion;
    this.telefono = telefono;
    this.correo = correo;
    this.activo = activo;
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

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }
    public String getGiroComercial() {
    return giroComercial;
}

public void setGiroComercial(String giroComercial) {
    this.giroComercial = giroComercial;
}

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}