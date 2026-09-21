package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.ProductoDAO;

import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.modelo.DistribucionPago;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;
import com.mycompany.sistemacontable.modelo.ResultadoCompra;
import com.mycompany.sistemacontable.modelo.ResultadoVenta;

import com.mycompany.sistemacontable.servicio.CompraService;
import com.mycompany.sistemacontable.servicio.CalculoIVAService;
import com.mycompany.sistemacontable.servicio.VentaService;

import java.awt.BorderLayout;
import java.awt.Color;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;

public class DialogoCompraVenta extends JDialog {

    private final String tipoOperacion;

    private final CompraService compraService;
    private final VentaService ventaService;
    private final CalculoIVAService calculoIVAService;
    private final ProductoDAO productoDAO;

    private JSpinner spFecha;

    private JComboBox<Producto> cmbProducto;

    private JTextField txtCantidad;
    private JTextField txtPrecioUnitario;

    private JLabel lblTotalCalculado;

    private JComboBox<String> cmbFormaPago;

    private JTextArea txtConcepto;

    private JButton btnGuardar;
    private JButton btnCancelar;

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

    public DialogoCompraVenta(
            Window propietario,
            String tipoOperacion
    ) {

        super(
                propietario,
                "COMPRA".equals(tipoOperacion)
                        ? "Registrar Compra"
                        : "Registrar Venta",
                ModalityType.APPLICATION_MODAL
        );

        this.tipoOperacion =
                tipoOperacion;

        compraService =
                new CompraService();

        ventaService =
                new VentaService();

        calculoIVAService =
                new CalculoIVAService();

        productoDAO =
                new ProductoDAO();

        configurarVentana();
        construirInterfaz();
        cargarProductos();
    }

    private void configurarVentana() {

        setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        setSize(
                650,
                760
        );

        setMinimumSize(
                new Dimension(
                        600,
                        680
                )
        );

        setLocationRelativeTo(
                getOwner()
        );

        setResizable(true);

        setLayout(
                new BorderLayout()
        );

        getContentPane().setBackground(
                COLOR_FONDO
        );
    }

    private void construirInterfaz() {

        add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        add(
                DialogoUIUtils.envolverEnScroll(
                        crearFormulario(),
                        COLOR_FONDO
                ),
                BorderLayout.CENTER
        );

        add(
                crearBotones(),
                BorderLayout.SOUTH
        );
    }

    private JPanel crearEncabezado() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
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
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                22,
                                28,
                                22,
                                28
                        )
                )
        );

        boolean esCompra =
                "COMPRA".equals(
                        tipoOperacion
                );

        String titulo =
                esCompra
                        ? "Nueva Compra"
                        : "Nueva Venta";

        String descripcion =
                esCompra
                        ? "Registra cantidad y costo unitario neto; ContaProMax calcula subtotal, IVA, total y Kardex."
                        : "Registra el monto total y el precio unitario; las unidades se calculan automáticamente.";

        JLabel lblTitulo =
                new JLabel(
                        "<html>"
                        + "<span style='font-size:20px;'>"
                        + titulo
                        + "</span>"
                        + "<br>"
                        + "<span style='font-size:11px; font-weight:normal;'>"
                        + descripcion
                        + "</span>"
                        + "</html>"
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        lblTitulo.setForeground(
                COLOR_TEXTO
        );

        panel.add(
                lblTitulo,
                BorderLayout.WEST
        );

        return panel;
    }

    private JPanel crearFormulario() {

        JPanel fondo =
                new JPanel(
                        new BorderLayout()
                );

        fondo.setBackground(
                COLOR_FONDO
        );

        fondo.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        28,
                        20,
                        28
                )
        );

        JPanel tarjeta =
                new JPanel(
                        new GridBagLayout()
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                22,
                                22,
                                22,
                                22
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        agregarEtiqueta(
                tarjeta,
                gbc,
                "Fecha de la operación"
        );

        spFecha =
                new JSpinner(
                        DialogoUIUtils.crearModeloFechaPeriodoActivo()
                );

        JSpinner.DateEditor editorFecha =
                new JSpinner.DateEditor(
                        spFecha,
                        "dd/MM/yyyy"
                );

        spFecha.setEditor(
                editorFecha
        );

        configurarCampo(
                spFecha
        );

        agregarCampo(
                tarjeta,
                gbc,
                spFecha
        );

        agregarEtiqueta(
                tarjeta,
                gbc,
                "Producto"
        );

        cmbProducto =
                new JComboBox<>();

        cmbProducto.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        cmbProducto.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );

        cmbProducto.addActionListener(
                e -> productoSeleccionado()
        );

        agregarCampo(
                tarjeta,
                gbc,
                cmbProducto
        );

        agregarEtiqueta(
                tarjeta,
                gbc,
                "Cantidad de unidades"
        );

        txtCantidad =
                crearCampoTexto();

        txtCantidad.setToolTipText(
                "COMPRA".equals(tipoOperacion)
                        ? "Ingresa la cantidad física de unidades compradas."
                        : "Ingresa la cantidad física de unidades vendidas."
        );

        agregarCampo(
                tarjeta,
                gbc,
                txtCantidad
        );

        agregarEtiqueta(
                tarjeta,
                gbc,
                "COMPRA".equals(tipoOperacion)
                        ? "Costo unitario de compra ($)"
                        : "Precio unitario de venta ($)"
        );

        txtPrecioUnitario =
                crearCampoTexto();

        txtPrecioUnitario.setToolTipText(
                "Puedes cambiar este precio para cada operación."
        );

        agregarCampo(
                tarjeta,
                gbc,
                txtPrecioUnitario
        );

        agregarEtiqueta(
                tarjeta,
                gbc,
                "COMPRA".equals(tipoOperacion)
                        ? "Resumen de compra"
                        : "Resumen de venta"
        );

        lblTotalCalculado =
                new JLabel(
                        "0"
                );

        lblTotalCalculado.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        lblTotalCalculado.setForeground(
                COLOR_PRIMARIO
        );

        JPanel panelTotal =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        panelTotal.setBackground(
                Color.WHITE
        );

        panelTotal.add(
                lblTotalCalculado
        );

        agregarCampo(
                tarjeta,
                gbc,
                panelTotal
        );

        JLabel lblAyuda =
                new JLabel(
                        "<html>"
                        + "<div style='width:460px;'>"
                        + ("COMPRA".equals(tipoOperacion)
                            ? "Compra: cantidad × costo unitario = subtotal neto; el IVA se agrega aparte y Kardex usa el costo neto."
                            : "Venta: cantidad × precio unitario determina el importe base; el IVA se calcula según la configuración y Kardex descuenta las unidades vendidas.")
                        + "</div>"
                        + "</html>"
                );

        lblAyuda.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        lblAyuda.setForeground(
                COLOR_SECUNDARIO
        );

        agregarCampo(
                tarjeta,
                gbc,
                lblAyuda
        );

        agregarEtiqueta(
                tarjeta,
                gbc,
                "COMPRA".equals(tipoOperacion)
                        ? "Forma de pago"
                        : "Forma de cobro"
        );

        cmbFormaPago =
                new JComboBox<>(
                        new String[]{"EFECTIVO", "BANCO", "CREDITO", "MIXTO"}
                );

        cmbFormaPago.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        cmbFormaPago.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );

        agregarCampo(
                tarjeta,
                gbc,
                cmbFormaPago
        );

        agregarEtiqueta(
                tarjeta,
                gbc,
                "Concepto"
        );

        txtConcepto =
                new JTextArea();

        txtConcepto.setRows(
                3
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
                        14
                )
        );

        txtConcepto.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        190,
                                        195,
                                        205
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                8,
                                8,
                                8
                        )
                )
        );

        agregarCampo(
                tarjeta,
                gbc,
                txtConcepto
        );

        DocumentListener listener =
                new DocumentListener() {

                    @Override
                    public void insertUpdate(
                            DocumentEvent e
                    ) {
                        actualizarTotal();
                    }

                    @Override
                    public void removeUpdate(
                            DocumentEvent e
                    ) {
                        actualizarTotal();
                    }

                    @Override
                    public void changedUpdate(
                            DocumentEvent e
                    ) {
                        actualizarTotal();
                    }
                };

        txtCantidad
                .getDocument()
                .addDocumentListener(
                        listener
                );

        txtPrecioUnitario
                .getDocument()
                .addDocumentListener(
                        listener
                );

        fondo.add(
                tarjeta,
                BorderLayout.CENTER
        );

        return fondo;
    }

    private void cargarProductos() {

        try {

            List<Producto> productos =
                    productoDAO.listarProductosActivos();

            cmbProducto.removeAllItems();

            for (Producto producto : productos) {
                cmbProducto.addItem(
                        producto
                );
            }

            if (cmbProducto.getItemCount() == 0) {

                btnGuardar.setEnabled(
                        false
                );

                JOptionPane.showMessageDialog(
                        this,
                        "No existen productos activos.",
                        "Sin productos",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            cmbProducto.setSelectedIndex(
                    0
            );

            productoSeleccionado();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(e),
                    "No se pudieron cargar los productos",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void productoSeleccionado() {

        Producto producto =
                (Producto) cmbProducto
                        .getSelectedItem();

        if (producto == null
                || txtPrecioUnitario == null) {
            return;
        }

        BigDecimal precio;

        if ("COMPRA".equals(
                tipoOperacion
        )) {

            precio =
                    producto.getCostoCompra();

        } else {

            precio =
                    producto.getPrecioVenta();
        }

        if (precio != null
                && precio.compareTo(
                        BigDecimal.ZERO
                ) > 0) {

            txtPrecioUnitario.setText(
                    precio
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            )
                            .toPlainString()
            );

        } else {

            txtPrecioUnitario.setText(
                    ""
            );
        }

        actualizarTotal();
    }

    private void actualizarTotal() {
        if (lblTotalCalculado == null) return;

        try {
            BigDecimal valor = obtenerNumeroOpcional(txtCantidad);
            BigDecimal precio = obtenerNumeroOpcional(txtPrecioUnitario);

            if (valor == null || precio == null
                    || valor.compareTo(BigDecimal.ZERO) <= 0
                    || precio.compareTo(BigDecimal.ZERO) <= 0) {
                lblTotalCalculado.setText("0");
                return;
            }

            BigDecimal subtotal = valor.multiply(precio)
                    .setScale(2, RoundingMode.HALF_UP);

            ResultadoIVA r = "COMPRA".equals(tipoOperacion)
                    ? calculoIVAService.calcularSobreBase(subtotal)
                    : calculoIVAService.calcular(subtotal);

            lblTotalCalculado.setText(
                    "<html>Subtotal $" + r.getSubtotal().setScale(2).toPlainString()
                    + " &nbsp; IVA $" + r.getIva().setScale(2).toPlainString()
                    + " &nbsp; Total $" + r.getTotal().setScale(2).toPlainString()
                    + "</html>"
            );
        } catch (Exception e) {
            lblTotalCalculado.setText("0");
        }
    }

    private JPanel crearBotones() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                16
                        )
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setBorder(
                BorderFactory.createMatteBorder(
                        1,
                        0,
                        0,
                        0,
                        COLOR_BORDE
                )
        );

        btnCancelar =
                new JButton(
                        "Cancelar"
                );

        configurarBotonSecundario(
                btnCancelar
        );

        btnCancelar.addActionListener(
                e -> dispose()
        );

        btnGuardar =
                new JButton(
                        "COMPRA".equals(tipoOperacion)
                                ? "Registrar compra"
                                : "Registrar venta"
                );

        configurarBotonPrincipal(
                btnGuardar
        );

        btnGuardar.addActionListener(
                e -> guardar()
        );

        panel.add(
                btnCancelar
        );

        panel.add(
                btnGuardar
        );

        return panel;
    }

    private void guardar() {

        try {
            LocalDate fecha = obtenerFecha();

            Producto producto =
                    (Producto) cmbProducto.getSelectedItem();

            if (producto == null) {
                throw new IllegalArgumentException(
                        "Debe seleccionar un producto."
                );
            }

            String formaPago = cmbFormaPago.getSelectedItem().toString();
            String concepto = txtConcepto.getText().trim();

            if ("COMPRA".equals(tipoOperacion)) {
                BigDecimal cantidad = obtenerNumero(
                        txtCantidad,
                        "cantidad de unidades"
                ).setScale(6, RoundingMode.HALF_UP);

                BigDecimal costoUnitario = obtenerNumero(
                        txtPrecioUnitario,
                        "costo unitario"
                ).setScale(6, RoundingMode.HALF_UP);

                BigDecimal subtotal = cantidad.multiply(costoUnitario)
                        .setScale(2, RoundingMode.HALF_UP);

                ResultadoIVA calculo =
                        calculoIVAService.calcularSobreBase(subtotal);

                DistribucionPago distribucion =
                        construirDistribucionCompra(
                                formaPago,
                                calculo.getTotal()
                        );

                int confirmacion = JOptionPane.showConfirmDialog(
                        this,
                        """
                        ¿Deseas registrar esta compra?

                        Producto: %s
                        Cantidad: %s
                        Costo unitario neto: $%,.2f
                        Subtotal: $%,.2f
                        IVA: $%,.2f
                        Total: $%,.2f
                        Forma: %s

                        Contabilidad: Compras + IVA Crédito Fiscal.
                        Kardex PEPS: %s unidades a $%,.2f.
                        """.formatted(
                                producto.getNombre(),
                                cantidad.stripTrailingZeros().toPlainString(),
                                costoUnitario,
                                calculo.getSubtotal(),
                                calculo.getIva(),
                                calculo.getTotal(),
                                distribucion.obtenerFormaPago(),
                                cantidad.stripTrailingZeros().toPlainString(),
                                costoUnitario
                        ),
                        "Confirmar compra",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirmacion != JOptionPane.YES_OPTION) {
                    return;
                }

                btnGuardar.setEnabled(false);

                ResultadoCompra resultado =
                        compraService.registrarCompra(
                                fecha,
                                producto.getIdProducto(),
                                cantidad,
                                costoUnitario,
                                distribucion,
                                "PROVEEDORES",
                                concepto
                        );

                JOptionPane.showMessageDialog(
                        this,
                        """
                        Compra registrada correctamente.

                        Producto: %s
                        Cantidad: %s
                        Costo unitario: $%,.2f
                        Compras: $%,.2f
                        IVA Crédito Fiscal: $%,.2f
                        Total: $%,.2f
                        Forma: %s
                        Asiento N.º %d
                        Existencia actual: %s unidades
                        """.formatted(
                                producto.getNombre(),
                                cantidad.stripTrailingZeros().toPlainString(),
                                costoUnitario,
                                resultado.getSubtotal(),
                                resultado.getIva(),
                                resultado.getTotal(),
                                distribucion.obtenerFormaPago(),
                                resultado.getNumeroAsiento(),
                                resultado.getNuevaExistencia()
                                        .stripTrailingZeros()
                                        .toPlainString()
                        ),
                        "Compra registrada",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {
                BigDecimal cantidad = obtenerNumero(
                        txtCantidad,
                        "cantidad de unidades"
                ).setScale(6, RoundingMode.HALF_UP);

                BigDecimal precioUnitario = obtenerNumero(
                        txtPrecioUnitario,
                        "precio unitario"
                ).setScale(6, RoundingMode.HALF_UP);

                BigDecimal importe = cantidad.multiply(precioUnitario)
                        .setScale(2, RoundingMode.HALF_UP);

                ResultadoIVA calculo = calculoIVAService.calcular(importe);

                DistribucionPago distribucion = construirDistribucionVenta(
                        formaPago,
                        calculo.getTotal()
                );

                int confirmacion = JOptionPane.showConfirmDialog(
                        this,
                        """
                        ¿Deseas registrar esta venta?

                        Producto: %s
                        Cantidad: %s
                        Precio unitario: $%,.2f
                        Subtotal: $%,.2f
                        IVA: $%,.2f
                        Total: $%,.2f
                        Forma: %s

                        Kardex PEPS: salida de %s unidades.
                        """.formatted(
                                producto.getNombre(),
                                cantidad.stripTrailingZeros().toPlainString(),
                                precioUnitario,
                                calculo.getSubtotal(),
                                calculo.getIva(),
                                calculo.getTotal(),
                                distribucion.obtenerFormaPago(),
                                cantidad.stripTrailingZeros().toPlainString()
                        ),
                        "Confirmar venta",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirmacion != JOptionPane.YES_OPTION) {
                    return;
                }

                btnGuardar.setEnabled(false);

                ResultadoVenta resultado = ventaService.registrarVenta(
                        fecha,
                        producto.getIdProducto(),
                        cantidad,
                        precioUnitario,
                        distribucion,
                        concepto
                );

                JOptionPane.showMessageDialog(
                        this,
                        """
                        Venta registrada correctamente.

                        Producto: %s
                        Cantidad: %s
                        Precio unitario de venta: $%,.2f
                        Ventas: $%,.2f
                        IVA Débito Fiscal: $%,.2f
                        Total: $%,.2f
                        Forma: %s
                        Asiento N.º %d
                        Unidades restantes: %s
                        Saldo del Kardex: $%,.2f
                        """.formatted(
                                producto.getNombre(),
                                cantidad.stripTrailingZeros().toPlainString(),
                                precioUnitario,
                                resultado.getSubtotal(),
                                resultado.getIva(),
                                resultado.getTotal(),
                                distribucion.obtenerFormaPago(),
                                resultado.getNumeroAsiento(),
                                resultado.getNuevaExistencia()
                                        .stripTrailingZeros()
                                        .toPlainString(),
                                resultado.getSaldoKardex()
                        ),
                        "Venta registrada",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(e),
                    "No se pudo registrar",
                    JOptionPane.ERROR_MESSAGE
            );

            btnGuardar.setEnabled(true);
        }
    }

    private DistribucionPago construirDistribucionCompra(
            String formaPago,
            BigDecimal total
    ) {
        total = total.setScale(2, RoundingMode.HALF_UP);

        if ("EFECTIVO".equals(formaPago)) {
            return new DistribucionPago(
                    total,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }

        if ("BANCO".equals(formaPago)) {
            return new DistribucionPago(
                    BigDecimal.ZERO,
                    total,
                    BigDecimal.ZERO
            );
        }

        if ("CREDITO".equals(formaPago)) {
            return new DistribucionPago(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    total
            );
        }

        if (!"MIXTO".equals(formaPago)) {
            throw new IllegalArgumentException("Forma de pago no válida.");
        }

        JTextField txtPorcentaje = new JTextField("50");
        JComboBox<String> cmbMedio =
                new JComboBox<>(new String[]{"BANCO", "EFECTIVO"});

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;

        agregarCampoSimple(panel, gbc, 0, "% pagado inmediatamente", txtPorcentaje);
        agregarCampoSimple(panel, gbc, 1, "Medio del pago inmediato", cmbMedio);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Distribución de pago mixto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            throw new IllegalArgumentException("Se canceló la distribución del pago mixto.");
        }

        BigDecimal porcentaje = obtenerNumero(
                txtPorcentaje,
                "porcentaje pagado inmediatamente"
        ).setScale(2, RoundingMode.HALF_UP);

        if (porcentaje.compareTo(BigDecimal.ZERO) <= 0
                || porcentaje.compareTo(new BigDecimal("100")) >= 0) {
            throw new IllegalArgumentException(
                    "El porcentaje del pago inmediato debe ser mayor que 0 y menor que 100."
            );
        }

        BigDecimal pagoAhora = total
                .multiply(porcentaje)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        BigDecimal credito = total.subtract(pagoAhora)
                .setScale(2, RoundingMode.HALF_UP);

        if ("BANCO".equals(cmbMedio.getSelectedItem())) {
            return new DistribucionPago(
                    BigDecimal.ZERO,
                    pagoAhora,
                    credito
            );
        }

        return new DistribucionPago(
                pagoAhora,
                BigDecimal.ZERO,
                credito
        );
    }

    private DistribucionPago construirDistribucionVenta(
            String formaCobro,
            BigDecimal total
    ) {
        total = total.setScale(2, RoundingMode.HALF_UP);

        if ("EFECTIVO".equals(formaCobro)) {
            return new DistribucionPago(total, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        if ("BANCO".equals(formaCobro)) {
            return new DistribucionPago(BigDecimal.ZERO, total, BigDecimal.ZERO);
        }

        if ("CREDITO".equals(formaCobro)) {
            return new DistribucionPago(BigDecimal.ZERO, BigDecimal.ZERO, total);
        }

        if (!"MIXTO".equals(formaCobro)) {
            throw new IllegalArgumentException("Forma de cobro no válida.");
        }

        JTextField txtPorcentaje = new JTextField("50");
        JComboBox<String> cmbMedio =
                new JComboBox<>(new String[]{"BANCO", "EFECTIVO"});

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;

        agregarCampoSimple(panel, gbc, 0, "% cobrado inmediatamente", txtPorcentaje);
        agregarCampoSimple(panel, gbc, 1, "Medio del cobro inmediato", cmbMedio);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Distribución de cobro mixto",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION) {
            throw new IllegalArgumentException("Se canceló la distribución del cobro mixto.");
        }

        BigDecimal porcentaje = obtenerNumero(
                txtPorcentaje,
                "porcentaje cobrado inmediatamente"
        ).setScale(2, RoundingMode.HALF_UP);

        if (porcentaje.compareTo(BigDecimal.ZERO) <= 0
                || porcentaje.compareTo(new BigDecimal("100")) >= 0) {
            throw new IllegalArgumentException(
                    "El porcentaje del cobro inmediato debe ser mayor que 0 y menor que 100."
            );
        }

        BigDecimal cobroAhora = total
                .multiply(porcentaje)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        BigDecimal credito = total.subtract(cobroAhora)
                .setScale(2, RoundingMode.HALF_UP);

        if ("BANCO".equals(cmbMedio.getSelectedItem())) {
            return new DistribucionPago(
                    BigDecimal.ZERO,
                    cobroAhora,
                    credito
            );
        }

        return new DistribucionPago(
                cobroAhora,
                BigDecimal.ZERO,
                credito
        );
    }


    private void agregarCampoSimple(
            JPanel panel,
            GridBagConstraints gbc,
            int fila,
            String etiqueta,
            java.awt.Component componente
    ) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0.45;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.55;
        panel.add(componente, gbc);
    }

    private LocalDate obtenerFecha() {

        Date fecha =
                (Date) spFecha.getValue();

        return Instant
                .ofEpochMilli(
                        fecha.getTime()
                )
                .atZone(
                        ZoneId.systemDefault()
                )
                .toLocalDate();
    }

    private BigDecimal obtenerNumero(
            JTextField campo,
            String nombre
    ) {

        BigDecimal numero =
                obtenerNumeroOpcional(
                        campo
                );

        if (numero == null) {
            throw new IllegalArgumentException(
                    "Debe ingresar la "
                    + nombre
                    + "."
            );
        }

        if (numero.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "La "
                    + nombre
                    + " debe ser mayor que cero."
            );
        }

        return numero;
    }

    private BigDecimal obtenerNumeroOpcional(
            JTextField campo
    ) {

        if (campo == null) {
            return null;
        }

        String texto =
                campo
                        .getText()
                        .trim()
                        .replace(
                                "$",
                                ""
                        )
                        .replace(
                                " ",
                                ""
                        );

        if (texto.isBlank()) {
            return null;
        }

        texto =
                normalizarNumero(
                        texto
                );

        try {

            return new BigDecimal(
                    texto
            );

        } catch (NumberFormatException e) {

            return null;
        }
    }

    private String normalizarNumero(
            String texto
    ) {

        boolean tienePunto =
                texto.contains(
                        "."
                );

        boolean tieneComa =
                texto.contains(
                        ","
                );

        if (tienePunto
                && tieneComa) {

            int ultimoPunto =
                    texto.lastIndexOf(
                            "."
                    );

            int ultimaComa =
                    texto.lastIndexOf(
                            ","
                    );

            if (ultimoPunto
                    > ultimaComa) {

                return texto.replace(
                        ",",
                        ""
                );

            } else {

                return texto
                        .replace(
                                ".",
                                ""
                        )
                        .replace(
                                ",",
                                "."
                        );
            }
        }

        if (tieneComa) {

            int posicion =
                    texto.lastIndexOf(
                            ","
                    );

            int decimales =
                    texto.length()
                    - posicion
                    - 1;

            if (decimales == 1
                    || decimales == 2) {

                return texto.replace(
                        ",",
                        "."
                );
            }

            return texto.replace(
                    ",",
                    ""
            );
        }

        return texto;
    }

    private JTextField crearCampoTexto() {

        JTextField campo =
                new JTextField();

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        190,
                                        195,
                                        205
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                5,
                                8,
                                5,
                                8
                        )
                )
        );

        return campo;
    }

    private void configurarCampo(
            JSpinner campo
    ) {

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );
    }

    private void agregarEtiqueta(
            JPanel panel,
            GridBagConstraints gbc,
            String texto
    ) {

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        7,
                        0
                );

        panel.add(
                crearEtiqueta(
                        texto
                ),
                gbc
        );
    }

    private void agregarCampo(
            JPanel panel,
            GridBagConstraints gbc,
            java.awt.Component componente
    ) {

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        18,
                        0
                );

        panel.add(
                componente,
                gbc
        );
    }

    private String obtenerMensajeError(
            Throwable error
    ) {

        Throwable actual =
                error;

        String ultimoMensaje =
                "Ocurrió un error desconocido.";

        while (actual != null) {

            if (actual.getMessage() != null
                    && !actual
                            .getMessage()
                            .isBlank()) {

                ultimoMensaje =
                        actual.getMessage();
            }

            actual =
                    actual.getCause();
        }

        return ultimoMensaje;
    }

    private JLabel crearEtiqueta(
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
                COLOR_TEXTO
        );

        return label;
    }

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

                        if (boton.isEnabled()) {
                            boton.setBackground(
                                    COLOR_PRIMARIO
                            );
                        }
                    }
                }
        );
    }

    private void configurarBotonSecundario(
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
                COLOR_TEXTO
        );

        boton.setBackground(
                Color.WHITE
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                17,
                                9,
                                17
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
    }
}