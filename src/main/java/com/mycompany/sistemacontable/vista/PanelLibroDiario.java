package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.LineaLibroDiario;
import com.mycompany.sistemacontable.servicio.LibroDiarioService;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class PanelLibroDiario extends JPanel {

    private static final Color COLOR_FONDO = new Color(245, 247, 250);
    private static final Color COLOR_TEXTO = new Color(30, 41, 59);
    private static final Color COLOR_SECUNDARIO = new Color(100, 116, 139);
    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private static final Color COLOR_PRIMARIO_HOVER = new Color(29, 78, 216);
    private static final Color COLOR_CELESTE = new Color(239, 246, 255);
    private static final Color COLOR_CELESTE_2 = new Color(248, 251, 255);
    private static final Color COLOR_EXITO = new Color(22, 163, 74);
    private static final Color COLOR_ERROR = new Color(220, 38, 38);

    private final LibroDiarioService service = new LibroDiarioService();
    private final boolean puedeEditar;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{
                "Asiento", "Fecha", "Código", "Cuenta",
                "Concepto", "Descripción", "Debe", "Haber"
            },
            0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tabla = new JTable(model);
    private final java.util.List<Integer> idsPorFila = new ArrayList<>();

    private final JLabel totalDebe = new JLabel("$0.00");
    private final JLabel totalHaber = new JLabel("$0.00");
    private final JLabel estado = new JLabel("Sin movimientos");

    private final DateTimeFormatter fechaFmt =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PanelLibroDiario() {
        this(false);
    }

    public PanelLibroDiario(boolean puedeEditar) {
        this.puedeEditar = puedeEditar;
        construir();
        cargarLibroDiario();
    }

    private void construir() {
        setLayout(new BorderLayout(0, 18));
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 26, 26, 26));

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearAreaTabla(), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel top = new JPanel(new BorderLayout(18, 0));
        top.setOpaque(false);

        JPanel izquierda = new JPanel(new BorderLayout(14, 0));
        izquierda.setOpaque(false);

        JLabel iconoLibro = crearIconoTitulo();
        izquierda.add(iconoLibro, BorderLayout.WEST);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Libro Diario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 29));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Asientos contables registrados durante el período activo."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(COLOR_SECUNDARIO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(Box.createVerticalGlue());
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);
        textos.add(Box.createVerticalGlue());

        izquierda.add(textos, BorderLayout.CENTER);
        top.add(izquierda, BorderLayout.WEST);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 4));
        botones.setOpaque(false);

        JButton ver = crearBoton("Ver asiento", true, crearIconoBoton("VER"));
        JButton actualizar = crearBoton("Actualizar", false, crearIconoBoton("ACTUALIZAR"));

        ver.addActionListener(e -> verAsiento());
        actualizar.addActionListener(e -> cargarLibroDiario());

        botones.add(ver);
        botones.add(actualizar);

        top.add(botones, BorderLayout.EAST);

        return top;
    }

    private JPanel crearAreaTabla() {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        configurarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        tarjeta.add(scroll, BorderLayout.CENTER);
        return tarjeta;
    }

    private void configurarTabla() {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(34);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(235, 240, 246));
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionBackground(new Color(219, 234, 254));
        tabla.setSelectionForeground(COLOR_TEXTO);

        JTableHeader header = tabla.getTableHeader();
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new EncabezadoRenderer());

        configurarAnchosColumnas();

        for (int i = 0; i < tabla.getColumnCount(); i++) {
            boolean dinero = i == 6 || i == 7;
            boolean asiento = i == 0;
            tabla.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(new LibroCellRenderer(dinero, asiento));
        }

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verAsiento();
                }
            }
        });
    }

    private void configurarAnchosColumnas() {
        int[] anchos = {
            70,   // Asiento
            95,   // Fecha
            120,  // Código
            175,  // Cuenta
            200,  // Concepto
            220,  // Descripción
            115,  // Debe
            115   // Haber
        };

        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout(18, 0));
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JPanel estadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        estadoPanel.setOpaque(false);

        JLabel puntoEstado = new JLabel("●");
        puntoEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        puntoEstado.setForeground(COLOR_SECUNDARIO);
        puntoEstado.setName("PUNTO_ESTADO");

        estado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        estado.setForeground(COLOR_SECUNDARIO);

        estadoPanel.add(puntoEstado);
        estadoPanel.add(estado);
        pie.add(estadoPanel, BorderLayout.WEST);

        JPanel totales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        totales.setOpaque(false);

        totales.add(crearTarjetaTotal("Total Debe", totalDebe, new Color(239, 246, 255)));
        totales.add(crearTarjetaTotal("Total Haber", totalHaber, new Color(245, 243, 255)));

        pie.add(totales, BorderLayout.EAST);
        return pie;
    }

    private JPanel crearTarjetaTotal(String titulo, JLabel valor, Color fondo) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(fondo);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel lblTitulo = new JLabel(titulo + ":");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_SECUNDARIO);

        valor.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valor.setForeground(COLOR_TEXTO);

        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(valor, BorderLayout.EAST);

        return panel;
    }

    public final void cargarLibroDiario() {
        try {
            model.setRowCount(0);
            idsPorFila.clear();

            java.util.List<LineaLibroDiario> lineas =
                    service.obtenerLibroDiario();

            for (LineaLibroDiario l : lineas) {
                idsPorFila.add(l.getIdAsiento());

                model.addRow(new Object[]{
                    l.getNumeroAsiento(),
                    l.getFecha() == null ? "" : l.getFecha().format(fechaFmt),
                    l.getCodigoCuenta(),
                    l.getNombreCuenta(),
                    l.getConcepto(),
                    l.getDescripcion(),
                    money(l.getDebe()),
                    money(l.getHaber())
                });
            }

            BigDecimal debe = service.obtenerTotalDebe();
            BigDecimal haber = service.obtenerTotalHaber();

            totalDebe.setText(money(debe));
            totalHaber.setText(money(haber));

            if (lineas.isEmpty()) {
                actualizarEstado(
                        "Sin movimientos registrados",
                        COLOR_SECUNDARIO
                );
            } else if (debe.compareTo(haber) == 0) {
                actualizarEstado(
                        "Libro Diario cuadrado",
                        COLOR_EXITO
                );
            } else {
                actualizarEstado(
                        "Libro Diario descuadrado",
                        COLOR_ERROR
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo cargar el Libro Diario",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void actualizarEstado(String texto, Color color) {
        estado.setText(texto);
        estado.setForeground(color);

        Container padre = estado.getParent();
        if (padre != null) {
            for (Component c : padre.getComponents()) {
                if (c instanceof JLabel lbl
                        && "PUNTO_ESTADO".equals(lbl.getName())) {
                    lbl.setForeground(color);
                }
            }
        }
    }

    private void verAsiento() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una línea del asiento que deseas ver."
            );
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        int id = idsPorFila.get(filaModelo);

        Window ventana = SwingUtilities.getWindowAncestor(this);

        DialogoAsientoDetalle dialogo = new DialogoAsientoDetalle(
                ventana,
                id,
                puedeEditar,
                () -> cargarLibroDiario()
        );

        dialogo.setVisible(true);
    }

    private JButton crearBoton(String texto, boolean principal, Icon icono) {
        JButton boton = new JButton(texto, icono);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(principal ? Color.WHITE : COLOR_TEXTO);
        boton.setBackground(principal ? COLOR_PRIMARIO : Color.WHITE);
        boton.setIconTextGap(8);
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        principal ? COLOR_PRIMARIO : COLOR_BORDE
                ),
                BorderFactory.createEmptyBorder(9, 13, 9, 13)
        ));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(
                        principal
                                ? COLOR_PRIMARIO_HOVER
                                : new Color(248, 250, 252)
                );
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(
                        principal
                                ? COLOR_PRIMARIO
                                : Color.WHITE
                );
            }
        });

        return boton;
    }

    private JLabel crearIconoTitulo() {
        JLabel label = new JLabel(crearIconoLibroGrande(), SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(219, 234, 254));
        label.setPreferredSize(new Dimension(52, 52));
        label.setMinimumSize(new Dimension(52, 52));
        label.setMaximumSize(new Dimension(52, 52));
        label.setBorder(BorderFactory.createLineBorder(new Color(191, 219, 254)));
        return label;
    }

    private Icon crearIconoLibroGrande() {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(COLOR_PRIMARIO);
                g2.setStroke(new BasicStroke(2f));

                g2.drawRoundRect(x + 5, y + 4, 24, 27, 4, 4);
                g2.drawLine(x + 12, y + 4, x + 12, y + 31);
                g2.drawLine(x + 16, y + 11, x + 25, y + 11);
                g2.drawLine(x + 16, y + 17, x + 25, y + 17);
                g2.drawLine(x + 16, y + 23, x + 23, y + 23);

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 36;
            }

            @Override
            public int getIconHeight() {
                return 36;
            }
        };
    }

    private Icon crearIconoBoton(String tipo) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                boolean botonPrincipal = c instanceof JButton
                        && Color.WHITE.equals(c.getForeground());

                g2.setColor(
                        botonPrincipal
                                ? Color.WHITE
                                : COLOR_PRIMARIO
                );
                g2.setStroke(new BasicStroke(
                        1.7f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND
                ));

                if ("VER".equals(tipo)) {
                    g2.drawOval(x + 2, y + 5, 14, 8);
                    g2.fillOval(x + 8, y + 8, 3, 3);
                } else {
                    g2.drawArc(x + 3, y + 3, 12, 12, 35, 280);
                    g2.drawLine(x + 13, y + 2, x + 16, y + 5);
                    g2.drawLine(x + 16, y + 5, x + 12, y + 6);
                }

                g2.dispose();
            }

            @Override
            public int getIconWidth() {
                return 18;
            }

            @Override
            public int getIconHeight() {
                return 18;
            }
        };
    }

    private String money(BigDecimal valor) {
        return "$" + String.format(
                "%,.2f",
                (valor == null ? BigDecimal.ZERO : valor)
                        .setScale(2, RoundingMode.HALF_UP)
        );
    }

    private class EncabezadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            label.setOpaque(true);
            label.setBackground(COLOR_CELESTE);
            label.setForeground(COLOR_TEXTO);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setHorizontalAlignment(
                    column >= 6
                            ? SwingConstants.RIGHT
                            : SwingConstants.CENTER
            );
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                            0,
                            0,
                            1,
                            1,
                            new Color(203, 213, 225)
                    ),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)
            ));

            return label;
        }
    }

    private class LibroCellRenderer extends DefaultTableCellRenderer {

        private final boolean dinero;
        private final boolean columnaAsiento;

        LibroCellRenderer(boolean dinero, boolean columnaAsiento) {
            this.dinero = dinero;
            this.columnaAsiento = columnaAsiento;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            label.setFont(new Font(
                    "Segoe UI",
                    columnaAsiento ? Font.BOLD : Font.PLAIN,
                    12
            ));

            label.setForeground(
                    columnaAsiento
                            ? COLOR_PRIMARIO
                            : COLOR_TEXTO
            );

            label.setHorizontalAlignment(
                    dinero
                            ? SwingConstants.RIGHT
                            : SwingConstants.LEFT
            );

            boolean iniciaAsiento = row == 0;

            if (row > 0) {
                Object actual = table.getValueAt(row, 0);
                Object anterior = table.getValueAt(row - 1, 0);
                iniciaAsiento = !Objects.equals(actual, anterior);
            }

            if (isSelected) {
                label.setBackground(new Color(219, 234, 254));
                label.setForeground(COLOR_TEXTO);
            } else {
                Object asiento = table.getValueAt(row, 0);
                int grupo = asiento == null ? row : asiento.toString().hashCode();
                label.setBackground(
                        Math.abs(grupo) % 2 == 0
                                ? Color.WHITE
                                : COLOR_CELESTE_2
                );
            }

            Border bordeSuperior = iniciaAsiento
                    ? BorderFactory.createMatteBorder(
                            2,
                            0,
                            0,
                            0,
                            new Color(147, 197, 253)
                    )
                    : BorderFactory.createMatteBorder(
                            1,
                            0,
                            0,
                            0,
                            new Color(241, 245, 249)
                    );

            label.setBorder(BorderFactory.createCompoundBorder(
                    bordeSuperior,
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)
            ));

            return label;
        }
    }
}
