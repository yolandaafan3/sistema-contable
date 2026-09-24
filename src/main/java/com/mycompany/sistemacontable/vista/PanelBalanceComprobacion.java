package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.BalanceComprobacion;
import com.mycompany.sistemacontable.modelo.LineaBalanceComprobacion;
import com.mycompany.sistemacontable.servicio.BalanceComprobacionService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelBalanceComprobacion extends JPanel {

    private final BalanceComprobacionService service =
            new BalanceComprobacionService();

    private final DefaultTableModel modelo;

    private final JTable tabla;

    private final JTextField txtBuscar =
            new JTextField();

    private final JComboBox<String> cmbFiltroSaldo =
            new JComboBox<>(new String[]{
                "Todas las cuentas",
                "Saldo deudor",
                "Saldo acreedor"
            });

    private final JLabel lblTotales =
            new JLabel();

    private final JLabel lblEstado =
            new JLabel();

    private final JLabel lblContador =
            new JLabel("0 cuentas visibles");

    private final JLabel lblMovDebe =
            new JLabel("$0.00");

    private final JLabel lblMovHaber =
            new JLabel("$0.00");

    private final JLabel lblSaldoDeudor =
            new JLabel("$0.00");

    private final JLabel lblSaldoAcreedor =
            new JLabel("$0.00");

    private final List<LineaBalanceComprobacion> lineasActuales =
            new ArrayList<>();

    private final Color FONDO =
            new Color(245, 247, 250);

    private final Color TEXTO =
            new Color(30, 41, 59);

    private final Color SECUNDARIO =
            new Color(100, 116, 139);

    private final Color BORDE =
            new Color(226, 232, 240);

    private final Color PRIMARIO =
            new Color(37, 99, 235);

    private final Color PRIMARIO_HOVER =
            new Color(29, 78, 216);

    private final Color CELESTE_SUAVE =
            new Color(239, 246, 255);

    private final Color CELESTE_SELECCION =
            new Color(219, 234, 254);

    private final Color EXITO =
            new Color(22, 163, 74);

    private final Color ERROR =
            new Color(220, 38, 38);

    public PanelBalanceComprobacion() {

        setLayout(
                new BorderLayout(
                        0,
                        18
                )
        );

        setBackground(
                FONDO
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        26,
                        26,
                        26
                )
        );

        modelo =
                new DefaultTableModel(
                        new String[]{
                            "Código",
                            "Cuenta",
                            "Movimiento Debe",
                            "Movimiento Haber",
                            "Saldo Deudor",
                            "Saldo Acreedor"
                        },
                        0
                ) {
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        tabla =
                new JTable(
                        modelo
                );

        add(
                crearZonaSuperior(),
                BorderLayout.NORTH
        );

        add(
                crearTabla(),
                BorderLayout.CENTER
        );

        add(
                crearPie(),
                BorderLayout.SOUTH
        );

        configurarEventosFiltros();

        cargarBalance();
    }

    private JPanel crearZonaSuperior() {

        JPanel zona =
                new JPanel();

        zona.setOpaque(
                false
        );

        zona.setLayout(
                new BoxLayout(
                        zona,
                        BoxLayout.Y_AXIS
                )
        );

        zona.add(
                crearEncabezado()
        );

        zona.add(
                Box.createVerticalStrut(
                        18
                )
        );

        zona.add(
                crearPanelFiltros()
        );

        zona.add(
                Box.createVerticalStrut(
                        14
                )
        );

        zona.add(
                crearTarjetasResumen()
        );

        return zona;
    }

    private JPanel crearEncabezado() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(
                false
        );

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Balance de Comprobación"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel sub =
                new JLabel(
                        "Movimientos y saldos de todas las cuentas con actividad."
                );

        sub.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        sub.setForeground(
                SECUNDARIO
        );

        textos.add(
                titulo
        );

        textos.add(
                Box.createVerticalStrut(
                        5
                )
        );

        textos.add(
                sub
        );

        JButton actualizar =
                crearBotonPrincipal(
                        "↻  Actualizar"
                );

        actualizar.addActionListener(
                e -> cargarBalance()
        );

        panel.add(
                textos,
                BorderLayout.WEST
        );

        panel.add(
                actualizar,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel crearPanelFiltros() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                14,
                                0
                        )
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                12,
                                14,
                                12,
                                14
                        )
                )
        );

        JPanel izquierda =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        izquierda.setOpaque(
                false
        );

        JLabel icono =
                new JLabel(
                        "⌕"
                );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        18
                )
        );

        icono.setForeground(
                PRIMARIO
        );

        txtBuscar.setPreferredSize(
                new Dimension(
                        290,
                        38
                )
        );

        txtBuscar.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        txtBuscar.setToolTipText(
                "Buscar por código o nombre de cuenta"
        );

        txtBuscar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        203,
                                        213,
                                        225
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                7,
                                10,
                                7,
                                10
                        )
                )
        );

        cmbFiltroSaldo.setPreferredSize(
                new Dimension(
                        175,
                        38
                )
        );

        cmbFiltroSaldo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        izquierda.add(
                icono
        );

        izquierda.add(
                txtBuscar
        );

        izquierda.add(
                cmbFiltroSaldo
        );

        lblContador.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblContador.setForeground(
                SECUNDARIO
        );

        panel.add(
                izquierda,
                BorderLayout.WEST
        );

        panel.add(
                lblContador,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel crearTarjetasResumen() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                14,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        94
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Movimiento Debe",
                        lblMovDebe,
                        "D",
                        new Color(
                                219,
                                234,
                                254
                        ),
                        PRIMARIO
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Movimiento Haber",
                        lblMovHaber,
                        "H",
                        new Color(
                                224,
                                242,
                                254
                        ),
                        new Color(
                                14,
                                165,
                                233
                        )
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Saldo Deudor",
                        lblSaldoDeudor,
                        "↙",
                        new Color(
                                220,
                                252,
                                231
                        ),
                        new Color(
                                22,
                                163,
                                74
                        )
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Saldo Acreedor",
                        lblSaldoAcreedor,
                        "↗",
                        new Color(
                                243,
                                232,
                                255
                        ),
                        new Color(
                                126,
                                34,
                                206
                        )
                )
        );

        return panel;
    }

    private JPanel crearTarjetaResumen(
            String titulo,
            JLabel valor,
            String iconoTexto,
            Color fondoIcono,
            Color colorIcono
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                13,
                                15,
                                13,
                                15
                        )
                )
        );

        JLabel icono =
                new JLabel(
                        iconoTexto,
                        SwingConstants.CENTER
                );

        icono.setOpaque(
                true
        );

        icono.setBackground(
                fondoIcono
        );

        icono.setForeground(
                colorIcono
        );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        17
                )
        );

        icono.setPreferredSize(
                new Dimension(
                        42,
                        42
                )
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(
                false
        );

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        lblTitulo.setForeground(
                SECUNDARIO
        );

        valor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        valor.setForeground(
                TEXTO
        );

        textos.add(
                lblTitulo
        );

        textos.add(
                Box.createVerticalStrut(
                        4
                )
        );

        textos.add(
                valor
        );

        tarjeta.add(
                icono,
                BorderLayout.WEST
        );

        tarjeta.add(
                textos,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JScrollPane crearTabla() {

        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        tabla.setRowHeight(
                34
        );

        tabla.setGridColor(
                BORDE
        );

        tabla.setShowVerticalLines(
                false
        );

        tabla.setShowHorizontalLines(
                true
        );

        tabla.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setSelectionBackground(
                CELESTE_SELECCION
        );

        tabla.setSelectionForeground(
                TEXTO
        );

        tabla.setAutoCreateRowSorter(
                true
        );

        tabla.setFillsViewportHeight(
                true
        );

        JTableHeader header =
                tabla.getTableHeader();

        header.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        header.setPreferredSize(
                new Dimension(
                        0,
                        40
                )
        );

        header.setBackground(
                new Color(
                        241,
                        245,
                        249
                )
        );

        header.setForeground(
                TEXTO
        );

        header.setReorderingAllowed(
                false
        );

        RenderTabla renderTexto =
                new RenderTabla(
                        SwingConstants.LEFT
                );

        RenderTabla renderMonto =
                new RenderTabla(
                        SwingConstants.RIGHT
                );

        tabla.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        renderTexto
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        renderTexto
                );

        for (
                int i = 2;
                i <= 5;
                i++
        ) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(
                            renderMonto
                    );
        }

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        110
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        250
                );

        for (
                int i = 2;
                i <= 5;
                i++
        ) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(
                            160
                    );
        }

        JScrollPane scroll =
                new JScrollPane(
                        tabla
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );

        scroll.getViewport()
                .setBackground(
                        Color.WHITE
                );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(
                        18
                );

        return scroll;
    }

    private JPanel crearPie() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                15,
                                0
                        )
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                13,
                                16,
                                13,
                                16
                        )
                )
        );

        lblEstado.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblTotales.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblTotales.setForeground(
                TEXTO
        );

        panel.add(
                lblEstado,
                BorderLayout.WEST
        );

        panel.add(
                lblTotales,
                BorderLayout.EAST
        );

        return panel;
    }

    private void configurarEventosFiltros() {

        txtBuscar.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {
                                aplicarFiltros();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {
                                aplicarFiltros();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {
                                aplicarFiltros();
                            }
                        }
                );

        cmbFiltroSaldo.addActionListener(
                e -> aplicarFiltros()
        );
    }

    public final void cargarBalance() {

        modelo.setRowCount(
                0
        );

        lineasActuales.clear();

        lblTotales.setText(
                ""
        );

        lblEstado.setText(
                "Sin datos del período activo"
        );

        lblEstado.setForeground(
                SECUNDARIO
        );

        lblMovDebe.setText(
                "$0.00"
        );

        lblMovHaber.setText(
                "$0.00"
        );

        lblSaldoDeudor.setText(
                "$0.00"
        );

        lblSaldoAcreedor.setText(
                "$0.00"
        );

        try {

            BalanceComprobacion balance =
                    service.generarBalance();

            if (
                    balance.getLineas()
                    != null
            ) {

                lineasActuales.addAll(
                        balance.getLineas()
                );
            }

            lblMovDebe.setText(
                    dinero(
                            balance.getTotalMovimientoDebe()
                    )
            );

            lblMovHaber.setText(
                    dinero(
                            balance.getTotalMovimientoHaber()
                    )
            );

            lblSaldoDeudor.setText(
                    dinero(
                            balance.getTotalSaldoDeudor()
                    )
            );

            lblSaldoAcreedor.setText(
                    dinero(
                            balance.getTotalSaldoAcreedor()
                    )
            );

            lblTotales.setText(
                    "Mov. Debe: "
                    + dinero(
                            balance.getTotalMovimientoDebe()
                    )
                    + "   ·   Mov. Haber: "
                    + dinero(
                            balance.getTotalMovimientoHaber()
                    )
                    + "   ·   Saldo Deudor: "
                    + dinero(
                            balance.getTotalSaldoDeudor()
                    )
                    + "   ·   Saldo Acreedor: "
                    + dinero(
                            balance.getTotalSaldoAcreedor()
                    )
            );

            boolean ok =
                    balance.isMovimientosCuadrados()
                    &&
                    balance.isSaldosCuadrados();

            lblEstado.setText(
                    ok
                            ? "✓ Balance cuadrado"
                            : "⚠ Revisar balance"
            );

            lblEstado.setForeground(
                    ok
                            ? EXITO
                            : ERROR
            );

            aplicarFiltros();

        } catch (
                Exception e
        ) {

            mostrarError(
                    e
            );
        }
    }

    private void aplicarFiltros() {

        modelo.setRowCount(
                0
        );

        String texto =
                txtBuscar.getText()
                == null
                        ? ""
                        : txtBuscar.getText()
                                .trim()
                                .toLowerCase();

        String filtroSaldo =
                cmbFiltroSaldo.getSelectedItem()
                == null
                        ? "Todas las cuentas"
                        : cmbFiltroSaldo.getSelectedItem()
                                .toString();

        int visibles =
                0;

        for (
                LineaBalanceComprobacion linea
                : lineasActuales
        ) {

            String codigo =
                    linea.getCodigo()
                    == null
                            ? ""
                            : linea.getCodigo()
                                    .toLowerCase();

            String nombre =
                    linea.getNombre()
                    == null
                            ? ""
                            : linea.getNombre()
                                    .toLowerCase();

            if (
                    !texto.isBlank()
                    &&
                    !codigo.contains(
                            texto
                    )
                    &&
                    !nombre.contains(
                            texto
                    )
            ) {

                continue;
            }

            BigDecimal deudor =
                    linea.getSaldoDeudor()
                    == null
                            ? BigDecimal.ZERO
                            : linea.getSaldoDeudor();

            BigDecimal acreedor =
                    linea.getSaldoAcreedor()
                    == null
                            ? BigDecimal.ZERO
                            : linea.getSaldoAcreedor();

            if (
                    "Saldo deudor".equals(
                            filtroSaldo
                    )
                    &&
                    deudor.compareTo(
                            BigDecimal.ZERO
                    )
                    <= 0
            ) {

                continue;
            }

            if (
                    "Saldo acreedor".equals(
                            filtroSaldo
                    )
                    &&
                    acreedor.compareTo(
                            BigDecimal.ZERO
                    )
                    <= 0
            ) {

                continue;
            }

            modelo.addRow(
                    new Object[]{
                        linea.getCodigo(),
                        linea.getNombre(),
                        dinero(
                                linea.getMovimientoDebe()
                        ),
                        dinero(
                                linea.getMovimientoHaber()
                        ),
                        dinero(
                                linea.getSaldoDeudor()
                        ),
                        dinero(
                                linea.getSaldoAcreedor()
                        )
                    }
            );

            visibles++;
        }

        lblContador.setText(
                visibles
                + (
                        visibles == 1
                                ? " cuenta visible"
                                : " cuentas visibles"
                )
        );
    }

    private JButton crearBotonPrincipal(
            String texto
    ) {

        JButton boton =
                new JButton(
                        texto
                );

        boton.setUI(
                new BasicButtonUI()
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setBackground(
                PRIMARIO
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        18,
                        10,
                        18
                )
        );

        boton.setFocusPainted(
                false
        );

        boton.setOpaque(
                true
        );

        boton.setContentAreaFilled(
                true
        );

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                PRIMARIO_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                PRIMARIO
                        );
                    }
                }
        );

        return boton;
    }

    private String dinero(
            BigDecimal valor
    ) {

        if (
                valor
                == null
        ) {

            valor =
                    BigDecimal.ZERO;
        }

        return "$"
                + String.format(
                        "%,.2f",
                        valor.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
    }

    private void mostrarError(
            Throwable error
    ) {

        String mensaje =
                "Ocurrió un error desconocido.";

        for (
                Throwable actual = error;
                actual != null;
                actual = actual.getCause()
        ) {

            if (
                    actual.getMessage()
                    != null
                    &&
                    !actual.getMessage()
                            .isBlank()
            ) {

                mensaje =
                        actual.getMessage();
            }
        }

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "No se pudo cargar el Balance de Comprobación",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private class RenderTabla
            extends DefaultTableCellRenderer {

        private final int alineacion;

        public RenderTabla(
                int alineacion
        ) {

            this.alineacion =
                    alineacion;

            setOpaque(
                    true
            );
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

            Component componente =
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            setHorizontalAlignment(
                    alineacion
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            8,
                            0,
                            8
                    )
            );

            if (
                    isSelected
            ) {

                componente.setBackground(
                        CELESTE_SELECCION
                );

                componente.setForeground(
                        TEXTO
                );

            } else {

                componente.setBackground(
                        row % 2 == 0
                                ? Color.WHITE
                                : new Color(
                                        248,
                                        250,
                                        252
                                )
                );

                if (
                        column == 4
                        &&
                        value != null
                        &&
                        !"$0.00".equals(
                                value.toString()
                        )
                ) {

                    componente.setForeground(
                            new Color(
                                    21,
                                    128,
                                    61
                            )
                    );

                } else if (
                        column == 5
                        &&
                        value != null
                        &&
                        !"$0.00".equals(
                                value.toString()
                        )
                ) {

                    componente.setForeground(
                            new Color(
                                    126,
                                    34,
                                    206
                            )
                    );

                } else {

                    componente.setForeground(
                            TEXTO
                    );
                }
            }

            return componente;
        }
    }
}
