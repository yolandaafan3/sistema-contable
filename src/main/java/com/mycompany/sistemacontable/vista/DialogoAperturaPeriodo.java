package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.AperturaPeriodoService;

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

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.plaf.basic.BasicButtonUI;

public class DialogoAperturaPeriodo extends JDialog {

    private final AperturaPeriodoService aperturaService;

    private JSpinner spFecha;
    private JTextField txtEfectivo;
    private JTextField txtInventarioInicial;

    private final Color COLOR_FONDO =
            new Color(245, 247, 250);

    private final Color COLOR_TEXTO =
            new Color(30, 41, 59);

    private final Color COLOR_SECUNDARIO =
            new Color(100, 116, 139);

    private final Color COLOR_BORDE =
            new Color(226, 232, 240);

    private final Color COLOR_AZUL =
            new Color(37, 99, 235);

    private final Color COLOR_AZUL_HOVER =
            new Color(29, 78, 216);

    public DialogoAperturaPeriodo(
            Window propietario
    ) {

        super(
                propietario,
                "Apertura del Período",
                ModalityType.APPLICATION_MODAL
        );

        aperturaService =
                new AperturaPeriodoService();

        configurarVentana();

        construirInterfaz();
    }

    private void configurarVentana() {

        setSize(
                700,
                620
        );

        setMinimumSize(
                new Dimension(
                        560,
                        460
                )
        );

        setLocationRelativeTo(
                getOwner()
        );

        setResizable(true);

        setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

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
                        crearContenido(),
                        COLOR_FONDO
                ),
                BorderLayout.CENTER
        );

        add(
                crearPie(),
                BorderLayout.SOUTH
        );
    }

    private JPanel crearEncabezado() {

        JPanel panel =
                new JPanel();

        panel.setBackground(
                Color.WHITE
        );

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
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
                                25,
                                35,
                                25,
                                35
                        )
                )
        );

        JLabel titulo =
                new JLabel(
                        "Apertura del Período"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        26
                )
        );

        titulo.setForeground(
                COLOR_TEXTO
        );

        JLabel descripcion =
                new JLabel(
                        "Registra los valores monetarios con los que inicia la empresa."
                );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        descripcion.setForeground(
                COLOR_SECUNDARIO
        );

        titulo.setAlignmentX(
                LEFT_ALIGNMENT
        );

        descripcion.setAlignmentX(
                LEFT_ALIGNMENT
        );

        panel.add(
                titulo
        );

        panel.add(
                Box.createVerticalStrut(
                        5
                )
        );

        panel.add(
                descripcion
        );

        return panel;
    }

    private JPanel crearContenido() {

        JPanel fondo =
                new JPanel(
                        new BorderLayout()
                );

        fondo.setBackground(
                COLOR_FONDO
        );

        fondo.setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        35,
                        30,
                        35
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
                                25,
                                25,
                                25,
                                25
                        )
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        8,
                        0
                );

        tarjeta.add(
                crearEtiqueta(
                        "Fecha de apertura"
                ),
                gbc
        );

        gbc.gridy++;

        spFecha =
                crearSelectorFecha();

        tarjeta.add(
                spFecha,
                gbc
        );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        20,
                        0,
                        8,
                        0
                );

        tarjeta.add(
                crearEtiqueta(
                        "Efectivo inicial ($)"
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

        txtEfectivo =
                crearCampoTexto();

        txtEfectivo.setText(
                ""
        );

        txtEfectivo.setToolTipText(
                "Ingresa el efectivo disponible al inicio del período."
        );

        tarjeta.add(
                txtEfectivo,
                gbc
        );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        20,
                        0,
                        8,
                        0
                );

        tarjeta.add(
                crearEtiqueta(
                        "Valor del inventario inicial ($)"
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

        txtInventarioInicial =
                crearCampoTexto();

        txtInventarioInicial.setText(
                ""
        );

        txtInventarioInicial.setToolTipText(
                "Ingresa el valor contable del inventario al inicio del período."
        );

        tarjeta.add(
                txtInventarioInicial,
                gbc
        );

        gbc.gridy++;

        gbc.insets =
                new Insets(
                        25,
                        0,
                        0,
                        0
                );

        JLabel ayuda =
                new JLabel(
                        "<html>"
                        + "<div style='width:520px;'>"
                        + "<b>Registro de apertura:</b>"
                        + "<br><br>"
                        + "El efectivo y el inventario inicial se registran al Debe. "
                        + "El sistema calcula automáticamente el Capital Social "
                        + "necesario para cuadrar la partida."
                        + "<br><br>"
                        + "Las unidades del inventario para el Kardex se calculan "
                        + "automáticamente con la configuración vigente del producto."
                        + "</div>"
                        + "</html>"
                );

        ayuda.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        ayuda.setForeground(
                COLOR_SECUNDARIO
        );

        ayuda.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        191,
                                        219,
                                        254
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                15,
                                15,
                                15,
                                15
                        )
                )
        );

        ayuda.setOpaque(
                true
        );

        ayuda.setBackground(
                new Color(
                        239,
                        246,
                        255
                )
        );

        tarjeta.add(
                ayuda,
                gbc
        );

        fondo.add(
                tarjeta,
                BorderLayout.NORTH
        );

        return fondo;
    }

    private JPanel crearPie() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                12,
                                15
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

        JButton btnCancelar =
                crearBotonSecundario(
                        "Cancelar"
                );

        JButton btnRegistrar =
                crearBotonPrincipal(
                        "Registrar apertura"
                );

        btnCancelar.addActionListener(
                e -> dispose()
        );

        btnRegistrar.addActionListener(
                e -> registrarApertura()
        );

        panel.add(
                btnCancelar
        );

        panel.add(
                btnRegistrar
        );

        return panel;
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
                        36
                )
        );

        return campo;
    }

    private JSpinner crearSelectorFecha() {

        Calendar calendario =
                Calendar.getInstance();

        calendario.set(
                Calendar.YEAR,
                2026
        );

        calendario.set(
                Calendar.MONTH,
                Calendar.JANUARY
        );

        calendario.set(
                Calendar.DAY_OF_MONTH,
                1
        );

        calendario.set(
                Calendar.HOUR_OF_DAY,
                12
        );

        calendario.set(
                Calendar.MINUTE,
                0
        );

        calendario.set(
                Calendar.SECOND,
                0
        );

        calendario.set(
                Calendar.MILLISECOND,
                0
        );

        SpinnerDateModel modelo =
                new SpinnerDateModel(
                        calendario.getTime(),
                        null,
                        null,
                        Calendar.DAY_OF_MONTH
                );

        JSpinner spinner =
                new JSpinner(
                        modelo
                );

        spinner.setEditor(
                new JSpinner.DateEditor(
                        spinner,
                        "dd/MM/yyyy"
                )
        );

        spinner.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        spinner.setPreferredSize(
                new Dimension(
                        0,
                        36
                )
        );

        return spinner;
    }

    private JButton crearBotonPrincipal(
            String texto
    ) {

        JButton boton =
                new JButton(
                        texto
                );

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
                COLOR_AZUL
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        11,
                        20,
                        11,
                        20
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

                        boton.setBackground(
                                COLOR_AZUL_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                COLOR_AZUL
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
                                10,
                                18,
                                10,
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

        return boton;
    }

    private void registrarApertura() {

        try {

            LocalDate fecha =
                    convertirFecha(
                            (Date) spFecha.getValue()
                    );

            BigDecimal efectivo =
                    convertirDecimal(
                            txtEfectivo.getText(),
                            "efectivo inicial"
                    );

            BigDecimal inventario =
                    convertirDecimal(
                            txtInventarioInicial.getText(),
                            "valor del inventario inicial"
                    );

            BigDecimal capital =
                    efectivo.add(
                            inventario
                    );

            int confirmacion =
                    JOptionPane.showConfirmDialog(
                            this,
                            """
                            Se registrará la apertura del período.

                            Caja: $%,.2f
                            Inventario inicial: $%,.2f
                            Capital Social: $%,.2f

                            Esto generará el Asiento N.º 1.

                            ¿Deseas continuar?
                            """.formatted(
                                    efectivo,
                                    inventario,
                                    capital
                            ),
                            "Confirmar apertura",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (confirmacion
                    != JOptionPane.YES_OPTION) {

                return;
            }

            String resultado =
                    aperturaService.registrarApertura(
                            fecha,
                            efectivo,
                            inventario
                    );

            JOptionPane.showMessageDialog(
                    this,
                    resultado,
                    "Apertura registrada",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(
                            e
                    ),
                    "No se pudo registrar la apertura",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private LocalDate convertirFecha(
            Date fecha
    ) {

        return fecha.toInstant()
                .atZone(
                        ZoneId.systemDefault()
                )
                .toLocalDate();
    }

    private BigDecimal convertirDecimal(
            String texto,
            String campo
    ) {

        if (texto == null
                ||
            texto.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe ingresar "
                    + campo
                    + "."
            );
        }

        try {

            return new BigDecimal(
                    texto.trim()
                            .replace(
                                    ",",
                                    ""
                            )
                            .replace(
                                    "$",
                                    ""
                            )
            );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "El valor ingresado en "
                    + campo
                    + " no es válido."
            );
        }
    }

    private String obtenerMensajeError(
            Throwable error
    ) {

        Throwable actual =
                error;

        String mensaje =
                "Ocurrió un error desconocido.";

        while (actual != null) {

            if (actual.getMessage() != null
                    &&
                !actual.getMessage().isBlank()) {

                mensaje =
                        actual.getMessage();
            }

            actual =
                    actual.getCause();
        }

        return mensaje;
    }
}