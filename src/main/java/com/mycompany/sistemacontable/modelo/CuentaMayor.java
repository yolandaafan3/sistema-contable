package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CuentaMayor {

    private int idCuenta;
    private String codigo;
    private String nombre;
    private String naturaleza;

    private BigDecimal totalDebe;
    private BigDecimal totalHaber;

    private BigDecimal saldo;
    private String tipoSaldo;

    private List<MovimientoMayor> movimientos;

    public CuentaMayor() {

        totalDebe = BigDecimal.ZERO;
        totalHaber = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;

        movimientos = new ArrayList<>();
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

    public String getNaturaleza() {
        return naturaleza;
    }

    public void setNaturaleza(String naturaleza) {
        this.naturaleza = naturaleza;
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

    public String getTipoSaldo() {
        return tipoSaldo;
    }

    public void setTipoSaldo(String tipoSaldo) {
        this.tipoSaldo = tipoSaldo;
    }

    public List<MovimientoMayor> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(
            List<MovimientoMayor> movimientos
    ) {

        this.movimientos = movimientos;
    }

    public void agregarMovimiento(
            MovimientoMayor movimiento
    ) {

        movimientos.add(movimiento);
    }
}