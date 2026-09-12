package com.mycompany.sistemacontable.modelo;

public class Cuenta {

    private int idCuenta;
    private String codigo;
    private String nombre;
    private String tipo;
    private String clasificacion;
    private String naturaleza;
    private String rolReporte;
    private Integer idCuentaPadre;
    private boolean permiteMovimiento;
    private boolean activo;

    public Cuenta() {
    }

    public Cuenta(int idCuenta, String codigo, String nombre,
                  String tipo, String clasificacion,
                  String naturaleza, String rolReporte,
                  Integer idCuentaPadre,
                  boolean permiteMovimiento,
                  boolean activo) {

        this.idCuenta = idCuenta;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.clasificacion = clasificacion;
        this.naturaleza = naturaleza;
        this.rolReporte = rolReporte;
        this.idCuentaPadre = idCuentaPadre;
        this.permiteMovimiento = permiteMovimiento;
        this.activo = activo;
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

    public Integer getIdCuentaPadre() {
        return idCuentaPadre;
    }

    public void setIdCuentaPadre(Integer idCuentaPadre) {
        this.idCuentaPadre = idCuentaPadre;
    }

    public boolean isPermiteMovimiento() {
        return permiteMovimiento;
    }

    public void setPermiteMovimiento(boolean permiteMovimiento) {
        this.permiteMovimiento = permiteMovimiento;
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