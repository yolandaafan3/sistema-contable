package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.modelo.Cuenta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PanelCatalogoCuentas extends JPanel {

    private final CuentaDAO cuentaDAO;

    private JTable tablaCuentas;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField txtBuscar;
    private JComboBox<String> cmbTipo;
    private JComboBox<String> cmbMovimiento;
    private JComboBox<String> cmbEstado;

    private final JLabel lblCantidad =
            new JLabel("0 cuentas visibles");

    private final JLabel lblTotal =
            new JLabel("0");

    private final JLabel lblMovimiento =
            new JLabel("0");

    private final JLabel lblActivas =
            new JLabel("0");

    private final JLabel lblTipos =
            new JLabel("0");

    private final Color FONDO =
            new Color(245, 247, 250);

    private final Color TEXTO =
            new Color(30, 41, 59);

    private final Color SECUNDARIO =
            new Color(100, 116, 139);

    private final Color BORDE =
            new Color(226, 232, 240);

    private final Color AZUL =
            new Color(37, 99, 235);

    private final Color AZUL_HOVER =
            new Color(29, 78, 216);

    private final Color VERDE =
            new Color(22, 163, 74);

    private final Color VERDE_HOVER =
            new Color(21, 128, 61);

    private final Color NARANJA =
            new Color(234, 88, 12);

    private final Color MORADO =
            new Color(126, 34, 206);

    private final Color CELESTE_SUAVE =
            new Color(239, 246, 255);

    private final Color SELECCION =
            new Color(219, 234, 254);

    public PanelCatalogoCuentas() {

        cuentaDAO =
                new CuentaDAO();

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
                crearContenido(),
                BorderLayout.CENTER
        );

        cargarCuentas();
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
                        "Catálogo de Cuentas"
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

        JLabel descripcion =
                new JLabel(
                        "Consulta, filtra y exporta las cuentas contables utilizadas por el sistema."
                );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        descripcion.setForeground(
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
                descripcion
        );

        JButton btnActualizar =
                crearBotonPrincipal(
                        "↻  Actualizar",
                        AZUL,
                        AZUL_HOVER
                );

        btnActualizar.addActionListener(
                e -> cargarCuentas()
        );

        JButton btnExportar =
                crearBotonPrincipal(
                        "⇩  Exportar a Excel",
                        VERDE,
                        VERDE_HOVER
                );

        btnExportar.addActionListener(
                e -> exportarCatalogoExcel()
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

        acciones.add(
                btnActualizar
        );

        acciones.add(
                btnExportar
        );

        panel.add(
                textos,
                BorderLayout.WEST
        );

        panel.add(
                acciones,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel crearResumen() {

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
                        100
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Total de cuentas",
                        lblTotal,
                        "≡",
                        new Color(
                                219,
                                234,
                                254
                        ),
                        AZUL
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Permiten movimiento",
                        lblMovimiento,
                        "↕",
                        new Color(
                                220,
                                252,
                                231
                        ),
                        VERDE
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Cuentas activas",
                        lblActivas,
                        "✓",
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
                        "Tipos contables",
                        lblTipos,
                        "▤",
                        new Color(
                                243,
                                232,
                                255
                        ),
                        MORADO
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

    private JPanel crearContenido() {

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
                                16,
                                16,
                                16
                        )
                )
        );

        tarjeta.add(
                crearBarraFiltros(),
                BorderLayout.NORTH
        );

        tarjeta.add(
                crearTabla(),
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearBarraFiltros() {

        JPanel principal =
                new JPanel(
                        new BorderLayout(
                                14,
                                0
                        )
                );

        principal.setOpaque(
                false
        );

        JPanel filtros =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        filtros.setOpaque(
                false
        );

        JLabel iconoBuscar =
                new JLabel(
                        "⌕"
                );

        iconoBuscar.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        18
                )
        );

        iconoBuscar.setForeground(
                AZUL
        );

        txtBuscar =
                new JTextField();

        txtBuscar.setPreferredSize(
                new Dimension(
                        250,
                        36
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
                "Buscar por código, nombre, tipo, clasificación, naturaleza o rol"
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

        cmbTipo =
                new JComboBox<>(
                        new String[]{
                            "Todos los tipos",
                            "ACTIVO",
                            "PASIVO",
                            "PATRIMONIO",
                            "INGRESO",
                            "COSTO",
                            "GASTO"
                        }
                );

        cmbMovimiento =
                new JComboBox<>(
                        new String[]{
                            "Movimiento: Todos",
                            "Movimiento: Sí",
                            "Movimiento: No"
                        }
                );

        cmbEstado =
                new JComboBox<>(
                        new String[]{
                            "Estado: Todos",
                            "Estado: Activa",
                            "Estado: Inactiva"
                        }
                );

        configurarCombo(
                cmbTipo,
                150
        );

        configurarCombo(
                cmbMovimiento,
                155
        );

        configurarCombo(
                cmbEstado,
                145
        );

        JButton limpiar =
                crearBotonSecundario(
                        "Limpiar filtros"
                );

        limpiar.addActionListener(
                e -> limpiarFiltros()
        );

        filtros.add(
                iconoBuscar
        );

        filtros.add(
                txtBuscar
        );

        filtros.add(
                cmbTipo
        );

        filtros.add(
                cmbMovimiento
        );

        filtros.add(
                cmbEstado
        );

        filtros.add(
                limpiar
        );

        lblCantidad.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblCantidad.setForeground(
                SECUNDARIO
        );

        principal.add(
                filtros,
                BorderLayout.WEST
        );

        principal.add(
                lblCantidad,
                BorderLayout.EAST
        );

        return principal;
    }

    private void configurarCombo(
            JComboBox<String> combo,
            int ancho
    ) {

        combo.setPreferredSize(
                new Dimension(
                        ancho,
                        36
                )
        );

        combo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        combo.setBackground(
                Color.WHITE
        );
    }

    private JScrollPane crearTabla() {

        modeloTabla =
                new DefaultTableModel(
                        new Object[]{
                            "Código",
                            "Nombre",
                            "Tipo",
                            "Clasificación",
                            "Naturaleza",
                            "Rol Reporte",
                            "Movimiento",
                            "Estado"
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

        tablaCuentas =
                new JTable(
                        modeloTabla
                );

        tablaCuentas.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        tablaCuentas.setRowHeight(
                35
        );

        tablaCuentas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tablaCuentas.setSelectionBackground(
                SELECCION
        );

        tablaCuentas.setSelectionForeground(
                TEXTO
        );

        tablaCuentas.setGridColor(
                BORDE
        );

        tablaCuentas.setShowVerticalLines(
                false
        );

        tablaCuentas.setShowHorizontalLines(
                true
        );

        tablaCuentas.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        tablaCuentas.setFillsViewportHeight(
                true
        );

        JTableHeader header =
                tablaCuentas.getTableHeader();

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

        sorter =
                new TableRowSorter<>(
                        modeloTabla
                );

        tablaCuentas.setRowSorter(
                sorter
        );

        configurarAnchoColumnas();
        configurarRenderers();
        configurarEventosFiltros();

        JScrollPane scroll =
                new JScrollPane(
                        tablaCuentas
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

    private void configurarAnchoColumnas() {

        TableColumnModel columnas =
                tablaCuentas.getColumnModel();

        columnas.getColumn(0)
                .setPreferredWidth(
                        100
                );

        columnas.getColumn(1)
                .setPreferredWidth(
                        240
                );

        columnas.getColumn(2)
                .setPreferredWidth(
                        105
                );

        columnas.getColumn(3)
                .setPreferredWidth(
                        125
                );

        columnas.getColumn(4)
                .setPreferredWidth(
                        105
                );

        columnas.getColumn(5)
                .setPreferredWidth(
                        165
                );

        columnas.getColumn(6)
                .setPreferredWidth(
                        95
                );

        columnas.getColumn(7)
                .setPreferredWidth(
                        90
                );
    }

    private void configurarRenderers() {

        tablaCuentas.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.LEFT
                        )
                );

        tablaCuentas.getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.LEFT
                        )
                );

        tablaCuentas.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new RenderTipo()
                );

        tablaCuentas.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.CENTER
                        )
                );

        tablaCuentas.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new RenderNaturaleza()
                );

        tablaCuentas.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.CENTER
                        )
                );

        tablaCuentas.getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        new RenderMovimiento()
                );

        tablaCuentas.getColumnModel()
                .getColumn(7)
                .setCellRenderer(
                        new RenderEstado()
                );
    }

    private void configurarEventosFiltros() {

        txtBuscar.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {
                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                aplicarFiltros();
                            }

                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                aplicarFiltros();
                            }

                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                aplicarFiltros();
                            }
                        }
                );

        cmbTipo.addActionListener(
                e -> aplicarFiltros()
        );

        cmbMovimiento.addActionListener(
                e -> aplicarFiltros()
        );

        cmbEstado.addActionListener(
                e -> aplicarFiltros()
        );
    }

    private void aplicarFiltros() {

        if (
                sorter == null
        ) {
            return;
        }

        List<RowFilter<Object, Object>> filtros =
                new java.util.ArrayList<>();

        String texto =
                txtBuscar.getText()
                        .trim();

        if (
                !texto.isEmpty()
        ) {

            filtros.add(
                    RowFilter.regexFilter(
                            "(?i)"
                            + Pattern.quote(
                                    texto
                            )
                    )
            );
        }

        String tipo =
                String.valueOf(
                        cmbTipo.getSelectedItem()
                );

        if (
                !"Todos los tipos".equals(
                        tipo
                )
        ) {

            filtros.add(
                    RowFilter.regexFilter(
                            "^"
                            + Pattern.quote(
                                    tipo
                            )
                            + "$",
                            2
                    )
            );
        }

        String movimiento =
                String.valueOf(
                        cmbMovimiento.getSelectedItem()
                );

        if (
                movimiento.endsWith(
                        "Sí"
                )
        ) {

            filtros.add(
                    RowFilter.regexFilter(
                            "^Sí$",
                            6
                    )
            );

        } else if (
                movimiento.endsWith(
                        "No"
                )
        ) {

            filtros.add(
                    RowFilter.regexFilter(
                            "^No$",
                            6
                    )
            );
        }

        String estado =
                String.valueOf(
                        cmbEstado.getSelectedItem()
                );

        if (
                estado.endsWith(
                        "Activa"
                )
        ) {

            filtros.add(
                    RowFilter.regexFilter(
                            "^Activa$",
                            7
                    )
            );

        } else if (
                estado.endsWith(
                        "Inactiva"
                )
        ) {

            filtros.add(
                    RowFilter.regexFilter(
                            "^Inactiva$",
                            7
                    )
            );
        }

        if (
                filtros.isEmpty()
        ) {

            sorter.setRowFilter(
                    null
            );

        } else {

            sorter.setRowFilter(
                    RowFilter.andFilter(
                            filtros
                    )
            );
        }

        actualizarCantidadVisible();
    }

    private void limpiarFiltros() {

        txtBuscar.setText(
                ""
        );

        cmbTipo.setSelectedIndex(
                0
        );

        cmbMovimiento.setSelectedIndex(
                0
        );

        cmbEstado.setSelectedIndex(
                0
        );

        aplicarFiltros();
    }

    private void actualizarCantidadVisible() {

        int visibles =
                tablaCuentas.getRowCount();

        lblCantidad.setText(
                visibles
                + (
                        visibles == 1
                                ? " cuenta visible"
                                : " cuentas visibles"
                )
        );
    }

    private void cargarCuentas() {

        try {

            List<Cuenta> cuentas =
                    cuentaDAO.listarTodas(
                            true
                    );

            modeloTabla.setRowCount(
                    0
            );

            int movimiento =
                    0;

            int activas =
                    0;

            java.util.Set<String> tipos =
                    new java.util.HashSet<>();

            for (
                    Cuenta cuenta
                    : cuentas
            ) {

                String tipo =
                        formatearTexto(
                                cuenta.getTipo()
                        );

                modeloTabla.addRow(
                        new Object[]{
                            cuenta.getCodigo(),
                            cuenta.getNombre(),
                            tipo,
                            formatearTexto(
                                    cuenta.getClasificacion()
                            ),
                            formatearTexto(
                                    cuenta.getNaturaleza()
                            ),
                            formatearRol(
                                    cuenta.getRolReporte()
                            ),
                            cuenta.isPermiteMovimiento()
                                    ? "Sí"
                                    : "No",
                            cuenta.isActivo()
                                    ? "Activa"
                                    : "Inactiva"
                        }
                );

                if (
                        cuenta.isPermiteMovimiento()
                ) {
                    movimiento++;
                }

                if (
                        cuenta.isActivo()
                ) {
                    activas++;
                }

                if (
                        cuenta.getTipo() != null
                        &&
                        !cuenta.getTipo()
                                .isBlank()
                ) {

                    tipos.add(
                            cuenta.getTipo()
                                    .toUpperCase()
                    );
                }
            }

            lblTotal.setText(
                    String.valueOf(
                            cuentas.size()
                    )
            );

            lblMovimiento.setText(
                    String.valueOf(
                            movimiento
                    )
            );

            lblActivas.setText(
                    String.valueOf(
                            activas
                    )
            );

            lblTipos.setText(
                    String.valueOf(
                            tipos.size()
                    )
            );

            actualizarCantidadVisible();

        } catch (
                Exception e
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(
                            e
                    ),
                    "Error al cargar catálogo",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void exportarCatalogoExcel() {

        try {

            List<Cuenta> cuentas =
                    cuentaDAO.listarTodas(
                            true
                    );

            JFileChooser chooser =
                    new JFileChooser();

            chooser.setDialogTitle(
                    "Guardar catálogo de cuentas"
            );

            chooser.setFileFilter(
                    new FileNameExtensionFilter(
                            "Archivo Excel (*.xlsx)",
                            "xlsx"
                    )
            );

            chooser.setSelectedFile(
                    new File(
                            "ContaProMax_Catalogo_de_Cuentas.xlsx"
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

            if (
                    archivo.exists()
                    &&
                    JOptionPane.showConfirmDialog(
                            this,
                            "El archivo ya existe. ¿Deseas reemplazarlo?",
                            "Confirmar",
                            JOptionPane.YES_NO_OPTION
                    )
                    != JOptionPane.YES_OPTION
            ) {
                return;
            }

            try (
                    Workbook wb =
                            new XSSFWorkbook()
            ) {

                Sheet hoja =
                        wb.createSheet(
                                "Catálogo de Cuentas"
                        );

                hoja.setDisplayGridlines(
                        false
                );

                insertarLogoExcel(
                        wb,
                        hoja
                );

                CellStyle tituloStyle =
                        wb.createCellStyle();

                org.apache.poi.ss.usermodel.Font tituloFont =
                        wb.createFont();

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

                Row filaTitulo =
                        hoja.createRow(
                                0
                        );

                filaTitulo.setHeightInPoints(
                        28
                );

                Cell titulo =
                        filaTitulo.createCell(
                                2
                        );

                titulo.setCellValue(
                        "ContaProMax"
                );

                titulo.setCellStyle(
                        tituloStyle
                );

                hoja.addMergedRegion(
                        new CellRangeAddress(
                                0,
                                0,
                                2,
                                7
                        )
                );

                CellStyle subStyle =
                        wb.createCellStyle();

                org.apache.poi.ss.usermodel.Font subFont =
                        wb.createFont();

                subFont.setBold(
                        true
                );

                subFont.setFontHeightInPoints(
                        (short) 13
                );

                subStyle.setFont(
                        subFont
                );

                Row filaSub =
                        hoja.createRow(
                                1
                        );

                Cell sub =
                        filaSub.createCell(
                                2
                        );

                sub.setCellValue(
                        "Catálogo de Cuentas"
                );

                sub.setCellStyle(
                        subStyle
                );

                hoja.addMergedRegion(
                        new CellRangeAddress(
                                1,
                                1,
                                2,
                                7
                        )
                );

                Row filaInfo =
                        hoja.createRow(
                                2
                        );

                Cell info =
                        filaInfo.createCell(
                                2
                        );

                info.setCellValue(
                        "Exportado: "
                        + LocalDateTime.now()
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "dd/MM/yyyy HH:mm"
                                        )
                                )
                        + "   |   Total de cuentas: "
                        + cuentas.size()
                );

                hoja.addMergedRegion(
                        new CellRangeAddress(
                                2,
                                2,
                                2,
                                7
                        )
                );

                CellStyle encabezado =
                        wb.createCellStyle();

                encabezado.setFillForegroundColor(
                        IndexedColors.DARK_BLUE.getIndex()
                );

                encabezado.setFillPattern(
                        FillPatternType.SOLID_FOREGROUND
                );

                encabezado.setAlignment(
                        HorizontalAlignment.CENTER
                );

                encabezado.setVerticalAlignment(
                        VerticalAlignment.CENTER
                );

                encabezado.setBorderBottom(
                        BorderStyle.THIN
                );

                encabezado.setBorderTop(
                        BorderStyle.THIN
                );

                encabezado.setBorderLeft(
                        BorderStyle.THIN
                );

                encabezado.setBorderRight(
                        BorderStyle.THIN
                );

                org.apache.poi.ss.usermodel.Font fuente =
                        wb.createFont();

                fuente.setBold(
                        true
                );

                fuente.setColor(
                        IndexedColors.WHITE.getIndex()
                );

                encabezado.setFont(
                        fuente
                );

                String[] columnas = {
                    "Código",
                    "Nombre",
                    "Tipo",
                    "Clasificación",
                    "Naturaleza",
                    "Rol Reporte",
                    "Movimiento",
                    "Estado"
                };

                Row header =
                        hoja.createRow(
                                4
                        );

                header.setHeightInPoints(
                        24
                );

                for (
                        int i = 0;
                        i < columnas.length;
                        i++
                ) {

                    Cell celda =
                            header.createCell(
                                    i
                            );

                    celda.setCellValue(
                            columnas[i]
                    );

                    celda.setCellStyle(
                            encabezado
                    );
                }

                CellStyle cuerpo =
                        wb.createCellStyle();

                cuerpo.setBorderBottom(
                        BorderStyle.THIN
                );

                cuerpo.setBorderTop(
                        BorderStyle.THIN
                );

                cuerpo.setBorderLeft(
                        BorderStyle.THIN
                );

                cuerpo.setBorderRight(
                        BorderStyle.THIN
                );

                cuerpo.setVerticalAlignment(
                        VerticalAlignment.CENTER
                );

                int fila =
                        5;

                for (
                        Cuenta cuenta
                        : cuentas
                ) {

                    Row r =
                            hoja.createRow(
                                    fila++
                            );

                    String[] valores = {
                        cuenta.getCodigo(),
                        cuenta.getNombre(),
                        formatearTexto(
                                cuenta.getTipo()
                        ),
                        formatearTexto(
                                cuenta.getClasificacion()
                        ),
                        formatearTexto(
                                cuenta.getNaturaleza()
                        ),
                        formatearRol(
                                cuenta.getRolReporte()
                        ),
                        cuenta.isPermiteMovimiento()
                                ? "Sí"
                                : "No",
                        cuenta.isActivo()
                                ? "Activa"
                                : "Inactiva"
                    };

                    for (
                            int i = 0;
                            i < valores.length;
                            i++
                    ) {

                        Cell celda =
                                r.createCell(
                                        i
                                );

                        celda.setCellValue(
                                valores[i]
                        );

                        celda.setCellStyle(
                                cuerpo
                        );
                    }
                }

                hoja.createFreezePane(
                        0,
                        5
                );

                hoja.setAutoFilter(
                        new CellRangeAddress(
                                4,
                                Math.max(
                                        4,
                                        fila - 1
                                ),
                                0,
                                7
                        )
                );

                for (
                        int i = 0;
                        i < columnas.length;
                        i++
                ) {

                    hoja.autoSizeColumn(
                            i
                    );

                    hoja.setColumnWidth(
                            i,
                            Math.min(
                                    Math.max(
                                            hoja.getColumnWidth(i)
                                            + 800,
                                            3200
                                    ),
                                    15000
                            )
                    );
                }

                try (
                        FileOutputStream out =
                                new FileOutputStream(
                                        archivo
                                )
                ) {

                    wb.write(
                            out
                    );
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Catálogo exportado correctamente en:\n"
                    + archivo.getAbsolutePath(),
                    "Exportación completada",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (
                Exception e
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo exportar el catálogo:\n"
                    + e.getMessage(),
                    "Error de exportación",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void insertarLogoExcel(
            Workbook workbook,
            Sheet hoja
    ) {

        try (
                InputStream input =
                        getClass()
                                .getResourceAsStream(
                                        "/branding/ContraProMaxLogo.png"
                                )
        ) {

            if (
                    input == null
            ) {
                return;
            }

            int imagen =
                    workbook.addPicture(
                            input.readAllBytes(),
                            Workbook.PICTURE_TYPE_PNG
                    );

            CreationHelper helper =
                    workbook.getCreationHelper();

            Drawing<?> drawing =
                    hoja.createDrawingPatriarch();

            ClientAnchor anchor =
                    helper.createClientAnchor();

            anchor.setCol1(
                    0
            );

            anchor.setRow1(
                    0
            );

            anchor.setCol2(
                    2
            );

            anchor.setRow2(
                    3
            );

            Picture picture =
                    drawing.createPicture(
                            anchor,
                            imagen
                    );

        } catch (
                Exception ignored
        ) {
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
                fondo
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
                                13,
                                9,
                                13
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

    private String formatearTexto(
            String texto
    ) {

        if (
                texto == null
                ||
                texto.isBlank()
        ) {

            return "-";
        }

        return texto.replace(
                "_",
                " "
        );
    }

    private String formatearRol(
            String rol
    ) {

        if (
                rol == null
                ||
                rol.isBlank()
                ||
                rol.equalsIgnoreCase(
                        "NINGUNO"
                )
        ) {

            return "-";
        }

        return rol.replace(
                "_",
                " "
        );
    }

    private String obtenerMensajeError(
            Throwable error
    ) {

        Throwable actual =
                error;

        String mensaje =
                "Ocurrió un error desconocido.";

        while (
                actual != null
        ) {

            if (
                    actual.getMessage() != null
                    &&
                    !actual.getMessage()
                            .isBlank()
            ) {

                mensaje =
                        actual.getMessage();
            }

            actual =
                    actual.getCause();
        }

        return mensaje;
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

    private class RenderTipo
            extends DefaultTableCellRenderer {

        public RenderTipo() {

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

            String tipo =
                    value == null
                            ? ""
                            : value.toString()
                                    .toUpperCase();

            setText(
                    tipo
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

            switch (
                    tipo
            ) {

                case "ACTIVO" -> {
                    setBackground(
                            new Color(
                                    239,
                                    246,
                                    255
                            )
                    );
                    setForeground(
                            AZUL
                    );
                }

                case "PASIVO" -> {
                    setBackground(
                            new Color(
                                    254,
                                    242,
                                    242
                            )
                    );
                    setForeground(
                            new Color(
                                    220,
                                    38,
                                    38
                            )
                    );
                }

                case "PATRIMONIO" -> {
                    setBackground(
                            new Color(
                                    243,
                                    232,
                                    255
                            )
                    );
                    setForeground(
                            MORADO
                    );
                }

                case "INGRESO" -> {
                    setBackground(
                            new Color(
                                    240,
                                    253,
                                    244
                            )
                    );
                    setForeground(
                            VERDE
                    );
                }

                case "COSTO", "GASTO" -> {
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
                }

                default -> {
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
            }

            return this;
        }
    }

    private class RenderNaturaleza
            extends DefaultTableCellRenderer {

        public RenderNaturaleza() {

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

            String texto =
                    value == null
                            ? ""
                            : value.toString();

            setText(
                    texto
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

            } else if (
                    "DEUDORA".equalsIgnoreCase(
                            texto
                    )
            ) {

                setBackground(
                        new Color(
                                239,
                                246,
                                255
                        )
                );

                setForeground(
                        AZUL
                );

            } else {

                setBackground(
                        new Color(
                                248,
                                250,
                                252
                        )
                );

                setForeground(
                        MORADO
                );
            }

            return this;
        }
    }

    private class RenderMovimiento
            extends DefaultTableCellRenderer {

        public RenderMovimiento() {

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

            String texto =
                    value == null
                            ? ""
                            : value.toString();

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

                setText(
                        texto
                );

                setBackground(
                        SELECCION
                );

                setForeground(
                        TEXTO
                );

            } else if (
                    "Sí".equalsIgnoreCase(
                            texto
                    )
            ) {

                setText(
                        "✓  Sí"
                );

                setBackground(
                        new Color(
                                240,
                                253,
                                244
                        )
                );

                setForeground(
                        VERDE
                );

            } else {

                setText(
                        "—  No"
                );

                setBackground(
                        new Color(
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

    private class RenderEstado
            extends DefaultTableCellRenderer {

        public RenderEstado() {

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

            String estado =
                    value == null
                            ? ""
                            : value.toString();

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

                setText(
                        estado
                );

                setBackground(
                        SELECCION
                );

                setForeground(
                        TEXTO
                );

            } else if (
                    "Activa".equalsIgnoreCase(
                            estado
                    )
            ) {

                setText(
                        "●  Activa"
                );

                setBackground(
                        new Color(
                                240,
                                253,
                                244
                        )
                );

                setForeground(
                        VERDE
                );

            } else {

                setText(
                        "●  Inactiva"
                );

                setBackground(
                        new Color(
                                254,
                                242,
                                242
                        )
                );

                setForeground(
                        new Color(
                                220,
                                38,
                                38
                        )
                );
            }

            return this;
        }
    }
}
