package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.servicio.ProductoService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import javax.swing.JTable;
import javax.swing.JTextField;
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
        setSize(980, 620);
        setMinimumSize(new Dimension(820, 500));
        setLocationRelativeTo(getOwner());
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout(0, 18));
        raiz.setBackground(FONDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(24, 26, 24, 26));

        JPanel encabezado = new JPanel(new BorderLayout(20, 0));
        encabezado.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Productos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Administra el catálogo. El inventario nace de las compras y se controla en Kardex PEPS."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JPanel controles = new JPanel();
        controles.setOpaque(false);

        txtBuscar.setPreferredSize(new Dimension(220, 38));
        txtBuscar.setToolTipText("Buscar por código o nombre");
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrar();
            }
        });

        JButton nuevo = crearBoton("Nuevo producto", true);
        JButton editar = crearBoton("Editar producto", false);
        JButton estado = crearBoton("Activar / Desactivar", false);
        nuevo.addActionListener(e -> abrirEditor(null));
        editar.addActionListener(e -> editarSeleccionado());
        estado.addActionListener(e -> cambiarEstadoSeleccionado());

        controles.add(txtBuscar);
        controles.add(nuevo);
        controles.add(editar);
        controles.add(estado);

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(controles, BorderLayout.EAST);

        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(30);
        tabla.setGridColor(BORDE);
        tabla.setShowVerticalLines(false);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));

        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);

        JButton cerrar = crearBoton("Cerrar", false);
        cerrar.addActionListener(e -> dispose());

        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.add(cerrar);
        pie.add(acciones, BorderLayout.EAST);

        raiz.add(encabezado, BorderLayout.NORTH);
        raiz.add(scroll, BorderLayout.CENTER);
        raiz.add(pie, BorderLayout.SOUTH);

        setContentPane(raiz);
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

        JTextField txtCodigo = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtDescripcion = new JTextField();
        JTextField txtCosto = new JTextField();
        JTextField txtPrecio = new JTextField();

        if (existente != null) {
            txtCodigo.setText(existente.getCodigo());
            txtNombre.setText(existente.getNombre());
            txtDescripcion.setText(
                    existente.getDescripcion() == null ? "" : existente.getDescripcion()
            );
            txtCosto.setText(decimal(existente.getCostoCompra(), 2));
            txtPrecio.setText(decimal(existente.getPrecioVenta(), 2));
        }

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 7, 7, 7);

        agregarCampo(formulario, gbc, 0, "Código", txtCodigo);
        agregarCampo(formulario, gbc, 1, "Nombre", txtNombre);
        agregarCampo(formulario, gbc, 2, "Descripción", txtDescripcion);
        agregarCampo(formulario, gbc, 3, "Costo de compra de referencia", txtCosto);
        agregarCampo(formulario, gbc, 4, "Precio de venta de referencia", txtPrecio);

        JLabel ayuda = new JLabel(
                "<html><div style='width:420px'>La existencia inicia en 0. "
                + "Las compras crean las unidades y las capas PEPS; editar esta ficha no revaloriza inventario.</div></html>"
        );
        ayuda.setForeground(SECUNDARIO);
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        formulario.add(ayuda, gbc);
        gbc.gridwidth = 1;

        JButton guardar = crearBoton("Guardar", true);
        JButton cancelar = crearBoton("Cancelar", false);

        JPanel acciones = new JPanel();
        acciones.add(cancelar);
        acciones.add(guardar);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(acciones, BorderLayout.SOUTH);

        editor.setContentPane(contenido);
        editor.setSize(620, 420);
        editor.setLocationRelativeTo(this);

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

                BigDecimal costo = leerDecimal(txtCosto.getText(), 2, "costo de compra de referencia");
                BigDecimal precio = leerDecimal(txtPrecio.getText(), 2, "precio de venta de referencia");

                if (costo.compareTo(BigDecimal.ZERO) < 0
                        || precio.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException(
                            "Los valores de referencia no pueden ser negativos."
                    );
                }

                if (costo.compareTo(MAX_MONTO) > 0 || precio.compareTo(MAX_MONTO) > 0) {
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

    private void agregarCampo(
            JPanel panel, GridBagConstraints gbc,
            int fila, String etiqueta, JTextField campo
    ) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.35;
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
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
                        BorderFactory.createEmptyBorder(9, 14, 9, 14)
                )
        );
        return boton;
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
