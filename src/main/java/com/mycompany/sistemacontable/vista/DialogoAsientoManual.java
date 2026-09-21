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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
        setSize(1120, 820);
        setMinimumSize(new Dimension(980, 720));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
    }

    private void construirInterfaz() {
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        add(crearBotonesFinales(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE),
                BorderFactory.createEmptyBorder(20, 28, 20, 28)
        ));

        JLabel titulo = new JLabel(
                "<html><span style='font-size:20px;'>Asiento Contable</span><br>"
                + "<span style='font-size:11px; font-weight:normal;'>"
                + "Describe la operación, revisa las cuentas sugeridas y registra una sola vez. "
                + "Las compras y ventas de productos actualizan automáticamente el Kardex PEPS."
                + "</span></html>"
        );
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(COLOR_TEXTO);
        panel.add(titulo, BorderLayout.WEST);

        return panel;
    }

    private JPanel crearContenido() {
        JPanel fondo = new JPanel(new BorderLayout(0, 15));
        fondo.setBackground(COLOR_FONDO);
        fondo.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        fondo.add(crearDatosGenerales(), BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new javax.swing.BoxLayout(centro, javax.swing.BoxLayout.Y_AXIS));
        JPanel areaTabla = crearAreaTabla();
        areaTabla.setPreferredSize(new Dimension(900, 310));
        centro.add(areaTabla);
        centro.add(javax.swing.Box.createVerticalStrut(12));
        panelTs = crearPanelTs();
        centro.add(panelTs);

        JScrollPane scrollCentro = new JScrollPane(centro);
        scrollCentro.setBorder(null);
        scrollCentro.getViewport().setOpaque(false);
        scrollCentro.setOpaque(false);
        scrollCentro.getVerticalScrollBar().setUnitIncrement(18);
        fondo.add(scrollCentro, BorderLayout.CENTER);
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
        gbc.insets = new Insets(0, 0, 6, 15);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.18;
        tarjeta.add(crearEtiqueta("Fecha"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.27;
        tarjeta.add(crearEtiqueta("Tipo de operación"), gbc);
        gbc.gridx = 2; gbc.weightx = 0.55; gbc.insets = new Insets(0, 0, 6, 0);
        tarjeta.add(crearEtiqueta("Concepto de la operación"), gbc);

        gbc.gridy = 1; gbc.gridx = 0; gbc.weightx = 0.18; gbc.insets = new Insets(0, 0, 0, 15);
        spFecha = new JSpinner(DialogoUIUtils.crearModeloFechaPeriodoActivo());
        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "dd/MM/yyyy"));
        spFecha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spFecha.setPreferredSize(new Dimension(0, 42));
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
                "Compra de activo",
                "Préstamo bancario",
                "Aporte de capital",
                "Otro / asiento manual"
        });
        cmbTipoOperacion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbTipoOperacion.addActionListener(e -> aplicarTipoSeleccionado());
        gbc.gridx = 1; gbc.weightx = 0.27;
        tarjeta.add(cmbTipoOperacion, gbc);

        txtConcepto = new JTextArea(2, 20);
        txtConcepto.setLineWrap(true);
        txtConcepto.setWrapStyleWord(true);
        txtConcepto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtConcepto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 195, 205)),
                BorderFactory.createEmptyBorder(7, 8, 7, 8)
        ));
        gbc.gridx = 2; gbc.weightx = 0.55; gbc.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(txtConcepto, gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 3; gbc.weightx = 1; gbc.insets = new Insets(9, 0, 0, 0);
        lblEstadoInventario = new JLabel(
                "Selecciona el tipo de operación. Para compras y ventas el sistema solicitará producto, monto y valor unitario."
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
            case "Compra de activo" -> "Compra de activo";
            case "Préstamo bancario" -> "Préstamo bancario";
            case "Aporte de capital" -> "Aporte de capital";
            default -> "";
        };
    }

    private JPanel crearAreaTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JLabel titulo = new JLabel("Detalle del asiento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(COLOR_TEXTO);
        encabezado.add(titulo, BorderLayout.WEST);
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
        tabla.setRowHeight(34);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.setGridColor(COLOR_BORDE);
        tabla.setSelectionBackground(new Color(219, 234, 254));
        tabla.setSelectionForeground(COLOR_TEXTO);

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

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;

        int fila = 0;
        agregarCampo(formulario, gbc, fila++, "Buscar producto", txtBuscar);
        agregarCampo(formulario, gbc, fila++, "Producto", cmbProducto);
        agregarCampo(formulario, gbc, fila++, "Cantidad de unidades", txtCantidadOperacion);

        agregarCampo(
                formulario,
                gbc,
                fila++,
                esCompra
                        ? "Costo unitario neto ($)"
                        : "Precio unitario de venta ($)",
                txtPrecio
        );

        if (esCompra) {
            agregarCampo(
                    formulario,
                    gbc,
                    fila++,
                    "Subtotal sin IVA ($) (alternativa al costo unitario)",
                    txtSubtotalCompra
            );

            agregarCampo(
                    formulario,
                    gbc,
                    fila++,
                    "Total factura con IVA ($) (alternativa)",
                    txtTotalFacturaCompra
            );
        }

        agregarCampo(formulario, gbc, fila++, "Cálculo de la operación", lblCalculo);
        agregarCampo(formulario, gbc, fila++, "Forma de pago/cobro", cmbFormaPago);

        agregarCampo(
                formulario,
                gbc,
                fila++,
                esCompra
                        ? "Cuenta por pagar (crédito o mixto)"
                        : "Cuenta por cobrar (crédito o mixto)",
                cmbCuentaCredito
        );

        agregarCampo(
                formulario,
                gbc,
                fila++,
                esCompra
                        ? "Medio del pago inmediato (solo mixto)"
                        : "Medio del cobro inmediato (solo mixto)",
                cmbMedioInmediato
        );

        agregarCampo(
                formulario,
                gbc,
                fila++,
                esCompra
                        ? "% pagado inmediatamente (solo mixto)"
                        : "% cobrado inmediatamente (solo mixto)",
                txtPorcentajeInmediato
        );

        agregarCampo(
                formulario,
                gbc,
                fila++,
                "Distribución",
                lblDistribucion
        );

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        formulario.add(lblReferencia, gbc);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                formulario,
                "Datos para Kardex PEPS",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
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
        JPanel cont = new JPanel();
        cont.setBackground(Color.WHITE);
        cont.setLayout(new javax.swing.BoxLayout(cont, javax.swing.BoxLayout.Y_AXIS));
        cont.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        JLabel t = new JLabel("Vista previa de cuentas T");
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(COLOR_TEXTO);
        cont.add(t);
        cont.add(javax.swing.Box.createVerticalStrut(8));
        JPanel grilla = new JPanel(new java.awt.GridLayout(0, 3, 12, 12));
        grilla.setOpaque(false);
        grilla.setName("GRILLA_TS");
        cont.add(grilla);
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
                JPanel t = new JPanel(new BorderLayout());
                t.setBackground(new Color(248,250,252));
                t.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDE),
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 12));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        lblTotalDebe = crearValorResumen("Total Debe: $0.00");
        lblTotalHaber = crearValorResumen("Total Haber: $0.00");
        lblDiferencia = crearValorResumen("Diferencia: $0.00");
        lblDiferencia.setForeground(COLOR_EXITO);

        panel.add(lblTotalDebe);
        panel.add(lblTotalHaber);
        panel.add(lblDiferencia);

        return panel;
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 16));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(
                1, 0, 0, 0, COLOR_BORDE
        ));

        JButton btnCancelar = new JButton("Cancelar");
        configurarBotonSecundario(btnCancelar);
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar = new JButton("Registrar asiento");
        configurarBotonPrincipal(btnGuardar);
        btnGuardar.addActionListener(e -> guardar());

        panel.add(btnCancelar);
        panel.add(btnGuardar);

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
        boton.setBorder(BorderFactory.createEmptyBorder(9, 14, 9, 14));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void configurarBotonSecundario(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setForeground(COLOR_TEXTO);
        boton.setBackground(new Color(241, 245, 249));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
