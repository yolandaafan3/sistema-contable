package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.CapaPeps;
import com.mycompany.sistemacontable.modelo.MovimientoKardex;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.servicio.KardexService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelKardexPeps extends JPanel {

    private final KardexService service =
            new KardexService();

    private final JTextField txtFiltro =
            new JTextField();

    private final JLabel lblResumen =
            new JLabel();

    private final JLabel lblResumenLotes =
            new JLabel();

    private final JLabel lblProductos =
            new JLabel("0");

    private final JLabel lblMovimientos =
            new JLabel("0");

    private final JLabel lblLotes =
            new JLabel("0");

    private final JLabel lblValorPeps =
            new JLabel("$0.00");

    private DefaultTableModel modelo;
    private DefaultTableModel modeloLotes;

    private JTable tablaMovimientos;
    private JTable tablaLotes;
    private JTabbedPane tabs;

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    private final Color CELESTE =
            new Color(14, 165, 233);

    private final Color CELESTE_SUAVE =
            new Color(239, 246, 255);

    private final Color CELESTE_SELECCION =
            new Color(219, 234, 254);

    private final Color VERDE =
            new Color(22, 163, 74);

    private final Color VERDE_SUAVE =
            new Color(240, 253, 244);

    private final Color ROJO =
            new Color(220, 38, 38);

    private final Color ROJO_SUAVE =
            new Color(254, 242, 242);

    private final Color MORADO =
            new Color(126, 34, 206);

    public PanelKardexPeps() {

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
                crearTabs(),
                BorderLayout.CENTER
        );

        cargarKardex();
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
                        "Kardex PEPS"
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
                        "Movimientos y lotes PEPS por producto. Cada compra crea o alimenta una capa con su costo propio."
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

        JPanel controles =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        controles.setOpaque(
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
                PRIMARIO
        );

        txtFiltro.setPreferredSize(
                new Dimension(
                        245,
                        38
                )
        );

        txtFiltro.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        txtFiltro.setToolTipText(
                "Buscar por código o nombre del producto"
        );

        txtFiltro.setBorder(
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

        txtFiltro.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased(
                            KeyEvent e
                    ) {

                        cargarKardex();
                    }
                }
        );

        JButton verDetalle =
                crearBotonSecundario(
                        "◉  Ver detalle"
                );

        verDetalle.addActionListener(
                e -> verDetalleSeleccionado()
        );

        JButton actualizar =
                crearBotonPrincipal(
                        "↻  Actualizar"
                );

        actualizar.addActionListener(
                e -> cargarKardex()
        );

        controles.add(
                iconoBuscar
        );

        controles.add(
                txtFiltro
        );

        controles.add(
                verDetalle
        );

        controles.add(
                actualizar
        );

        panel.add(
                textos,
                BorderLayout.WEST
        );

        panel.add(
                controles,
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
                        100
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Productos visibles",
                        lblProductos,
                        "▣",
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
                        "Movimientos",
                        lblMovimientos,
                        "↕",
                        new Color(
                                224,
                                242,
                                254
                        ),
                        CELESTE
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Lotes / Capas",
                        lblLotes,
                        "▤",
                        new Color(
                                243,
                                232,
                                255
                        ),
                        MORADO
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Valor PEPS",
                        lblValorPeps,
                        "$",
                        VERDE_SUAVE,
                        VERDE
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

    private JTabbedPane crearTabs() {

        tabs =
                new JTabbedPane();

        tabs.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        tabs.setBackground(
                Color.WHITE
        );

        tabs.setForeground(
                TEXTO
        );

        tabs.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );

        tabs.addTab(
                "  Movimientos Kardex  ",
                crearPanelMovimientos()
        );

        tabs.addTab(
                "  Lotes / Capas PEPS  ",
                crearPanelLotes()
        );

        return tabs;
    }

    private JPanel crearPanelMovimientos() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                12
                        )
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        12,
                        12,
                        12
                )
        );

        modelo =
                new DefaultTableModel(
                        new String[]{
                            "Producto",
                            "Fecha",
                            "Asiento",
                            "Concepto",
                            "Entrada",
                            "Salida",
                            "Existencia",
                            "Costo Unit.",
                            "Costo PEPS",
                            "Saldo Deudor",
                            "Saldo Acreedor",
                            "Saldo"
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

        tablaMovimientos =
                tablaBase(
                        modelo
                );

        configurarTablaMovimientos(
                tablaMovimientos
        );

        JScrollPane scroll =
                new JScrollPane(
                        tablaMovimientos
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

        panel.add(
                crearCabeceraInterna(
                        "Movimientos del Kardex",
                        "Entradas, salidas, existencias y valores acumulados por producto."
                ),
                BorderLayout.NORTH
        );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        panel.add(
                crearPie(
                        lblResumen
                ),
                BorderLayout.SOUTH
        );

        return panel;
    }

    private JPanel crearPanelLotes() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                12
                        )
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        12,
                        12,
                        12
                )
        );

        modeloLotes =
                new DefaultTableModel(
                        new String[]{
                            "Producto",
                            "Lote",
                            "Fecha",
                            "Kardex entrada",
                            "Costo unitario",
                            "Cantidad original",
                            "Cantidad disponible",
                            "Consumido",
                            "Valor disponible",
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

        tablaLotes =
                tablaBase(
                        modeloLotes
                );

        configurarTablaLotes(
                tablaLotes
        );

        JScrollPane scroll =
                new JScrollPane(
                        tablaLotes
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

        panel.add(
                crearCabeceraInterna(
                        "Lotes y capas PEPS",
                        "Disponibilidad, consumo y valor pendiente de cada lote de inventario."
                ),
                BorderLayout.NORTH
        );

        panel.add(
                scroll,
                BorderLayout.CENTER
        );

        panel.add(
                crearPie(
                        lblResumenLotes
                ),
                BorderLayout.SOUTH
        );

        return panel;
    }

    private JPanel crearCabeceraInterna(
            String titulo,
            String descripcion
    ) {

        JPanel panel =
                new JPanel();

        panel.setOpaque(
                false
        );

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel t =
                new JLabel(
                        titulo
                );

        t.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        t.setForeground(
                TEXTO
        );

        JLabel d =
                new JLabel(
                        descripcion
                );

        d.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        d.setForeground(
                SECUNDARIO
        );

        panel.add(
                t
        );

        panel.add(
                Box.createVerticalStrut(
                        3
                )
        );

        panel.add(
                d
        );

        return panel;
    }

    private JTable tablaBase(
            DefaultTableModel model
    ) {

        JTable tabla =
                new JTable(
                        model
                );

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

        tabla.setAutoCreateRowSorter(
                true
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

        return tabla;
    }

    private void configurarTablaMovimientos(
            JTable tabla
    ) {

        RenderTabla texto =
                new RenderTabla(
                        SwingConstants.LEFT,
                        -1
                );

        RenderTabla numero =
                new RenderTabla(
                        SwingConstants.RIGHT,
                        -1
                );

        tabla.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        texto
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        texto
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        texto
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        texto
                );

        for (
                int i = 4;
                i <= 11;
                i++
        ) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(
                            numero
                    );
        }

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        155
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        95
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(
                        75
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(
                        220
                );

        for (
                int i = 4;
                i <= 11;
                i++
        ) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(
                            105
                    );
        }
    }

    private void configurarTablaLotes(
            JTable tabla
    ) {

        RenderTabla texto =
                new RenderTabla(
                        SwingConstants.LEFT,
                        -1
                );

        RenderTabla numero =
                new RenderTabla(
                        SwingConstants.RIGHT,
                        -1
                );

        tabla.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        texto
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        texto
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        texto
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        texto
                );

        for (
                int i = 4;
                i <= 8;
                i++
        ) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(
                            numero
                    );
        }

        tabla.getColumnModel()
                .getColumn(9)
                .setCellRenderer(
                        new RenderEstadoLote()
                );

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        175
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        85
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(
                        95
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(
                        105
                );

        for (
                int i = 4;
                i <= 8;
                i++
        ) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(
                            120
                    );
        }

        tabla.getColumnModel()
                .getColumn(9)
                .setPreferredWidth(
                        110
                );
    }

    private JPanel crearPie(
            JLabel label
    ) {

        JPanel pie =
                new JPanel(
                        new BorderLayout()
                );

        pie.setBackground(
                CELESTE_SUAVE
        );

        pie.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        191,
                                        219,
                                        254
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                11,
                                14,
                                11,
                                14
                        )
                )
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        label.setForeground(
                TEXTO
        );

        pie.add(
                label,
                BorderLayout.WEST
        );

        return pie;
    }

    public final void cargarKardex() {

        if (
                modelo == null
                ||
                modeloLotes == null
        ) {

            return;
        }

        try {

            modelo.setRowCount(
                    0
            );

            modeloLotes.setRowCount(
                    0
            );

            List<Producto> productos =
                    service.listarProductosActivos();

            String filtro =
                    txtFiltro.getText()
                    == null
                            ? ""
                            : txtFiltro.getText()
                                    .trim()
                                    .toLowerCase();

            List<FilaKardex> filas =
                    new ArrayList<>();

            int productosVisibles =
                    0;

            int lotes =
                    0;

            BigDecimal saldoTotal =
                    BigDecimal.ZERO;

            BigDecimal valorLotes =
                    BigDecimal.ZERO;

            for (
                    Producto producto
                    : productos
            ) {

                String codigo =
                        producto.getCodigo()
                        == null
                                ? ""
                                : producto.getCodigo()
                                        .toLowerCase();

                String nombre =
                        producto.getNombre()
                        == null
                                ? ""
                                : producto.getNombre()
                                        .toLowerCase();

                if (
                        !filtro.isBlank()
                        &&
                        !codigo.contains(
                                filtro
                        )
                        &&
                        !nombre.contains(
                                filtro
                        )
                ) {

                    continue;
                }

                productosVisibles++;

                service.recalcular(
                        producto.getIdProducto()
                );

                for (
                        MovimientoKardex movimiento
                        : service.obtenerMovimientos(
                                producto.getIdProducto()
                        )
                ) {

                    filas.add(
                            new FilaKardex(
                                    producto,
                                    movimiento
                            )
                    );
                }

                BigDecimal saldo =
                        service.obtenerSaldoPeps(
                                producto.getIdProducto()
                        );

                if (
                        saldo != null
                ) {

                    saldoTotal =
                            saldoTotal.add(
                                    saldo
                            );
                }

                for (
                        CapaPeps capa
                        : service.obtenerCapas(
                                producto.getIdProducto()
                        )
                ) {

                    lotes++;

                    BigDecimal original =
                            nz(
                                    capa.getCantidadOriginal()
                            );

                    BigDecimal disponible =
                            nz(
                                    capa.getCantidadDisponible()
                            );

                    BigDecimal consumido =
                            original.subtract(
                                    disponible
                            );

                    BigDecimal valor =
                            disponible.multiply(
                                    nz(
                                            capa.getCostoUnitario()
                                    )
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

                    valorLotes =
                            valorLotes.add(
                                    valor
                            );

                    modeloLotes.addRow(
                            new Object[]{
                                producto.getCodigo()
                                + " - "
                                + producto.getNombre(),

                                "Lote #"
                                + capa.getIdCapa(),

                                capa.getFecha()
                                == null
                                        ? ""
                                        : capa.getFecha()
                                                .format(
                                                        formatoFecha
                                                ),

                                capa.getIdKardexEntrada(),

                                dinero(
                                        capa.getCostoUnitario()
                                ),

                                numero(
                                        original
                                ),

                                numero(
                                        disponible
                                ),

                                numero(
                                        consumido
                                ),

                                dinero(
                                        valor
                                ),

                                disponible.compareTo(
                                        BigDecimal.ZERO
                                )
                                == 0
                                        ? "AGOTADO"
                                        : (
                                                disponible.compareTo(
                                                        original
                                                )
                                                < 0
                                                        ? "PARCIAL"
                                                        : "DISPONIBLE"
                                        )
                            }
                    );
                }
            }

            filas.sort(
                    Comparator.comparing(
                            (FilaKardex fila)
                                    -> fila.movimiento.getFecha(),
                            Comparator.nullsLast(
                                    Comparator.naturalOrder()
                            )
                    )
                    .thenComparing(
                            fila
                                    -> fila.producto.getCodigo()
                                    == null
                                            ? ""
                                            : fila.producto.getCodigo()
                    )
            );

            for (
                    FilaKardex fila
                    : filas
            ) {

                MovimientoKardex movimiento =
                        fila.movimiento;

                Producto producto =
                        fila.producto;

                modelo.addRow(
                        new Object[]{
                            producto.getCodigo()
                            + " - "
                            + producto.getNombre(),

                            movimiento.getFecha()
                            == null
                                    ? ""
                                    : movimiento.getFecha()
                                            .format(
                                                    formatoFecha
                                            ),

                            movimiento.getNumeroAsiento()
                            == null
                                    ? "—"
                                    : movimiento.getNumeroAsiento(),

                            movimiento.getConcepto(),

                            numero(
                                    movimiento.getUnidadesEntrada()
                            ),

                            numero(
                                    movimiento.getUnidadesSalida()
                            ),

                            numero(
                                    movimiento.getUnidadesExistencia()
                            ),

                            dinero(
                                    movimiento.getCostoUnitario()
                            ),

                            dinero(
                                    movimiento.getCostoPeps()
                            ),

                            dinero(
                                    movimiento.getSaldoDeudor()
                            ),

                            dinero(
                                    movimiento.getSaldoAcreedor()
                            ),

                            dinero(
                                    movimiento.getSaldo()
                            )
                        }
                );
            }

            lblProductos.setText(
                    String.valueOf(
                            productosVisibles
                    )
            );

            lblMovimientos.setText(
                    String.valueOf(
                            filas.size()
                    )
            );

            lblLotes.setText(
                    String.valueOf(
                            lotes
                    )
            );

            lblValorPeps.setText(
                    dinero(
                            saldoTotal
                    )
            );

            lblResumen.setText(
                    "Productos mostrados: "
                    + productosVisibles
                    + "   ·   Movimientos: "
                    + filas.size()
                    + "   ·   Valor total PEPS: "
                    + dinero(
                            saldoTotal
                    )
            );

            lblResumenLotes.setText(
                    "Lotes mostrados: "
                    + lotes
                    + "   ·   Valor disponible en lotes: "
                    + dinero(
                            valorLotes
                    )
                    + "   ·   Las ventas consumen primero los lotes más antiguos."
            );

        } catch (
                Exception e
        ) {

            lblResumen.setText(
                    "No se pudo cargar el Kardex: "
                    + obtenerMensajeError(
                            e
                    )
            );

            lblResumenLotes.setText(
                    "No se pudieron cargar los lotes: "
                    + obtenerMensajeError(
                            e
                    )
            );
        }
    }

    private void verDetalleSeleccionado() {

        if (tabs == null) {
            return;
        }

        boolean esMovimiento =
                tabs.getSelectedIndex() == 0;

        JTable tabla =
                esMovimiento
                        ? tablaMovimientos
                        : tablaLotes;

        if (tabla == null) {
            return;
        }

        int filaVista =
                tabla.getSelectedRow();

        if (filaVista < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona primero una fila para consultar su detalle.",
                    "Kardex PEPS",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return;
        }

        int filaModelo =
                tabla.convertRowIndexToModel(
                        filaVista
                );

        mostrarDialogoDetalle(
                tabla,
                filaModelo,
                esMovimiento
        );
    }

    private void mostrarDialogoDetalle(
            JTable tabla,
            int fila,
            boolean esMovimiento
    ) {

        Window owner =
                SwingUtilities.getWindowAncestor(
                        this
                );

        JDialog dialogo =
                new JDialog(
                        owner,
                        esMovimiento
                                ? "Detalle del movimiento Kardex"
                                : "Detalle de la capa PEPS",
                        JDialog.ModalityType.APPLICATION_MODAL
                );

        dialogo.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialogo.setSize(
                esMovimiento
                        ? 820
                        : 780,
                esMovimiento
                        ? 650
                        : 610
        );

        dialogo.setMinimumSize(
                new Dimension(
                        680,
                        520
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

        String producto =
                valorModelo(
                        tabla,
                        fila,
                        0
                );

        dialogo.add(
                crearEncabezadoDetalle(
                        esMovimiento
                                ? "Detalle del movimiento Kardex"
                                : "Detalle de la capa PEPS",
                        producto,
                        esMovimiento
                                ? "↕"
                                : "▤"
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

        if (esMovimiento) {

            contenido.add(
                    crearTarjetaInformacionMovimiento(
                            tabla,
                            fila
                    )
            );

            contenido.add(
                    Box.createVerticalStrut(
                            14
                    )
            );

            contenido.add(
                    crearTarjetaCantidadesMovimiento(
                            tabla,
                            fila
                    )
            );

            contenido.add(
                    Box.createVerticalStrut(
                            14
                    )
            );

            contenido.add(
                    crearTarjetaValoresMovimiento(
                            tabla,
                            fila
                    )
            );

        } else {

            contenido.add(
                    crearTarjetaInformacionLote(
                            tabla,
                            fila
                    )
            );

            contenido.add(
                    Box.createVerticalStrut(
                            14
                    )
            );

            contenido.add(
                    crearTarjetaCantidadesLote(
                            tabla,
                            fila
                    )
            );

            contenido.add(
                    Box.createVerticalStrut(
                            14
                    )
            );

            contenido.add(
                    crearTarjetaValorLote(
                            tabla,
                            fila
                    )
            );
        }

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
            String titulo,
            String producto,
            String simbolo
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
                                20,
                                24,
                                20,
                                24
                        )
                )
        );

        JLabel icono =
                new JLabel(
                        simbolo,
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
                        22
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

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        22
                )
        );

        lblTitulo.setForeground(
                TEXTO
        );

        JLabel lblProducto =
                new JLabel(
                        producto
                );

        lblProducto.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        lblProducto.setForeground(
                SECUNDARIO
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
                lblProducto
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

    private JPanel crearTarjetaInformacionMovimiento(
            JTable tabla,
            int fila
    ) {

        JPanel contenido =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                12,
                                10
                        )
                );

        contenido.setOpaque(
                false
        );

        contenido.add(
                crearCampoDetalle(
                        "Fecha",
                        valorModelo(
                                tabla,
                                fila,
                                1
                        )
                )
        );

        contenido.add(
                crearCampoDetalle(
                        "Asiento",
                        valorModelo(
                                tabla,
                                fila,
                                2
                        )
                )
        );

        JPanel concepto =
                crearCampoDetalle(
                        "Concepto",
                        valorModelo(
                                tabla,
                                fila,
                                3
                        )
                );

        JPanel tarjeta =
                crearTarjetaDetalle(
                        "Información del movimiento",
                        "Datos que identifican el movimiento registrado en el Kardex.",
                        contenido
                );

        tarjeta.add(
                concepto,
                BorderLayout.SOUTH
        );

        return tarjeta;
    }

    private JPanel crearTarjetaCantidadesMovimiento(
            JTable tabla,
            int fila
    ) {

        JPanel metricas =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                12,
                                0
                        )
                );

        metricas.setOpaque(
                false
        );

        metricas.add(
                crearMetricaDetalle(
                        "Entrada",
                        valorModelo(
                                tabla,
                                fila,
                                4
                        ),
                        VERDE,
                        VERDE_SUAVE,
                        "+"
                )
        );

        metricas.add(
                crearMetricaDetalle(
                        "Salida",
                        valorModelo(
                                tabla,
                                fila,
                                5
                        ),
                        ROJO,
                        ROJO_SUAVE,
                        "−"
                )
        );

        metricas.add(
                crearMetricaDetalle(
                        "Existencia",
                        valorModelo(
                                tabla,
                                fila,
                                6
                        ),
                        PRIMARIO,
                        CELESTE_SUAVE,
                        "▣"
                )
        );

        return crearTarjetaDetalle(
                "Movimiento de unidades",
                "Entradas, salidas y existencia resultante del producto.",
                metricas
        );
    }

    private JPanel crearTarjetaValoresMovimiento(
            JTable tabla,
            int fila
    ) {

        JPanel principal =
                new JPanel();

        principal.setOpaque(
                false
        );

        principal.setLayout(
                new BoxLayout(
                        principal,
                        BoxLayout.Y_AXIS
                )
        );

        JPanel costos =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                12,
                                0
                        )
                );

        costos.setOpaque(
                false
        );

        costos.add(
                crearCampoDetalle(
                        "Costo unitario",
                        valorModelo(
                                tabla,
                                fila,
                                7
                        )
                )
        );

        costos.add(
                crearCampoDetalle(
                        "Costo PEPS",
                        valorModelo(
                                tabla,
                                fila,
                                8
                        )
                )
        );

        JPanel saldos =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                12,
                                0
                        )
                );

        saldos.setOpaque(
                false
        );

        saldos.add(
                crearMetricaDetalle(
                        "Saldo deudor",
                        valorModelo(
                                tabla,
                                fila,
                                9
                        ),
                        PRIMARIO,
                        CELESTE_SUAVE,
                        "D"
                )
        );

        saldos.add(
                crearMetricaDetalle(
                        "Saldo acreedor",
                        valorModelo(
                                tabla,
                                fila,
                                10
                        ),
                        MORADO,
                        new Color(
                                243,
                                232,
                                255
                        ),
                        "H"
                )
        );

        saldos.add(
                crearMetricaDetalle(
                        "Saldo",
                        valorModelo(
                                tabla,
                                fila,
                                11
                        ),
                        TEXTO,
                        new Color(
                                241,
                                245,
                                249
                        ),
                        "$"
                )
        );

        principal.add(
                costos
        );

        principal.add(
                Box.createVerticalStrut(
                        12
                )
        );

        principal.add(
                saldos
        );

        return crearTarjetaDetalle(
                "Costos y saldos",
                "Valoración PEPS y saldos contables asociados al movimiento.",
                principal
        );
    }

    private JPanel crearTarjetaInformacionLote(
            JTable tabla,
            int fila
    ) {

        JPanel contenido =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                12,
                                0
                        )
                );

        contenido.setOpaque(
                false
        );

        contenido.add(
                crearCampoDetalle(
                        "Lote",
                        valorModelo(
                                tabla,
                                fila,
                                1
                        )
                )
        );

        contenido.add(
                crearCampoDetalle(
                        "Fecha",
                        valorModelo(
                                tabla,
                                fila,
                                2
                        )
                )
        );

        contenido.add(
                crearCampoDetalle(
                        "Kardex entrada",
                        valorModelo(
                                tabla,
                                fila,
                                3
                        )
                )
        );

        return crearTarjetaDetalle(
                "Información de la capa PEPS",
                "Datos de origen del lote seleccionado.",
                contenido
        );
    }

    private JPanel crearTarjetaCantidadesLote(
            JTable tabla,
            int fila
    ) {

        JPanel metricas =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                12,
                                0
                        )
                );

        metricas.setOpaque(
                false
        );

        metricas.add(
                crearMetricaDetalle(
                        "Cantidad original",
                        valorModelo(
                                tabla,
                                fila,
                                5
                        ),
                        PRIMARIO,
                        CELESTE_SUAVE,
                        "▣"
                )
        );

        metricas.add(
                crearMetricaDetalle(
                        "Disponible",
                        valorModelo(
                                tabla,
                                fila,
                                6
                        ),
                        VERDE,
                        VERDE_SUAVE,
                        "✓"
                )
        );

        metricas.add(
                crearMetricaDetalle(
                        "Consumido",
                        valorModelo(
                                tabla,
                                fila,
                                7
                        ),
                        ROJO,
                        ROJO_SUAVE,
                        "−"
                )
        );

        return crearTarjetaDetalle(
                "Control de unidades",
                "Cantidad original, disponibilidad actual y unidades consumidas.",
                metricas
        );
    }

    private JPanel crearTarjetaValorLote(
            JTable tabla,
            int fila
    ) {

        JPanel contenido =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        contenido.setOpaque(
                false
        );

        JPanel valores =
                new JPanel(
                        new GridLayout(
                                1,
                                2,
                                12,
                                0
                        )
                );

        valores.setOpaque(
                false
        );

        valores.add(
                crearCampoDetalle(
                        "Costo unitario",
                        valorModelo(
                                tabla,
                                fila,
                                4
                        )
                )
        );

        valores.add(
                crearCampoDetalle(
                        "Valor disponible",
                        valorModelo(
                                tabla,
                                fila,
                                8
                        )
                )
        );

        JLabel estado =
                crearEstadoDetalle(
                        valorModelo(
                                tabla,
                                fila,
                                9
                        )
                );

        contenido.add(
                valores,
                BorderLayout.CENTER
        );

        contenido.add(
                estado,
                BorderLayout.EAST
        );

        return crearTarjetaDetalle(
                "Valoración del lote",
                "Costo registrado y valor pendiente de consumir en esta capa.",
                contenido
        );
    }

    private JPanel crearTarjetaDetalle(
            String titulo,
            String descripcion,
            Component contenido
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

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        lblTitulo.setForeground(
                TEXTO
        );

        JLabel lblDescripcion =
                new JLabel(
                        descripcion
                );

        lblDescripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        lblDescripcion.setForeground(
                SECUNDARIO
        );

        cabecera.add(
                lblTitulo
        );

        cabecera.add(
                Box.createVerticalStrut(
                        3
                )
        );

        cabecera.add(
                lblDescripcion
        );

        tarjeta.add(
                cabecera,
                BorderLayout.NORTH
        );

        tarjeta.add(
                contenido,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearCampoDetalle(
            String titulo,
            String valor
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
                        valor == null
                                || valor.isBlank()
                                        ? "—"
                                        : valor
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

        campo.add(
                lblTitulo
        );

        campo.add(
                Box.createVerticalStrut(
                        4
                )
        );

        campo.add(
                lblValor
        );

        return campo;
    }

    private JPanel crearMetricaDetalle(
            String titulo,
            String valor,
            Color color,
            Color fondo,
            String simbolo
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                10,
                                0
                        )
                );

        tarjeta.setBackground(
                fondo
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                aclararColor(
                                        color
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                11,
                                12,
                                11,
                                12
                        )
                )
        );

        JLabel icono =
                new JLabel(
                        simbolo,
                        SwingConstants.CENTER
                );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        17
                )
        );

        icono.setForeground(
                color
        );

        icono.setPreferredSize(
                new Dimension(
                        28,
                        28
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
                        11
                )
        );

        lblTitulo.setForeground(
                SECUNDARIO
        );

        JLabel lblValor =
                new JLabel(
                        valor == null
                                || valor.isBlank()
                                        ? "0"
                                        : valor
                );

        lblValor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        lblValor.setForeground(
                color
        );

        textos.add(
                lblTitulo
        );

        textos.add(
                Box.createVerticalStrut(
                        3
                )
        );

        textos.add(
                lblValor
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

    private JLabel crearEstadoDetalle(
            String estado
    ) {

        JLabel label =
                new JLabel(
                        estado,
                        SwingConstants.CENTER
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        label.setPreferredSize(
                new Dimension(
                        125,
                        58
                )
        );

        label.setOpaque(
                true
        );

        label.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        12,
                        8,
                        12
                )
        );

        if (
                "DISPONIBLE".equals(
                        estado
                )
        ) {

            label.setBackground(
                    VERDE_SUAVE
            );

            label.setForeground(
                    VERDE
            );

        } else if (
                "PARCIAL".equals(
                        estado
                )
        ) {

            label.setBackground(
                    new Color(
                            255,
                            247,
                            237
                    )
            );

            label.setForeground(
                    new Color(
                            194,
                            65,
                            12
                    )
            );

        } else if (
                "AGOTADO".equals(
                        estado
                )
        ) {

            label.setBackground(
                    ROJO_SUAVE
            );

            label.setForeground(
                    ROJO
            );

        } else {

            label.setBackground(
                    new Color(
                            241,
                            245,
                            249
                    )
            );

            label.setForeground(
                    TEXTO
            );
        }

        return label;
    }

    private JPanel crearPieDetalle(
            JDialog dialogo
    ) {

        JPanel pie =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                14
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
                        "Cerrar"
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

    private String valorModelo(
            JTable tabla,
            int fila,
            int columna
    ) {

        Object valor =
                tabla.getModel()
                        .getValueAt(
                                fila,
                                columna
                        );

        return valor == null
                ? ""
                : valor.toString();
    }

    private Color aclararColor(
            Color color
    ) {

        int rojo =
                Math.min(
                        255,
                        color.getRed()
                        + 135
                );

        int verde =
                Math.min(
                        255,
                        color.getGreen()
                        + 135
                );

        int azul =
                Math.min(
                        255,
                        color.getBlue()
                        + 135
                );

        return new Color(
                rojo,
                verde,
                azul
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
                                14,
                                9,
                                14
                        )
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

        return boton;
    }

    private BigDecimal nz(
            BigDecimal valor
    ) {

        return valor
                == null
                        ? BigDecimal.ZERO
                        : valor;
    }

    private String numero(
            BigDecimal valor
    ) {

        return nz(
                valor
        )
        .setScale(
                6,
                RoundingMode.HALF_UP
        )
        .stripTrailingZeros()
        .toPlainString();
    }

    private String dinero(
            BigDecimal valor
    ) {

        return "$"
                + nz(
                        valor
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                )
                .toPlainString();
    }

    private String obtenerMensajeError(
            Throwable error
    ) {

        Throwable actual =
                error;

        String mensaje =
                "Error desconocido.";

        while (
                actual != null
        ) {

            if (
                    actual.getMessage() != null
                    &&
                    !actual.getMessage().isBlank()
            ) {

                mensaje =
                        actual.getMessage();
            }

            actual =
                    actual.getCause();
        }

        return mensaje;
    }

    private class RenderTabla
            extends DefaultTableCellRenderer {

        private final int alineacion;

        public RenderTabla(
                int alineacion,
                int tipo
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
                            7,
                            0,
                            7
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

                componente.setForeground(
                        TEXTO
                );

                if (
                        table == tablaMovimientos
                ) {

                    if (
                            column == 4
                            &&
                            value != null
                            &&
                            !"0".equals(
                                    value.toString()
                            )
                    ) {

                        componente.setForeground(
                                VERDE
                        );

                    } else if (
                            column == 5
                            &&
                            value != null
                            &&
                            !"0".equals(
                                    value.toString()
                            )
                    ) {

                        componente.setForeground(
                                ROJO
                        );
                    }
                }
            }

            return componente;
        }
    }

    private class RenderEstadoLote
            extends DefaultTableCellRenderer {

        public RenderEstadoLote() {

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

            Component componente =
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
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
                            4,
                            8,
                            4,
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

                return componente;
            }

            String estado =
                    value
                    == null
                            ? ""
                            : value.toString();

            if (
                    "DISPONIBLE".equals(
                            estado
                    )
            ) {

                componente.setBackground(
                        VERDE_SUAVE
                );

                componente.setForeground(
                        VERDE
                );

            } else if (
                    "PARCIAL".equals(
                            estado
                    )
            ) {

                componente.setBackground(
                        new Color(
                                255,
                                247,
                                237
                        )
                );

                componente.setForeground(
                        new Color(
                                194,
                                65,
                                12
                        )
                );

            } else if (
                    "AGOTADO".equals(
                            estado
                    )
            ) {

                componente.setBackground(
                        ROJO_SUAVE
                );

                componente.setForeground(
                        ROJO
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

    private static class FilaKardex {

        final Producto producto;
        final MovimientoKardex movimiento;

        FilaKardex(
                Producto producto,
                MovimientoKardex movimiento
        ) {

            this.producto =
                    producto;

            this.movimiento =
                    movimiento;
        }
    }
}
