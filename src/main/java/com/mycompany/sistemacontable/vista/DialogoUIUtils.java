package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.servicio.PeriodoService;
import java.awt.Color;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpinnerDateModel;

final class DialogoUIUtils {

    private DialogoUIUtils() {
    }

    static JScrollPane envolverEnScroll(
            JPanel contenido,
            Color colorFondo
    ) {

        JScrollPane scroll =
                new JScrollPane(
                        contenido
                );

        scroll.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(16);

        scroll.getViewport()
                .setBackground(colorFondo);

        scroll.setBackground(colorFondo);
        scroll.setWheelScrollingEnabled(true);

        return scroll;
    }

    /**
     * Modelo de fecha acotado al período contable abierto.
     * Si hoy no pertenece al período, usa el extremo válido más cercano.
     */
    static SpinnerDateModel crearModeloFechaPeriodoActivo() {
        try {
            PeriodoContable periodo = new PeriodoService().obtenerPeriodoActivo();
            LocalDate inicio = periodo.getFechaInicio();
            LocalDate fin = periodo.getFechaFin();
            LocalDate hoy = LocalDate.now();

            LocalDate fechaInicial;
            if (hoy.isBefore(inicio)) {
                fechaInicial = inicio;
            } else if (hoy.isAfter(fin)) {
                fechaInicial = fin;
            } else {
                fechaInicial = hoy;
            }

            Date valor = convertir(fechaInicial);
            Date minimo = convertir(inicio);
            Date maximo = convertir(fin);

            return new SpinnerDateModel(
                    valor,
                    minimo,
                    maximo,
                    Calendar.DAY_OF_MONTH
            );
        } catch (Exception e) {
            // Los servicios contables siguen validando la fecha al guardar.
            return new SpinnerDateModel();
        }
    }

    private static Date convertir(LocalDate fecha) {
        return Date.from(
                fecha.atStartOfDay(ZoneId.systemDefault()).toInstant()
        );
    }
}
