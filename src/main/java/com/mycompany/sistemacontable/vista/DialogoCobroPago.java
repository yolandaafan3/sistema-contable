package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.OperacionesGeneralesService;

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
import javax.swing.plaf.basic.BasicButtonUI;

public class DialogoCobroPago extends JDialog {

    private final String tipoOperacion;

    private final OperacionesGeneralesService operacionesService;

    private JSpinner spFecha;
    private JTextField txtMonto;
    private JLabel lblSaldoPendiente;
    private JButton btnUsarSaldoCompleto;
    private BigDecimal saldoPendiente = BigDecimal.ZERO;
    private JComboBox<String> cmbMedio;
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


    public DialogoCobroPago(
            Window propietario,
            String tipoOperacion
    ) {

        super(
                propietario,
                "COBRO_CLIENTE".equals(tipoOperacion)
                        ? "Cobro a Cliente"
                        : "Pago a Proveedor",
                ModalityType.APPLICATION_MODAL
        );

        this.tipoOperacion =
                tipoOperacion;

        operacionesService =
                new OperacionesGeneralesService();

        configurarVentana();

        construirInterfaz();
        cargarSaldoPendiente();
    }


    private void configurarVentana() {

        setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        setSize(
                580,
                590
        );

        setMinimumSize(
                new Dimension(
                        500,
                        430
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


        String titulo;

        String descripcion;


        if (esCobroCliente()) {

            titulo =
                    "Cobro a Cliente";

            descripcion =
                    "Registra el cobro de una cuenta pendiente del cliente.";

        } else {

            titulo =
                    "Pago a Proveedor";

            descripcion =
                    "Registra el pago de una obligación pendiente con el proveedor.";
        }


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
                        18
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


        // =====================================================
        // FECHA
        // =====================================================

        gbc.insets =
                new Insets(
                        0,
                        0,
                        7,
                        0
                );

        tarjeta.add(
                crearEtiqueta(
                        "Fecha de la operación"
                ),
                gbc
        );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        18,
                        0
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

        spFecha.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        spFecha.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );

        tarjeta.add(
                spFecha,
                gbc
        );


        // =====================================================
        // SALDO PENDIENTE
        // =====================================================

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 7, 0);
        tarjeta.add(crearEtiqueta(esCobroCliente()
                ? "Saldo pendiente en Clientes"
                : "Saldo pendiente en Proveedores"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 18, 0);
        JPanel panelSaldo = new JPanel(new BorderLayout(10, 0));
        panelSaldo.setOpaque(false);
        lblSaldoPendiente = new JLabel("Calculando...");
        lblSaldoPendiente.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSaldoPendiente.setForeground(COLOR_PRIMARIO);
        btnUsarSaldoCompleto = new JButton("Usar saldo completo");
        configurarBotonSecundario(btnUsarSaldoCompleto);
        btnUsarSaldoCompleto.addActionListener(e -> {
            if (saldoPendiente.compareTo(BigDecimal.ZERO) > 0) {
                txtMonto.setText(saldoPendiente.setScale(2, RoundingMode.HALF_UP).toPlainString());
            }
        });
        panelSaldo.add(lblSaldoPendiente, BorderLayout.WEST);
        panelSaldo.add(btnUsarSaldoCompleto, BorderLayout.EAST);
        tarjeta.add(panelSaldo, gbc);

        // =====================================================
        // MONTO
        // =====================================================

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        7,
                        0
                );

        tarjeta.add(
                crearEtiqueta(
                        "Monto ($)"
                ),
                gbc
        );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        7,
                        0
                );

        txtMonto =
                new JTextField();

        txtMonto.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        txtMonto.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );

        txtMonto.setToolTipText(
                "Ingresa el monto de la operación."
        );

        tarjeta.add(
                txtMonto,
                gbc
        );


        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        18,
                        0
                );

        JLabel ayuda =
                new JLabel(
                        "<html>"
                        + "<div style='width:440px;'>"
                        + "Esta operación no vuelve a calcular IVA. "
                        + "El IVA ya fue reconocido en la compra o venta original."
                        + "</div>"
                        + "</html>"
                );

        ayuda.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        ayuda.setForeground(
                COLOR_SECUNDARIO
        );

        tarjeta.add(
                ayuda,
                gbc
        );


        // =====================================================
        // MEDIO
        // =====================================================

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        7,
                        0
                );

        tarjeta.add(
                crearEtiqueta(
                        esCobroCliente()
                                ? "Medio de cobro"
                                : "Medio de pago"
                ),
                gbc
        );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        18,
                        0
                );

        cmbMedio =
                new JComboBox<>(
                        new String[]{
                            "EFECTIVO",
                            "BANCO"
                        }
                );

        cmbMedio.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        cmbMedio.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );

        tarjeta.add(
                cmbMedio,
                gbc
        );


        // =====================================================
        // CONCEPTO
        // =====================================================

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        7,
                        0
                );

        tarjeta.add(
                crearEtiqueta(
                        "Concepto"
                ),
                gbc
        );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        0,
                        0
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

        txtConcepto.setText(
                esCobroCliente()
                        ? "Cobro a cliente"
                        : "Pago a proveedor"
        );

        tarjeta.add(
                txtConcepto,
                gbc
        );


        fondo.add(
                tarjeta,
                BorderLayout.CENTER
        );

        return fondo;
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
                        esCobroCliente()
                                ? "Registrar cobro"
                                : "Registrar pago"
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


    private void cargarSaldoPendiente() {
        try {
            saldoPendiente = esCobroCliente()
                    ? operacionesService.obtenerSaldoClientesPendiente()
                    : operacionesService.obtenerSaldoProveedoresPendiente();

            if (lblSaldoPendiente != null) {
                lblSaldoPendiente.setText(String.format("$%,.2f", saldoPendiente));
                lblSaldoPendiente.setForeground(
                        saldoPendiente.compareTo(BigDecimal.ZERO) > 0
                                ? COLOR_PRIMARIO
                                : COLOR_SECUNDARIO
                );
            }
            if (btnUsarSaldoCompleto != null) {
                btnUsarSaldoCompleto.setEnabled(saldoPendiente.compareTo(BigDecimal.ZERO) > 0);
            }
            if (btnGuardar != null) {
                btnGuardar.setEnabled(saldoPendiente.compareTo(BigDecimal.ZERO) > 0);
            }
        } catch (Exception e) {
            saldoPendiente = BigDecimal.ZERO;
            if (lblSaldoPendiente != null) {
                lblSaldoPendiente.setText("No disponible");
            }
            if (btnUsarSaldoCompleto != null) btnUsarSaldoCompleto.setEnabled(false);
            if (btnGuardar != null) btnGuardar.setEnabled(false);
            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(e),
                    "No se pudo consultar el saldo pendiente",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void guardar() {

        try {

            LocalDate fecha =
                    obtenerFecha();

            BigDecimal monto =
                    obtenerMonto();

            if (saldoPendiente.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        esCobroCliente()
                                ? "No existe saldo pendiente en Clientes para cobrar."
                                : "No existe saldo pendiente en Proveedores para pagar."
                );
            }
            if (monto.compareTo(saldoPendiente) > 0) {
                throw new IllegalArgumentException(
                        "El monto ingresado ($" + monto.setScale(2)
                        + ") supera el saldo pendiente ($" + saldoPendiente.setScale(2) + ")."
                );
            }

            String medio =
                    cmbMedio
                            .getSelectedItem()
                            .toString();

            String concepto =
                    txtConcepto
                            .getText()
                            .trim();


            String nombreOperacion =
                    esCobroCliente()
                            ? "cobro a cliente"
                            : "pago a proveedor";


            String detalleAsiento;

            if (esCobroCliente()) {

                detalleAsiento =
                        "BANCO".equals(medio)
                                ? """
                                  Banco: DEBE $%,.2f
                                  Clientes: HABER $%,.2f
                                  """.formatted(
                                        monto,
                                        monto
                                )
                                : """
                                  Caja: DEBE $%,.2f
                                  Clientes: HABER $%,.2f
                                  """.formatted(
                                        monto,
                                        monto
                                );

            } else {

                detalleAsiento =
                        "BANCO".equals(medio)
                                ? """
                                  Proveedores: DEBE $%,.2f
                                  Banco: HABER $%,.2f
                                  """.formatted(
                                        monto,
                                        monto
                                )
                                : """
                                  Proveedores: DEBE $%,.2f
                                  Caja: HABER $%,.2f
                                  """.formatted(
                                        monto,
                                        monto
                                );
            }


            int confirmacion =
                    JOptionPane.showConfirmDialog(
                            this,
                            """
                            ¿Deseas registrar este %s?

                            Fecha: %s
                            Monto: $%,.2f
                            Medio: %s

                            %s
                            """.formatted(
                                    nombreOperacion,
                                    fecha,
                                    monto,
                                    medio,
                                    detalleAsiento
                            ),
                            "Confirmar operación",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );


            if (confirmacion
                    != JOptionPane.YES_OPTION) {

                return;
            }


            btnGuardar.setEnabled(
                    false
            );


            if (esCobroCliente()) {

                operacionesService
                        .registrarCobroCliente(
                                fecha,
                                monto,
                                medio,
                                concepto
                        );


                JOptionPane.showMessageDialog(
                        this,
                        """
                        Cobro a cliente registrado correctamente.

                        El asiento contable fue generado automáticamente.
                        """,
                        "Cobro registrado",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                operacionesService
                        .registrarPagoProveedor(
                                fecha,
                                monto,
                                medio,
                                concepto
                        );


                JOptionPane.showMessageDialog(
                        this,
                        """
                        Pago a proveedor registrado correctamente.

                        El asiento contable fue generado automáticamente.
                        """,
                        "Pago registrado",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }


            dispose();


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(
                            e
                    ),
                    "No se pudo registrar la operación",
                    JOptionPane.ERROR_MESSAGE
            );

            btnGuardar.setEnabled(
                    true
            );
        }
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


    private BigDecimal obtenerMonto() {

        String texto =
                txtMonto
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

            throw new IllegalArgumentException(
                    "Debe ingresar el monto."
            );
        }


        texto =
                normalizarNumero(
                        texto
                );


        BigDecimal monto;


        try {

            monto =
                    new BigDecimal(
                            texto
                    );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "El monto ingresado no es válido."
            );
        }


        if (monto.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new IllegalArgumentException(
                    "El monto debe ser mayor que cero."
            );
        }


        return monto.setScale(
                2,
                RoundingMode.HALF_UP
        );
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
                &&
            tieneComa) {

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
                    ||
                decimales == 2) {

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


    private boolean esCobroCliente() {

        return "COBRO_CLIENTE".equals(
                tipoOperacion
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
                    &&
                !actual.getMessage().isBlank()) {

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

                        boton.setBackground(
                                COLOR_PRIMARIO
                        );
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
                new Color(
                        241,
                        245,
                        249
                )
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                18,
                                9,
                                18
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