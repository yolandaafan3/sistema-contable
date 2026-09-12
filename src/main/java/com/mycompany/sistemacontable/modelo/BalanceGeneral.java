package com.mycompany.sistemacontable.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BalanceGeneral {

    private List<LineaBalanceGeneral> activosCorrientes;
    private List<LineaBalanceGeneral> activosNoCorrientes;

    private List<LineaBalanceGeneral> pasivosCorrientes;
    private List<LineaBalanceGeneral> pasivosNoCorrientes;

    private List<LineaBalanceGeneral> patrimonio;

    private BigDecimal totalActivoCorriente;
    private BigDecimal totalActivoNoCorriente;
    private BigDecimal totalActivo;

    private BigDecimal totalPasivoCorriente;
    private BigDecimal totalPasivoNoCorriente;
    private BigDecimal totalPasivo;

    private BigDecimal totalPatrimonio;

    private BigDecimal totalPasivoPatrimonio;

    private BigDecimal diferencia;

    private boolean cuadrado;

    public BalanceGeneral() {

        activosCorrientes = new ArrayList<>();
        activosNoCorrientes = new ArrayList<>();

        pasivosCorrientes = new ArrayList<>();
        pasivosNoCorrientes = new ArrayList<>();

        patrimonio = new ArrayList<>();

        totalActivoCorriente = BigDecimal.ZERO;
        totalActivoNoCorriente = BigDecimal.ZERO;
        totalActivo = BigDecimal.ZERO;

        totalPasivoCorriente = BigDecimal.ZERO;
        totalPasivoNoCorriente = BigDecimal.ZERO;
        totalPasivo = BigDecimal.ZERO;

        totalPatrimonio = BigDecimal.ZERO;

        totalPasivoPatrimonio = BigDecimal.ZERO;

        diferencia = BigDecimal.ZERO;

        cuadrado = false;
    }

    public List<LineaBalanceGeneral> getActivosCorrientes() {
        return activosCorrientes;
    }

    public void setActivosCorrientes(
            List<LineaBalanceGeneral> activosCorrientes
    ) {
        this.activosCorrientes = activosCorrientes;
    }

    public List<LineaBalanceGeneral> getActivosNoCorrientes() {
        return activosNoCorrientes;
    }

    public void setActivosNoCorrientes(
            List<LineaBalanceGeneral> activosNoCorrientes
    ) {
        this.activosNoCorrientes = activosNoCorrientes;
    }

    public List<LineaBalanceGeneral> getPasivosCorrientes() {
        return pasivosCorrientes;
    }

    public void setPasivosCorrientes(
            List<LineaBalanceGeneral> pasivosCorrientes
    ) {
        this.pasivosCorrientes = pasivosCorrientes;
    }

    public List<LineaBalanceGeneral> getPasivosNoCorrientes() {
        return pasivosNoCorrientes;
    }

    public void setPasivosNoCorrientes(
            List<LineaBalanceGeneral> pasivosNoCorrientes
    ) {
        this.pasivosNoCorrientes = pasivosNoCorrientes;
    }

    public List<LineaBalanceGeneral> getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(
            List<LineaBalanceGeneral> patrimonio
    ) {
        this.patrimonio = patrimonio;
    }

    public BigDecimal getTotalActivoCorriente() {
        return totalActivoCorriente;
    }

    public void setTotalActivoCorriente(
            BigDecimal totalActivoCorriente
    ) {
        this.totalActivoCorriente = totalActivoCorriente;
    }

    public BigDecimal getTotalActivoNoCorriente() {
        return totalActivoNoCorriente;
    }

    public void setTotalActivoNoCorriente(
            BigDecimal totalActivoNoCorriente
    ) {
        this.totalActivoNoCorriente = totalActivoNoCorriente;
    }

    public BigDecimal getTotalActivo() {
        return totalActivo;
    }

    public void setTotalActivo(
            BigDecimal totalActivo
    ) {
        this.totalActivo = totalActivo;
    }

    public BigDecimal getTotalPasivoCorriente() {
        return totalPasivoCorriente;
    }

    public void setTotalPasivoCorriente(
            BigDecimal totalPasivoCorriente
    ) {
        this.totalPasivoCorriente = totalPasivoCorriente;
    }

    public BigDecimal getTotalPasivoNoCorriente() {
        return totalPasivoNoCorriente;
    }

    public void setTotalPasivoNoCorriente(
            BigDecimal totalPasivoNoCorriente
    ) {
        this.totalPasivoNoCorriente = totalPasivoNoCorriente;
    }

    public BigDecimal getTotalPasivo() {
        return totalPasivo;
    }

    public void setTotalPasivo(
            BigDecimal totalPasivo
    ) {
        this.totalPasivo = totalPasivo;
    }

    public BigDecimal getTotalPatrimonio() {
        return totalPatrimonio;
    }

    public void setTotalPatrimonio(
            BigDecimal totalPatrimonio
    ) {
        this.totalPatrimonio = totalPatrimonio;
    }

    public BigDecimal getTotalPasivoPatrimonio() {
        return totalPasivoPatrimonio;
    }

    public void setTotalPasivoPatrimonio(
            BigDecimal totalPasivoPatrimonio
    ) {
        this.totalPasivoPatrimonio = totalPasivoPatrimonio;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(
            BigDecimal diferencia
    ) {
        this.diferencia = diferencia;
    }

    public boolean isCuadrado() {
        return cuadrado;
    }

    public void setCuadrado(boolean cuadrado) {
        this.cuadrado = cuadrado;
    }
}