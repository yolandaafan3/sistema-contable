package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.servicio.ProductoService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DialogoProducto extends JDialog {

    private static final BigDecimal MAX_MONTO =
            new BigDecimal("9999999999999999.99");

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final ProductoService productoService = new ProductoService();
    private final DefaultTableModel modelo;
    private final JTable tabla;
    private final JTextField txtBuscar = new JTextField();
    private List<Producto> productos;

    private final Color FONDO = new Color(245, 247, 250);
    private final Color TEXTO = new Color(30, 41, 59);
    private final Color SECUNDARIO = new Color(100, 116, 139);
    private final Color BORDE = new Color(226, 232, 240);
    private final Color PRIMARIO = new Color(37, 99, 235);
    private final Color PRIMARIO_HOVER = new Color(29, 78, 216);
    private final Color CELESTE_SUAVE = new Color(219, 234, 254);
    private final Color VERDE = new Color(22, 163, 74);
    private final Color ROJO = new Color(220, 38, 38);

    public DialogoProducto(Window owner) {
        super(owner, "Productos", ModalityType.APPLICATION_MODAL);

        modelo = new DefaultTableModel(
                new String[]{
                    "Código", "Producto", "Costo referencia",
                    "Precio venta", "Existencia", "Estado"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabla = new JTable(modelo);

        configurarVentana();
        construirInterfaz();
        cargarProductos();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1080, 680);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(getOwner());
        getContentPane().setBackground(FONDO);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(0, 18));
        raiz.setBackground(FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));

        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        raiz.add(crearZonaTabla(), BorderLayout.CENTER);
        raiz.add(crearPie(), BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private JPanel crearEncabezado() {
        JPanel contenedor = new JPanel(new BorderLayout(18, 14));
        contenedor.setOpaque(false);

        JPanel cabecera = new JPanel(new BorderLayout(16, 0));
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel icono = new JLabel(crearIconoProducto(46));
        icono.setHorizontalAlignment(SwingConstants.CENTER);
        icono.setPreferredSize(new Dimension(52, 52));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Productos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 27));
        titulo.setForeground(TEXTO);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(
                "Administra el catálogo. El inventario nace de las compras y se controla en Kardex PEPS."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(SECUNDARIO);
        subtitulo.setAlignmentX(LEFT_ALIGNMENT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        cabecera.add(icono, BorderLayout.WEST);
        cabecera.add(textos, BorderLayout.CENTER);

        JPanel controles = new JPanel(new BorderLayout(12, 0));
        controles.setOpaque(false);

        JPanel buscarPanel = new JPanel(new BorderLayout(8, 0));
        buscarPanel.setBackground(Color.WHITE);
        buscarPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));

        JLabel lupa = new JLabel("⌕");
        lupa.setFont(new Font("Segoe UI Symbol", Font.BOLD, 19));
        lupa.setForeground(SECUNDARIO);

        txtBuscar.setPreferredSize(new Dimension(250, 34));
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setBorder(null);
        txtBuscar.setToolTipText("Buscar por código o nombre");
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrar();
            }
        });

        buscarPanel.add(lupa, BorderLayout.WEST);
        buscarPanel.add(txtBuscar, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);

        JButton nuevo = crearBoton("+  Nuevo producto", true);
        JButton editar = crearBoton("Editar producto", false);
        JButton estado = crearBoton("Activar / Desactivar", false);

        nuevo.addActionListener(e -> abrirEditor(null));
        editar.addActionListener(e -> editarSeleccionado());
        estado.addActionListener(e -> cambiarEstadoSeleccionado());

        botones.add(nuevo);
        botones.add(editar);
        botones.add(estado);

        controles.add(buscarPanel, BorderLayout.WEST);
        controles.add(botones, BorderLayout.EAST);

        contenedor.add(cabecera, BorderLayout.NORTH);
        contenedor.add(controles, BorderLayout.SOUTH);

        return contenedor;
    }

    private JPanel crearZonaTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(BORDE));

        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(34);
        tabla.setGridColor(BORDE);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(CELESTE_SUAVE);
        tabla.setSelectionForeground(TEXTO);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);

        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setForeground(TEXTO);
        tabla.getTableHeader().setBackground(new Color(248, 250, 252));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tabla.getTableHeader().setReorderingAllowed(false);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(250);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(110);

        DefaultTableCellRenderer estadoRenderer = new DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column
            ) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column
                );

                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                if (!isSelected) {
                    label.setBackground(Color.WHITE);
                    if ("Activo".equals(String.valueOf(value))) {
                        label.setForeground(VERDE);
                    } else {
                        label.setForeground(ROJO);
                    }
                }

                return label;
            }
        };

        tabla.getColumnModel().getColumn(5).setCellRenderer(estadoRenderer);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);

        JLabel ayuda = new JLabel(
                "Selecciona un producto para editarlo o cambiar su estado."
        );
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ayuda.setForeground(SECUNDARIO);

        JButton cerrar = crearBoton("Cerrar", false);
        cerrar.addActionListener(e -> dispose());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(cerrar);

        pie.add(ayuda, BorderLayout.WEST);
        pie.add(acciones, BorderLayout.EAST);

        return pie;
    }

    private void cargarProductos() {
        try {
            productos = productoDAO.listarTodos();
            filtrar();
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void filtrar() {
        modelo.setRowCount(0);
        if (productos == null) return;

        String filtro = txtBuscar.getText() == null
                ? "" : txtBuscar.getText().trim().toLowerCase();

        for (Producto p : productos) {
            String codigo = p.getCodigo() == null ? "" : p.getCodigo().toLowerCase();
            String nombre = p.getNombre() == null ? "" : p.getNombre().toLowerCase();

            if (!filtro.isBlank()
                    && !codigo.contains(filtro)
                    && !nombre.contains(filtro)) {
                continue;
            }

            modelo.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                dinero(p.getCostoCompra()),
                dinero(p.getPrecioVenta()),
                cantidad(p.getExistenciaActual()),
                p.isActivo() ? "Activo" : "Inactivo"
            });
        }
    }

    private void editarSeleccionado() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista < 0) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un producto de la tabla.",
                    "Productos", JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        String codigo = modelo.getValueAt(filaModelo, 0).toString();

        for (Producto p : productos) {
            if (codigo.equals(p.getCodigo())) {
                abrirEditor(p);
                return;
            }
        }
    }

    private void cambiarEstadoSeleccionado() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista < 0) {
            JOptionPane.showMessageDialog(
                    this, "Selecciona un producto de la tabla.",
                    "Productos", JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        String codigo = modelo.getValueAt(filaModelo, 0).toString();
        Producto seleccionado = null;

        for (Producto p : productos) {
            if (codigo.equals(p.getCodigo())) {
                seleccionado = p;
                break;
            }
        }

        if (seleccionado == null) {
            JOptionPane.showMessageDialog(
                    this, "No se encontró el producto seleccionado.",
                    "Productos", JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean nuevoEstado = !seleccionado.isActivo();

        if (!nuevoEstado) {
            BigDecimal existencia = seleccionado.getExistenciaActual() == null
                    ? BigDecimal.ZERO : seleccionado.getExistenciaActual();
            if (existencia.compareTo(BigDecimal.ZERO) != 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No puedes desactivar este producto porque todavía tiene "
                        + cantidad(existencia)
                        + " unidad(es) en existencia.\n\n"
                        + "Primero deja su existencia en 0 mediante las operaciones correspondientes.",
                        "Producto con existencia",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
        }

        String accion = nuevoEstado ? "activar" : "desactivar";
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas " + accion + " el producto "
                + seleccionado.getCodigo() + " - " + seleccionado.getNombre() + "?\n\n"
                + (nuevoEstado
                    ? "Volverá a aparecer en los selectores de operaciones."
                    : "Dejará de aparecer en los selectores de compras, ventas y demás operaciones."),
                nuevoEstado ? "Activar producto" : "Desactivar producto",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) return;

        try {
            productoService.cambiarEstado(seleccionado.getIdProducto(), nuevoEstado);
            cargarProductos();
            JOptionPane.showMessageDialog(
                    this,
                    "Producto " + (nuevoEstado ? "activado" : "desactivado") + " correctamente.",
                    "Productos",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void abrirEditor(Producto existente) {
        JDialog editor = new JDialog(
                this,
                existente == null ? "Nuevo producto" : "Editar producto",
                ModalityType.APPLICATION_MODAL
        );

        editor.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        editor.setSize(720, 560);
        editor.setMinimumSize(new Dimension(650, 500));
        editor.setLocationRelativeTo(this);
        editor.getContentPane().setBackground(FONDO);

        JTextField txtCodigo = crearCampoTexto();
        JTextField txtNombre = crearCampoTexto();
        JTextField txtDescripcion = crearCampoTexto();
        JTextField txtCosto = crearCampoTexto();
        JTextField txtPrecio = crearCampoTexto();

        if (existente != null) {
            txtCodigo.setText(existente.getCodigo());
            txtNombre.setText(existente.getNombre());
            txtDescripcion.setText(
                    existente.getDescripcion() == null ? "" : existente.getDescripcion()
            );
            txtCosto.setText(decimal(existente.getCostoCompra(), 2));
            txtPrecio.setText(decimal(existente.getPrecioVenta(), 2));
        }

        JPanel raiz = new JPanel(new BorderLayout(0, 16));
        raiz.setBackground(FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JPanel encabezado = new JPanel(new BorderLayout(14, 0));
        encabezado.setBackground(Color.WHITE);
        encabezado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel icono = new JLabel(crearIconoProducto(42));
        icono.setPreferredSize(new Dimension(48, 48));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel(existente == null ? "Nuevo producto" : "Editar producto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 23));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                existente == null
                        ? "Registra la información base del producto."
                        : "Actualiza la información de referencia del producto."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(subtitulo);

        encabezado.add(icono, BorderLayout.WEST);
        encabezado.add(textos, BorderLayout.CENTER);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBackground(Color.WHITE);
        formulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 7, 7, 7);

        agregarCampo(formulario, gbc, 0, "Código", txtCodigo);
        agregarCampo(formulario, gbc, 1, "Nombre", txtNombre);
        agregarCampo(formulario, gbc, 2, "Descripción", txtDescripcion);
        agregarCampo(formulario, gbc, 3, "Costo de compra de referencia", txtCosto);
        agregarCampo(formulario, gbc, 4, "Precio de venta de referencia", txtPrecio);

        JPanel ayudaPanel = new JPanel(new BorderLayout(10, 0));
        ayudaPanel.setBackground(new Color(239, 246, 255));
        ayudaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel info = new JLabel("i", SwingConstants.CENTER);
        info.setOpaque(true);
        info.setBackground(PRIMARIO);
        info.setForeground(Color.WHITE);
        info.setFont(new Font("Segoe UI", Font.BOLD, 13));
        info.setPreferredSize(new Dimension(26, 26));

        JLabel ayuda = new JLabel(
                "<html>La existencia inicia en 0. Las compras crean las unidades y las capas PEPS; "
                + "editar esta ficha no revaloriza inventario.</html>"
        );
        ayuda.setForeground(SECUNDARIO);
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        ayudaPanel.add(info, BorderLayout.WEST);
        ayudaPanel.add(ayuda, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(14, 7, 7, 7);
        formulario.add(ayudaPanel, gbc);
        gbc.gridwidth = 1;

        JButton guardar = crearBoton("Guardar", true);
        JButton cancelar = crearBoton("Cancelar", false);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        acciones.add(cancelar);
        acciones.add(guardar);

        raiz.add(encabezado, BorderLayout.NORTH);
        raiz.add(formulario, BorderLayout.CENTER);
        raiz.add(acciones, BorderLayout.SOUTH);

        editor.setContentPane(raiz);

        cancelar.addActionListener(e -> editor.dispose());

        guardar.addActionListener(e -> {
            try {
                String codigo = txtCodigo.getText().trim();
                String nombre = txtNombre.getText().trim();

                if (codigo.isBlank()) {
                    throw new IllegalArgumentException("El código es obligatorio.");
                }
                if (nombre.isBlank()) {
                    throw new IllegalArgumentException("El nombre es obligatorio.");
                }

                BigDecimal costo = leerDecimal(
                        txtCosto.getText(), 2,
                        "costo de compra de referencia"
                );

                BigDecimal precio = leerDecimal(
                        txtPrecio.getText(), 2,
                        "precio de venta de referencia"
                );

                if (costo.compareTo(BigDecimal.ZERO) < 0
                        || precio.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException(
                            "Los valores de referencia no pueden ser negativos."
                    );
                }

                if (costo.compareTo(MAX_MONTO) > 0
                        || precio.compareTo(MAX_MONTO) > 0) {
                    throw new IllegalArgumentException(
                            "El costo o precio supera el límite permitido."
                    );
                }

                Producto p = existente == null ? new Producto() : existente;
                p.setCodigo(codigo);
                p.setNombre(nombre);
                p.setDescripcion(txtDescripcion.getText().trim());
                p.setCostoCompra(costo);
                p.setPrecioVenta(precio);

                if (existente == null) {
                    productoService.registrar(p);
                } else {
                    productoService.actualizar(p);
                }

                editor.dispose();
                cargarProductos();

            } catch (Exception ex) {
                mostrarError(ex);
            }
        });

        editor.setVisible(true);
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setPreferredSize(new Dimension(0, 38));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(6, 9, 6, 9)
        ));
        return campo;
    }

    private void agregarCampo(
            JPanel panel, GridBagConstraints gbc,
            int fila, String etiqueta, JTextField campo
    ) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(TEXTO);

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.34;
        gbc.insets = new Insets(7, 7, 7, 16);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.66;
        gbc.insets = new Insets(7, 7, 7, 7);
        panel.add(campo, gbc);
    }

    private BigDecimal leerDecimal(String texto, int escala, String campo) {
        if (texto == null || texto.trim().isBlank()) {
            throw new IllegalArgumentException("Completa el " + campo + ".");
        }

        String limpio = texto.trim();

        if (limpio.contains(",") && limpio.contains(".")) {
            throw new IllegalArgumentException(
                    "En " + campo + " usa punto o coma decimal, no ambos."
            );
        }

        limpio = limpio.replace(',', '.');

        try {
            return new BigDecimal(limpio).setScale(escala, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El " + campo + " debe ser numérico."
            );
        }
    }

    private JButton crearBoton(String texto, boolean principal) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(principal ? Color.WHITE : TEXTO);
        boton.setBackground(principal ? PRIMARIO : Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(principal ? PRIMARIO : BORDE),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15)
                )
        );

        if (principal) {
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
        }

        return boton;
    }

    private ImageIcon crearIconoProducto(int tamano) {
        BufferedImage img = new BufferedImage(
                tamano, tamano, BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(CELESTE_SUAVE);
        g2.fillRoundRect(0, 0, tamano, tamano, 14, 14);

        g2.setColor(PRIMARIO);
        g2.setStroke(new java.awt.BasicStroke(
                Math.max(2f, tamano / 20f),
                java.awt.BasicStroke.CAP_ROUND,
                java.awt.BasicStroke.JOIN_ROUND
        ));

        int margen = tamano / 4;
        int ancho = tamano - (margen * 2);
        int alto = tamano / 3;
        int y = tamano / 3;

        g2.drawRoundRect(margen, y, ancho, alto, 4, 4);
        g2.drawLine(margen, y, tamano / 2, y - tamano / 8);
        g2.drawLine(tamano / 2, y - tamano / 8, tamano - margen, y);
        g2.drawLine(tamano / 2, y - tamano / 8, tamano / 2, y + alto);

        g2.dispose();
        return new ImageIcon(img);
    }

    private String dinero(BigDecimal valor) {
        return "$" + decimal(valor, 2);
    }

    private String cantidad(BigDecimal valor) {
        if (valor == null) valor = BigDecimal.ZERO;
        return valor.setScale(6, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    private String decimal(BigDecimal valor, int escala) {
        if (valor == null) valor = BigDecimal.ZERO;
        return valor.setScale(escala, RoundingMode.HALF_UP).toPlainString();
    }

    private void mostrarError(Throwable error) {
        Throwable actual = error;
        String mensaje = "Ocurrió un error.";

        while (actual != null) {
            if (actual.getMessage() != null && !actual.getMessage().isBlank()) {
                mensaje = actual.getMessage();
            }
            actual = actual.getCause();
        }

        JOptionPane.showMessageDialog(
                this, mensaje, "Productos", JOptionPane.WARNING_MESSAGE
        );
    }
}
