package com.mycompany.sistemacontable.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;

public class PanelOperaciones extends JPanel {

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

    public PanelOperaciones() {
        configurarPanel();
        construirInterfaz();
    }

    private void configurarPanel() {

        setLayout(new BorderLayout());

        setBackground(COLOR_FONDO);

        setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        30,
                        30,
                        30
                )
        );
    }

    private void construirInterfaz() {

    JPanel contenido = new JPanel();
    contenido.setOpaque(false);

    contenido.setLayout(
            new BoxLayout(
                    contenido,
                    BoxLayout.Y_AXIS
            )
    );

    JLabel titulo = new JLabel(
            "Operaciones Contables"
    );

    titulo.setFont(
            new Font(
                    "Segoe UI",
                    Font.BOLD,
                    28
            )
    );

    titulo.setForeground(
            COLOR_TEXTO
    );

    titulo.setAlignmentX(
            LEFT_ALIGNMENT
    );


    JLabel subtitulo = new JLabel(
            "Registra los hechos económicos de la empresa mediante asientos contables."
    );

    subtitulo.setFont(
            new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    14
            )
    );

    subtitulo.setForeground(
            COLOR_SECUNDARIO
    );

    subtitulo.setAlignmentX(
            LEFT_ALIGNMENT
    );


    contenido.add(titulo);

    contenido.add(
            Box.createVerticalStrut(5)
    );

    contenido.add(subtitulo);

    contenido.add(
            Box.createVerticalStrut(28)
    );


    // =====================================================
    // NUEVO ASIENTO CONTABLE
    // =====================================================

    contenido.add(
            crearTituloSeccion(
                    "Registro de operaciones"
            )
    );

    contenido.add(
            Box.createVerticalStrut(5)
    );

    contenido.add(
            crearDescripcionSeccion(
                    "Registra una nueva operación antes de enviarla al Libro Diario."
            )
    );

    contenido.add(
            Box.createVerticalStrut(12)
    );


    JPanel asientos = crearGrid();


    asientos.add(
            crearTarjeta(
                    "Nuevo Asiento Contable",
                    "Describe la operación, obtén cuentas sugeridas "
                    + "y revisa los valores en Debe y Haber antes "
                    + "de registrarla.",
                    "Registrar asiento",
                    "ASIENTO_MANUAL"
            )
    );

    asientos.add(
            crearTarjeta(
                    "Productos",
                    "Administra el catálogo de productos, sus costos y precios "
                    + "de referencia.",
                    "Administrar productos",
                    "PRODUCTOS"
            )
    );


    contenido.add(asientos);


    contenido.add(
            Box.createVerticalStrut(30)
    );


    // La apertura del período se administra únicamente desde "Períodos Contables".

    JScrollPane scroll =
            new JScrollPane(
                    contenido
            );

    scroll.setBorder(null);

    scroll.setOpaque(false);

    scroll.getViewport()
            .setOpaque(false);

    scroll.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );

    scroll.getVerticalScrollBar()
            .setUnitIncrement(18);


    SwingUtilities.invokeLater(
            () -> scroll.getVerticalScrollBar()
                    .setValue(0)
    );


    add(
            scroll,
            BorderLayout.CENTER
    );
}

    private JPanel crearGrid() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                0,
                                2,
                                16,
                                16
                        )
                );

        panel.setOpaque(false);

        panel.setAlignmentX(
                LEFT_ALIGNMENT
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1000
                )
        );

        return panel;
    }

    private JLabel crearTituloSeccion(
            String texto
    ) {

        JLabel label =
                new JLabel(texto);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        label.setForeground(
                COLOR_TEXTO
        );

        label.setAlignmentX(
                LEFT_ALIGNMENT
        );

        return label;
    }

    private JLabel crearDescripcionSeccion(
            String texto
    ) {

        JLabel label =
                new JLabel(texto);

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

        label.setAlignmentX(
                LEFT_ALIGNMENT
        );

        return label;
    }

    private JPanel crearTarjeta(
            String titulo,
            String descripcion,
            String textoBoton,
            String operacion
    ) {

        JPanel tarjeta =
                new JPanel();

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setLayout(
                new BoxLayout(
                        tarjeta,
                        BoxLayout.Y_AXIS
                )
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                16,
                                18,
                                16,
                                18
                        )
                )
        );

        tarjeta.setPreferredSize(
                new Dimension(
                        280,
                        145
                )
        );

        JLabel lblTitulo =
                new JLabel(titulo);

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

        lblTitulo.setAlignmentX(
                LEFT_ALIGNMENT
        );

        JLabel lblDescripcion =
                new JLabel(
                        "<html><div style='width:230px;'>"
                        + descripcion
                        + "</div></html>"
                );

        lblDescripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        lblDescripcion.setForeground(
                COLOR_SECUNDARIO
        );

        lblDescripcion.setAlignmentX(
                LEFT_ALIGNMENT
        );

        JButton boton =
                new JButton(
                        textoBoton
                );

        configurarBoton(
                boton
        );

        boton.addActionListener(
                e -> abrirOperacion(
                        operacion
                )
        );

        tarjeta.add(
                lblTitulo
        );

        tarjeta.add(
                Box.createVerticalStrut(7)
        );

        tarjeta.add(
                lblDescripcion
        );

        tarjeta.add(
                Box.createVerticalGlue()
        );

        tarjeta.add(
                boton
        );

        return tarjeta;
    }

    private void configurarBoton(
            JButton boton
    ) {

        boton.setUI(
                new BasicButtonUI()
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
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
                        8,
                        14,
                        8,
                        14
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

        boton.setAlignmentX(
                LEFT_ALIGNMENT
        );

        boton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        36
                )
        );

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                COLOR_PRIMARIO_HOVER
                        );
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

    private void seleccionarDevolucion() {

        Object[] opciones = {
                "Devolución de compra",
                "Devolución de venta",
                "Cancelar"
        };

        int seleccion =
                JOptionPane.showOptionDialog(
                        this,
                        "Selecciona el tipo de devolución que deseas registrar.",
                        "Registrar devolución",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]
                );

        if (seleccion == 0) {

            abrirOperacion(
                    "DEVOLUCION_COMPRA"
            );

        } else if (seleccion == 1) {

            abrirOperacion(
                    "DEVOLUCION_VENTA"
            );
        }
    }

    private void abrirOperacion(
            String operacion
    ) {

        if ("DEVOLUCION".equals(
                operacion
        )) {

            seleccionarDevolucion();
            return;
        }

        Window ventana =
                SwingUtilities.getWindowAncestor(
                        this
                );

        if ("TRANSFERENCIA_CAJA_BANCO".equals(
                operacion
        )) {

            DialogoTransferenciaEfectivo dialogo =
                    new DialogoTransferenciaEfectivo(
                            ventana
                    );

            dialogo.setVisible(
                    true
            );

            return;
        }

        if ("COMPRA".equals(
                operacion
        )
                ||
            "VENTA".equals(
                operacion
        )) {

            DialogoCompraVenta dialogo =
                    new DialogoCompraVenta(
                            ventana,
                            operacion
                    );

            dialogo.setVisible(
                    true
            );

            return;
        }

        if ("DEVOLUCION_COMPRA".equals(
                operacion
        )
                ||
            "DEVOLUCION_VENTA".equals(
                operacion
        )) {

            DialogoDevolucion dialogo =
                    new DialogoDevolucion(
                            ventana,
                            operacion
                    );

            dialogo.setVisible(
                    true
            );

            return;
        }

        if ("COBRO_CLIENTE".equals(
                operacion
        )
                ||
            "PAGO_PROVEEDOR".equals(
                operacion
        )) {

            DialogoCobroPago dialogo =
                    new DialogoCobroPago(
                            ventana,
                            operacion
                    );

            dialogo.setVisible(
                    true
            );

            return;
        }

        if ("GASTO".equals(
                operacion
        )
                ||
            "COMPRA_ACTIVO".equals(
                operacion
        )) {

            DialogoGastoActivo dialogo =
                    new DialogoGastoActivo(
                            ventana,
                            operacion
                    );

            dialogo.setVisible(
                    true
            );

            return;
        }

        if ("PRESTAMO".equals(
                operacion
        )
                ||
            "APORTE_CAPITAL".equals(
                operacion
        )) {

            DialogoFinanciamiento dialogo =
                    new DialogoFinanciamiento(
                            ventana,
                            operacion
                    );

            dialogo.setVisible(
                    true
            );

            return;
        }

        if ("PRODUCTOS".equals(
                operacion
        )) {

            DialogoProducto dialogo =
                    new DialogoProducto(
                            ventana
                    );

            dialogo.setVisible(
                    true
            );

            return;
        }

        if ("ASIENTO_MANUAL".equals(
                operacion
        )) {

            DialogoAsientoManual dialogo =
                    new DialogoAsientoManual(
                            ventana
                    );

            dialogo.setVisible(
                    true
            );
        }
    }
}