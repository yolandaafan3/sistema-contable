package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.CuentaDAO;

import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DistribucionPago;
import com.mycompany.sistemacontable.modelo.ResultadoIVA;

import com.mycompany.sistemacontable.servicio.CalculoIVAService;
import com.mycompany.sistemacontable.servicio.GastosActivosService;

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
import javax.swing.JCheckBox;
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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class DialogoGastoActivo extends JDialog {

    private final String tipoOperacion;

    private final GastosActivosService gastosActivosService;
    private final CalculoIVAService ivaService;
    private final CuentaDAO cuentaDAO;

    private JSpinner spFecha;

    private JComboBox<Cuenta> cmbCuenta;

    private JTextField txtMonto;

    private JCheckBox chkAplicaIva;

    private JLabel lblSubtotalValor;
    private JLabel lblIvaValor;
    private JLabel lblTotalValor;

    private JTextField txtEfectivo;
    private JTextField txtBanco;
    private JTextField txtCredito;

    private JLabel lblDistribuidoValor;
    private JLabel lblDiferenciaValor;

    private JTextArea txtConcepto;

    private JButton btnGuardar;
    private JButton btnCancelar;

    private BigDecimal totalCalculado =
            BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );


    private final Color COLOR_FONDO =
            new Color(245, 247, 250);

    private final Color COLOR_TEXTO =
            new Color(30, 41, 59);

    private final Color COLOR_SECUNDARIO =
            new Color(100, 116, 139);

    private final Color COLOR_PRIMARIO =
            new Color(37, 99, 235);

    private final Color COLOR_BORDE =
            new Color(226, 232, 240);

    private final Color COLOR_EXITO =
            new Color(22, 163, 74);

    private final Color COLOR_ERROR =
            new Color(220, 38, 38);


    public DialogoGastoActivo(
            Window propietario,
            String tipoOperacion
    ) {

        super(
                propietario,
                "GASTO".equals(tipoOperacion)
                        ? "Registrar Gasto"
                        : "Compra de Activo",
                ModalityType.APPLICATION_MODAL
        );


        this.tipoOperacion =
                tipoOperacion;


        gastosActivosService =
                new GastosActivosService();


        ivaService =
                new CalculoIVAService();


        cuentaDAO =
                new CuentaDAO();


        configurarVentana();

        construirInterfaz();

        cargarCuentas();

        actualizarCalculos();
    }


    // =========================================================
    // CONFIGURACION
    // =========================================================

    private void configurarVentana() {

        setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );


        setSize(
                690,
                700
        );


        setMinimumSize(
                new Dimension(
                        560,
                        480
                )
        );


        setLocationRelativeTo(
                getOwner()
        );

        setResizable(true);


        setLayout(
                new BorderLayout()
        );


        getContentPane()
                .setBackground(
                        COLOR_FONDO
                );
    }


    // =========================================================
    // INTERFAZ
    // =========================================================

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


    // =========================================================
    // ENCABEZADO
    // =========================================================

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


        if (esGasto()) {

            titulo =
                    "Registrar Gasto";

            descripcion =
                    "Registra gastos administrativos, de venta, financieros u otros.";

        } else {

            titulo =
                    "Compra de Activo";

            descripcion =
                    "Registra la adquisición de un activo no corriente.";
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


    // =========================================================
    // FORMULARIO
    // =========================================================

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
                        20,
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

        gbc.gridwidth = 2;

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
                        6,
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
                        16,
                        0
                );


        spFecha =
                new JSpinner(
                        new SpinnerDateModel()
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
        // CUENTA
        // =====================================================

        gbc.gridy++;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );


        tarjeta.add(
                crearEtiqueta(
                        esGasto()
                                ? "Cuenta de gasto"
                                : "Cuenta de activo"
                ),
                gbc
        );


        gbc.gridy++;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        16,
                        0
                );


        cmbCuenta =
                new JComboBox<>();


        cmbCuenta.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );


        cmbCuenta.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );


        tarjeta.add(
                cmbCuenta,
                gbc
        );


        // =====================================================
        // MONTO
        // =====================================================

        gbc.gridy++;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );


        tarjeta.add(
                crearEtiqueta(
                        "Monto de la operación"
                ),
                gbc
        );


        gbc.gridy++;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        12,
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


        txtMonto.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {

                                actualizarCalculos();
                            }


                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {

                                actualizarCalculos();
                            }


                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {

                                actualizarCalculos();
                            }
                        }
                );


        tarjeta.add(
                txtMonto,
                gbc
        );


        // =====================================================
        // IVA
        // =====================================================

        gbc.gridy++;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        16,
                        0
                );


        chkAplicaIva =
                new JCheckBox(
                        "La operación aplica IVA"
                );


        chkAplicaIva.setSelected(
                true
        );


        chkAplicaIva.setBackground(
                Color.WHITE
        );


        chkAplicaIva.setForeground(
                COLOR_TEXTO
        );


        chkAplicaIva.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );


        chkAplicaIva.addActionListener(
                e -> actualizarCalculos()
        );


        tarjeta.add(
                chkAplicaIva,
                gbc
        );


        // =====================================================
        // RESUMEN
        // =====================================================

        gbc.gridy++;


        gbc.gridwidth = 1;

        gbc.weightx = 0.5;


        gbc.gridx = 0;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        5,
                        10
                );


        tarjeta.add(
                crearEtiqueta(
                        "Subtotal:"
                ),
                gbc
        );


        gbc.gridx = 1;


        lblSubtotalValor =
                crearValor(
                        "$0.00"
                );


        tarjeta.add(
                lblSubtotalValor,
                gbc
        );


        gbc.gridy++;

        gbc.gridx = 0;


        tarjeta.add(
                crearEtiqueta(
                        "IVA:"
                ),
                gbc
        );


        gbc.gridx = 1;


        lblIvaValor =
                crearValor(
                        "$0.00"
                );


        tarjeta.add(
                lblIvaValor,
                gbc
        );


        gbc.gridy++;

        gbc.gridx = 0;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        18,
                        10
                );


        tarjeta.add(
                crearEtiqueta(
                        "Total:"
                ),
                gbc
        );


        gbc.gridx = 1;


        lblTotalValor =
                crearValor(
                        "$0.00"
                );


        lblTotalValor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );


        tarjeta.add(
                lblTotalValor,
                gbc
        );


        // =====================================================
        // DISTRIBUCION
        // =====================================================

        gbc.gridy++;

        gbc.gridx = 0;

        gbc.gridwidth = 2;

        gbc.weightx = 1;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        0
                );


        tarjeta.add(
                crearEtiqueta(
                        "Distribución del pago"
                ),
                gbc
        );


        gbc.gridy++;


        gbc.gridwidth = 1;

        gbc.weightx = 0.33;


        gbc.gridx = 0;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        5,
                        10
                );


        tarjeta.add(
                crearEtiquetaPequena(
                        "Efectivo"
                ),
                gbc
        );


        gbc.gridx = 1;


        tarjeta.add(
                crearEtiquetaPequena(
                        "Banco"
                ),
                gbc
        );


        gbc.gridy++;

        gbc.gridx = 0;


        txtEfectivo =
                crearCampoPago();


        tarjeta.add(
                txtEfectivo,
                gbc
        );


        gbc.gridx = 1;


        txtBanco =
                crearCampoPago();


        tarjeta.add(
                txtBanco,
                gbc
        );


        gbc.gridy++;

        gbc.gridx = 0;


        gbc.insets =
                new Insets(
                        8,
                        0,
                        5,
                        10
                );


        tarjeta.add(
                crearEtiquetaPequena(
                        "Crédito"
                ),
                gbc
        );


        gbc.gridy++;


        gbc.gridx = 0;


        gbc.gridwidth = 2;


        txtCredito =
                crearCampoPago();


        tarjeta.add(
                txtCredito,
                gbc
        );


        // =====================================================
        // TOTAL DISTRIBUIDO
        // =====================================================

        gbc.gridy++;


        gbc.gridwidth = 1;

        gbc.gridx = 0;


        gbc.insets =
                new Insets(
                        12,
                        0,
                        5,
                        10
                );


        tarjeta.add(
                crearEtiqueta(
                        "Total distribuido:"
                ),
                gbc
        );


        gbc.gridx = 1;


        lblDistribuidoValor =
                crearValor(
                        "$0.00"
                );


        tarjeta.add(
                lblDistribuidoValor,
                gbc
        );


        gbc.gridy++;

        gbc.gridx = 0;


        tarjeta.add(
                crearEtiqueta(
                        "Diferencia:"
                ),
                gbc
        );


        gbc.gridx = 1;


        lblDiferenciaValor =
                crearValor(
                        "$0.00"
                );


        tarjeta.add(
                lblDiferenciaValor,
                gbc
        );


        // =====================================================
        // CONCEPTO
        // =====================================================

        gbc.gridy++;

        gbc.gridx = 0;

        gbc.gridwidth = 2;


        gbc.insets =
                new Insets(
                        16,
                        0,
                        6,
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


    // =========================================================
    // CAMPO DE PAGO
    // =========================================================

    private JTextField crearCampoPago() {

        JTextField campo =
                new JTextField(
                        "0.00"
                );


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


        campo.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {

                                actualizarDistribucion();
                            }


                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {

                                actualizarDistribucion();
                            }


                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {

                                actualizarDistribucion();
                            }
                        }
                );


        return campo;
    }


    // =========================================================
    // CARGAR CUENTAS
    // =========================================================

    private void cargarCuentas() {

        try {

            List<Cuenta> cuentas =
                    cuentaDAO.listarCuentasMovimiento();


            cmbCuenta.removeAllItems();


            for (Cuenta cuenta : cuentas) {

                if (cuenta == null) {

                    continue;
                }


                if (!cuenta.isActivo()) {

                    continue;
                }


                if (!cuenta.isPermiteMovimiento()) {

                    continue;
                }


                if (esCuentaValida(
                        cuenta
                )) {

                    cmbCuenta.addItem(
                            cuenta
                    );
                }
            }


            if (cmbCuenta.getItemCount() == 0) {

                btnGuardar.setEnabled(
                        false
                );


                JOptionPane.showMessageDialog(
                        this,
                        esGasto()
                                ? "No existen cuentas de gasto activas que permitan movimientos."
                                : "No existen cuentas de Activo No Corriente activas que permitan movimientos.",
                        "No hay cuentas disponibles",
                        JOptionPane.WARNING_MESSAGE
                );
            }


        } catch (Exception e) {

            btnGuardar.setEnabled(
                    false
            );


            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(
                            e
                    ),
                    "Error al cargar cuentas",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================================================
    // VALIDAR CUENTA PARA EL COMBO
    // =========================================================

    private boolean esCuentaValida(
            Cuenta cuenta
    ) {

        if (esGasto()) {

            if (!"GASTO".equals(
                    cuenta.getTipo()
            )) {

                return false;
            }


            String clasificacion =
                    cuenta.getClasificacion();


            return "ADMINISTRATIVO".equals(
                    clasificacion
            )
                    ||
                   "VENTA".equals(
                           clasificacion
                   )
                    ||
                   "FINANCIERO".equals(
                           clasificacion
                   )
                    ||
                   "OTRO".equals(
                           clasificacion
                   );
        }


        return "ACTIVO".equals(
                cuenta.getTipo()
        )
                &&
               "NO_CORRIENTE".equals(
                       cuenta.getClasificacion()
               );
    }


    // =========================================================
    // CALCULOS IVA
    // =========================================================

    private void actualizarCalculos() {

        try {

            BigDecimal monto =
                    leerDecimalOpcional(
                            txtMonto
                    );


            if (monto.compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                totalCalculado =
                        BigDecimal.ZERO.setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


                mostrarResumen(
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO
                );


                actualizarDistribucion();

                return;
            }


            BigDecimal subtotal;

            BigDecimal iva;

            BigDecimal total;


            if (chkAplicaIva != null
                    &&
                chkAplicaIva.isSelected()) {

                ResultadoIVA resultado =
                        ivaService.calcular(
                                monto
                        );


                subtotal =
                        resultado.getSubtotal();


                iva =
                        resultado.getIva();


                total =
                        resultado.getTotal();

            } else {

                subtotal =
                        monto.setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


                iva =
                        BigDecimal.ZERO.setScale(
                                2,
                                RoundingMode.HALF_UP
                        );


                total =
                        subtotal;
            }


            totalCalculado =
                    total.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );


            mostrarResumen(
                    subtotal,
                    iva,
                    total
            );


            actualizarDistribucion();


        } catch (Exception e) {

            totalCalculado =
                    BigDecimal.ZERO.setScale(
                            2,
                            RoundingMode.HALF_UP
                    );


            mostrarResumen(
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );


            actualizarDistribucion();
        }
    }


    private void mostrarResumen(
            BigDecimal subtotal,
            BigDecimal iva,
            BigDecimal total
    ) {

        if (lblSubtotalValor != null) {

            lblSubtotalValor.setText(
                    "$"
                    + formato(
                            subtotal
                    )
            );
        }


        if (lblIvaValor != null) {

            lblIvaValor.setText(
                    "$"
                    + formato(
                            iva
                    )
            );
        }


        if (lblTotalValor != null) {

            lblTotalValor.setText(
                    "$"
                    + formato(
                            total
                    )
            );
        }
    }


    // =========================================================
    // DISTRIBUCION
    // =========================================================

    private void actualizarDistribucion() {

        if (lblDistribuidoValor == null
                ||
            lblDiferenciaValor == null) {

            return;
        }


        try {

            BigDecimal efectivo =
                    leerDecimalOpcional(
                            txtEfectivo
                    );


            BigDecimal banco =
                    leerDecimalOpcional(
                            txtBanco
                    );


            BigDecimal credito =
                    leerDecimalOpcional(
                            txtCredito
                    );


            BigDecimal distribuido =
                    efectivo
                            .add(
                                    banco
                            )
                            .add(
                                    credito
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


            BigDecimal diferencia =
                    totalCalculado
                            .subtract(
                                    distribuido
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );


            lblDistribuidoValor.setText(
                    "$"
                    + formato(
                            distribuido
                    )
            );


            lblDiferenciaValor.setText(
                    "$"
                    + formato(
                            diferencia
                    )
            );


            if (diferencia.compareTo(
                    BigDecimal.ZERO
            ) == 0) {

                lblDiferenciaValor.setForeground(
                        COLOR_EXITO
                );

            } else {

                lblDiferenciaValor.setForeground(
                        COLOR_ERROR
                );
            }


        } catch (Exception e) {

            lblDistribuidoValor.setText(
                    "$0.00"
            );


            lblDiferenciaValor.setText(
                    "$"
                    + formato(
                            totalCalculado
                    )
            );


            lblDiferenciaValor.setForeground(
                    COLOR_ERROR
            );
        }
    }


    // =========================================================
    // BOTONES
    // =========================================================

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
                        esGasto()
                                ? "Registrar gasto"
                                : "Registrar activo"
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


    // =========================================================
    // GUARDAR
    // =========================================================

    private void guardar() {

        try {

            LocalDate fecha =
                    obtenerFecha();


            BigDecimal monto =
                    obtenerMonto();


            Cuenta cuenta =
                    (Cuenta) cmbCuenta
                            .getSelectedItem();


            if (cuenta == null) {

                throw new IllegalArgumentException(
                        "Debe seleccionar una cuenta."
                );
            }


            boolean aplicaIva =
                    chkAplicaIva.isSelected();


            BigDecimal efectivo =
                    obtenerValorPago(
                            txtEfectivo,
                            "efectivo"
                    );


            BigDecimal banco =
                    obtenerValorPago(
                            txtBanco,
                            "banco"
                    );


            BigDecimal credito =
                    obtenerValorPago(
                            txtCredito,
                            "credito"
                    );


            DistribucionPago distribucion =
                    new DistribucionPago(
                            efectivo,
                            banco,
                            credito
                    );


            actualizarCalculos();


            if (distribucion.getTotal()
                    .compareTo(
                            totalCalculado
                    ) != 0) {

                throw new IllegalArgumentException(
                        "La distribución del pago debe ser exactamente $"
                        + formato(
                                totalCalculado
                        )
                        + ". Actualmente has distribuido $"
                        + formato(
                                distribucion.getTotal()
                        )
                        + "."
                );
            }


            String concepto =
                    txtConcepto
                            .getText()
                            .trim();


            btnGuardar.setEnabled(
                    false
            );


            if (esGasto()) {

                gastosActivosService
                        .registrarGasto(
                                fecha,
                                monto,
                                aplicaIva,
                                cuenta.getIdCuenta(),
                                distribucion,
                                concepto
                        );


                JOptionPane.showMessageDialog(
                        this,
                        "Gasto registrado correctamente.\n"
                        + "El asiento contable fue generado automáticamente.",
                        "Gasto registrado",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                gastosActivosService
                        .registrarCompraActivo(
                                fecha,
                                monto,
                                aplicaIva,
                                cuenta.getIdCuenta(),
                                distribucion,
                                concepto
                        );


                JOptionPane.showMessageDialog(
                        this,
                        "Compra de activo registrada correctamente.\n"
                        + "El asiento contable fue generado automáticamente.",
                        "Activo registrado",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }


            dispose();


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Revisa los valores numéricos ingresados.",
                    "Dato incorrecto",
                    JOptionPane.WARNING_MESSAGE
            );


            btnGuardar.setEnabled(
                    true
            );


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


    // =========================================================
    // FECHA
    // =========================================================

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


    // =========================================================
    // MONTO
    // =========================================================

    private BigDecimal obtenerMonto() {

        String texto =
                txtMonto
                        .getText()
                        .trim()
                        .replace(
                                ",",
                                "."
                        );


        if (texto.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe ingresar el monto."
            );
        }


        BigDecimal monto =
                new BigDecimal(
                        texto
                );


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


    // =========================================================
    // VALORES DE PAGO
    // =========================================================

    private BigDecimal obtenerValorPago(
            JTextField campo,
            String nombre
    ) {

        String texto =
                campo
                        .getText()
                        .trim()
                        .replace(
                                ",",
                                "."
                        );


        if (texto.isBlank()) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }


        BigDecimal valor =
                new BigDecimal(
                        texto
                );


        if (valor.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "El valor de "
                    + nombre
                    + " no puede ser negativo."
            );
        }


        return valor.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    private BigDecimal leerDecimalOpcional(
            JTextField campo
    ) {

        if (campo == null) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }


        String texto =
                campo
                        .getText()
                        .trim()
                        .replace(
                                ",",
                                "."
                        );


        if (texto.isBlank()) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }


        return new BigDecimal(
                texto
        ).setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =========================================================
    // TIPO
    // =========================================================

    private boolean esGasto() {

        return "GASTO".equals(
                tipoOperacion
        );
    }


    // =========================================================
    // FORMATOS
    // =========================================================

    private String formato(
            BigDecimal valor
    ) {

        if (valor == null) {

            return "0.00";
        }


        return valor.setScale(
                2,
                RoundingMode.HALF_UP
        ).toPlainString();
    }


    // =========================================================
    // ERROR
    // =========================================================

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


    // =========================================================
    // COMPONENTES
    // =========================================================

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


    private JLabel crearEtiquetaPequena(
            String texto
    ) {

        JLabel label =
                new JLabel(
                        texto
                );


        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );


        label.setForeground(
                COLOR_SECUNDARIO
        );


        return label;
    }


    private JLabel crearValor(
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


    // =========================================================
    // BOTON PRINCIPAL
    // =========================================================

    private void configurarBotonPrincipal(
            JButton boton
    ) {

        boton.setUI(
                new BasicButtonUI()
        );


        boton.setOpaque(
                true
        );


        boton.setContentAreaFilled(
                true
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


        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }


    // =========================================================
    // BOTON SECUNDARIO
    // =========================================================

    private void configurarBotonSecundario(
            JButton boton
    ) {

        boton.setUI(
                new BasicButtonUI()
        );


        boton.setOpaque(
                true
        );


        boton.setContentAreaFilled(
                true
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


        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );
    }
}