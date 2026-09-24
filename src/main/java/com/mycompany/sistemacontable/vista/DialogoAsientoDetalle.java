package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.servicio.GestionAsientoService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class DialogoAsientoDetalle extends JDialog {

    private final int idAsiento;
    private final boolean puedeEditar;
    private final Runnable alGuardar;

    private final GestionAsientoService service =
            new GestionAsientoService();

    private final CuentaDAO cuentaDAO =
            new CuentaDAO();

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

    private final Color VERDE =
            new Color(22, 163, 74);

    private final Color CELESTE_SUAVE =
            new Color(239, 246, 255);

    private final Color SELECCION =
            new Color(219, 234, 254);

    public DialogoAsientoDetalle(
            Window owner,
            int idAsiento,
            boolean puedeEditar,
            Runnable alGuardar
    ) {

        super(
                owner,
                "Detalle del asiento",
                ModalityType.APPLICATION_MODAL
        );

        this.idAsiento =
                idAsiento;

        this.puedeEditar =
                puedeEditar;

        this.alGuardar =
                alGuardar;

        setDefaultCloseOperation(
                DISPOSE_ON_CLOSE
        );

        setMinimumSize(
                new Dimension(
                        860,
                        570
                )
        );

        setSize(
                980,
                650
        );

        construir();

        setLocationRelativeTo(
                owner
        );
    }

    private void construir() {

        AsientoContable asiento =
                service.obtenerAsiento(
                        idAsiento
                );

        if (
                asiento == null
        ) {

            dispose();
            return;
        }

        List<DetalleAsiento> detalles =
                service.obtenerDetalles(
                        idAsiento
                );

        getContentPane().setBackground(
                FONDO
        );

        setLayout(
                new BorderLayout()
        );

        add(
                crearEncabezado(
                        asiento
                ),
                BorderLayout.NORTH
        );

        add(
                crearContenido(
                        asiento,
                        detalles
                ),
                BorderLayout.CENTER
        );

        add(
                crearPie(
                        asiento
                ),
                BorderLayout.SOUTH
        );
    }

    private JPanel crearEncabezado(
            AsientoContable asiento
    ) {

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
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                22,
                                18,
                                22
                        )
                )
        );

        JLabel icono =
                new JLabel(
                        "≡",
                        SwingConstants.CENTER
                );

        icono.setOpaque(
                true
        );

        icono.setBackground(
                CELESTE_SUAVE
        );

        icono.setForeground(
                PRIMARIO
        );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        23
                )
        );

        icono.setPreferredSize(
                new Dimension(
                        50,
                        50
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

        JLabel titulo =
                new JLabel(
                        "Asiento N.º "
                        + asiento.getNumeroAsiento()
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel concepto =
                new JLabel(
                        asiento.getConcepto() == null
                                || asiento.getConcepto().isBlank()
                                ? "Sin concepto registrado"
                                : asiento.getConcepto()
                );

        concepto.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        concepto.setForeground(
                SECUNDARIO
        );

        textos.add(
                titulo
        );

        textos.add(
                Box.createVerticalStrut(
                        4
                )
        );

        textos.add(
                concepto
        );

        JLabel estado =
                new JLabel(
                        asiento.getEstado() == null
                                ? ""
                                : asiento.getEstado()
                );

        estado.setOpaque(
                true
        );

        estado.setBackground(
                new Color(
                        220,
                        252,
                        231
                )
        );

        estado.setForeground(
                VERDE
        );

        estado.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        estado.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        12,
                        8,
                        12
                )
        );

        panel.add(
                icono,
                BorderLayout.WEST
        );

        panel.add(
                textos,
                BorderLayout.CENTER
        );

        panel.add(
                estado,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel crearContenido(
            AsientoContable asiento,
            List<DetalleAsiento> detalles
    ) {

        JPanel contenido =
                new JPanel();

        contenido.setBackground(
                FONDO
        );

        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );

        contenido.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        22,
                        18,
                        22
                )
        );

        contenido.add(
                crearResumen(
                        asiento,
                        detalles
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        16
                )
        );

        contenido.add(
                crearTarjetaDetalle(
                        detalles
                )
        );

        return contenido;
    }

    private JPanel crearResumen(
            AsientoContable asiento,
            List<DetalleAsiento> detalles
    ) {

        JPanel resumen =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                12,
                                0
                        )
                );

        resumen.setOpaque(
                false
        );

        resumen.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        86
                )
        );

        resumen.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        resumen.add(
                crearDatoResumen(
                        "Fecha",
                        asiento.getFecha() == null
                                ? "—"
                                : asiento.getFecha().toString()
                )
        );

        resumen.add(
                crearDatoResumen(
                        "Tipo",
                        asiento.getTipoAsiento() == null
                                ? "—"
                                : asiento.getTipoAsiento()
                )
        );

        resumen.add(
                crearDatoResumen(
                        "Líneas",
                        String.valueOf(
                                detalles == null
                                        ? 0
                                        : detalles.size()
                        )
                )
        );

        resumen.add(
                crearDatoResumen(
                        "Estado",
                        asiento.getEstado() == null
                                ? "—"
                                : asiento.getEstado()
                )
        );

        return resumen;
    }

    private JPanel crearDatoResumen(
            String titulo,
            String valor
    ) {

        JPanel tarjeta =
                new JPanel();

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setLayout(
                new BoxLayout(
                        tarjeta,
                        BoxLayout.Y_AXIS
                )
        );

        tarjeta.setBorder(
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

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        lblTitulo.setForeground(
                SECUNDARIO
        );

        JLabel lblValor =
                new JLabel(
                        valor
                );

        lblValor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblValor.setForeground(
                TEXTO
        );

        tarjeta.add(
                lblTitulo
        );

        tarjeta.add(
                Box.createVerticalStrut(
                        4
                )
        );

        tarjeta.add(
                lblValor
        );

        return tarjeta;
    }

    private JPanel crearTarjetaDetalle(
            List<DetalleAsiento> detalles
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                12
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
                                14,
                                14,
                                14,
                                14
                        )
                )
        );

        tarjeta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel titulo =
                new JLabel(
                        "Detalle del asiento"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        titulo.setForeground(
                TEXTO
        );

        DefaultTableModel modelo =
                new DefaultTableModel(
                        new Object[]{
                            "Código",
                            "Cuenta",
                            "Descripción",
                            "Debe",
                            "Haber"
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

        BigDecimal totalDebe =
                BigDecimal.ZERO;

        BigDecimal totalHaber =
                BigDecimal.ZERO;

        if (
                detalles != null
        ) {

            for (
                    DetalleAsiento detalle
                    : detalles
            ) {

                Cuenta cuenta =
                        cuentaDAO.buscarPorId(
                                detalle.getIdCuenta()
                        );

                BigDecimal debe =
                        detalle.getDebe() == null
                                ? BigDecimal.ZERO
                                : detalle.getDebe();

                BigDecimal haber =
                        detalle.getHaber() == null
                                ? BigDecimal.ZERO
                                : detalle.getHaber();

                totalDebe =
                        totalDebe.add(
                                debe
                        );

                totalHaber =
                        totalHaber.add(
                                haber
                        );

                modelo.addRow(
                        new Object[]{
                            cuenta == null
                                    ? ""
                                    : cuenta.getCodigo(),

                            cuenta == null
                                    ? ""
                                    : cuenta.getNombre(),

                            detalle.getDescripcion(),

                            money(
                                    debe
                            ),

                            money(
                                    haber
                            )
                        }
                );
            }
        }

        JTable tabla =
                new JTable(
                        modelo
                );

        configurarTabla(
                tabla
        );

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

        scroll.setPreferredSize(
                new Dimension(
                        880,
                        280
                )
        );

        JPanel totales =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                18,
                                0
                        )
                );

        totales.setOpaque(
                false
        );

        JLabel debe =
                new JLabel(
                        "Total Debe: "
                        + money(
                                totalDebe
                        )
                );

        JLabel haber =
                new JLabel(
                        "Total Haber: "
                        + money(
                                totalHaber
                        )
                );

        BigDecimal diferencia =
                totalDebe.subtract(
                        totalHaber
                ).abs();

        JLabel diferenciaLabel =
                new JLabel(
                        "Diferencia: "
                        + money(
                                diferencia
                        )
                );

        debe.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        haber.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        diferenciaLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        debe.setForeground(
                TEXTO
        );

        haber.setForeground(
                TEXTO
        );

        diferenciaLabel.setForeground(
                diferencia.compareTo(
                        BigDecimal.ZERO
                ) == 0
                        ? VERDE
                        : new Color(
                                220,
                                38,
                                38
                        )
        );

        totales.add(
                debe
        );

        totales.add(
                haber
        );

        totales.add(
                diferenciaLabel
        );

        tarjeta.add(
                titulo,
                BorderLayout.NORTH
        );

        tarjeta.add(
                scroll,
                BorderLayout.CENTER
        );

        tarjeta.add(
                totales,
                BorderLayout.SOUTH
        );

        return tarjeta;
    }

    private void configurarTabla(
            JTable tabla
    ) {

        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        tabla.setRowHeight(
                34
        );

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setSelectionBackground(
                SELECCION
        );

        tabla.setSelectionForeground(
                TEXTO
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

        tabla.setFillsViewportHeight(
                true
        );

        JTableHeader header =
                tabla.getTableHeader();

        header.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        header.setPreferredSize(
                new Dimension(
                        0,
                        38
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

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        110
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        220
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(
                        280
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(
                        130
                );

        tabla.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(
                        130
                );

        tabla.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.LEFT
                        )
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.LEFT
                        )
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.LEFT
                        )
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.RIGHT
                        )
                );

        tabla.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.RIGHT
                        )
                );
    }

    private JPanel crearPie(
            AsientoContable asiento
    ) {

        JPanel pie =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                13
                        )
                );

        pie.setBackground(
                Color.WHITE
        );

        pie.setBorder(
                BorderFactory.createMatteBorder(
                        1,
                        0,
                        0,
                        0,
                        BORDE
                )
        );

        JButton cerrar =
                crearBotonSecundario(
                        "Cerrar"
                );

        cerrar.addActionListener(
                e -> dispose()
        );

        pie.add(
                cerrar
        );

        if (
                puedeEditar
        ) {

            JButton editar =
                    crearBotonPrincipal(
                            "Editar asiento"
                    );

            boolean manual =
                    "MANUAL".equals(
                            asiento.getTipoAsiento()
                    )
                    &&
                    asiento.getIdOperacion() == null;

            editar.setEnabled(
                    manual
            );

            editar.setToolTipText(
                    manual
                            ? "Modificar este asiento manual"
                            : "Los asientos automáticos se corrigen desde la operación que los originó"
            );

            editar.addActionListener(
                    e -> {

                        new DialogoEditarAsientoManual(
                                this,
                                idAsiento,
                                () -> {

                                    if (
                                            alGuardar != null
                                    ) {

                                        alGuardar.run();
                                    }

                                    dispose();
                                }
                        ).setVisible(
                                true
                        );
                    }
            );

            pie.add(
                    editar
            );
        }

        getRootPane().setDefaultButton(
                cerrar
        );

        return pie;
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
                        12
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
                        16,
                        10,
                        16
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

                        if (
                                boton.isEnabled()
                        ) {

                            boton.setBackground(
                                    PRIMARIO_HOVER
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        if (
                                boton.isEnabled()
                        ) {

                            boton.setBackground(
                                    PRIMARIO
                            );
                        }
                    }
                }
        );

        return boton;
    }

    private JButton crearBotonSecundario(
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
                        12
                )
        );

        boton.setForeground(
                TEXTO
        );

        boton.setBackground(
                Color.WHITE
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                15,
                                9,
                                15
                        )
                )
        );

        boton.setFocusPainted(
                false
        );

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        return boton;
    }

    private String money(
            BigDecimal valor
    ) {

        if (
                valor == null
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

    private class RenderTexto
            extends DefaultTableCellRenderer {

        private final int alineacion;

        public RenderTexto(
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

                setBackground(
                        SELECCION
                );

                setForeground(
                        TEXTO
                );

            } else {

                setBackground(
                        row % 2 == 0
                                ? Color.WHITE
                                : new Color(
                                        248,
                                        250,
                                        252
                                )
                );

                setForeground(
                        TEXTO
                );
            }

            return this;
        }
    }
}
