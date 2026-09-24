package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import com.mycompany.sistemacontable.modelo.DistribucionPago;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;
import com.mycompany.sistemacontable.servicio.AsientoManualService;
import com.mycompany.sistemacontable.servicio.CalculoIVAService;
import com.mycompany.sistemacontable.servicio.SugerenciaAsientoService;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.Window;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

public class DialogoAsientoManual extends JDialog {

    private final AsientoManualService asientoManualService;
    private final SugerenciaAsientoService sugerenciaAsientoService;
    private final CuentaDAO cuentaDAO;
    private final ProductoDAO productoDAO;
    private final CalculoIVAService calculoIVAService;

    private JSpinner spFecha;
    private JComboBox<String> cmbTipoOperacion;
    private JTextArea txtConcepto;
    private JPanel panelTs;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JComboBox<Cuenta> cmbEditorCuenta;

    private JLabel lblTotalDebe;
    private JLabel lblTotalHaber;
    private JLabel lblDiferencia;
    private JLabel lblEstadoInventario;

    private JButton btnGuardar;
    private JButton btnAgregarLinea;

    private String tipoInventario;
    private Producto productoInventario;
    private BigDecimal cantidadInventario;
    private BigDecimal precioUnitarioInventario;
    private String formaPagoInventario;
    private String cuentaCreditoInventario;
    private DistribucionPago distribucionPagoInventario;

    private static final BigDecimal MAX_MONTO =
            new BigDecimal("9999999999999999.99");
    private static final BigDecimal MAX_CANTIDAD =
            new BigDecimal("999999999999.999999");

    private final Color COLOR_FONDO = new Color(245, 247, 250);
    private final Color COLOR_TEXTO = new Color(30, 41, 59);
    private final Color COLOR_SECUNDARIO = new Color(100, 116, 139);
    private final Color COLOR_PRIMARIO = new Color(37, 99, 235);
    private final Color COLOR_BORDE = new Color(226, 232, 240);
    private final Color COLOR_EXITO = new Color(22, 163, 74);
    private final Color COLOR_ERROR = new Color(220, 38, 38);

    public DialogoAsientoManual(Window propietario) {
        super(propietario, "Asiento Contable", ModalityType.APPLICATION_MODAL);

        asientoManualService = new AsientoManualService();
        sugerenciaAsientoService = new SugerenciaAsientoService();
        cuentaDAO = new CuentaDAO();
        productoDAO = new ProductoDAO();
        calculoIVAService = new CalculoIVAService();

        configurarVentana();
        construirInterfaz();
        cargarCuentas();

        agregarLinea();
        agregarLinea();
        actualizarTotales();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        Rectangle areaUtil = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        int ancho = Math.min(1480, Math.max(1180, (int) (areaUtil.width * 0.90)));
        int alto = Math.min(920, Math.max(760, (int) (areaUtil.height * 0.90)));

        setSize(ancho, alto);
        setMinimumSize(new Dimension(1080, 740));
        setResizable(true);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
        setLocationRelativeTo(getOwner());
    }

    private void construirInterfaz() {
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        add(crearBotonesFinales(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(16, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE),
                BorderFactory.createEmptyBorder(18, 28, 18, 28)
        ));

        JPanel icono = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(219, 234, 254));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                g2.setColor(COLOR_PRIMARIO);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                int x = 14;
                int y = 11;
                int w = getWidth() - 28;
                int h = getHeight() - 22;

                g2.drawRoundRect(x, y, w, h, 5, 5);
                g2.drawLine(x + 7, y + 10, x + w - 7, y + 10);
                g2.drawLine(x + 7, y + 17, x + w - 12, y + 17);
                g2.drawLine(x + 7, y + 24, x + w - 18, y + 24);

                g2.dispose();
            }
        };
        icono.setOpaque(false);
        icono.setPreferredSize(new Dimension(52, 52));
        icono.setMinimumSize(new Dimension(52, 52));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Asiento Contable");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 27));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descripcion = new JLabel(
                "Describe la operación, revisa las cuentas sugeridas y registra una sola vez. "
                + "Las compras y ventas de productos actualizan automáticamente el Kardex PEPS."
        );
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descripcion.setForeground(COLOR_SECUNDARIO);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(descripcion);

        JLabel estado = new JLabel("NUEVO ASIENTO");
        estado.setOpaque(true);
        estado.setBackground(new Color(239, 246, 255));
        estado.setForeground(COLOR_PRIMARIO);
        estado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        estado.setBorder(BorderFactory.createEmptyBorder(7, 11, 7, 11));

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 7));
        derecha.setOpaque(false);
        derecha.add(estado);

        panel.add(icono, BorderLayout.WEST);
        panel.add(textos, BorderLayout.CENTER);
        panel.add(derecha, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearContenido() {
        JPanel fondo = new JPanel(new BorderLayout(0, 14));
        fondo.setBackground(COLOR_FONDO);
        fondo.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        fondo.add(crearDatosGenerales(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);

        JPanel areaTabla = crearAreaTabla();
        areaTabla.setPreferredSize(new Dimension(900, 350));
        centro.add(areaTabla, BorderLayout.CENTER);

        panelTs = crearPanelTs();
        JScrollPane scrollTs = new JScrollPane(panelTs);
        scrollTs.setBorder(null);
        scrollTs.setOpaque(false);
        scrollTs.getViewport().setOpaque(false);
        scrollTs.getVerticalScrollBar().setUnitIncrement(16);
        scrollTs.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollTs.setPreferredSize(new Dimension(900, 170));
        scrollTs.setMinimumSize(new Dimension(0, 145));

        centro.add(scrollTs, BorderLayout.SOUTH);

        fondo.add(centro, BorderLayout.CENTER);
        fondo.add(crearResumen(), BorderLayout.SOUTH);

        return fondo;
    }

    private JPanel crearDatosGenerales() {
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 6, 16);
        gbc.weighty = 0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        tarjeta.add(crearEtiqueta("Fecha"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.28;
        tarjeta.add(crearEtiqueta("Tipo de operación"), gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.72;
        gbc.insets = new Insets(0, 0, 6, 0);
        tarjeta.add(crearEtiqueta("Concepto de la operación"), gbc);

        spFecha = new JSpinner(DialogoUIUtils.crearModeloFechaPeriodoActivo());
        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "dd/MM/yyyy"));
        spFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spFecha.setPreferredSize(new Dimension(175, 42));
        spFecha.setMinimumSize(new Dimension(175, 42));

        if (spFecha.getEditor() instanceof JSpinner.DefaultEditor editor) {
            editor.getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
            editor.getTextField().setHorizontalAlignment(JTextField.CENTER);
            editor.getTextField().setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 0, 16);
        tarjeta.add(spFecha, gbc);

        cmbTipoOperacion = new JComboBox<>(new String[]{
                "Seleccionar...",
                "Compra de mercadería",
                "Venta de mercadería",
                "Devolución sobre compra",
                "Devolución sobre venta",
                "Cobro a cliente",
                "Pago a proveedor",
                "Gasto",
                "Gasto sobre compra",
                "Pago por anticipado",
                "Póliza de seguro",
                "Compra de activo",
                "Préstamo bancario",
                "Aporte de capital",
                "Otro / asiento manual"
        });
        cmbTipoOperacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipoOperacion.setPreferredSize(new Dimension(250, 42));
        cmbTipoOperacion.setMinimumSize(new Dimension(220, 42));
        cmbTipoOperacion.addActionListener(e -> aplicarTipoSeleccionado());

        gbc.gridx = 1;
        gbc.weightx = 0.28;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        tarjeta.add(cmbTipoOperacion, gbc);

        txtConcepto = new JTextArea(2, 20);
        txtConcepto.setLineWrap(true);
        txtConcepto.setWrapStyleWord(true);
        txtConcepto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConcepto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 195, 205)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        txtConcepto.setPreferredSize(new Dimension(0, 54));

        gbc.gridx = 2;
        gbc.weightx = 0.72;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(txtConcepto, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 0, 0);

        lblEstadoInventario = new JLabel(
                "ⓘ  Selecciona el tipo de operación. Para compras y ventas el sistema solicitará producto, monto y valor unitario."
        );
        lblEstadoInventario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEstadoInventario.setForeground(COLOR_SECUNDARIO);
        tarjeta.add(lblEstadoInventario, gbc);

        return tarjeta;
    }

    private void aplicarTipoSeleccionado() {
        if (cmbTipoOperacion == null || txtConcepto == null) {
            return;
        }

        String seleccion = String.valueOf(cmbTipoOperacion.getSelectedItem());

        // El concepto acompaña siempre al tipo seleccionado. Antes solo se
        // actualizaba si el campo estaba vacío, por lo que al pasar de Compra
        // a Venta podía quedarse el texto "Compra".
        txtConcepto.setText(conceptoPredeterminado(seleccion));

        limpiarDatosInventario();

        if (modeloTabla != null) {
            modeloTabla.setRowCount(0);
            agregarLinea();
            agregarLinea();
            actualizarTotales();
        }
    }

    private String conceptoPredeterminado(String seleccion) {
        return switch (seleccion) {
            case "Compra de mercadería" -> "Compra de mercadería";
            case "Venta de mercadería" -> "Venta de mercadería";
            case "Devolución sobre compra" -> "Devolución sobre compra";
            case "Devolución sobre venta" -> "Devolución sobre venta";
            case "Cobro a cliente" -> "Cobro a cliente";
            case "Pago a proveedor" -> "Pago a proveedor";
            case "Gasto" -> "Gasto";
            case "Gasto sobre compra" -> "Gasto sobre compra";
            case "Pago por anticipado" -> "Pago por anticipado";
            case "Póliza de seguro" -> "Póliza de seguro pagada por anticipado";
            case "Compra de activo" -> "Compra de activo";
            case "Préstamo bancario" -> "Préstamo bancario";
            case "Aporte de capital" -> "Aporte de capital";
            default -> "";
        };
    }

    private JPanel crearAreaTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(14, 16, 16, 16)
        ));

        JPanel encabezado = new JPanel(new BorderLayout(12, 0));
        encabezado.setOpaque(false);

        JPanel tituloPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tituloPanel.setOpaque(false);

        JLabel marca = new JLabel("●");
        marca.setForeground(COLOR_PRIMARIO);
        marca.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel titulo = new JLabel("Detalle del asiento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 7, 0, 0));

        tituloPanel.add(marca);
        tituloPanel.add(titulo);

        encabezado.add(tituloPanel, BorderLayout.WEST);
        encabezado.add(crearBotonesLineas(), BorderLayout.EAST);

        modeloTabla = new DefaultTableModel(
                new Object[]{"Cuenta", "Debe", "Haber"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Cuenta.class : String.class;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(36);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tabla.getTableHeader().setBackground(new Color(248, 250, 252));
        tabla.getTableHeader().setForeground(COLOR_TEXTO);
        tabla.setGridColor(COLOR_BORDE);
        tabla.setSelectionBackground(new Color(219, 234, 254));
        tabla.setSelectionForeground(COLOR_TEXTO);
        tabla.setFillsViewportHeight(true);
        tabla.setShowVerticalLines(true);
        tabla.setShowHorizontalLines(true);

        cmbEditorCuenta = new JComboBox<>();
        cmbEditorCuenta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getColumnModel().getColumn(0)
                .setCellEditor(new DefaultCellEditor(cmbEditorCuenta));

        tabla.getColumnModel().getColumn(0).setPreferredWidth(500);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(140);

        modeloTabla.addTableModelListener(e -> actualizarTotales());

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(encabezado, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearBotonesLineas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);

        JButton btnSugerir = new JButton("Preparar operación / sugerir cuentas");
        configurarBotonPrincipal(btnSugerir);
        btnSugerir.addActionListener(e -> sugerirCuentas());

        btnAgregarLinea = new JButton("+ Agregar línea");
        configurarBotonPrincipal(btnAgregarLinea);
        btnAgregarLinea.addActionListener(e -> agregarLinea());

        JButton btnEliminar = new JButton("Eliminar línea");
        configurarBotonSecundario(btnEliminar);
        btnEliminar.addActionListener(e -> eliminarLinea());

        panel.add(btnSugerir);
        panel.add(btnAgregarLinea);
        panel.add(btnEliminar);

        return panel;
    }

    private void cargarCuentas() {
        try {
            cmbEditorCuenta.removeAllItems();

            for (Cuenta cuenta : cuentaDAO.listarCuentasMovimiento()) {
                if (cuenta != null && cuenta.isActivo() && cuenta.isPermiteMovimiento()) {
                    cmbEditorCuenta.addItem(cuenta);
                }
            }

            if (cmbEditorCuenta.getItemCount() == 0) {
                btnGuardar.setEnabled(false);
                btnAgregarLinea.setEnabled(false);
                throw new IllegalStateException(
                        "No existen cuentas de movimiento disponibles."
                );
            }

        } catch (Exception e) {
            if (btnGuardar != null) {
                btnGuardar.setEnabled(false);
            }
            if (btnAgregarLinea != null) {
                btnAgregarLinea.setEnabled(false);
            }

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(e),
                    "Error al cargar cuentas",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void sugerirCuentas() {
        try {
            detenerEdicion();

            String descripcion = txtConcepto.getText().trim();

            if (descripcion.isBlank()) {
                throw new IllegalArgumentException(
                        "Escribe primero la descripción de la operación."
                );
            }

            String tipo = sugerenciaAsientoService.detectarTipo(descripcion);
            String seleccion = cmbTipoOperacion == null ? "" : String.valueOf(cmbTipoOperacion.getSelectedItem());
            if ("Compra de mercadería".equals(seleccion)) tipo = SugerenciaAsientoService.COMPRA_INVENTARIO;
            else if ("Venta de mercadería".equals(seleccion)) tipo = SugerenciaAsientoService.VENTA_INVENTARIO;
            else if ("Devolución sobre compra".equals(seleccion)) {
                new DialogoDevolucion(this, "DEVOLUCION_COMPRA").setVisible(true);
                return;
            } else if ("Devolución sobre venta".equals(seleccion)) {
                new DialogoDevolucion(this, "DEVOLUCION_VENTA").setVisible(true);
                return;
            }
            else if ("Cobro a cliente".equals(seleccion)) {
                new DialogoCobroPago(this, "COBRO_CLIENTE").setVisible(true);
                return;
            } else if ("Pago a proveedor".equals(seleccion)) {
                new DialogoCobroPago(this, "PAGO_PROVEEDOR").setVisible(true);
                return;
            } else if ("Gasto".equals(seleccion)) {
                new DialogoGastoActivo(this, "GASTO").setVisible(true);
                return;
            } else if ("Gasto sobre compra".equals(seleccion)) {
                new DialogoGastoActivo(this, "GASTO_COMPRA").setVisible(true);
                return;
            } else if ("Pago por anticipado".equals(seleccion)) {
                new DialogoGastoActivo(this, "PAGO_ANTICIPADO").setVisible(true);
                return;
            } else if ("Póliza de seguro".equals(seleccion)) {
                new DialogoGastoActivo(this, "POLIZA_SEGURO").setVisible(true);
                return;
            } else if ("Compra de activo".equals(seleccion)) {
                new DialogoGastoActivo(this, "COMPRA_ACTIVO").setVisible(true);
                return;
            } else if ("Préstamo bancario".equals(seleccion)) {
                new DialogoFinanciamiento(this, "PRESTAMO").setVisible(true);
                return;
            } else if ("Aporte de capital".equals(seleccion)) {
                new DialogoFinanciamiento(this, "APORTE_CAPITAL").setVisible(true);
                return;
            }

            if (SugerenciaAsientoService.DEVOLUCION_COMPRA.equals(tipo)
                    || SugerenciaAsientoService.DEVOLUCION_VENTA.equals(tipo)) {
                throw new IllegalArgumentException(
                        "Las devoluciones no deben registrarse como una compra/venta normal. "
                        + "Usa Operaciones > Devoluciones para vincular la operación original "
                        + "y actualizar correctamente las capas PEPS."
                );
            }

            boolean esCompraAmbigua =
                    SugerenciaAsientoService.NORMAL.equals(tipo)
                    && descripcion.toLowerCase().contains("compra");

            if (esCompraAmbigua) {
                int respuesta = JOptionPane.showConfirmDialog(
                        this,
                        "¿Esta compra corresponde a mercadería/productos destinados a la venta?\n\n"
                        + "Sí: afectará el Kardex PEPS.\n"
                        + "No: se registrará solamente como operación contable.",
                        "Clasificar compra",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (respuesta == JOptionPane.CANCEL_OPTION
                        || respuesta == JOptionPane.CLOSED_OPTION) {
                    return;
                }

                if (respuesta == JOptionPane.YES_OPTION) {
                    tipo = SugerenciaAsientoService.COMPRA_INVENTARIO;
                }
            }

            List<DetalleAsiento> sugerencias;

            if (SugerenciaAsientoService.COMPRA_INVENTARIO.equals(tipo)
                    || SugerenciaAsientoService.VENTA_INVENTARIO.equals(tipo)) {

                BigDecimal cantidad =
                        sugerenciaAsientoService.extraerCantidadInventario(descripcion);

                BigDecimal precio =
                        sugerenciaAsientoService.extraerPrecioUnitario(descripcion);

                BigDecimal montoTotal =
                        sugerenciaAsientoService.extraerMontoTotalInventario(descripcion);

                validarDatosDetectados(cantidad, precio, montoTotal);

                if (!capturarDatosInventario(tipo, cantidad, precio, montoTotal)) {
                    return;
                }

                if (SugerenciaAsientoService.COMPRA_INVENTARIO.equals(tipo)) {
                    sugerencias = sugerenciaAsientoService.sugerirCompraInventario(
                            cantidadInventario,
                            precioUnitarioInventario,
                            distribucionPagoInventario,
                            cuentaCreditoInventario
                    );
                } else {
                    sugerencias = sugerenciaAsientoService.sugerirVentaInventario(
                            cantidadInventario,
                            precioUnitarioInventario,
                            distribucionPagoInventario
                    );
                }

            } else {
                limpiarDatosInventario();
                sugerencias = sugerenciaAsientoService.sugerir(descripcion);
            }

            modeloTabla.setRowCount(0);

            for (DetalleAsiento detalle : sugerencias) {
                Cuenta cuenta = buscarCuentaEnCombo(detalle.getIdCuenta());

                if (cuenta == null) {
                    throw new IllegalStateException(
                            "No se encontró en el catálogo la cuenta "
                            + detalle.getDescripcion()
                    );
                }

                modeloTabla.addRow(new Object[]{
                    cuenta,
                    formatear(detalle.getDebe()),
                    formatear(detalle.getHaber())
                });
            }

            actualizarTotales();

            if (tipoInventario != null) {
                lblEstadoInventario.setText(
                        ("COMPRA".equals(tipoInventario)
                                ? "Compra de mercadería"
                                : "Venta de mercadería")
                        + " · "
                        + productoInventario
                        + " · Cantidad: "
                        + cantidadInventario.stripTrailingZeros().toPlainString()
                        + " · Valor unitario de la operación: $"
                        + precioUnitarioInventario.setScale(2).toPlainString()
                        + " · Al registrar se actualizará Kardex PEPS."
                );
                lblEstadoInventario.setForeground(COLOR_EXITO);
            } else {
                lblEstadoInventario.setText(
                        "Asiento normal: no genera movimiento de Kardex."
                );
                lblEstadoInventario.setForeground(COLOR_SECUNDARIO);
            }

        } catch (Exception e) {
            limpiarDatosInventario();
            modeloTabla.setRowCount(0);
            actualizarTotales();

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(e),
                    "No se pudieron sugerir las cuentas",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void validarDatosDetectados(
            BigDecimal cantidad,
            BigDecimal precio,
            BigDecimal monto
    ) {
        if (cantidad != null) {
            if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad escrita debe ser mayor que cero."
                );
            }

            if (cantidad.abs().compareTo(MAX_CANTIDAD) > 0) {
                throw new IllegalArgumentException(
                        "La cantidad supera el límite permitido por el sistema."
                );
            }
        }

        if (precio != null) {
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "El precio o costo unitario debe ser mayor que cero."
                );
            }

            if (precio.abs().compareTo(MAX_MONTO) > 0) {
                throw new IllegalArgumentException(
                        "El precio unitario supera el límite permitido."
                );
            }
        }

        if (monto != null) {
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "El monto de la operación debe ser mayor que cero."
                );
            }

            if (monto.abs().compareTo(MAX_MONTO) > 0) {
                throw new IllegalArgumentException(
                        "El monto supera el límite permitido por el sistema."
                );
            }
        }
    }

    private boolean tieneParteDecimal(BigDecimal valor) {
        return valor != null
                && valor.stripTrailingZeros().scale() > 0;
    }

    private boolean capturarDatosInventario(
            String tipo,
            BigDecimal cantidadDetectada,
            BigDecimal precioDetectado,
            BigDecimal montoTotalDetectado
    ) {
        List<Producto> productos = productoDAO.listarProductosActivos();

        if (productos == null || productos.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay productos activos. Registra primero un producto.",
                    "Sin productos",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        boolean esCompra = "COMPRA".equals(tipo);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setToolTipText("Busca por código o nombre");

        JComboBox<Producto> cmbProducto = new JComboBox<>();
        cmbProducto.setPreferredSize(new Dimension(360, 34));
        cargarProductosFiltrados(cmbProducto, productos, "");

        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                SwingUtilities.invokeLater(() ->
                        cargarProductosFiltrados(
                                cmbProducto,
                                productos,
                                txtBuscar.getText()
                        )
                );
            }
        });

        JTextField txtCantidadOperacion = new JTextField(
                cantidadDetectada == null
                        ? ""
                        : cantidadDetectada.stripTrailingZeros().toPlainString()
        );

        JTextField txtPrecio = new JTextField(
                precioDetectado == null
                        ? ""
                        : precioDetectado.setScale(2, RoundingMode.HALF_UP).toPlainString()
        );

        JTextField txtSubtotalCompra = new JTextField();
        JTextField txtTotalFacturaCompra = new JTextField();

        /*
         * Si el concepto trae un monto pero no trae precio unitario, lo
         * dejamos como total de factura sugerido. El usuario puede decidir
         * si corresponde al subtotal neto o al total con IVA.
         */
        if (esCompra
                && (precioDetectado == null || precioDetectado.compareTo(BigDecimal.ZERO) <= 0)
                && montoTotalDetectado != null
                && montoTotalDetectado.compareTo(BigDecimal.ZERO) > 0) {
            txtTotalFacturaCompra.setText(
                    montoTotalDetectado.setScale(2, RoundingMode.HALF_UP).toPlainString()
            );
        }

        txtCantidadOperacion.setToolTipText(
                "Cantidad física de unidades que entrarán o saldrán del Kardex."
        );

        txtPrecio.setToolTipText(
                esCompra
                        ? "Costo unitario neto de esta compra. Si no lo conoces, deja este campo vacío e ingresa el subtotal sin IVA."
                        : "Precio unitario de esta venta."
        );

        txtSubtotalCompra.setToolTipText(
                "Subtotal de mercadería antes de IVA. Si lo completas, ContaProMax calcula el costo unitario."
        );

        txtTotalFacturaCompra.setToolTipText(
                "Total de la factura con IVA incluido. Si es el único importe disponible, ContaProMax recupera la base neta y el costo unitario."
        );

        JLabel lblReferencia = new JLabel(
                "Selecciona un producto para consultar su valor de referencia."
        );
        lblReferencia.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblReferencia.setForeground(COLOR_SECUNDARIO);

        JLabel lblCalculo = new JLabel("Completa cantidad y costo/subtotal");
        lblCalculo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCalculo.setForeground(COLOR_PRIMARIO);

        JComboBox<String> cmbFormaPago =
                new JComboBox<>(new String[]{"EFECTIVO", "BANCO", "CREDITO", "MIXTO"});

        String formaSugerida =
                sugerenciaAsientoService.detectarFormaPago(txtConcepto.getText());

        cmbFormaPago.setSelectedItem(formaSugerida);

        JComboBox<String> cmbCuentaCredito = new JComboBox<>(
                esCompra
                        ? new String[]{"PROVEEDORES", "ACREEDORES VARIOS"}
                        : new String[]{"CLIENTES"}
        );

        JComboBox<String> cmbMedioInmediato = new JComboBox<>(
                new String[]{"BANCO", "EFECTIVO"}
        );

        if (txtConcepto.getText() != null
                && txtConcepto.getText().toLowerCase().contains("cheque")) {
            cmbMedioInmediato.setSelectedItem("BANCO");
        }

        BigDecimal porcentajeDetectado =
                sugerenciaAsientoService.extraerPorcentajePagoInmediato(
                        txtConcepto.getText()
                );

        JTextField txtPorcentajeInmediato = new JTextField(
                porcentajeDetectado == null
                        ? "50"
                        : porcentajeDetectado.stripTrailingZeros().toPlainString()
        );

        JLabel lblDistribucion = new JLabel(" ");
        lblDistribucion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDistribucion.setForeground(COLOR_SECUNDARIO);

        final ResultadoIVA[] resultadoActual = new ResultadoIVA[1];
        final BigDecimal[] cantidadActual = new BigDecimal[1];
        final BigDecimal[] precioActual = new BigDecimal[1];

        Runnable actualizarCalculo = () -> {
            Producto p = (Producto) cmbProducto.getSelectedItem();

            if (p == null) {
                lblReferencia.setText("No hay coincidencias.");
                lblCalculo.setText("Sin producto");
                resultadoActual[0] = null;
                return;
            }

            BigDecimal referencia = esCompra
                    ? p.getCostoCompra()
                    : p.getPrecioVenta();

            lblReferencia.setText(
                    (esCompra
                            ? "Costo de compra de referencia: $"
                            : "Precio de venta de referencia: $")
                    + (referencia == null
                        ? "0.00"
                        : referencia.setScale(2, RoundingMode.HALF_UP).toPlainString())
                    + "  (solo informativo)"
            );

            try {
                BigDecimal cantidad = leerDecimalOpcional(
                        txtCantidadOperacion.getText(),
                        6
                );

                if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
                    lblCalculo.setText("Ingresa una cantidad mayor que cero");
                    resultadoActual[0] = null;
                    return;
                }

                BigDecimal precio = leerDecimalOpcional(txtPrecio.getText(), 6);
                BigDecimal subtotalEscrito = esCompra
                        ? leerDecimalOpcional(txtSubtotalCompra.getText(), 2)
                        : null;

                BigDecimal totalFacturaEscrito = esCompra
                        ? leerDecimalOpcional(txtTotalFacturaCompra.getText(), 2)
                        : null;

                BigDecimal base;

                if (esCompra) {
                    if (precio != null && precio.compareTo(BigDecimal.ZERO) > 0) {
                        base = cantidad.multiply(precio)
                                .setScale(2, RoundingMode.HALF_UP);

                        if (subtotalEscrito != null
                                && subtotalEscrito.compareTo(BigDecimal.ZERO) > 0
                                && base.subtract(subtotalEscrito).abs()
                                        .compareTo(new BigDecimal("0.01")) > 0) {
                            lblCalculo.setText(
                                    "Cantidad × costo no coincide con el subtotal escrito"
                            );
                            resultadoActual[0] = null;
                            return;
                        }

                        ResultadoIVA rDesdeBase =
                                calculoIVAService.calcularSobreBase(base);

                        if (totalFacturaEscrito != null
                                && totalFacturaEscrito.compareTo(BigDecimal.ZERO) > 0
                                && rDesdeBase.getTotal()
                                        .subtract(totalFacturaEscrito)
                                        .abs()
                                        .compareTo(new BigDecimal("0.01")) > 0) {
                            lblCalculo.setText(
                                    "Cantidad × costo no coincide con el total de factura escrito"
                            );
                            resultadoActual[0] = null;
                            return;
                        }

                        resultadoActual[0] = rDesdeBase;

                    } else if (subtotalEscrito != null
                            && subtotalEscrito.compareTo(BigDecimal.ZERO) > 0) {
                        base = subtotalEscrito.setScale(2, RoundingMode.HALF_UP);
                        precio = base.divide(cantidad, 6, RoundingMode.HALF_UP);
                        resultadoActual[0] =
                                calculoIVAService.calcularSobreBase(base);

                        if (totalFacturaEscrito != null
                                && totalFacturaEscrito.compareTo(BigDecimal.ZERO) > 0
                                && resultadoActual[0].getTotal()
                                        .subtract(totalFacturaEscrito)
                                        .abs()
                                        .compareTo(new BigDecimal("0.01")) > 0) {
                            lblCalculo.setText(
                                    "Subtotal e importe total no coinciden"
                            );
                            resultadoActual[0] = null;
                            return;
                        }

                    } else if (totalFacturaEscrito != null
                            && totalFacturaEscrito.compareTo(BigDecimal.ZERO) > 0) {
                        resultadoActual[0] =
                                calculoIVAService.calcularDesdeTotalConIva(
                                        totalFacturaEscrito
                                );
                        base = resultadoActual[0].getSubtotal();
                        precio = base.divide(cantidad, 6, RoundingMode.HALF_UP);

                    } else {
                        lblCalculo.setText(
                                "Ingresa costo unitario, subtotal sin IVA o total de factura"
                        );
                        resultadoActual[0] = null;
                        return;
                    }
                } else {
                    if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
                        lblCalculo.setText("Ingresa el precio unitario");
                        resultadoActual[0] = null;
                        return;
                    }

                    base = cantidad.multiply(precio)
                            .setScale(2, RoundingMode.HALF_UP);

                    resultadoActual[0] =
                            calculoIVAService.calcular(base);
                }

                cantidadActual[0] = cantidad;
                precioActual[0] = precio;

                ResultadoIVA r = resultadoActual[0];

                lblCalculo.setText(
                        "Subtotal: $" + r.getSubtotal().setScale(2).toPlainString()
                        + "   |   IVA: $" + r.getIva().setScale(2).toPlainString()
                        + "   |   Total: $" + r.getTotal().setScale(2).toPlainString()
                );

                if ("MIXTO".equals(cmbFormaPago.getSelectedItem())) {
                    try {
                        BigDecimal porcentaje =
                                leerDecimalOpcional(
                                        txtPorcentajeInmediato.getText(),
                                        2
                                );

                        if (porcentaje == null
                                || porcentaje.compareTo(BigDecimal.ZERO) < 0
                                || porcentaje.compareTo(new BigDecimal("100")) > 0) {
                            lblDistribucion.setText(
                                    "El porcentaje inmediato debe estar entre 0 y 100."
                            );
                        } else {
                            BigDecimal pagoAhora = r.getTotal()
                                    .multiply(porcentaje)
                                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                            BigDecimal credito = r.getTotal()
                                    .subtract(pagoAhora)
                                    .setScale(2, RoundingMode.HALF_UP);

                            lblDistribucion.setText(
                                    porcentaje.stripTrailingZeros().toPlainString()
                                    + "% " + cmbMedioInmediato.getSelectedItem()
                                    + ": $" + pagoAhora.toPlainString()
                                    + "   |   Crédito: $" + credito.toPlainString()
                            );
                        }
                    } catch (Exception ex) {
                        lblDistribucion.setText("Revisa el porcentaje del pago inmediato.");
                    }
                } else {
                    lblDistribucion.setText(" ");
                }

            } catch (Exception ex) {
                lblCalculo.setText("Revisa cantidad, costo y subtotal");
                resultadoActual[0] = null;
            }
        };

        Runnable actualizarControlesPago = () -> {
            String forma = (String) cmbFormaPago.getSelectedItem();
            boolean credito = "CREDITO".equals(forma) || "MIXTO".equals(forma);
            boolean mixto = "MIXTO".equals(forma);

            cmbCuentaCredito.setEnabled(credito);
            cmbMedioInmediato.setEnabled(mixto);
            txtPorcentajeInmediato.setEnabled(mixto);
            actualizarCalculo.run();
        };

        cmbProducto.addActionListener(e -> actualizarCalculo.run());
        cmbFormaPago.addActionListener(e -> actualizarControlesPago.run());
        cmbMedioInmediato.addActionListener(e -> actualizarCalculo.run());

        javax.swing.event.DocumentListener listenerCalculo =
                new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarCalculo.run(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarCalculo.run(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarCalculo.run(); }
        };

        txtCantidadOperacion.getDocument().addDocumentListener(listenerCalculo);
        txtPrecio.getDocument().addDocumentListener(listenerCalculo);
        txtSubtotalCompra.getDocument().addDocumentListener(listenerCalculo);
        txtTotalFacturaCompra.getDocument().addDocumentListener(listenerCalculo);
        txtPorcentajeInmediato.getDocument().addDocumentListener(listenerCalculo);

        if (cmbProducto.getItemCount() > 0) {
            cmbProducto.setSelectedIndex(0);
        }

        actualizarControlesPago.run();

        configurarCampoKardex(txtBuscar);
        configurarCampoKardex(txtCantidadOperacion);
        configurarCampoKardex(txtPrecio);
        configurarCampoKardex(txtSubtotalCompra);
        configurarCampoKardex(txtTotalFacturaCompra);
        configurarCampoKardex(txtPorcentajeInmediato);

        configurarComboKardex(cmbProducto);
        configurarComboKardex(cmbFormaPago);
        configurarComboKardex(cmbCuentaCredito);
        configurarComboKardex(cmbMedioInmediato);

        JPanel tarjetaProducto = crearTarjetaKardex(
                "Producto e importes",
                esCompra
                        ? "Selecciona la mercadería y completa el costo de la compra."
                        : "Selecciona la mercadería y completa los datos de la venta."
        );

        JPanel camposProducto = new JPanel(new GridBagLayout());
        camposProducto.setOpaque(false);

        GridBagConstraints gp = new GridBagConstraints();
        gp.fill = GridBagConstraints.HORIZONTAL;
        gp.anchor = GridBagConstraints.WEST;
        gp.insets = new Insets(6, 0, 6, 12);
        gp.weightx = 1;

        int filaProducto = 0;
        agregarCampoKardex(camposProducto, gp, filaProducto++, "Buscar producto", txtBuscar);
        agregarCampoKardex(camposProducto, gp, filaProducto++, "Producto", cmbProducto);
        agregarCampoKardex(camposProducto, gp, filaProducto++, "Cantidad de unidades", txtCantidadOperacion);

        agregarCampoKardex(
                camposProducto,
                gp,
                filaProducto++,
                esCompra ? "Costo unitario neto ($)" : "Precio unitario de venta ($)",
                txtPrecio
        );

        if (esCompra) {
            agregarCampoKardex(
                    camposProducto,
                    gp,
                    filaProducto++,
                    "Subtotal sin IVA ($)",
                    txtSubtotalCompra
            );
            agregarCampoKardex(
                    camposProducto,
                    gp,
                    filaProducto++,
                    "Total factura con IVA ($)",
                    txtTotalFacturaCompra
            );
        }

        lblReferencia.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JPanel contenidoProducto = new JPanel(new BorderLayout(0, 8));
        contenidoProducto.setOpaque(false);
        contenidoProducto.add(camposProducto, BorderLayout.CENTER);
        contenidoProducto.add(lblReferencia, BorderLayout.SOUTH);
        tarjetaProducto.add(contenidoProducto, BorderLayout.CENTER);

        JPanel tarjetaCalculo = new JPanel(new BorderLayout(12, 0));
        tarjetaCalculo.setBackground(new Color(239, 246, 255));
        tarjetaCalculo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(191, 219, 254)),
                BorderFactory.createEmptyBorder(13, 15, 13, 15)
        ));

        JLabel iconoCalculo = new JLabel("$", JLabel.CENTER);
        iconoCalculo.setOpaque(true);
        iconoCalculo.setBackground(new Color(219, 234, 254));
        iconoCalculo.setForeground(COLOR_PRIMARIO);
        iconoCalculo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconoCalculo.setPreferredSize(new Dimension(42, 42));

        JPanel textosCalculo = new JPanel();
        textosCalculo.setOpaque(false);
        textosCalculo.setLayout(new BoxLayout(textosCalculo, BoxLayout.Y_AXIS));

        JLabel tituloCalculo = new JLabel("Cálculo de la operación");
        tituloCalculo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tituloCalculo.setForeground(COLOR_TEXTO);

        lblCalculo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCalculo.setForeground(COLOR_PRIMARIO);

        textosCalculo.add(tituloCalculo);
        textosCalculo.add(Box.createVerticalStrut(4));
        textosCalculo.add(lblCalculo);

        tarjetaCalculo.add(iconoCalculo, BorderLayout.WEST);
        tarjetaCalculo.add(textosCalculo, BorderLayout.CENTER);

        JPanel tarjetaPago = crearTarjetaKardex(
                esCompra ? "Forma de pago" : "Forma de cobro",
                "Define cómo se distribuirá el total de la operación."
        );

        JPanel camposPago = new JPanel(new GridBagLayout());
        camposPago.setOpaque(false);

        GridBagConstraints gg = new GridBagConstraints();
        gg.fill = GridBagConstraints.HORIZONTAL;
        gg.anchor = GridBagConstraints.WEST;
        gg.insets = new Insets(6, 0, 6, 12);
        gg.weightx = 1;

        int filaPago = 0;
        agregarCampoKardex(camposPago, gg, filaPago++, "Forma de pago/cobro", cmbFormaPago);
        agregarCampoKardex(
                camposPago,
                gg,
                filaPago++,
                esCompra ? "Cuenta por pagar" : "Cuenta por cobrar",
                cmbCuentaCredito
        );
        agregarCampoKardex(
                camposPago,
                gg,
                filaPago++,
                esCompra ? "Medio del pago inmediato" : "Medio del cobro inmediato",
                cmbMedioInmediato
        );
        agregarCampoKardex(
                camposPago,
                gg,
                filaPago++,
                esCompra ? "% pagado inmediatamente" : "% cobrado inmediatamente",
                txtPorcentajeInmediato
        );

        tarjetaPago.add(camposPago, BorderLayout.CENTER);

        JPanel panelDistribucion = new JPanel(new BorderLayout(10, 0));
        panelDistribucion.setBackground(new Color(248, 250, 252));
        panelDistribucion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(11, 13, 11, 13)
        ));

        JLabel lblTituloDistribucion = new JLabel("Distribución");
        lblTituloDistribucion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTituloDistribucion.setForeground(COLOR_TEXTO);

        lblDistribucion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDistribucion.setForeground(COLOR_SECUNDARIO);

        panelDistribucion.add(lblTituloDistribucion, BorderLayout.WEST);
        panelDistribucion.add(lblDistribucion, BorderLayout.CENTER);

        JPanel cuerpoDialogo = new JPanel();
        cuerpoDialogo.setBackground(COLOR_FONDO);
        cuerpoDialogo.setLayout(new BoxLayout(cuerpoDialogo, BoxLayout.Y_AXIS));
        cuerpoDialogo.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        tarjetaProducto.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaCalculo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaPago.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelDistribucion.setAlignmentX(Component.LEFT_ALIGNMENT);

        cuerpoDialogo.add(tarjetaProducto);
        cuerpoDialogo.add(Box.createVerticalStrut(12));
        cuerpoDialogo.add(tarjetaCalculo);
        cuerpoDialogo.add(Box.createVerticalStrut(12));
        cuerpoDialogo.add(tarjetaPago);
        cuerpoDialogo.add(Box.createVerticalStrut(12));
        cuerpoDialogo.add(panelDistribucion);

        JScrollPane scrollDialogo = new JScrollPane(cuerpoDialogo);
        scrollDialogo.setBorder(null);
        scrollDialogo.getViewport().setBackground(COLOR_FONDO);
        scrollDialogo.getVerticalScrollBar().setUnitIncrement(16);
        scrollDialogo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JDialog dialogoDatos = new JDialog(
                this,
                "Datos para Kardex PEPS",
                ModalityType.APPLICATION_MODAL
        );
        dialogoDatos.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogoDatos.setLayout(new BorderLayout());
        dialogoDatos.getContentPane().setBackground(COLOR_FONDO);

        JPanel encabezadoDialogo = new JPanel(new BorderLayout(14, 0));
        encabezadoDialogo.setBackground(Color.WHITE);
        encabezadoDialogo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE),
                BorderFactory.createEmptyBorder(17, 22, 17, 22)
        ));

        JLabel iconoDialogo = new JLabel("▦", JLabel.CENTER);
        iconoDialogo.setOpaque(true);
        iconoDialogo.setBackground(new Color(219, 234, 254));
        iconoDialogo.setForeground(COLOR_PRIMARIO);
        iconoDialogo.setFont(new Font("Segoe UI Symbol", Font.BOLD, 21));
        iconoDialogo.setPreferredSize(new Dimension(48, 48));

        JPanel textosDialogo = new JPanel();
        textosDialogo.setOpaque(false);
        textosDialogo.setLayout(new BoxLayout(textosDialogo, BoxLayout.Y_AXIS));

        JLabel tituloDialogo = new JLabel(
                esCompra ? "Datos de compra para Kardex PEPS" : "Datos de venta para Kardex PEPS"
        );
        tituloDialogo.setFont(new Font("Segoe UI", Font.BOLD, 21));
        tituloDialogo.setForeground(COLOR_TEXTO);

        JLabel subtituloDialogo = new JLabel(
                "Completa los datos que actualizarán inventario, IVA y distribución del pago."
        );
        subtituloDialogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtituloDialogo.setForeground(COLOR_SECUNDARIO);

        textosDialogo.add(tituloDialogo);
        textosDialogo.add(Box.createVerticalStrut(4));
        textosDialogo.add(subtituloDialogo);

        JLabel distintivo = new JLabel(esCompra ? "COMPRA" : "VENTA");
        distintivo.setOpaque(true);
        distintivo.setBackground(esCompra ? new Color(220, 252, 231) : new Color(239, 246, 255));
        distintivo.setForeground(esCompra ? COLOR_EXITO : COLOR_PRIMARIO);
        distintivo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        distintivo.setBorder(BorderFactory.createEmptyBorder(7, 11, 7, 11));

        encabezadoDialogo.add(iconoDialogo, BorderLayout.WEST);
        encabezadoDialogo.add(textosDialogo, BorderLayout.CENTER);
        encabezadoDialogo.add(distintivo, BorderLayout.EAST);

        final boolean[] aceptado = {false};

        JPanel pieDialogo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        pieDialogo.setBackground(Color.WHITE);
        pieDialogo.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE));

        JButton btnCancelarDatos = new JButton("Cancelar");
        configurarBotonSecundario(btnCancelarDatos);
        btnCancelarDatos.addActionListener(e -> dialogoDatos.dispose());

        JButton btnAceptarDatos = new JButton("Aceptar y preparar");
        configurarBotonPrincipal(btnAceptarDatos);
        btnAceptarDatos.addActionListener(e -> {
            aceptado[0] = true;
            dialogoDatos.dispose();
        });

        pieDialogo.add(btnCancelarDatos);
        pieDialogo.add(btnAceptarDatos);

        dialogoDatos.add(encabezadoDialogo, BorderLayout.NORTH);
        dialogoDatos.add(scrollDialogo, BorderLayout.CENTER);
        dialogoDatos.add(pieDialogo, BorderLayout.SOUTH);

        Rectangle areaDialogo = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        int anchoDialogo = Math.min(940, Math.max(780, (int) (areaDialogo.width * 0.66)));
        int altoDialogo = Math.min(800, Math.max(650, (int) (areaDialogo.height * 0.82)));

        dialogoDatos.setSize(anchoDialogo, altoDialogo);
        dialogoDatos.setMinimumSize(new Dimension(760, 620));
        dialogoDatos.setResizable(true);
        dialogoDatos.setLocationRelativeTo(this);
        dialogoDatos.getRootPane().setDefaultButton(btnAceptarDatos);
        dialogoDatos.setVisible(true);

        if (!aceptado[0]) {
            return false;
        }

        actualizarCalculo.run();

        Producto producto = (Producto) cmbProducto.getSelectedItem();

        if (producto == null) {
            throw new IllegalArgumentException(
                    "Busca y selecciona un producto válido."
            );
        }

        if (resultadoActual[0] == null
                || cantidadActual[0] == null
                || precioActual[0] == null) {
            throw new IllegalArgumentException(
                    esCompra
                            ? "Completa una cantidad válida y al menos uno de estos datos: costo unitario, subtotal sin IVA o total de factura."
                            : "Completa una cantidad y un precio unitario válidos."
            );
        }

        BigDecimal cantidad = cantidadActual[0]
                .setScale(6, RoundingMode.HALF_UP);

        BigDecimal precioOperacion = precioActual[0]
                .setScale(6, RoundingMode.HALF_UP);

        if (cantidad.compareTo(MAX_CANTIDAD) > 0) {
            throw new IllegalArgumentException(
                    "La cantidad supera el límite permitido."
            );
        }

        String forma = (String) cmbFormaPago.getSelectedItem();
        DistribucionPago distribucion = null;

        BigDecimal total = resultadoActual[0].getTotal()
                .setScale(2, RoundingMode.HALF_UP);

        if ("EFECTIVO".equals(forma)) {
            distribucion = new DistribucionPago(
                    total,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        } else if ("BANCO".equals(forma)) {
            distribucion = new DistribucionPago(
                    BigDecimal.ZERO,
                    total,
                    BigDecimal.ZERO
            );
        } else if ("CREDITO".equals(forma)) {
            distribucion = new DistribucionPago(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    total
            );
        } else if ("MIXTO".equals(forma)) {
            BigDecimal porcentaje = leerDecimalOpcional(
                    txtPorcentajeInmediato.getText(),
                    2
            );

            if (porcentaje == null
                    || porcentaje.compareTo(BigDecimal.ZERO) <= 0
                    || porcentaje.compareTo(new BigDecimal("100")) >= 0) {
                throw new IllegalArgumentException(
                        esCompra
                                ? "En un pago mixto el porcentaje inmediato debe ser mayor que 0 y menor que 100."
                                : "En un cobro mixto el porcentaje inmediato debe ser mayor que 0 y menor que 100."
                );
            }

            BigDecimal inmediato = total
                    .multiply(porcentaje)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            BigDecimal credito = total
                    .subtract(inmediato)
                    .setScale(2, RoundingMode.HALF_UP);

            if ("BANCO".equals(cmbMedioInmediato.getSelectedItem())) {
                distribucion = new DistribucionPago(
                        BigDecimal.ZERO,
                        inmediato,
                        credito
                );
            } else {
                distribucion = new DistribucionPago(
                        inmediato,
                        BigDecimal.ZERO,
                        credito
                );
            }
        }

        tipoInventario = tipo;
        productoInventario = producto;
        cantidadInventario = cantidad;
        precioUnitarioInventario = precioOperacion;
        formaPagoInventario = forma;
        cuentaCreditoInventario =
                ("CREDITO".equals(forma) || "MIXTO".equals(forma))
                        ? (String) cmbCuentaCredito.getSelectedItem()
                        : null;
        distribucionPagoInventario = distribucion;

        return true;
    }

    private BigDecimal leerDecimalOpcional(String texto, int escala) {
        if (texto == null || texto.trim().isBlank()) {
            return null;
        }

        String limpio = texto.trim();

        if (limpio.contains(",") && limpio.contains(".")) {
            throw new IllegalArgumentException(
                    "Usa punto o coma decimal, no ambos."
            );
        }

        limpio = limpio.replace(',', '.');

        try {
            return new BigDecimal(limpio)
                    .setScale(escala, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor numérico no válido.");
        }
    }

    private void cargarProductosFiltrados(
            JComboBox<Producto> combo,
            List<Producto> productos,
            String filtro
    ) {
        Producto seleccionado = (Producto) combo.getSelectedItem();
        Integer idSeleccionado = seleccionado == null
                ? null
                : seleccionado.getIdProducto();

        String criterio = filtro == null
                ? ""
                : filtro.trim().toLowerCase();

        combo.removeAllItems();

        for (Producto producto : productos) {
            String codigo = producto.getCodigo() == null
                    ? ""
                    : producto.getCodigo().toLowerCase();

            String nombre = producto.getNombre() == null
                    ? ""
                    : producto.getNombre().toLowerCase();

            if (criterio.isBlank()
                    || codigo.contains(criterio)
                    || nombre.contains(criterio)) {
                combo.addItem(producto);
            }
        }

        if (idSeleccionado != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                Producto p = combo.getItemAt(i);
                if (p.getIdProducto() == idSeleccionado) {
                    combo.setSelectedIndex(i);
                    return;
                }
            }
        }

        if (combo.getItemCount() > 0) {
            combo.setSelectedIndex(0);
        }
    }

    private JPanel crearTarjetaKardex(
            String titulo,
            String subtitulo
    ) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(15, 17, 15, 17)
        ));

        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(COLOR_TEXTO);

        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitulo.setForeground(COLOR_SECUNDARIO);

        cabecera.add(lblTitulo);
        cabecera.add(Box.createVerticalStrut(3));
        cabecera.add(lblSubtitulo);

        tarjeta.add(cabecera, BorderLayout.NORTH);
        return tarjeta;
    }

    private void agregarCampoKardex(
            JPanel panel,
            GridBagConstraints base,
            int fila,
            String etiqueta,
            Component componente
    ) {
        GridBagConstraints gLabel = (GridBagConstraints) base.clone();
        gLabel.gridx = 0;
        gLabel.gridy = fila;
        gLabel.weightx = 0;
        gLabel.fill = GridBagConstraints.NONE;
        gLabel.anchor = GridBagConstraints.WEST;
        gLabel.insets = new Insets(6, 0, 6, 18);

        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(COLOR_TEXTO);
        panel.add(label, gLabel);

        GridBagConstraints gCampo = (GridBagConstraints) base.clone();
        gCampo.gridx = 1;
        gCampo.gridy = fila;
        gCampo.weightx = 1;
        gCampo.fill = GridBagConstraints.HORIZONTAL;
        gCampo.insets = new Insets(6, 0, 6, 0);

        if (componente instanceof JComponent jc) {
            jc.setPreferredSize(new Dimension(420, 38));
            jc.setMinimumSize(new Dimension(220, 38));
        }

        panel.add(componente, gCampo);
    }

    private void configurarCampoKardex(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setBackground(Color.WHITE);
        campo.setForeground(COLOR_TEXTO);
        campo.setPreferredSize(new Dimension(420, 38));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
    }

    private void configurarComboKardex(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        combo.setBackground(Color.WHITE);
        combo.setForeground(COLOR_TEXTO);
        combo.setPreferredSize(new Dimension(420, 38));
    }

    private void agregarCampo(
            JPanel panel,
            GridBagConstraints gbc,
            int fila,
            String etiqueta,
            java.awt.Component componente
    ) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.35;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(componente, gbc);
    }

    private void limpiarDatosInventario() {
        tipoInventario = null;
        productoInventario = null;
        cantidadInventario = null;
        precioUnitarioInventario = null;
        formaPagoInventario = null;
        cuentaCreditoInventario = null;
        distribucionPagoInventario = null;
    }

    private Cuenta buscarCuentaEnCombo(int idCuenta) {
        for (int i = 0; i < cmbEditorCuenta.getItemCount(); i++) {
            Cuenta cuenta = cmbEditorCuenta.getItemAt(i);
            if (cuenta != null && cuenta.getIdCuenta() == idCuenta) {
                return cuenta;
            }
        }
        return null;
    }

    private void agregarLinea() {
        if (cmbEditorCuenta == null || cmbEditorCuenta.getItemCount() == 0) {
            return;
        }

        modeloTabla.addRow(new Object[]{
            cmbEditorCuenta.getItemAt(0),
            "0.00",
            "0.00"
        });
    }

    private void eliminarLinea() {
        int fila = tabla.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona la línea que deseas eliminar.",
                    "Selecciona una línea",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (modeloTabla.getRowCount() <= 2) {
            JOptionPane.showMessageDialog(
                    this,
                    "El asiento debe conservar al menos dos líneas.",
                    "No se puede eliminar",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        detenerEdicion();
        modeloTabla.removeRow(tabla.convertRowIndexToModel(fila));
        actualizarTotales();
    }

    private JPanel crearPanelTs() {
        JPanel cont = new JPanel(new BorderLayout(0, 10));
        cont.setBackground(Color.WHITE);
        cont.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JLabel t = new JLabel("Vista previa de cuentas T");
        t.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t.setForeground(COLOR_TEXTO);

        JLabel ayuda = new JLabel("Se actualiza automáticamente con las líneas del asiento");
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ayuda.setForeground(COLOR_SECUNDARIO);

        encabezado.add(t, BorderLayout.WEST);
        encabezado.add(ayuda, BorderLayout.EAST);

        JPanel grilla = new JPanel(new GridLayout(0, 3, 12, 12));
        grilla.setOpaque(false);
        grilla.setName("GRILLA_TS");

        cont.add(encabezado, BorderLayout.NORTH);
        cont.add(grilla, BorderLayout.CENTER);

        actualizarTs(cont);
        return cont;
    }

    private void actualizarTs() {
        if (panelTs != null) actualizarTs(panelTs);
    }

    private void actualizarTs(JPanel cont) {
        JPanel grilla = null;
        for (java.awt.Component c : cont.getComponents()) {
            if (c instanceof JPanel p && "GRILLA_TS".equals(p.getName())) { grilla = p; break; }
        }
        if (grilla == null) return;
        grilla.removeAll();
        if (modeloTabla == null || modeloTabla.getRowCount() == 0) {
            JLabel vacio = new JLabel("Las cuentas T aparecerán aquí al preparar el asiento.");
            vacio.setForeground(COLOR_SECUNDARIO);
            grilla.add(vacio);
        } else {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                Object oc = modeloTabla.getValueAt(i, 0);
                if (!(oc instanceof Cuenta cuenta)) continue;
                BigDecimal debe; BigDecimal haber;
                try {
                    debe = leerDecimal(modeloTabla.getValueAt(i, 1));
                    haber = leerDecimal(modeloTabla.getValueAt(i, 2));
                } catch (Exception ex) {
                    debe = BigDecimal.ZERO; haber = BigDecimal.ZERO;
                }
                JPanel t = new JPanel(new BorderLayout(0, 7));
                t.setBackground(new Color(248, 250, 252));
                t.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(3, 1, 1, 1, COLOR_PRIMARIO),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
                JLabel nom = new JLabel("<html><b>" + cuenta.getCodigo() + " - " + cuenta.getNombre() + "</b></html>");
                nom.setHorizontalAlignment(JLabel.CENTER);
                t.add(nom, BorderLayout.NORTH);
                JPanel vals = new JPanel(new java.awt.GridLayout(2,2,4,4));
                vals.setOpaque(false);
                vals.add(new JLabel("Debe", JLabel.CENTER)); vals.add(new JLabel("Haber", JLabel.CENTER));
                vals.add(new JLabel("$" + formatear(debe), JLabel.CENTER));
                vals.add(new JLabel("$" + formatear(haber), JLabel.CENTER));
                t.add(vals, BorderLayout.CENTER);
                grilla.add(t);
            }
        }
        grilla.revalidate(); grilla.repaint(); cont.revalidate(); cont.repaint();
    }

    private JPanel crearResumen() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 24, 11));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(4, 16, 4, 16)
        ));

        lblTotalDebe = crearValorResumen("Total Debe: $0.00");
        lblTotalHaber = crearValorResumen("Total Haber: $0.00");
        lblDiferencia = crearValorResumen("Diferencia: $0.00");
        lblDiferencia.setForeground(COLOR_EXITO);

        panel.add(lblTotalDebe);
        panel.add(crearSeparadorResumen());
        panel.add(lblTotalHaber);
        panel.add(crearSeparadorResumen());
        panel.add(lblDiferencia);

        return panel;
    }

    private JComponent crearSeparadorResumen() {
        JPanel separador = new JPanel();
        separador.setBackground(COLOR_BORDE);
        separador.setPreferredSize(new Dimension(1, 22));
        return separador;
    }

    private void actualizarTotales() {
        if (modeloTabla == null || lblTotalDebe == null) {
            return;
        }

        BigDecimal debe = BigDecimal.ZERO;
        BigDecimal haber = BigDecimal.ZERO;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            try {
                debe = debe.add(leerDecimal(modeloTabla.getValueAt(i, 1)));
                haber = haber.add(leerDecimal(modeloTabla.getValueAt(i, 2)));
            } catch (Exception ignored) {
            }
        }

        debe = debe.setScale(2, RoundingMode.HALF_UP);
        haber = haber.setScale(2, RoundingMode.HALF_UP);
        BigDecimal diferencia =
                debe.subtract(haber).abs().setScale(2, RoundingMode.HALF_UP);

        lblTotalDebe.setText("Total Debe: $" + debe.toPlainString());
        lblTotalHaber.setText("Total Haber: $" + haber.toPlainString());
        lblDiferencia.setText("Diferencia: $" + diferencia.toPlainString());
        lblDiferencia.setForeground(
                diferencia.compareTo(BigDecimal.ZERO) == 0
                        ? COLOR_EXITO
                        : COLOR_ERROR
        );
        actualizarTs();
    }

    private JPanel crearBotonesFinales() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE),
                BorderFactory.createEmptyBorder(12, 28, 12, 28)
        ));

        JLabel indicacion = new JLabel(
                "Revisa que el Debe y el Haber estén cuadrados antes de registrar."
        );
        indicacion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        indicacion.setForeground(COLOR_SECUNDARIO);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);

        JButton btnCancelar = new JButton("Cancelar");
        configurarBotonSecundario(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar = new JButton("Registrar asiento");
        configurarBotonPrincipal(btnGuardar);
        btnGuardar.addActionListener(e -> guardar());

        acciones.add(btnCancelar);
        acciones.add(btnGuardar);

        panel.add(indicacion, BorderLayout.WEST);
        panel.add(acciones, BorderLayout.EAST);

        return panel;
    }

    private void guardar() {
        try {
            detenerEdicion();

            LocalDate fecha = obtenerFecha();
            String concepto = txtConcepto.getText().trim();

            if (concepto.isBlank()) {
                throw new IllegalArgumentException(
                        "El concepto del asiento es obligatorio."
                );
            }

            String tipoDetectado =
                    sugerenciaAsientoService.detectarTipo(concepto);

            // Las compras de activos son operaciones de la empresa, no mercadería
            // para la venta. Nunca deben crear una operación COMPRA de inventario ni
            // alterar Kardex/PEPS aunque el texto contenga la palabra "compra".
            if (SugerenciaAsientoService.COMPRA_ACTIVO.equals(tipoDetectado)) {
                limpiarDatosInventario();
            }

            if (SugerenciaAsientoService.DEVOLUCION_COMPRA.equals(tipoDetectado)
                    || SugerenciaAsientoService.DEVOLUCION_VENTA.equals(tipoDetectado)) {
                throw new IllegalArgumentException(
                        "Para devoluciones usa Operaciones > Devoluciones. "
                        + "Ese formulario enlaza la compra/venta original y protege el Kardex PEPS."
                );
            }

            boolean textoEsInventario =
                    SugerenciaAsientoService.COMPRA_INVENTARIO.equals(tipoDetectado)
                    || SugerenciaAsientoService.VENTA_INVENTARIO.equals(tipoDetectado);

            if (textoEsInventario && tipoInventario == null) {
                throw new IllegalArgumentException(
                        "Este asiento es una compra/venta de inventario. "
                        + "Primero pulsa 'Sugerir cuentas' para seleccionar "
                        + "el producto, confirmar unidades y precio."
                );
            }

            if (tipoInventario != null
                    && (productoInventario == null
                        || cantidadInventario == null
                        || precioUnitarioInventario == null)) {
                throw new IllegalArgumentException(
                        "Faltan los datos del producto para actualizar Kardex."
                );
            }

            List<DetalleAsiento> detalles = crearDetalles(concepto);

            btnGuardar.setEnabled(false);

            asientoManualService.registrar(
                    fecha,
                    concepto,
                    detalles,
                    tipoInventario,
                    productoInventario == null
                            ? null
                            : productoInventario.getIdProducto(),
                    cantidadInventario,
                    precioUnitarioInventario,
                    formaPagoInventario
            );

            String mensaje =
                    "Asiento registrado correctamente.\n"
                    + "El Libro Diario, Mayorización y balances se actualizarán automáticamente.";

            if (tipoInventario != null) {
                mensaje += "\nEl Kardex PEPS del producto también fue actualizado.";
            }

            JOptionPane.showMessageDialog(
                    this,
                    mensaje,
                    "Asiento registrado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(e),
                    "No se pudo registrar el asiento",
                    JOptionPane.ERROR_MESSAGE
            );

            btnGuardar.setEnabled(true);
        }
    }

    private List<DetalleAsiento> crearDetalles(String concepto) {
        if (modeloTabla.getRowCount() < 2) {
            throw new IllegalArgumentException(
                    "El asiento debe tener al menos dos líneas."
            );
        }

        List<DetalleAsiento> detalles = new ArrayList<>();

        for (int fila = 0; fila < modeloTabla.getRowCount(); fila++) {
            Object valorCuenta = modeloTabla.getValueAt(fila, 0);

            if (!(valorCuenta instanceof Cuenta)) {
                throw new IllegalArgumentException(
                        "Debe seleccionar una cuenta en la línea "
                        + (fila + 1) + "."
                );
            }

            Cuenta cuenta = (Cuenta) valorCuenta;
            BigDecimal debe = leerDecimal(modeloTabla.getValueAt(fila, 1));
            BigDecimal haber = leerDecimal(modeloTabla.getValueAt(fila, 2));

            if (debe.compareTo(BigDecimal.ZERO) < 0
                    || haber.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(
                        "Debe y Haber no pueden contener valores negativos."
                );
            }

            if (debe.compareTo(BigDecimal.ZERO) > 0
                    && haber.compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException(
                        "La línea " + (fila + 1)
                        + " no puede tener valores en Debe y Haber al mismo tiempo."
                );
            }

            if (debe.compareTo(BigDecimal.ZERO) == 0
                    && haber.compareTo(BigDecimal.ZERO) == 0) {
                throw new IllegalArgumentException(
                        "La línea " + (fila + 1)
                        + " debe contener un valor en Debe o Haber."
                );
            }

            DetalleAsiento detalle = new DetalleAsiento();
            detalle.setIdCuenta(cuenta.getIdCuenta());
            detalle.setDescripcion(concepto);
            detalle.setDebe(debe);
            detalle.setHaber(haber);
            detalles.add(detalle);
        }

        return detalles;
    }

    private LocalDate obtenerFecha() {
        Date fecha = (Date) spFecha.getValue();
        return Instant.ofEpochMilli(fecha.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private void detenerEdicion() {
        if (tabla != null && tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }
    }

    private BigDecimal leerDecimal(Object valor) {
        if (valor == null || valor.toString().trim().isBlank()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        String texto = valor.toString().trim().replace(",", ".");
        return new BigDecimal(texto).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal leerDecimalFlexible(String texto, int escala) {
        if (texto == null || texto.trim().isBlank()) {
            throw new IllegalArgumentException("Completa todos los datos de inventario.");
        }

        return new BigDecimal(
                texto.trim().replace(",", ".")
        ).setScale(escala, RoundingMode.HALF_UP);
    }

    private String formatear(BigDecimal monto) {
        return monto == null
                ? "0.00"
                : monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String obtenerMensajeError(Throwable error) {
        Throwable actual = error;
        String ultimo = "Ocurrió un error desconocido.";

        while (actual != null) {
            if (actual.getMessage() != null && !actual.getMessage().isBlank()) {
                ultimo = actual.getMessage();
            }
            actual = actual.getCause();
        }

        return ultimo;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(COLOR_TEXTO);
        return label;
    }

    private JLabel crearValorResumen(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(COLOR_TEXTO);
        return label;
    }

    private void configurarBotonPrincipal(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(Color.WHITE);
        boton.setBackground(COLOR_PRIMARIO);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void configurarBotonSecundario(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(COLOR_TEXTO);
        boton.setBackground(new Color(241, 245, 249));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(9, 16, 9, 16)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
