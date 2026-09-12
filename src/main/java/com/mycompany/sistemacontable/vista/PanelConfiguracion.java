package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.ConfiguracionContable;
import com.mycompany.sistemacontable.servicio.ConfiguracionService;
import com.mycompany.sistemacontable.servicio.ReinicioSistemaService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicButtonUI;

public class PanelConfiguracion extends JPanel {

    private final ReinicioSistemaService reinicioService;
    private final ConfiguracionService configuracionService;

    private JLabel lblConfiguracionActual;
    private JTextField txtPorcentajeIva;
    private JComboBox<String> cmbTipoIva;

    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private final Color COLOR_TEXTO = new Color(30, 41, 59);
    private final Color COLOR_SECUNDARIO = new Color(100, 116, 139);
    private final Color COLOR_BORDE = new Color(226, 232, 240);
    private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private final Color COLOR_ROJO = new Color(220, 38, 38);
    private final Color COLOR_ROJO_HOVER = new Color(185, 28, 28);

    public PanelConfiguracion() {
        reinicioService = new ReinicioSistemaService();
        configuracionService = new ConfiguracionService();
        configurarPanel();
        construirInterfaz();
        cargarConfiguracion();
    }

    private void configurarPanel() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
    }

    private void construirInterfaz() {
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Configuración");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Administración, IVA y herramientas generales del sistema.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(COLOR_SECUNDARIO);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(5));
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(28));
        contenido.add(crearTarjetaIva());
        contenido.add(Box.createVerticalStrut(20));
        contenido.add(crearTarjetaReinicio());

        add(contenido, BorderLayout.NORTH);
    }

    private JPanel crearTarjetaIva() {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("Configuración de IVA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(COLOR_TEXTO);

        lblConfiguracionActual = new JLabel("Cargando configuración...");
        lblConfiguracionActual.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblConfiguracionActual.setForeground(COLOR_SECUNDARIO);

        JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        campos.setOpaque(false);

        JLabel lblPorcentaje = new JLabel("IVA (%):");
        lblPorcentaje.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPorcentaje.setForeground(COLOR_TEXTO);

        txtPorcentajeIva = new JTextField(8);
        txtPorcentajeIva.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPorcentajeIva.setPreferredSize(new Dimension(100, 36));

        JLabel lblModo = new JLabel("Modo:");
        lblModo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblModo.setForeground(COLOR_TEXTO);

        cmbTipoIva = new JComboBox<>(new String[]{
            "IVA incluido en el monto",
            "Monto + IVA"
        });
        cmbTipoIva.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipoIva.setPreferredSize(new Dimension(210, 36));

        JButton btnGuardar = crearBotonPrincipal("Guardar IVA");
        btnGuardar.addActionListener(e -> guardarIva());

        campos.add(lblPorcentaje);
        campos.add(txtPorcentajeIva);
        campos.add(lblModo);
        campos.add(cmbTipoIva);
        campos.add(btnGuardar);

        JLabel ayuda = new JLabel(
                "<html><div style='width:760px;'>"
                + "<b>IVA incluido:</b> si escribes 10,000, ese valor ya contiene IVA. "
                + "<b>Monto + IVA:</b> si escribes 10,000, el sistema agrega el IVA encima."
                + "<br><br><b>Importante:</b> el cambio aplica solamente a operaciones nuevas; "
                + "los asientos ya contabilizados no se modifican."
                + "</div></html>"
        );
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ayuda.setForeground(COLOR_SECUNDARIO);

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblConfiguracionActual);
        tarjeta.add(Box.createVerticalStrut(18));
        tarjeta.add(campos);
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(ayuda);
        return tarjeta;
    }

    public final void cargarConfiguracion() {
        try {
            ConfiguracionContable c = configuracionService.obtener();
            txtPorcentajeIva.setText(c.getPorcentajeIva().stripTrailingZeros().toPlainString());
            cmbTipoIva.setSelectedIndex("MAS_IVA".equals(c.getTipoIva()) ? 1 : 0);
            lblConfiguracionActual.setText(
                    "Configuración actual: IVA " + c.getPorcentajeIva().stripTrailingZeros().toPlainString()
                    + "% · " + ("MAS_IVA".equals(c.getTipoIva()) ? "Monto + IVA" : "IVA incluido")
                    + " · Moneda " + c.getMoneda()
            );
        } catch (Exception e) {
            mostrarError(e, "No se pudo cargar la configuración");
        }
    }

    private void guardarIva() {
        try {
            String texto = txtPorcentajeIva.getText().trim().replace(",", ".");
            BigDecimal porcentaje = new BigDecimal(texto);
            String tipo = cmbTipoIva.getSelectedIndex() == 1 ? "MAS_IVA" : "INCLUIDO";

            int respuesta = JOptionPane.showConfirmDialog(
                    this,
                    "¿Guardar esta configuración de IVA?\n\n"
                    + "IVA: " + porcentaje + "%\n"
                    + "Modo: " + ("MAS_IVA".equals(tipo) ? "Monto + IVA" : "IVA incluido")
                    + "\n\nSolo afectará operaciones nuevas.",
                    "Confirmar configuración",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (respuesta != JOptionPane.YES_OPTION) return;

            configuracionService.actualizarIva(porcentaje, tipo);
            cargarConfiguracion();

            JOptionPane.showMessageDialog(
                    this,
                    "Configuración de IVA actualizada correctamente.",
                    "IVA actualizado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El porcentaje de IVA no es válido.", "Dato incorrecto", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            mostrarError(e, "No se pudo guardar la configuración");
        }
    }

    private JPanel crearTarjetaReinicio() {
        JPanel tarjeta = new JPanel(new BorderLayout(20, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(254, 202, 202)),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        tarjeta.setAlignmentX(LEFT_ALIGNMENT);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Zona de pruebas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(COLOR_TEXTO);

        JLabel descripcion = new JLabel(
                "<html><div style='width:650px;'>"
                + "Reinicia operaciones, asientos, detalles, Kardex, capas PEPS y existencias."
                + "<br>El catálogo, empresa, período, configuración y productos se conservan."
                + "</div></html>"
        );
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descripcion.setForeground(COLOR_SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(8));
        textos.add(descripcion);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        acciones.setOpaque(false);
        JButton btnReiniciar = crearBotonPeligro("Reiniciar datos de prueba");
        btnReiniciar.addActionListener(e -> confirmarReinicio());
        acciones.add(btnReiniciar);

        tarjeta.add(textos, BorderLayout.CENTER);
        tarjeta.add(acciones, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JButton crearBotonPrincipal(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(COLOR_PRIMARIO);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private JButton crearBotonPeligro(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(COLOR_ROJO);
        boton.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { boton.setBackground(COLOR_ROJO_HOVER); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { boton.setBackground(COLOR_ROJO); }
        });
        return boton;
    }

    private void confirmarReinicio() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas limpiar los datos de prueba?\n\n"
                + "Se eliminarán operaciones, asientos, detalles, Kardex, capas PEPS y existencias.",
                "Confirmar reinicio",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (respuesta != JOptionPane.YES_OPTION) return;

        int segunda = JOptionPane.showConfirmDialog(
                this,
                "Esta acción no se puede deshacer.\n\n¿Deseas continuar?",
                "Última confirmación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (segunda != JOptionPane.YES_OPTION) return;

        try {
            String resultado = reinicioService.reiniciarDatosPrueba();
            JOptionPane.showMessageDialog(this, resultado, "Sistema reiniciado", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            mostrarError(e, "Error al reiniciar");
        }
    }

    private void mostrarError(Throwable error, String titulo) {
        Throwable actual = error;
        String mensaje = "Ocurrió un error desconocido.";
        while (actual != null) {
            if (actual.getMessage() != null && !actual.getMessage().isBlank()) mensaje = actual.getMessage();
            actual = actual.getCause();
        }
        JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.ERROR_MESSAGE);
    }
}
