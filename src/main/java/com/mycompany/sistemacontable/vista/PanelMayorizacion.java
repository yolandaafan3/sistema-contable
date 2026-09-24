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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class PanelMayorizacion extends JPanel {

    private final MayorizacionService service = new MayorizacionService();

    private final JPanel panelCuentas = new JPanel();
    private final JLabel lblResumen = new JLabel("Sin datos");

    private final JTextField txtBuscar = new JTextField();
    private final JComboBox<String> cmbSaldo = new JComboBox<>(new String[]{
        "Todas las cuentas",
        "Saldo deudor",
        "Saldo acreedor"
    });

    private final JLabel lblTotalCuentas = new JLabel("0");
    private final JLabel lblDeudoras = new JLabel("0");
    private final JLabel lblAcreedoras = new JLabel("0");

    private List<CuentaMayor> cuentasActuales = new ArrayList<>();

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Color FONDO = new Color(245, 247, 250);
    private final Color TEXTO = new Color(30, 41, 59);
    private final Color SECUNDARIO = new Color(100, 116, 139);
    private final Color BORDE = new Color(226, 232, 240);
    private final Color PRIMARIO = new Color(37, 99, 235);
    private final Color PRIMARIO_HOVER = new Color(29, 78, 216);
    private final Color CELESTE_SUAVE = new Color(239, 246, 255);
    private final Color AZUL_SUAVE = new Color(219, 234, 254);
    private final Color VERDE = new Color(22, 163, 74);
    private final Color VERDE_SUAVE = new Color(240, 253, 244);
    private final Color MORADO = new Color(124, 58, 237);
    private final Color MORADO_SUAVE = new Color(245, 243, 255);

    public PanelMayorizacion() {
        setLayout(new BorderLayout(0, 16));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));

        add(crearEncabezadoCompleto(), BorderLayout.NORTH);

        panelCuentas.setOpaque(false);
        panelCuentas.setLayout(new BoxLayout(panelCuentas, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(panelCuentas);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(FONDO);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        add(crearPie(), BorderLayout.SOUTH);

        instalarEventosFiltros();
        cargarMayorizacion();
    }

    private JPanel crearEncabezadoCompleto() {
        JPanel contenedor = new JPanel();
        contenedor.setOpaque(false);
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        contenedor.add(crearEncabezado());
        contenedor.add(Box.createVerticalStrut(16));
        contenedor.add(crearResumenSuperior());
        contenedor.add(Box.createVerticalStrut(14));
        contenedor.add(crearBarraFiltros());

        return contenedor;
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setOpaque(false);

        JPanel izquierda = new JPanel(new BorderLayout(14, 0));
        izquierda.setOpaque(false);

        JLabel icono = crearIconoEncabezado();

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Mayorización");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 29));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Cuentas T generadas automáticamente a partir del Libro Diario."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        izquierda.add(icono, BorderLayout.WEST);
        izquierda.add(textos, BorderLayout.CENTER);

        JButton actualizar = crearBoton("↻  Actualizar");
        actualizar.addActionListener(e -> cargarMayorizacion());

        panel.add(izquierda, BorderLayout.WEST);
        panel.add(actualizar, BorderLayout.EAST);

        return panel;
    }

    private JLabel crearIconoEncabezado() {
        JLabel icono = new JLabel("T", SwingConstants.CENTER);
        icono.setOpaque(true);
        icono.setBackground(AZUL_SUAVE);
        icono.setForeground(PRIMARIO);
        icono.setFont(new Font("Segoe UI", Font.BOLD, 22));
        icono.setPreferredSize(new Dimension(48, 48));
        icono.setMinimumSize(new Dimension(48, 48));
        icono.setMaximumSize(new Dimension(48, 48));
        icono.setBorder(BorderFactory.createLineBorder(new Color(191, 219, 254)));
        return icono;
    }

    private JPanel crearResumenSuperior() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));

        panel.add(crearMiniTarjeta(
                "Cuentas con movimiento",
                lblTotalCuentas,
                CELESTE_SUAVE,
                PRIMARIO
        ));

        panel.add(crearMiniTarjeta(
                "Saldos deudores",
                lblDeudoras,
                VERDE_SUAVE,
                VERDE
        ));

        panel.add(crearMiniTarjeta(
                "Saldos acreedores",
                lblAcreedoras,
                MORADO_SUAVE,
                MORADO
        ));

        return panel;
    }

    private JPanel crearMiniTarjeta(
            String titulo,
            JLabel valor,
            Color fondo,
            Color acento
    ) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel icono = new JLabel("•", SwingConstants.CENTER);
        icono.setOpaque(true);
        icono.setBackground(fondo);
        icono.setForeground(acento);
        icono.setFont(new Font("Segoe UI", Font.BOLD, 24));
        icono.setPreferredSize(new Dimension(38, 38));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setForeground(SECUNDARIO);

        valor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valor.setForeground(TEXTO);

        textos.add(t);
        textos.add(Box.createVerticalStrut(2));
        textos.add(valor);

        panel.add(icono, BorderLayout.WEST);
        panel.add(textos, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearBarraFiltros() {
        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel buscador = new JPanel(new BorderLayout(8, 0));
        buscador.setOpaque(false);

        JLabel lupa = new JLabel("⌕");
        lupa.setFont(new Font("Segoe UI Symbol", Font.BOLD, 20));
        lupa.setForeground(PRIMARIO);

        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setPreferredSize(new Dimension(300, 38));
        txtBuscar.setToolTipText("Buscar por código o nombre de cuenta");
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        buscador.add(lupa, BorderLayout.WEST);
        buscador.add(txtBuscar, BorderLayout.CENTER);

        cmbSaldo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbSaldo.setPreferredSize(new Dimension(190, 38));

        panel.add(buscador, BorderLayout.CENTER);
        panel.add(cmbSaldo, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);

        lblResumen.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblResumen.setForeground(SECUNDARIO);

        JLabel ayuda = new JLabel(
                "Doble lectura: movimientos al Debe a la izquierda y al Haber a la derecha"
        );
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ayuda.setForeground(new Color(148, 163, 184));

        pie.add(lblResumen, BorderLayout.WEST);
        pie.add(ayuda, BorderLayout.EAST);
        return pie;
    }

    private void instalarEventosFiltros() {
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aplicarFiltros();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aplicarFiltros();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aplicarFiltros();
            }
        });

        cmbSaldo.addActionListener(e -> aplicarFiltros());
    }

    public final void cargarMayorizacion() {
        try {
            cuentasActuales = service.obtenerMayorizacion();
            actualizarIndicadores(cuentasActuales);
            aplicarFiltros();
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void aplicarFiltros() {
        if (panelCuentas == null) {
            return;
        }

        panelCuentas.removeAll();

        String texto = txtBuscar.getText() == null
                ? ""
                : txtBuscar.getText().trim().toLowerCase();

        String filtroSaldo = cmbSaldo.getSelectedItem() == null
                ? "Todas las cuentas"
                : cmbSaldo.getSelectedItem().toString();

        int visibles = 0;

        for (CuentaMayor cuenta : cuentasActuales) {
            String codigo = cuenta.getCodigo() == null
                    ? ""
                    : cuenta.getCodigo().toLowerCase();

            String nombre = cuenta.getNombre() == null
                    ? ""
                    : cuenta.getNombre().toLowerCase();

            if (!texto.isBlank()
                    && !codigo.contains(texto)
                    && !nombre.contains(texto)) {
                continue;
            }

            String tipoSaldo = cuenta.getTipoSaldo() == null
                    ? ""
                    : cuenta.getTipoSaldo().trim().toUpperCase();

            if ("Saldo deudor".equals(filtroSaldo)
                    && !"DEUDOR".equals(tipoSaldo)) {
                continue;
            }

            if ("Saldo acreedor".equals(filtroSaldo)
                    && !"ACREEDOR".equals(tipoSaldo)) {
                continue;
            }

            panelCuentas.add(crearCuentaT(cuenta));
            panelCuentas.add(Box.createVerticalStrut(14));
            visibles++;
        }

        if (cuentasActuales.isEmpty()) {
            panelCuentas.add(crearMensajeVacio(
                    "No hay movimientos para mayorizar.",
                    "Cuando existan asientos en el Libro Diario, las cuentas T aparecerán aquí."
            ));
        } else if (visibles == 0) {
            panelCuentas.add(crearMensajeVacio(
                    "No se encontraron cuentas.",
                    "Prueba con otro texto de búsqueda o cambia el filtro de saldo."
            ));
        }

        lblResumen.setText(
                visibles + " cuenta(s) visible(s) de "
                + cuentasActuales.size() + " con movimiento"
        );

        panelCuentas.revalidate();
        panelCuentas.repaint();
    }

    private JPanel crearMensajeVacio(String titulo, String descripcion) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(28, 28, 28, 28)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t.setForeground(TEXTO);
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel d = new JLabel(descripcion);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        d.setForeground(SECUNDARIO);
        d.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(t);
        panel.add(Box.createVerticalStrut(6));
        panel.add(d);

        return panel;
    }

    private void actualizarIndicadores(List<CuentaMayor> cuentas) {
        int deudoras = 0;
        int acreedoras = 0;

        for (CuentaMayor cuenta : cuentas) {
            String tipo = cuenta.getTipoSaldo() == null
                    ? ""
                    : cuenta.getTipoSaldo().trim().toUpperCase();

            if ("DEUDOR".equals(tipo)) {
                deudoras++;
            } else if ("ACREEDOR".equals(tipo)) {
                acreedoras++;
            }
        }

        lblTotalCuentas.setText(String.valueOf(cuentas.size()));
        lblDeudoras.setText(String.valueOf(deudoras));
        lblAcreedoras.setText(String.valueOf(acreedoras));
    }

    private JPanel crearCuentaT(CuentaMayor cuenta) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(0, 0, 14, 0)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 430));

        JPanel encabezado = new JPanel(new BorderLayout(12, 0));
        encabezado.setBackground(CELESTE_SUAVE);
        encabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 1, 0, PRIMARIO),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel titulo = new JLabel(
                cuenta.getCodigo() + "  ·  " + cuenta.getNombre()
        );
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(TEXTO);

        JLabel badgeSaldo = crearBadgeSaldo(cuenta);

        encabezado.add(titulo, BorderLayout.WEST);
        encabezado.add(badgeSaldo, BorderLayout.EAST);
        tarjeta.add(encabezado, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new GridLayout(1, 2, 0, 0));
        cuerpo.setBackground(Color.WHITE);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        cuerpo.add(crearLadoT("DEBE", cuenta.getMovimientos(), true));
        cuerpo.add(crearLadoT("HABER", cuenta.getMovimientos(), false));

        tarjeta.add(cuerpo, BorderLayout.CENTER);
        tarjeta.add(crearPieCuenta(cuenta), BorderLayout.SOUTH);

        return tarjeta;
    }

    private JLabel crearBadgeSaldo(CuentaMayor cuenta) {
        String tipo = cuenta.getTipoSaldo() == null
                ? "SIN SALDO"
                : cuenta.getTipoSaldo().trim().toUpperCase();

        JLabel badge = new JLabel(
                "Saldo " + tipo + "  ·  " + dinero(cuenta.getSaldo())
        );
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        if ("DEUDOR".equals(tipo)) {
            badge.setBackground(VERDE_SUAVE);
            badge.setForeground(VERDE);
        } else if ("ACREEDOR".equals(tipo)) {
            badge.setBackground(MORADO_SUAVE);
            badge.setForeground(MORADO);
        } else {
            badge.setBackground(new Color(241, 245, 249));
            badge.setForeground(SECUNDARIO);
        }

        return badge;
    }

    private JPanel crearPieCuenta(CuentaMayor cuenta) {
        JPanel pie = new JPanel(new GridLayout(1, 3, 10, 0));
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));

        pie.add(crearDatoCuenta(
                "Total Debe",
                dinero(cuenta.getTotalDebe()),
                new Color(239, 246, 255),
                PRIMARIO
        ));

        pie.add(crearDatoCuenta(
                "Total Haber",
                dinero(cuenta.getTotalHaber()),
                new Color(248, 250, 252),
                TEXTO
        ));

        String tipo = cuenta.getTipoSaldo() == null
                ? "Saldo"
                : "Saldo " + cuenta.getTipoSaldo();

        pie.add(crearDatoCuenta(
                tipo,
                dinero(cuenta.getSaldo()),
                "DEUDOR".equalsIgnoreCase(cuenta.getTipoSaldo())
                        ? VERDE_SUAVE
                        : MORADO_SUAVE,
                "DEUDOR".equalsIgnoreCase(cuenta.getTipoSaldo())
                        ? VERDE
                        : MORADO
        ));

        return pie;
    }

    private JPanel crearDatoCuenta(
            String titulo,
            String valor,
            Color fondo,
            Color colorValor
    ) {
        JPanel panel = new JPanel();
        panel.setBackground(fondo);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        t.setForeground(SECUNDARIO);
        t.setAlignmentX(LEFT_ALIGNMENT);

        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        v.setForeground(colorValor);
        v.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(t);
        panel.add(Box.createVerticalStrut(2));
        panel.add(v);

        return panel;
    }

    private JPanel crearLadoT(
            String titulo,
            List<MovimientoMayor> movimientos,
            boolean debe
    ) {
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
        cabecera.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cabecera.setForeground(debe ? PRIMARIO : new Color(71, 85, 105));
        cabecera.setAlignmentX(CENTER_ALIGNMENT);

        lado.add(cabecera);
        lado.add(Box.createVerticalStrut(8));

        boolean hay = false;

        for (MovimientoMayor movimiento : movimientos) {
            BigDecimal valor = debe
                    ? movimiento.getDebe()
                    : movimiento.getHaber();

            if (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) {
                hay = true;

                String fecha = movimiento.getFecha() == null
                        ? ""
                        : movimiento.getFecha().format(formatoFecha);

                JPanel fila = new JPanel(new BorderLayout(8, 0));
                fila.setOpaque(false);
                fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));

                JLabel referencia = new JLabel(
                        "As. " + movimiento.getNumeroAsiento() + "  ·  " + fecha
                );
                referencia.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                referencia.setForeground(SECUNDARIO);

                JLabel monto = new JLabel(dinero(valor));
                monto.setFont(new Font("Segoe UI", Font.BOLD, 12));
                monto.setForeground(TEXTO);

                fila.setToolTipText(movimiento.getConcepto());
                referencia.setToolTipText(movimiento.getConcepto());
                monto.setToolTipText(movimiento.getConcepto());

                fila.add(referencia, BorderLayout.WEST);
                fila.add(monto, BorderLayout.EAST);

                lado.add(fila);
                lado.add(Box.createVerticalStrut(5));
            }
        }

        if (!hay) {
            JLabel vacio = new JLabel("Sin movimientos", SwingConstants.CENTER);
            vacio.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            vacio.setForeground(new Color(148, 163, 184));
            vacio.setAlignmentX(CENTER_ALIGNMENT);
            lado.add(vacio);
        }

        return lado;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(PRIMARIO);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(PRIMARIO_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(PRIMARIO);
            }
        });

        return boton;
    }

    private String dinero(BigDecimal valor) {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }

        return "$" + String.format(
                "%,.2f",
                valor.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private void mostrarError(Throwable error) {
        Throwable actual = error;
        String mensaje = "Ocurrió un error desconocido.";

        while (actual != null) {
            if (actual.getMessage() != null
                    && !actual.getMessage().isBlank()) {
                mensaje = actual.getMessage();
            }

            actual = actual.getCause();
        }

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "No se pudo cargar la mayorización",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
