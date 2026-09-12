package com.mycompany.sistemacontable.vista;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
}
