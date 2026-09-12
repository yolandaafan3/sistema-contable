package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.LineaLibroDiario;
import com.mycompany.sistemacontable.servicio.LibroDiarioService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.format.DateTimeFormatter;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelLibroDiario extends JPanel {

    private final LibroDiarioService libroDiarioService;

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JLabel lblTotalDebe;
    private JLabel lblTotalHaber;
    private JLabel lblEstado;

    private JButton btnActualizar;

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy"
            );

    private final Color COLOR_FONDO =
            new Color(245, 247, 250);

    private final Color COLOR_TEXTO =
            new Color(30, 41, 59);

    private final Color COLOR_SECUNDARIO =
            new Color(100, 116, 139);

    private final Color COLOR_PRIMARIO =
            new Color(37, 99, 235);

    private final Color COLOR_PRIMARIO_HOVER =
            new Color(29, 78, 216);

    private final Color COLOR_BORDE =
            new Color(226, 232, 240);

    private final Color COLOR_EXITO =
            new Color(22, 163, 74);

    private final Color COLOR_ERROR =
            new Color(220, 38, 38);


    public PanelLibroDiario() {

        libroDiarioService =
                new LibroDiarioService();

        configurarPanel();

        construirInterfaz();

        cargarLibroDiario();
    }


    // =========================================================
    // CONFIGURACION
    // =========================================================

    private void configurarPanel() {

        setLayout(
                new BorderLayout(
                        0,
                        20
                )
        );

        setBackground(
                COLOR_FONDO
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        28,
                        30,
                        30,
                        30
                )
        );
    }


    // =========================================================
    // INTERFAZ
    // =========================================================

    private void construirInterfaz() {

        add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        add(
                crearTabla(),
                BorderLayout.CENTER
        );

        add(
                crearResumen(),
                BorderLayout.SOUTH
        );
    }


    // =========================================================
    // ENCABEZADO
    // =========================================================

    private JPanel crearEncabezado() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
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
                new javax.swing.BoxLayout(
                        textos,
                        javax.swing.BoxLayout.Y_AXIS
                )
        );


        JLabel titulo =
                new JLabel(
                        "Libro Diario"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(
                COLOR_TEXTO
        );


        JLabel descripcion =
                new JLabel(
                        "Asientos contables registrados durante el período activo."
                );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        descripcion.setForeground(
                COLOR_SECUNDARIO
        );


        textos.add(
                titulo
        );

        textos.add(
                javax.swing.Box.createVerticalStrut(
                        5
                )
        );

        textos.add(
                descripcion
        );


        btnActualizar =
                new JButton(
                        "Actualizar"
                );

        configurarBotonPrincipal(
                btnActualizar
        );

        btnActualizar.addActionListener(
                e -> cargarLibroDiario()
        );


        JPanel panelBoton =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                0,
                                0
                        )
                );

        panelBoton.setOpaque(
                false
        );

        panelBoton.add(
                btnActualizar
        );


        panel.add(
                textos,
                BorderLayout.WEST
        );

        panel.add(
                panelBoton,
                BorderLayout.EAST
        );


        return panel;
    }


    // =========================================================
    // TABLA
    // =========================================================

    private JScrollPane crearTabla() {

        String[] columnas = {
            "Asiento",
            "Fecha",
            "Código",
            "Cuenta",
            "Concepto",
            "Descripción",
            "Debe",
            "Haber"
        };


        modeloTabla =
                new DefaultTableModel(
                        columnas,
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
                        modeloTabla
                );


        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );


        tabla.setRowHeight(
                30
        );


        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );


        tabla.setShowHorizontalLines(
                true
        );


        tabla.setShowVerticalLines(
                false
        );


        tabla.setGridColor(
                COLOR_BORDE
        );


        tabla.setBackground(
                Color.WHITE
        );


        tabla.setForeground(
                COLOR_TEXTO
        );


        tabla.setSelectionBackground(
                new Color(
                        219,
                        234,
                        254
                )
        );


        tabla.setSelectionForeground(
                COLOR_TEXTO
        );


        tabla.setAutoCreateRowSorter(
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


        header.setBackground(
                new Color(
                        241,
                        245,
                        249
                )
        );


        header.setForeground(
                COLOR_TEXTO
        );


        header.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );


        // =====================================================
        // ANCHOS
        // =====================================================

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        65
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        90
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(
                        90
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(
                        180
                );

        tabla.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(
                        190
                );

        tabla.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(
                        190
                );

        tabla.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(
                        100
                );

        tabla.getColumnModel()
                .getColumn(7)
                .setPreferredWidth(
                        100
                );


        // =====================================================
        // ALINEAR MONTOS
        // =====================================================

        DefaultTableCellRenderer rendererMonto =
                new DefaultTableCellRenderer();

        rendererMonto.setHorizontalAlignment(
                SwingConstants.RIGHT
        );


        tabla.getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        rendererMonto
                );

        tabla.getColumnModel()
                .getColumn(7)
                .setCellRenderer(
                        rendererMonto
                );


        DefaultTableCellRenderer rendererCentro =
                new DefaultTableCellRenderer();

        rendererCentro.setHorizontalAlignment(
                SwingConstants.CENTER
        );


        tabla.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        rendererCentro
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        rendererCentro
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        rendererCentro
                );


        JScrollPane scroll =
                new JScrollPane(
                        tabla
                );


        scroll.setBorder(
                BorderFactory.createLineBorder(
                        COLOR_BORDE
                )
        );


        scroll.getViewport()
                .setBackground(
                        Color.WHITE
                );


        return scroll;
    }


    // =========================================================
    // RESUMEN
    // =========================================================

    private JPanel crearResumen() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                16,
                                20,
                                16,
                                20
                        )
                )
        );


        lblEstado =
                new JLabel(
                        "Sin movimientos"
                );

        lblEstado.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblEstado.setForeground(
                COLOR_SECUNDARIO
        );


        JPanel totales =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                25,
                                0
                        )
                );

        totales.setOpaque(
                false
        );


        JLabel textoDebe =
                crearEtiquetaResumen(
                        "Total Debe:"
                );


        lblTotalDebe =
                crearValorResumen(
                        "$0.00"
                );


        JLabel textoHaber =
                crearEtiquetaResumen(
                        "Total Haber:"
                );


        lblTotalHaber =
                crearValorResumen(
                        "$0.00"
                );


        totales.add(
                textoDebe
        );

        totales.add(
                lblTotalDebe
        );

        totales.add(
                textoHaber
        );

        totales.add(
                lblTotalHaber
        );


        panel.add(
                lblEstado,
                BorderLayout.WEST
        );

        panel.add(
                totales,
                BorderLayout.EAST
        );


        return panel;
    }


    // =========================================================
    // CARGAR DATOS
    // =========================================================

    public final void cargarLibroDiario() {

        try {

            btnActualizar.setEnabled(
                    false
            );


            modeloTabla.setRowCount(
                    0
            );


            List<LineaLibroDiario> lineas =
                    libroDiarioService
                            .obtenerLibroDiario();


            for (LineaLibroDiario linea : lineas) {

                modeloTabla.addRow(
                        new Object[]{
                            linea.getNumeroAsiento(),
                            linea.getFecha() == null
                                    ? ""
                                    : linea.getFecha()
                                            .format(
                                                    formatoFecha
                                            ),
                            linea.getCodigoCuenta(),
                            linea.getNombreCuenta(),
                            linea.getConcepto(),
                            linea.getDescripcion(),
                            formatoDinero(
                                    linea.getDebe()
                            ),
                            formatoDinero(
                                    linea.getHaber()
                            )
                        }
                );
            }


            BigDecimal totalDebe =
                    libroDiarioService
                            .obtenerTotalDebe();


            BigDecimal totalHaber =
                    libroDiarioService
                            .obtenerTotalHaber();


            lblTotalDebe.setText(
                    formatoDinero(
                            totalDebe
                    )
            );


            lblTotalHaber.setText(
                    formatoDinero(
                            totalHaber
                    )
            );


            if (lineas.isEmpty()) {

                lblEstado.setText(
                        "Sin movimientos registrados"
                );

                lblEstado.setForeground(
                        COLOR_SECUNDARIO
                );

            } else if (totalDebe.compareTo(
                    totalHaber
            ) == 0) {

                lblEstado.setText(
                        "✓ Libro Diario cuadrado"
                );

                lblEstado.setForeground(
                        COLOR_EXITO
                );

            } else {

                lblEstado.setText(
                        "⚠ Libro Diario descuadrado"
                );

                lblEstado.setForeground(
                        COLOR_ERROR
                );
            }


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(
                            e
                    ),
                    "No se pudo cargar el Libro Diario",
                    JOptionPane.ERROR_MESSAGE
            );


        } finally {

            btnActualizar.setEnabled(
                    true
            );
        }
    }


    // =========================================================
    // FORMATO
    // =========================================================

    private String formatoDinero(
            BigDecimal valor
    ) {

        if (valor == null) {

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


    // =========================================================
    // COMPONENTES
    // =========================================================

    private JLabel crearEtiquetaResumen(
            String texto
    ) {

        JLabel label =
                new JLabel(
                        texto
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        label.setForeground(
                COLOR_SECUNDARIO
        );

        return label;
    }


    private JLabel crearValorResumen(
            String texto
    ) {

        JLabel label =
                new JLabel(
                        texto
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        label.setForeground(
                COLOR_TEXTO
        );

        return label;
    }


    private String obtenerMensajeError(
            Throwable error
    ) {

        Throwable actual =
                error;

        String mensaje =
                "Ocurrió un error desconocido.";


        while (actual != null) {

            if (actual.getMessage() != null
                    &&
                !actual.getMessage().isBlank()) {

                mensaje =
                        actual.getMessage();
            }

            actual =
                    actual.getCause();
        }


        return mensaje;
    }


    // =========================================================
    // BOTON
    // =========================================================

    private void configurarBotonPrincipal(
            JButton boton
    ) {

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
                COLOR_PRIMARIO
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

                        if (boton.isEnabled()) {

                            boton.setBackground(
                                    COLOR_PRIMARIO_HOVER
                            );
                        }
                    }


                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                COLOR_PRIMARIO
                        );
                    }
                }
        );
    }
}