package com.mycompany.sistemacontable;

import com.mycompany.sistemacontable.servicio.InicializacionSistemaService;
import com.mycompany.sistemacontable.vista.VentanaLogin;
import com.mycompany.sistemacontable.servicio.InicializacionSeguridadService;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SistemaContable {

    public static void main(String[] args) {

        configurarLookAndFeel();


        try {

            InicializacionSistemaService inicializacion =
                    new InicializacionSistemaService();


            inicializacion.prepararCatalogoBase();

            new InicializacionSeguridadService().prepararSeguridad();


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    e.getMessage(),
                    "Error al preparar el sistema",
                    JOptionPane.ERROR_MESSAGE
            );


            return;
        }


        SwingUtilities.invokeLater(() -> {

            VentanaLogin ventana =
                    new VentanaLogin();


            ventana.setVisible(
                    true
            );
        });
    }


    private static void configurarLookAndFeel() {

        try {

            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
            );

        } catch (Exception e) {

            System.out.println(
                    "No se pudo cargar el estilo del sistema: "
                    + e.getMessage()
            );
        }
    }
}