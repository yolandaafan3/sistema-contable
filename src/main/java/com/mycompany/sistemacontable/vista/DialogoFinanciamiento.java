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

public class DialogoFinanciamiento extends JDialog {

    private final String tipoOperacion;

    private final OperacionesGeneralesService
            operacionesService;

    private JSpinner spFecha;

    private JTextField txtMonto;

    private JTextField txtPorcentajeComision;

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

    private final Color COLOR_BORDE =
            new Color(226, 232, 240);


    public DialogoFinanciamiento(
            Window propietario,
            String tipoOperacion
    ) {

        super(
                propietario,
                "PRESTAMO".equals(tipoOperacion)
                        ? "Préstamo Bancario"
                        : "Aporte de Capital",
                ModalityType.APPLICATION_MODAL
        );


        this.tipoOperacion =
                tipoOperacion;


        operacionesService =
                new OperacionesGeneralesService();


        configurarVentana();

        construirInterfaz();
    }


    // =========================================================
    // CONFIGURACION
    // =========================================================

    private void configurarVentana() {

        setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );


        if (esPrestamo()) {

            setSize(
                    580,
                    620
            );

            setMinimumSize(
                    new Dimension(
                            500,
                            460
                    )
            );

        } else {

            setSize(
                    570,
                    580
            );

            setMinimumSize(
                    new Dimension(
                            500,
                            430
                    )
            );
        }


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


        if (esPrestamo()) {

            titulo =
                    "Préstamo Bancario";

            descripcion =
                    "Registra el préstamo, comisión bancaria e IVA correspondiente.";

        } else {

            titulo =
                    "Aporte de Capital";

            descripcion =
                    "Registra un nuevo aporte realizado por los propietarios.";
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
                        esPrestamo()
                                ? "Monto del préstamo"
                                : "Monto del aporte"
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


        tarjeta.add(
                txtMonto,
                gbc
        );


        // =====================================================
        // COMISION SOLO PARA PRESTAMO
        // =====================================================

        if (esPrestamo()) {

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
                            "Porcentaje de comisión bancaria"
                    ),
                    gbc
            );


            gbc.gridy++;


            gbc.insets =
                    new Insets(
                            0,
                            0,
                            5,
                            0
                    );


            txtPorcentajeComision =
                    new JTextField(
                            "0"
                    );


            txtPorcentajeComision.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            14
                    )
            );


            txtPorcentajeComision.setPreferredSize(
                    new Dimension(
                            0,
                            38
                    )
            );


            tarjeta.add(
                    txtPorcentajeComision,
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


            JLabel lblAyuda =
                    new JLabel(
                            "Ingresa el porcentaje de comisión bancaria, si corresponde."
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


            tarjeta.add(
                    lblAyuda,
                    gbc
            );
        }


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
                        esPrestamo()
                                ? "Cuenta donde se recibe"
                                : "Medio del aporte"
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


        if (esPrestamo()) {

            cmbMedio.setSelectedItem(
                    "BANCO"
            );
        }


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
                        esPrestamo()
                                ? "Registrar préstamo"
                                : "Registrar aporte"
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


            String medio =
                    cmbMedio
                            .getSelectedItem()
                            .toString();


            String concepto =
                    txtConcepto
                            .getText()
                            .trim();


            btnGuardar.setEnabled(
                    false
            );


            if (esPrestamo()) {

                BigDecimal porcentajeComision =
                        obtenerPorcentajeComision();


                operacionesService
                        .registrarPrestamo(
                                fecha,
                                monto,
                                porcentajeComision,
                                medio,
                                concepto
                        );


                JOptionPane.showMessageDialog(
                        this,
                        "Préstamo bancario registrado correctamente.\n"
                        + "Se generó automáticamente el asiento con "
                        + "comisión e IVA cuando corresponde.",
                        "Préstamo registrado",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                operacionesService
                        .registrarAporteCapital(
                                fecha,
                                monto,
                                medio,
                                concepto
                        );


                JOptionPane.showMessageDialog(
                        this,
                        "Aporte de capital registrado correctamente.\n"
                        + "El asiento contable fue generado automáticamente.",
                        "Aporte registrado",
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
    // COMISION
    // =========================================================

    private BigDecimal obtenerPorcentajeComision() {

        if (!esPrestamo()) {

            return BigDecimal.ZERO;
        }


        String texto =
                txtPorcentajeComision
                        .getText()
                        .trim()
                        .replace(
                                ",",
                                "."
                        );


        if (texto.isBlank()) {

            return BigDecimal.ZERO;
        }


        BigDecimal porcentaje =
                new BigDecimal(
                        texto
                );


        if (porcentaje.compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new IllegalArgumentException(
                    "La comisión no puede ser negativa."
            );
        }


        if (porcentaje.compareTo(
                new BigDecimal("100")
        ) >= 0) {

            throw new IllegalArgumentException(
                    "La comisión debe ser menor al 100%."
            );
        }


        return porcentaje;
    }


    // =========================================================
    // TIPO
    // =========================================================

    private boolean esPrestamo() {

        return "PRESTAMO".equals(
                tipoOperacion
        );
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
    // ETIQUETA
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