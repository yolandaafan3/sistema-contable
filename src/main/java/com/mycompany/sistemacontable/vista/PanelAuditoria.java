package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.AuditoriaDAO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PanelAuditoria extends JPanel {

    private final DefaultTableModel modelo =
            new DefaultTableModel(
                    new Object[]{
                        "ID",
                        "Fecha",
                        "Usuario",
                        "Entidad",
                        "Registro",
                        "Acción",
                        "Antes",
                        "Después"
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

    private final JTable tabla =
            new JTable(
                    modelo
            );

    private final JLabel lblTotal =
            new JLabel("0");

    private final JLabel lblCreaciones =
            new JLabel("0");

    private final JLabel lblModificaciones =
            new JLabel("0");

    private final JLabel lblOtros =
            new JLabel("0");

    private final JLabel lblPie =
            new JLabel("Sin registros");

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

    private final Color VERDE_HOVER =
            new Color(21, 128, 61);

    private final Color NARANJA =
            new Color(234, 88, 12);

    private final Color ROJO =
            new Color(220, 38, 38);

    private final Color CELESTE_SUAVE =
            new Color(239, 246, 255);

    private final Color SELECCION =
            new Color(219, 234, 254);

    public PanelAuditoria() {

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

        add(
                crearZonaSuperior(),
                BorderLayout.NORTH
        );

        add(
                crearPanelTabla(),
                BorderLayout.CENTER
        );

        add(
                crearPie(),
                BorderLayout.SOUTH
        );

        cargar();
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
                crearResumen()
        );

        return zona;
    }

    private JPanel crearEncabezado() {

        JPanel encabezado =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        encabezado.setOpaque(
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
                        "Bitácora de auditoría"
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

        JLabel subtitulo =
                new JLabel(
                        "Consulta los cambios realizados y revisa el detalle completo de cada registro."
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitulo.setForeground(
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
                subtitulo
        );

        JPanel acciones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        acciones.setOpaque(
                false
        );

        JButton verDetalles =
                crearBotonPrincipal(
                        "◉  Ver detalles",
                        PRIMARIO,
                        PRIMARIO_HOVER
                );

        JButton actualizar =
                crearBotonSecundario(
                        "↻  Actualizar"
                );

        JButton exportar =
                crearBotonPrincipal(
                        "⇩  Exportar Excel",
                        VERDE,
                        VERDE_HOVER
                );

        verDetalles.addActionListener(
                e -> mostrarDetalleSeleccionado()
        );

        actualizar.addActionListener(
                e -> cargar()
        );

        exportar.addActionListener(
                e -> exportarAuditoria()
        );

        acciones.add(
                verDetalles
        );

        acciones.add(
                actualizar
        );

        acciones.add(
                exportar
        );

        encabezado.add(
                textos,
                BorderLayout.WEST
        );

        encabezado.add(
                acciones,
                BorderLayout.EAST
        );

        return encabezado;
    }

    private JPanel crearResumen() {

        JPanel resumen =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                14,
                                0
                        )
                );

        resumen.setOpaque(
                false
        );

        resumen.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        100
                )
        );

        resumen.add(
                crearTarjetaResumen(
                        "Registros",
                        lblTotal,
                        "≡",
                        new Color(
                                219,
                                234,
                                254
                        ),
                        PRIMARIO
                )
        );

        resumen.add(
                crearTarjetaResumen(
                        "Creaciones",
                        lblCreaciones,
                        "+",
                        new Color(
                                220,
                                252,
                                231
                        ),
                        VERDE
                )
        );

        resumen.add(
                crearTarjetaResumen(
                        "Modificaciones",
                        lblModificaciones,
                        "✎",
                        new Color(
                                255,
                                247,
                                237
                        ),
                        NARANJA
                )
        );

        resumen.add(
                crearTarjetaResumen(
                        "Otros eventos",
                        lblOtros,
                        "•",
                        new Color(
                                241,
                                245,
                                249
                        ),
                        SECUNDARIO
                )
        );

        return resumen;
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
                        18
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

    private JPanel crearPanelTabla() {

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
                                16,
                                16,
                                16,
                                16
                        )
                )
        );

        JPanel cabecera =
                new JPanel();

        cabecera.setOpaque(
                false
        );

        cabecera.setLayout(
                new BoxLayout(
                        cabecera,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Registros de auditoría"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel descripcion =
                new JLabel(
                        "Doble clic sobre una fila para consultar el detalle completo de los cambios."
                );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        descripcion.setForeground(
                SECUNDARIO
        );

        cabecera.add(
                titulo
        );

        cabecera.add(
                Box.createVerticalStrut(
                        3
                )
        );

        cabecera.add(
                descripcion
        );

        configurarTabla();

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

        tarjeta.add(
                cabecera,
                BorderLayout.NORTH
        );

        tarjeta.add(
                scroll,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private void configurarTabla() {

        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        tabla.setRowHeight(
                36
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

        tabla.setAutoCreateRowSorter(
                true
        );

        tabla.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
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

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        55
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        150
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(
                        185
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(
                        105
                );

        tabla.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(
                        80
                );

        tabla.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(
                        105
                );

        tabla.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(
                        220
                );

        tabla.getColumnModel()
                .getColumn(7)
                .setPreferredWidth(
                        220
                );

        tabla.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        new RenderTabla(
                                SwingConstants.CENTER
                        )
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        new RenderTabla(
                                SwingConstants.LEFT
                        )
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new RenderUsuario()
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new RenderEntidad()
                );

        tabla.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new RenderTabla(
                                SwingConstants.CENTER
                        )
                );

        tabla.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new RenderAccion()
                );

        tabla.getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        new RenderCambio()
                );

        tabla.getColumnModel()
                .getColumn(7)
                .setCellRenderer(
                        new RenderCambio()
                );

        ToolTipManager.sharedInstance()
                .setInitialDelay(
                        250
                );

        tabla.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent e
                    ) {

                        if (
                                e.getClickCount() == 2
                                &&
                                tabla.getSelectedRow() >= 0
                        ) {

                            mostrarDetalleSeleccionado();
                        }
                    }
                }
        );
    }

    private JPanel crearPie() {

        JPanel pie =
                new JPanel(
                        new BorderLayout()
                );

        pie.setBackground(
                Color.WHITE
        );

        pie.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                11,
                                15,
                                11,
                                15
                        )
                )
        );

        lblPie.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblPie.setForeground(
                SECUNDARIO
        );

        pie.add(
                lblPie,
                BorderLayout.WEST
        );

        JLabel ayuda =
                new JLabel(
                        "La bitácora es de consulta y conserva el historial de cambios."
                );

        ayuda.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        ayuda.setForeground(
                SECUNDARIO
        );

        pie.add(
                ayuda,
                BorderLayout.EAST
        );

        return pie;
    }

    public final void cargar() {

        modelo.setRowCount(
                0
        );

        List<Object[]> registros =
                new ArrayList<>();

        for (
                Object[] fila
                : new AuditoriaDAO().listar()
        ) {

            registros.add(
                    fila
            );

            modelo.addRow(
                    fila
            );
        }

        int creaciones =
                0;

        int modificaciones =
                0;

        int otros =
                0;

        for (
                Object[] fila
                : registros
        ) {

            String accion =
                    fila != null
                    && fila.length > 5
                    && fila[5] != null
                            ? String.valueOf(
                                    fila[5]
                            )
                            : "";

            String normalizada =
                    accion.trim()
                            .toUpperCase();

            if (
                    normalizada.contains("CRE")
            ) {

                creaciones++;

            } else if (
                    normalizada.contains("MOD")
                    ||
                    normalizada.contains("ACTUAL")
            ) {

                modificaciones++;

            } else {

                otros++;
            }
        }

        lblTotal.setText(
                String.valueOf(
                        registros.size()
                )
        );

        lblCreaciones.setText(
                String.valueOf(
                        creaciones
                )
        );

        lblModificaciones.setText(
                String.valueOf(
                        modificaciones
                )
        );

        lblOtros.setText(
                String.valueOf(
                        otros
                )
        );

        lblPie.setText(
                registros.size()
                + (
                        registros.size() == 1
                                ? " registro de auditoría"
                                : " registros de auditoría"
                )
        );
    }

    private void mostrarDetalleSeleccionado() {

        int filaVista =
                tabla.getSelectedRow();

        if (
                filaVista < 0
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un registro de la bitácora.",
                    "Bitácora de auditoría",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int fila =
                tabla.convertRowIndexToModel(
                        filaVista
                );

        Window owner =
                SwingUtilities.getWindowAncestor(
                        this
                );

        JDialog dialogo =
                new JDialog(
                        owner,
                        "Detalle de auditoría #"
                        + valor(
                                0,
                                fila
                        ),
                        JDialog.ModalityType.APPLICATION_MODAL
                );

        dialogo.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialogo.setSize(
                920,
                650
        );

        dialogo.setMinimumSize(
                new Dimension(
                        760,
                        560
                )
        );

        dialogo.setLocationRelativeTo(
                owner
        );

        dialogo.getContentPane()
                .setBackground(
                        FONDO
                );

        dialogo.setLayout(
                new BorderLayout()
        );

        dialogo.add(
                crearEncabezadoDetalle(
                        fila
                ),
                BorderLayout.NORTH
        );

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
                crearTarjetaDatosDetalle(
                        fila
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        14
                )
        );

        contenido.add(
                crearTarjetaCambios(
                        fila
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        contenido
                );

        scroll.setBorder(
                null
        );

        scroll.setOpaque(
                false
        );

        scroll.getViewport()
                .setOpaque(
                        false
                );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(
                        18
                );

        dialogo.add(
                scroll,
                BorderLayout.CENTER
        );

        dialogo.add(
                crearPieDetalle(
                        dialogo
                ),
                BorderLayout.SOUTH
        );

        dialogo.setVisible(
                true
        );
    }

    private JPanel crearEncabezadoDetalle(
            int fila
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
                        21
                )
        );

        icono.setPreferredSize(
                new Dimension(
                        48,
                        48
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
                        "Detalle de auditoría #"
                        + valor(
                                0,
                                fila
                        )
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel subtitulo =
                new JLabel(
                        valor(
                                3,
                                fila
                        )
                        + " · "
                        + valor(
                                5,
                                fila
                        )
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        subtitulo.setForeground(
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
                subtitulo
        );

        panel.add(
                icono,
                BorderLayout.WEST
        );

        panel.add(
                textos,
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel crearTarjetaDatosDetalle(
            int fila
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
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
                                16,
                                18,
                                16,
                                18
                        )
                )
        );

        tarjeta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        tarjeta.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        220
                )
        );

        JLabel titulo =
                new JLabel(
                        "Información del registro"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JPanel datos =
                new JPanel(
                        new GridLayout(
                                2,
                                3,
                                12,
                                12
                        )
                );

        datos.setOpaque(
                false
        );

        datos.add(
                crearCampoDetalle(
                        "Fecha",
                        valor(
                                1,
                                fila
                        )
                )
        );

        datos.add(
                crearCampoDetalle(
                        "Usuario",
                        valor(
                                2,
                                fila
                        )
                )
        );

        datos.add(
                crearCampoDetalle(
                        "Entidad",
                        valor(
                                3,
                                fila
                        )
                )
        );

        datos.add(
                crearCampoDetalle(
                        "Registro",
                        valor(
                                4,
                                fila
                        )
                )
        );

        datos.add(
                crearCampoDetalle(
                        "Acción",
                        valor(
                                5,
                                fila
                        )
                )
        );

        datos.add(
                crearCampoDetalle(
                        "ID auditoría",
                        valor(
                                0,
                                fila
                        )
                )
        );

        tarjeta.add(
                titulo,
                BorderLayout.NORTH
        );

        tarjeta.add(
                datos,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearTarjetaCambios(
            int fila
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
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
                                16,
                                18,
                                16,
                                18
                        )
                )
        );

        tarjeta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        tarjeta.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1000
                )
        );

        JPanel cabecera =
                new JPanel();

        cabecera.setOpaque(
                false
        );

        cabecera.setLayout(
                new BoxLayout(
                        cabecera,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Cambios registrados"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel ayuda =
                new JLabel(
                        "Compara el estado anterior con la información registrada después de la acción."
                );

        ayuda.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        ayuda.setForeground(
                SECUNDARIO
        );

        cabecera.add(
                titulo
        );

        cabecera.add(
                Box.createVerticalStrut(
                        3
                )
        );

        cabecera.add(
                ayuda
        );

        JPanel cambios =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                12,
                                0
                        )
                );

        cambios.setOpaque(
                false
        );

        cambios.add(
                crearBloqueDetalle(
                        "Antes",
                        valor(
                                6,
                                fila
                        ),
                        new Color(
                                248,
                                250,
                                252
                        )
                )
        );

        cambios.add(
                crearBloqueDetalle(
                        "Después",
                        valor(
                                7,
                                fila
                        ),
                        CELESTE_SUAVE
                )
        );

        tarjeta.add(
                cabecera,
                BorderLayout.NORTH
        );

        tarjeta.add(
                cambios,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearCampoDetalle(
            String titulo,
            String texto
    ) {

        JPanel campo =
                new JPanel();

        campo.setBackground(
                new Color(
                        248,
                        250,
                        252
                )
        );

        campo.setLayout(
                new BoxLayout(
                        campo,
                        BoxLayout.Y_AXIS
                )
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                10,
                                12,
                                10,
                                12
                        )
                )
        );

        JLabel label =
                new JLabel(
                        titulo
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        label.setForeground(
                SECUNDARIO
        );

        JLabel valor =
                new JLabel(
                        texto == null
                        || texto.isBlank()
                                ? "—"
                                : texto
                );

        valor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        valor.setForeground(
                TEXTO
        );

        campo.add(
                label
        );

        campo.add(
                Box.createVerticalStrut(
                        4
                )
        );

        campo.add(
                valor
        );

        return campo;
    }

    private JPanel crearBloqueDetalle(
            String titulo,
            String texto,
            Color fondo
    ) {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                7
                        )
                );

        panel.setBackground(
                fondo
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                12,
                                12,
                                12,
                                12
                        )
                )
        );

        JLabel label =
                new JLabel(
                        titulo
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        label.setForeground(
                "Después".equals(titulo)
                        ? PRIMARIO
                        : TEXTO
        );

        JTextArea area =
                new JTextArea(
                        texto == null
                        || texto.isBlank()
                                ? "(Sin datos)"
                                : texto
                );

        area.setLineWrap(
                true
        );

        area.setWrapStyleWord(
                true
        );

        area.setEditable(
                false
        );

        area.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        12
                )
        );

        area.setForeground(
                TEXTO
        );

        area.setBackground(
                Color.WHITE
        );

        area.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        area
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );

        panel.add(
                label,
                BorderLayout.NORTH
        );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel crearPieDetalle(
            JDialog dialogo
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
                crearBotonPrincipal(
                        "Cerrar",
                        PRIMARIO,
                        PRIMARIO_HOVER
                );

        cerrar.addActionListener(
                e -> dialogo.dispose()
        );

        pie.add(
                cerrar
        );

        dialogo.getRootPane()
                .setDefaultButton(
                        cerrar
                );

        return pie;
    }

    private void exportarAuditoria() {

        if (
                modelo.getRowCount() == 0
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "No hay registros de auditoría para exportar.",
                    "Bitácora de auditoría",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        JFileChooser chooser =
                new JFileChooser();

        chooser.setSelectedFile(
                new File(
                        "ContaProMax_Auditoria.xlsx"
                )
        );

        if (
                chooser.showSaveDialog(
                        this
                )
                != JFileChooser.APPROVE_OPTION
        ) {

            return;
        }

        File archivo =
                chooser.getSelectedFile();

        if (
                !archivo.getName()
                        .toLowerCase()
                        .endsWith(
                                ".xlsx"
                        )
        ) {

            archivo =
                    new File(
                            archivo.getAbsolutePath()
                            + ".xlsx"
                    );
        }

        try (
                Workbook workbook =
                        new XSSFWorkbook()
        ) {

            Sheet hoja =
                    workbook.createSheet(
                            "Auditoria"
                    );

            hoja.setDisplayGridlines(
                    false
            );

            CellStyle tituloStyle =
                    workbook.createCellStyle();

            org.apache.poi.ss.usermodel.Font tituloFont =
                    workbook.createFont();

            tituloFont.setBold(
                    true
            );

            tituloFont.setFontHeightInPoints(
                    (short) 18
            );

            tituloFont.setColor(
                    IndexedColors.DARK_BLUE.getIndex()
            );

            tituloStyle.setFont(
                    tituloFont
            );

            Row titulo =
                    hoja.createRow(
                            0
                    );

            Cell tituloCell =
                    titulo.createCell(
                            0
                    );

            tituloCell.setCellValue(
                    "ContaProMax - Bitácora de auditoría"
            );

            tituloCell.setCellStyle(
                    tituloStyle
            );

            hoja.addMergedRegion(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            0,
                            0,
                            0,
                            7
                    )
            );

            CellStyle headerStyle =
                    workbook.createCellStyle();

            headerStyle.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex()
            );

            headerStyle.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND
            );

            headerStyle.setAlignment(
                    HorizontalAlignment.CENTER
            );

            headerStyle.setVerticalAlignment(
                    VerticalAlignment.CENTER
            );

            headerStyle.setBorderBottom(
                    BorderStyle.THIN
            );

            headerStyle.setBorderTop(
                    BorderStyle.THIN
            );

            headerStyle.setBorderLeft(
                    BorderStyle.THIN
            );

            headerStyle.setBorderRight(
                    BorderStyle.THIN
            );

            org.apache.poi.ss.usermodel.Font headerFont =
                    workbook.createFont();

            headerFont.setBold(
                    true
            );

            headerFont.setColor(
                    IndexedColors.WHITE.getIndex()
            );

            headerStyle.setFont(
                    headerFont
            );

            CellStyle bodyStyle =
                    workbook.createCellStyle();

            bodyStyle.setVerticalAlignment(
                    VerticalAlignment.TOP
            );

            bodyStyle.setBorderBottom(
                    BorderStyle.THIN
            );

            bodyStyle.setBorderTop(
                    BorderStyle.THIN
            );

            bodyStyle.setBorderLeft(
                    BorderStyle.THIN
            );

            bodyStyle.setBorderRight(
                    BorderStyle.THIN
            );

            bodyStyle.setWrapText(
                    true
            );

            Row encabezado =
                    hoja.createRow(
                            2
                    );

            for (
                    int columna = 0;
                    columna < modelo.getColumnCount();
                    columna++
            ) {

                Cell celda =
                        encabezado.createCell(
                                columna
                        );

                celda.setCellValue(
                        modelo.getColumnName(
                                columna
                        )
                );

                celda.setCellStyle(
                        headerStyle
                );
            }

            for (
                    int fila = 0;
                    fila < modelo.getRowCount();
                    fila++
            ) {

                Row excelRow =
                        hoja.createRow(
                                fila + 3
                        );

                for (
                        int columna = 0;
                        columna < modelo.getColumnCount();
                        columna++
                ) {

                    Cell celda =
                            excelRow.createCell(
                                    columna
                            );

                    Object valor =
                            modelo.getValueAt(
                                    fila,
                                    columna
                            );

                    celda.setCellValue(
                            valor == null
                                    ? ""
                                    : String.valueOf(
                                            valor
                                    )
                    );

                    celda.setCellStyle(
                            bodyStyle
                    );
                }
            }

            for (
                    int columna = 0;
                    columna < modelo.getColumnCount();
                    columna++
            ) {

                hoja.autoSizeColumn(
                        columna
                );

                int ancho =
                        Math.min(
                                hoja.getColumnWidth(
                                        columna
                                )
                                + 800,
                                columna >= 6
                                        ? 18000
                                        : 8000
                        );

                hoja.setColumnWidth(
                        columna,
                        ancho
                );
            }

            hoja.createFreezePane(
                    0,
                    3
            );

            hoja.setAutoFilter(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            2,
                            Math.max(
                                    2,
                                    modelo.getRowCount() + 2
                            ),
                            0,
                            7
                    )
            );

            try (
                    FileOutputStream salida =
                            new FileOutputStream(
                                    archivo
                            )
            ) {

                workbook.write(
                        salida
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Auditoría exportada correctamente.",
                    "ContaProMax",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (
                Exception e
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo exportar la auditoría",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JButton crearBotonPrincipal(
            String texto,
            Color fondo,
            Color hover
    ) {

        JButton boton =
                new JButton(
                        texto
                );

        boton.setUI(
                new BasicButtonUI()
        );

        boton.setBackground(
                fondo
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        15,
                        10,
                        15
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
                                hover
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                fondo
                        );
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

        boton.setBackground(
                Color.WHITE
        );

        boton.setForeground(
                TEXTO
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                14,
                                9,
                                14
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

    private String valor(
            int columna,
            int fila
    ) {

        Object objeto =
                modelo.getValueAt(
                        fila,
                        columna
                );

        return objeto == null
                ? ""
                : String.valueOf(
                        objeto
                );
    }

    private String resumir(
            String texto,
            int limite
    ) {

        if (
                texto == null
                ||
                texto.isBlank()
        ) {

            return "—";
        }

        String limpio =
                texto.replace(
                        "\n",
                        " "
                )
                .replace(
                        "\r",
                        " "
                )
                .trim();

        if (
                limpio.length() <= limite
        ) {

            return limpio;
        }

        return limpio.substring(
                0,
                limite
        )
        + "...";
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
                        SELECCION
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

                componente.setForeground(
                        TEXTO
                );
            }

            return componente;
        }
    }

    private class RenderUsuario
            extends DefaultTableCellRenderer {

        public RenderUsuario() {

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

            setText(
                    "●  "
                    + (
                            value == null
                                    ? ""
                                    : value.toString()
                    )
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
                        PRIMARIO
                );
            }

            return this;
        }
    }

    private class RenderEntidad
            extends DefaultTableCellRenderer {

        public RenderEntidad() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

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

            setText(
                    value == null
                            ? ""
                            : value.toString()
            );

            setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            11
                    )
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            5,
                            8,
                            5,
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
                        CELESTE_SUAVE
                );

                setForeground(
                        PRIMARIO
                );
            }

            return this;
        }
    }

    private class RenderAccion
            extends DefaultTableCellRenderer {

        public RenderAccion() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

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

            String accion =
                    value == null
                            ? ""
                            : value.toString()
                                    .toUpperCase();

            setText(
                    accion
            );

            setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            11
                    )
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            5,
                            8,
                            5,
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

                return this;
            }

            if (
                    accion.contains(
                            "CRE"
                    )
            ) {

                setBackground(
                        new Color(
                                220,
                                252,
                                231
                        )
                );

                setForeground(
                        VERDE
                );

            } else if (
                    accion.contains(
                            "MOD"
                    )
                    ||
                    accion.contains(
                            "ACTUAL"
                    )
            ) {

                setBackground(
                        new Color(
                                255,
                                247,
                                237
                        )
                );

                setForeground(
                        NARANJA
                );

            } else if (
                    accion.contains(
                            "ELIM"
                    )
            ) {

                setBackground(
                        new Color(
                                254,
                                242,
                                242
                        )
                );

                setForeground(
                        ROJO
                );

            } else {

                setBackground(
                        new Color(
                                241,
                                245,
                                249
                        )
                );

                setForeground(
                        TEXTO
                );
            }

            return this;
        }
    }

    private class RenderCambio
            extends DefaultTableCellRenderer {

        public RenderCambio() {

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

            String original =
                    value == null
                            ? ""
                            : value.toString();

            setText(
                    resumir(
                            original,
                            58
                    )
            );

            setToolTipText(
                    original == null
                    || original.isBlank()
                            ? "(Sin datos)"
                            : original
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
                        SECUNDARIO
                );
            }

            return this;
        }
    }
}
