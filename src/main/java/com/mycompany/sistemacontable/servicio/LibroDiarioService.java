package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.LibroDiarioDAO;

import com.mycompany.sistemacontable.modelo.LineaLibroDiario;
import com.mycompany.sistemacontable.modelo.PeriodoContable;

import java.math.BigDecimal;

import java.util.List;

public class LibroDiarioService {

    private final LibroDiarioDAO libroDiarioDAO;
    private final PeriodoService periodoService;

    public LibroDiarioService() {

        libroDiarioDAO =
                new LibroDiarioDAO();

        periodoService =
                new PeriodoService();
    }


    public List<LineaLibroDiario> obtenerLibroDiario() {

        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();

        return libroDiarioDAO.listar(
                periodo.getIdPeriodo()
        );
    }


    public BigDecimal obtenerTotalDebe() {

        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();

        return libroDiarioDAO.obtenerTotalDebe(
                periodo.getIdPeriodo()
        );
    }


    public BigDecimal obtenerTotalHaber() {

        PeriodoContable periodo =
                periodoService.obtenerPeriodoActivo();

        return libroDiarioDAO.obtenerTotalHaber(
                periodo.getIdPeriodo()
        );
    }


    public boolean estaCuadrado() {

        BigDecimal debe =
                obtenerTotalDebe();

        BigDecimal haber =
                obtenerTotalHaber();

        return debe.compareTo(haber) == 0;
    }
}