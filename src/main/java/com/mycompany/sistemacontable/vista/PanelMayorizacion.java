package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.CuentaMayor;
import com.mycompany.sistemacontable.modelo.MovimientoMayor;
import com.mycompany.sistemacontable.servicio.MayorizacionService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

public class PanelMayorizacion extends JPanel {

    private final MayorizacionService service = new MayorizacionService();
    private final JPanel panelCuentas = new JPanel();
    private final JLabel lblResumen = new JLabel("Sin datos");

    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Color FONDO = new Color(245, 247, 250);
    private final Color TEXTO = new Color(30, 41, 59);
    private final Color SECUNDARIO = new Color(100, 116, 139);
    private final Color BORDE = new Color(226, 232, 240);
    private final Color PRIMARIO = new Color(37, 99, 235);

    public PanelMayorizacion() {
        setLayout(new BorderLayout(0, 18));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(28, 30, 30, 30));

        add(crearEncabezado(), BorderLayout.NORTH);

        panelCuentas.setOpaque(false);
        panelCuentas.setLayout(new BoxLayout(panelCuentas, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(panelCuentas);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        lblResumen.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblResumen.setForeground(SECUNDARIO);
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pie.setOpaque(false);
        pie.add(lblResumen);
        add(pie, BorderLayout.SOUTH);

        cargarMayorizacion();
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Mayorización");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel("Cuentas T generadas automáticamente a partir del Libro Diario.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JButton actualizar = crearBoton("Actualizar");
        actualizar.addActionListener(e -> cargarMayorizacion());

        panel.add(textos, BorderLayout.WEST);
        panel.add(actualizar, BorderLayout.EAST);
        return panel;
    }

    public final void cargarMayorizacion() {
        try {
            panelCuentas.removeAll();
            List<CuentaMayor> cuentas = service.obtenerMayorizacion();

            if (cuentas.isEmpty()) {
                JLabel vacio = new JLabel("No hay movimientos para mayorizar.");
                vacio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                vacio.setForeground(SECUNDARIO);
                panelCuentas.add(vacio);
            } else {
                for (CuentaMayor cuenta : cuentas) {
                    panelCuentas.add(crearCuentaT(cuenta));
                    panelCuentas.add(Box.createVerticalStrut(16));
                }
            }

            lblResumen.setText(cuentas.size() + " cuenta(s) con movimiento");
            panelCuentas.revalidate();
            panelCuentas.repaint();
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private JPanel crearCuentaT(CuentaMayor cuenta) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        JLabel titulo = new JLabel(cuenta.getCodigo() + "  ·  " + cuenta.getNombre());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(TEXTO);
        tarjeta.add(titulo, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridLayout(1, 2, 0, 0));
        cuerpo.setBackground(Color.WHITE);
        cuerpo.add(crearLadoT("DEBE", cuenta.getMovimientos(), true));
        cuerpo.add(crearLadoT("HABER", cuenta.getMovimientos(), false));
        tarjeta.add(cuerpo, BorderLayout.CENTER);

        String saldo = cuenta.getSaldo() == null ? "$0.00" : dinero(cuenta.getSaldo());
        JLabel pie = new JLabel(
                "Total Debe: " + dinero(cuenta.getTotalDebe())
                + "     |     Total Haber: " + dinero(cuenta.getTotalHaber())
                + "     |     Saldo " + cuenta.getTipoSaldo() + ": " + saldo
        );
        pie.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pie.setForeground(TEXTO);
        tarjeta.add(pie, BorderLayout.SOUTH);

        return tarjeta;
    }

    private JPanel crearLadoT(String titulo, List<MovimientoMayor> movimientos, boolean debe) {
        JPanel lado = new JPanel();
        lado.setBackground(Color.WHITE);
        lado.setLayout(new BoxLayout(lado, BoxLayout.Y_AXIS));
        lado.setBorder(BorderFactory.createCompoundBorder(
                debe
                        ? BorderFactory.createMatteBorder(1, 1, 1, 1, BORDE)
                        : BorderFactory.createMatteBorder(1, 0, 1, 1, BORDE),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel cabecera = new JLabel(titulo, SwingConstants.CENTER);
        cabecera.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cabecera.setForeground(TEXTO);
        cabecera.setAlignmentX(CENTER_ALIGNMENT);
        lado.add(cabecera);
        lado.add(Box.createVerticalStrut(8));

        boolean hay = false;
        for (MovimientoMayor m : movimientos) {
            BigDecimal valor = debe ? m.getDebe() : m.getHaber();
            if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
                hay = true;
                String fecha = m.getFecha() == null ? "" : m.getFecha().format(formatoFecha);
                JLabel linea = new JLabel(
                        "As. " + m.getNumeroAsiento() + " · " + fecha + "   " + dinero(valor)
                );
                linea.setToolTipText(m.getConcepto());
                linea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                linea.setForeground(TEXTO);
                lado.add(linea);
                lado.add(Box.createVerticalStrut(5));
            }
        }

        if (!hay) {
            JLabel vacio = new JLabel("—");
            vacio.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            vacio.setForeground(SECUNDARIO);
            lado.add(vacio);
        }
        return lado;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(PRIMARIO);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private String dinero(BigDecimal valor) {
        if (valor == null) valor = BigDecimal.ZERO;
        return "$" + String.format("%,.2f", valor.setScale(2, RoundingMode.HALF_UP));
    }

    private void mostrarError(Throwable error) {
        Throwable actual = error;
        String mensaje = "Ocurrió un error desconocido.";
        while (actual != null) {
            if (actual.getMessage() != null && !actual.getMessage().isBlank()) mensaje = actual.getMessage();
            actual = actual.getCause();
        }
        JOptionPane.showMessageDialog(this, mensaje, "No se pudo cargar la mayorización", JOptionPane.ERROR_MESSAGE);
    }
}
