package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.DevolucionCompraService;
import com.mycompany.sistemacontable.servicio.DevolucionVentaService;

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

public class DialogoDevolucion extends JDialog {

    private final String tipoOperacion;

    private final DevolucionCompraService devolucionCompraService;
    private final DevolucionVentaService devolucionVentaService;

    private JSpinner spFecha;
    private JTextField txtMonto;
    private JComboBox<String> cmbForma;
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


    public DialogoDevolucion(
            Window propietario,
            String tipoOperacion
    ) {

        super(
                propietario,
                "DEVOLUCION_COMPRA".equals(tipoOperacion)
                        ? "Devolución sobre Compra"
                        : "Devolución sobre Venta",
                ModalityType.APPLICATION_MODAL
        );

        this.tipoOperacion =
                tipoOperacion;

        devolucionCompraService =
                new DevolucionCompraService();

        devolucionVentaService =
                new DevolucionVentaService();

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

        setSize(
                600,
                630
        );

        setMinimumSize(
                new Dimension(
                        500,
                        440
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


        if (esDevolucionCompra()) {

            titulo =
                    "Devolución sobre Compra";

            descripcion =
                    "Ingresa el valor monetario total devuelto al proveedor.";

        } else {

            titulo =
                    "Devolución sobre Venta";

            descripcion =
                    "Ingresa el valor monetario total devuelto por el cliente.";
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
                        "Fecha de la devolución"
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
                        "Monto total de la devolución ($)"
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
                "Ingresa el monto total de la devolución."
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


        JLabel lblAyuda =
                new JLabel(
                        "<html>"
                        + "<div style='width:450px;'>"
                        + "Introduce directamente el valor en dólares. "
                        + "El sistema calculará automáticamente el IVA "
                        + "y las unidades que deben salir o regresar al Kardex."
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


        tarjeta.add(
                lblAyuda,
                gbc
        );


        // =====================================================
        // FORMA
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
                        esDevolucionCompra()
                                ? "Forma de reintegro"
                                : "Forma de devolución al cliente"
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


        cmbForma =
                new JComboBox<>(
                        new String[]{
                            "CREDITO",
                            "EFECTIVO",
                            "BANCO"
                        }
                );


        cmbForma.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );


        cmbForma.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );


        tarjeta.add(
                cmbForma,
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
                        esDevolucionCompra()
                                ? "Registrar devolución de compra"
                                : "Registrar devolución de venta"
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


            String forma =
                    cmbForma
                            .getSelectedItem()
                            .toString();


            String concepto =
                    txtConcepto
                            .getText()
                            .trim();


            String nombreOperacion =
                    esDevolucionCompra()
                            ? "devolución sobre compra"
                            : "devolución sobre venta";


            int confirmacion =
                    JOptionPane.showConfirmDialog(
                            this,
                            """
                            ¿Deseas registrar esta %s?

                            Fecha: %s
                            Monto: $%,.2f
                            Forma: %s

                            El sistema calculará automáticamente
                            el IVA y las unidades del Kardex.
                            """.formatted(
                                    nombreOperacion,
                                    fecha,
                                    monto,
                                    forma
                            ),
                            "Confirmar devolución",
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


            if (esDevolucionCompra()) {

                devolucionCompraService.registrar(
                        fecha,
                        monto,
                        forma,
                        concepto
                );


                JOptionPane.showMessageDialog(
                        this,
                        """
                        Devolución sobre compra registrada correctamente.

                        El asiento contable, IVA, Kardex,
                        PEPS y existencia fueron recalculados.
                        """,
                        "Devolución registrada",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                devolucionVentaService.registrar(
                        fecha,
                        monto,
                        forma,
                        concepto
                );


                JOptionPane.showMessageDialog(
                        this,
                        """
                        Devolución sobre venta registrada correctamente.

                        El asiento contable, IVA, Kardex,
                        PEPS y existencia fueron recalculados.
                        """,
                        "Devolución registrada",
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
                    "No se pudo registrar la devolución",
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
                                "$",
                                ""
                        )
                        .replace(
                                " ",
                                ""
                        );


        if (texto.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe ingresar el monto de la devolución."
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


        return monto;
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


    // =========================================================
    // TIPO
    // =========================================================

    private boolean esDevolucionCompra() {

        return "DEVOLUCION_COMPRA".equals(
                tipoOperacion
        );
    }


    // =========================================================
    // ERRORES
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


    // =========================================================
    // BOTON SECUNDARIO
    // =========================================================

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