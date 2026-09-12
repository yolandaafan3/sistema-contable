package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.PeriodoContableDAO;
import com.mycompany.sistemacontable.modelo.PeriodoContable;

import java.time.LocalDate;

public class PeriodoService {

    private final PeriodoContableDAO periodoDAO;

    public PeriodoService() {
        periodoDAO = new PeriodoContableDAO();
    }

    public PeriodoContable obtenerPeriodoActivo() {

        PeriodoContable periodo =
                periodoDAO.obtenerPeriodoAbierto();

        if (periodo == null) {

            throw new IllegalStateException(
                    "No existe un periodo contable abierto."
            );
        }

        return periodo;
    }


    public void validarFecha(LocalDate fecha) {

        if (fecha == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una fecha."
            );
        }

        PeriodoContable periodo =
                obtenerPeriodoActivo();

        if (fecha.isBefore(
                periodo.getFechaInicio()
        )) {

            throw new IllegalArgumentException(
                    "La fecha es anterior al inicio del periodo contable."
            );
        }

        if (fecha.isAfter(
                periodo.getFechaFin()
        )) {

            throw new IllegalArgumentException(
                    "La fecha es posterior al cierre del periodo contable."
            );
        }
    }


    public boolean fechaValida(LocalDate fecha) {

        try {

            validarFecha(fecha);
            return true;

        } catch (Exception e) {

            return false;
        }
    }
}