package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.servicio.DevolucionCompraService;
import com.mycompany.sistemacontable.servicio.DevolucionVentaService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class DialogoDevolucion extends JDialog {

    private final String tipoOperacion;

    private final ProductoDAO productoDAO =
            new ProductoDAO();

    private final DevolucionCompraService compraService =
            new DevolucionCompraService();

    private final DevolucionVentaService ventaService =
            new DevolucionVentaService();

    private JSpinner spFecha;
    private JComboBox<Producto> cmbProducto;
    private JComboBox<OperacionOrigenItem> cmbOrigen;
    private JTextField txtMonto;
    private JLabel lblUnidades;
    private JComboBox<String> cmbForma;
    private JLabel lblFormaOrigen;
    private JTextArea txtConcepto;

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

    private final Color NARANJA =
            new Color(180, 83, 9);

    public DialogoDevolucion(
            Window owner,
            String tipoOperacion
    ) {

        super(
                owner,
                "DEVOLUCION_COMPRA".equals(tipoOperacion)
                        ? "Devolución sobre Compra"
                        : "Devolución sobre Venta",
                ModalityType.APPLICATION_MODAL
        );

        this.tipoOperacion =
                tipoOperacion;

        setDefaultCloseOperation(
                DISPOSE_ON_CLOSE
        );

        setSize(
                880,
                700
        );

        setMinimumSize(
                new Dimension(
                        760,
                        620
                )
        );

        setResizable(
                true
        );

        construir();

        setLocationRelativeTo(
                owner
        );

        cargarProductos();
    }

    private boolean esCompra() {

        return "DEVOLUCION_COMPRA".equals(
                tipoOperacion
        );
    }

    private void construir() {

        setLayout(
                new BorderLayout()
        );

        getContentPane().setBackground(
                FONDO
        );

        add(
                crearEncabezado(),
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
                crearTarjetaOperacion()
        );

        contenido.add(
                Box.createVerticalStrut(
                        14
                )
        );

        contenido.add(
                crearTarjetaInformacion()
        );

        JScrollPane scroll =
                new JScrollPane(
                        contenido
                );

        scroll.setBorder(
                null
        );

        scroll.getViewport()
                .setBackground(
                        FONDO
                );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(
                        16
                );

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(
                scroll,
                BorderLayout.CENTER
        );

        add(
                crearPie(),
                BorderLayout.SOUTH
        );
    }

    private JPanel crearEncabezado() {

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
                        "↶",
                        SwingConstants.CENTER
                );

        icono.setOpaque(
                true
        );

        icono.setBackground(
                esCompra()
                        ? new Color(255, 247, 237)
                        : new Color(240, 253, 244)
        );

        icono.setForeground(
                esCompra()
                        ? NARANJA
                        : VERDE
        );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        24
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
                        esCompra()
                                ? "Registrar devolución sobre compra"
                                : "Registrar devolución sobre venta"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        23
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel subtitulo =
                new JLabel(
                        esCompra()
                                ? "La devolución reducirá las unidades del lote de compra original."
                                : "La mercancía devuelta regresará al inventario mediante Kardex PEPS."
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
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

        JLabel etiqueta =
                new JLabel(
                        esCompra()
                                ? "SALIDA DE INVENTARIO"
                                : "ENTRADA A INVENTARIO"
                );

        etiqueta.setOpaque(
                true
        );

        etiqueta.setBackground(
                esCompra()
                        ? new Color(255, 247, 237)
                        : new Color(240, 253, 244)
        );

        etiqueta.setForeground(
                esCompra()
                        ? NARANJA
                        : VERDE
        );

        etiqueta.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        etiqueta.setBorder(
                BorderFactory.createEmptyBorder(
                        7,
                        11,
                        7,
                        11
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
                etiqueta,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel crearTarjetaOperacion() {

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
                                18,
                                16,
                                18
                        )
                )
        );

        tarjeta.setAlignmentX(
                Component.LEFT_ALIGNMENT
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
                        "Datos de la devolución"
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

        JLabel ayuda =
                new JLabel(
                        "Selecciona la operación original y completa el monto que deseas devolver."
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

        JPanel formulario =
                new JPanel(
                        new GridBagLayout()
                );

        formulario.setOpaque(
                false
        );

        spFecha =
                new JSpinner(
                        DialogoUIUtils.crearModeloFechaPeriodoActivo()
                );

        spFecha.setEditor(
                new JSpinner.DateEditor(
                        spFecha,
                        "dd/MM/yyyy"
                )
        );

        cmbProducto =
                new JComboBox<>();

        cmbProducto.addActionListener(
                e -> cargarOrigenes()
        );

        cmbOrigen =
                new JComboBox<>();

        cmbOrigen.addActionListener(
                e -> {
                    actualizarUnidades();
                    actualizarFormaOrigen();
                }
        );

        txtMonto =
                crearCampoTexto();

        txtMonto.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                actualizarUnidades();
                            }

                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                actualizarUnidades();
                            }

                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                actualizarUnidades();
                            }
                        }
                );

        lblUnidades =
                new JLabel(
                        "0"
                );

        lblUnidades.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        lblUnidades.setForeground(
                PRIMARIO
        );

        cmbForma =
                new JComboBox<>(
                        new String[]{
                            "CREDITO",
                            "EFECTIVO",
                            "BANCO",
                            "MIXTO"
                        }
                );

        cmbForma.setEnabled(
                false
        );

        lblFormaOrigen =
                new JLabel(
                        "Se toma automáticamente del asiento original; si fue MIXTO, se conserva la misma proporción."
                );

        lblFormaOrigen.setForeground(
                SECUNDARIO
        );

        lblFormaOrigen.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        txtConcepto =
                new JTextArea(
                        3,
                        30
                );

        txtConcepto.setLineWrap(
                true
        );

        txtConcepto.setWrapStyleWord(
                true
        );

        txtConcepto.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        txtConcepto.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        10,
                        8,
                        10
                )
        );

        configurarComponente(
                spFecha
        );

        configurarComponente(
                cmbProducto
        );

        configurarComponente(
                cmbOrigen
        );

        configurarComponente(
                cmbForma
        );

        JScrollPane scrollConcepto =
                new JScrollPane(
                        txtConcepto
                );

        scrollConcepto.setBorder(
                BorderFactory.createLineBorder(
                        new Color(
                                203,
                                213,
                                225
                        )
                )
        );

        GridBagConstraints g =
                new GridBagConstraints();

        g.fill =
                GridBagConstraints.HORIZONTAL;

        g.anchor =
                GridBagConstraints.WEST;

        g.weightx =
                1;

        g.insets =
                new Insets(
                        6,
                        0,
                        6,
                        0
                );

        int fila =
                0;

        agregarFila(
                formulario,
                g,
                fila++,
                "Fecha",
                spFecha
        );

        agregarFila(
                formulario,
                g,
                fila++,
                "Producto",
                cmbProducto
        );

        agregarFila(
                formulario,
                g,
                fila++,
                esCompra()
                        ? "Compra de origen"
                        : "Venta de origen",
                cmbOrigen
        );

        agregarFila(
                formulario,
                g,
                fila++,
                "Monto de la devolución ($)",
                txtMonto
        );

        agregarFila(
                formulario,
                g,
                fila++,
                "Unidades calculadas",
                lblUnidades
        );

        agregarFila(
                formulario,
                g,
                fila++,
                esCompra()
                        ? "Forma según compra original"
                        : "Forma según venta original",
                cmbForma
        );

        agregarFila(
                formulario,
                g,
                fila++,
                "Concepto",
                scrollConcepto
        );

        tarjeta.add(
                cabecera,
                BorderLayout.NORTH
        );

        tarjeta.add(
                formulario,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearTarjetaInformacion() {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        tarjeta.setBackground(
                esCompra()
                        ? new Color(255, 247, 237)
                        : new Color(240, 253, 244)
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                esCompra()
                                        ? new Color(253, 186, 116)
                                        : new Color(187, 247, 208)
                        ),
                        BorderFactory.createEmptyBorder(
                                14,
                                15,
                                14,
                                15
                        )
                )
        );

        tarjeta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel icono =
                new JLabel(
                        "i",
                        SwingConstants.CENTER
                );

        icono.setOpaque(
                true
        );

        icono.setBackground(
                Color.WHITE
        );

        icono.setForeground(
                esCompra()
                        ? NARANJA
                        : VERDE
        );

        icono.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        icono.setPreferredSize(
                new Dimension(
                        38,
                        38
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
                        "Cómo se registrará"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel descripcion =
                new JLabel(
                        "<html><div style='width:650px'>"
                        + "La devolución queda vinculada a la operación original. "
                        + "Las unidades se calculan como monto ÷ costo/precio unitario de esa compra o venta. "
                        + (
                                esCompra()
                                        ? "En compra se descuenta del lote original."
                                        : "En venta la mercancía regresa al inventario al costo PEPS de la venta original."
                        )
                        + "</div></html>"
                );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
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
                        4
                )
        );

        textos.add(
                descripcion
        );

        textos.add(
                Box.createVerticalStrut(
                        6
                )
        );

        textos.add(
                lblFormaOrigen
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

    private JPanel crearPie() {

        JPanel pie =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                12
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

        JButton cancelar =
                crearBotonSecundario(
                        "Cancelar"
                );

        cancelar.addActionListener(
                e -> dispose()
        );

        JButton guardar =
                crearBotonPrincipal(
                        "Registrar devolución"
                );

        guardar.addActionListener(
                e -> guardar()
        );

        pie.add(
                cancelar
        );

        pie.add(
                guardar
        );

        getRootPane().setDefaultButton(
                guardar
        );

        return pie;
    }

    private JTextField crearCampoTexto() {

        JTextField campo =
                new JTextField();

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        campo.setBackground(
                Color.WHITE
        );

        campo.setForeground(
                TEXTO
        );

        campo.setPreferredSize(
                new Dimension(
                        430,
                        38
                )
        );

        campo.setBorder(
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

        return campo;
    }

    private void configurarComponente(
            javax.swing.JComponent componente
    ) {

        componente.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        componente.setPreferredSize(
                new Dimension(
                        430,
                        38
                )
        );
    }

    private void agregarFila(
            JPanel panel,
            GridBagConstraints base,
            int fila,
            String etiqueta,
            Component componente
    ) {

        GridBagConstraints gl =
                (GridBagConstraints) base.clone();

        gl.gridx =
                0;

        gl.gridy =
                fila;

        gl.weightx =
                0;

        gl.fill =
                GridBagConstraints.NONE;

        gl.insets =
                new Insets(
                        6,
                        0,
                        6,
                        18
                );

        JLabel label =
                new JLabel(
                        etiqueta
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

        panel.add(
                label,
                gl
        );

        GridBagConstraints gc =
                (GridBagConstraints) base.clone();

        gc.gridx =
                1;

        gc.gridy =
                fila;

        gc.weightx =
                1;

        gc.fill =
                GridBagConstraints.HORIZONTAL;

        gc.insets =
                new Insets(
                        6,
                        0,
                        6,
                        0
                );

        panel.add(
                componente,
                gc
        );
    }

    private JButton crearBotonPrincipal(
            String texto
    ) {

        JButton boton =
                new JButton(
                        texto
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

    private void cargarProductos() {

        cmbProducto.removeAllItems();

        for (
                Producto producto
                : productoDAO.listarProductosActivos()
        ) {

            cmbProducto.addItem(
                    producto
            );
        }

        if (
                cmbProducto.getItemCount() > 0
        ) {

            cmbProducto.setSelectedIndex(
                    0
            );
        }

        cargarOrigenes();
    }

    private void cargarOrigenes() {

        cmbOrigen.removeAllItems();

        Producto producto =
                (Producto) cmbProducto.getSelectedItem();

        if (
                producto == null
        ) {
            return;
        }

        String tipo =
                esCompra()
                        ? "COMPRA"
                        : "VENTA";

        String sql =
                "SELECT id_operacion,fecha,precio_unitario,cantidad,total,concepto,forma_pago "
                + "FROM operaciones "
                + "WHERE id_producto=? AND tipo_operacion=? "
                + "ORDER BY fecha DESC,id_operacion DESC";

        try (
                Connection conexion =
                        Conexion.conectar();

                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql
                        )
        ) {

            ps.setInt(
                    1,
                    producto.getIdProducto()
            );

            ps.setString(
                    2,
                    tipo
            );

            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {

                while (
                        rs.next()
                ) {

                    cmbOrigen.addItem(
                            new OperacionOrigenItem(
                                    rs.getInt(
                                            "id_operacion"
                                    ),
                                    rs.getDate(
                                            "fecha"
                                    ).toLocalDate(),
                                    rs.getBigDecimal(
                                            "precio_unitario"
                                    ),
                                    rs.getBigDecimal(
                                            "cantidad"
                                    ),
                                    rs.getBigDecimal(
                                            "total"
                                    ),
                                    rs.getString(
                                            "concepto"
                                    ),
                                    rs.getString(
                                            "forma_pago"
                                    )
                            )
                    );
                }
            }

        } catch (
                Exception e
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se pudieron cargar las operaciones de origen: "
                    + e.getMessage(),
                    "Devolución",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        actualizarUnidades();
        actualizarFormaOrigen();
    }

    private void actualizarFormaOrigen() {

        if (
                cmbForma == null
        ) {
            return;
        }

        OperacionOrigenItem origen =
                (OperacionOrigenItem) cmbOrigen.getSelectedItem();

        if (
                origen == null
        ) {

            cmbForma.setSelectedItem(
                    "EFECTIVO"
            );

            return;
        }

        if (
                "CREDITO".equals(
                        origen.forma
                )
                ||
                "EFECTIVO".equals(
                        origen.forma
                )
                ||
                "BANCO".equals(
                        origen.forma
                )
                ||
                "MIXTO".equals(
                        origen.forma
                )
        ) {

            cmbForma.setSelectedItem(
                    origen.forma
            );
        }
    }

    private void actualizarUnidades() {

        if (
                lblUnidades == null
        ) {
            return;
        }

        try {

            OperacionOrigenItem origen =
                    (OperacionOrigenItem) cmbOrigen.getSelectedItem();

            BigDecimal monto =
                    leerMonto(
                            false
                    );

            if (
                    origen == null
                    ||
                    monto == null
                    ||
                    origen.precio.signum() <= 0
            ) {

                lblUnidades.setText(
                        "0"
                );

                return;
            }

            lblUnidades.setText(
                    monto.divide(
                            origen.precio,
                            6,
                            RoundingMode.HALF_UP
                    )
                    .stripTrailingZeros()
                    .toPlainString()
            );

        } catch (
                Exception e
        ) {

            lblUnidades.setText(
                    "0"
            );
        }
    }

    private void guardar() {

        try {

            Producto producto =
                    (Producto) cmbProducto.getSelectedItem();

            OperacionOrigenItem origen =
                    (OperacionOrigenItem) cmbOrigen.getSelectedItem();

            if (
                    producto == null
            ) {

                throw new IllegalArgumentException(
                        "Selecciona un producto."
                );
            }

            if (
                    origen == null
            ) {

                throw new IllegalArgumentException(
                        "Selecciona la operación original que se está devolviendo."
                );
            }

            BigDecimal monto =
                    leerMonto(
                            true
                    );

            LocalDate fecha =
                    ((Date) spFecha.getValue())
                            .toInstant()
                            .atZone(
                                    ZoneId.systemDefault()
                            )
                            .toLocalDate();

            String forma =
                    origen.forma;

            String concepto =
                    txtConcepto.getText()
                            .trim();

            BigDecimal unidades =
                    monto.divide(
                            origen.precio,
                            6,
                            RoundingMode.HALF_UP
                    );

            String efecto =
                    esCompra()
                            ? "EFECTO EN KARDEX: SALIDA de inventario"
                            : "EFECTO EN KARDEX: ENTRADA de inventario";

            int ok =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Producto: "
                            + producto.getNombre()
                            + "\nOrigen: "
                            + origen
                            + "\nMonto: $"
                            + monto
                            + "\nUnidades: "
                            + unidades.stripTrailingZeros()
                                    .toPlainString()
                            + "\n"
                            + efecto
                            + "\n\n¿Registrar devolución?",
                            "Confirmar",
                            JOptionPane.YES_NO_OPTION
                    );

            if (
                    ok
                    != JOptionPane.YES_OPTION
            ) {
                return;
            }

            if (
                    esCompra()
            ) {

                compraService.registrar(
                        fecha,
                        producto.getIdProducto(),
                        origen.id,
                        monto,
                        forma,
                        concepto
                );

            } else {

                ventaService.registrar(
                        fecha,
                        producto.getIdProducto(),
                        origen.id,
                        monto,
                        forma,
                        concepto
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Devolución registrada. Kardex PEPS, lote y existencia fueron recalculados.",
                    "Correcto",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (
                Exception e
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    mensaje(
                            e
                    ),
                    "No se pudo registrar",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private BigDecimal leerMonto(
            boolean obligatorio
    ) {

        String texto =
                txtMonto.getText()
                        .trim()
                        .replace(
                                "$",
                                ""
                        )
                        .replace(
                                " ",
                                ""
                        );

        if (
                texto.isBlank()
        ) {

            if (
                    obligatorio
            ) {

                throw new IllegalArgumentException(
                        "Ingresa el monto de la devolución."
                );
            }

            return null;
        }

        texto =
                texto.replace(
                        ',',
                        '.'
                );

        try {

            BigDecimal valor =
                    new BigDecimal(
                            texto
                    ).setScale(
                            2,
                            RoundingMode.HALF_UP
                    );

            if (
                    valor.signum() <= 0
            ) {

                throw new IllegalArgumentException(
                        "El monto debe ser mayor que cero."
                );
            }

            return valor;

        } catch (
                NumberFormatException e
        ) {

            throw new IllegalArgumentException(
                    "El monto no es válido."
            );
        }
    }

    private String mensaje(
            Throwable e
    ) {

        String mensaje =
                "Ocurrió un error.";

        for (
                Throwable actual = e;
                actual != null;
                actual = actual.getCause()
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
        }

        return mensaje;
    }

    private static class OperacionOrigenItem {

        final int id;
        final LocalDate fecha;
        final BigDecimal precio;
        final BigDecimal cantidad;
        final BigDecimal total;
        final String concepto;
        final String forma;

        OperacionOrigenItem(
                int id,
                LocalDate fecha,
                BigDecimal precio,
                BigDecimal cantidad,
                BigDecimal total,
                String concepto,
                String forma
        ) {

            this.id =
                    id;

            this.fecha =
                    fecha;

            this.precio =
                    precio;

            this.cantidad =
                    cantidad;

            this.total =
                    total;

            this.concepto =
                    concepto;

            this.forma =
                    forma;
        }

        @Override
        public String toString() {

            return "#"
                    + id
                    + " - "
                    + fecha
                    + " - "
                    + (
                            concepto == null
                                    ? "Operación"
                                    : concepto
                    )
                    + " - "
                    + forma
                    + " - unit. $"
                    + precio;
        }
    }
}
