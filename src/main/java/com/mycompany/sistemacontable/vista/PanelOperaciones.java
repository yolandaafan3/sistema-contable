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


    // =========================================================
    // CONFIGURACION
    // =========================================================

    private void configurarPanel() {

        setLayout(
                new BorderLayout()
        );

        setBackground(
                COLOR_FONDO
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        28,
                        30,
                        30,
                        30
                )
        );
    }


    // =========================================================
    // INTERFAZ
    // =========================================================

    private void construirInterfaz() {

        JPanel contenido =
                new JPanel();

        contenido.setOpaque(
                false
        );

        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );


        JLabel titulo =
                new JLabel(
                        "Operaciones"
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


        JLabel subtitulo =
                new JLabel(
                        "Registra las operaciones contables y comerciales de la empresa."
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


        contenido.add(
                titulo
        );

        contenido.add(
                Box.createVerticalStrut(
                        5
                )
        );

        contenido.add(
                subtitulo
        );

        contenido.add(
                Box.createVerticalStrut(
                        28
                )
        );


        // =====================================================
        // INICIO DEL PERIODO
        // =====================================================

        contenido.add(
                crearTituloSeccion(
                        "Inicio del Período"
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        12
                )
        );


        JPanel apertura =
                crearGrid();


        apertura.add(
                crearTarjeta(
                        "Apertura del Período",
                        "Registra Caja, Inventario Inicial y Capital Social al comenzar el período contable.",
                        "Registrar apertura",
                        "APERTURA"
                )
        );


        contenido.add(
                apertura
        );


        contenido.add(
                Box.createVerticalStrut(
                        30
                )
        );


        // =====================================================
        // MOVIMIENTOS DE EFECTIVO
        // =====================================================

        contenido.add(
                crearTituloSeccion(
                        "Movimientos de Efectivo"
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        12
                )
        );


        JPanel efectivo =
                crearGrid();


        efectivo.add(
                crearTarjeta(
                        "Transferencia Caja → Banco",
                        "Traslada dinero disponible en Caja hacia Banco sin generar IVA ni afectar el inventario.",
                        "Registrar transferencia",
                        "TRANSFERENCIA_CAJA_BANCO"
                )
        );


        contenido.add(
                efectivo
        );


        contenido.add(
                Box.createVerticalStrut(
                        30
                )
        );


        // =====================================================
        // MERCADERIA
        // =====================================================

        contenido.add(
                crearTituloSeccion(
                        "Mercadería e Inventario"
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        12
                )
        );


        JPanel mercaderia =
                crearGrid();


        mercaderia.add(
                crearTarjeta(
                        "Nueva Compra",
                        "Registra una compra de mercadería, calcula IVA y actualiza automáticamente Kardex y PEPS.",
                        "Registrar compra",
                        "COMPRA"
                )
        );


        mercaderia.add(
                crearTarjeta(
                        "Nueva Venta",
                        "Registra una venta, calcula IVA y realiza la salida automática mediante PEPS.",
                        "Registrar venta",
                        "VENTA"
                )
        );


        mercaderia.add(
                crearTarjeta(
                        "Devolución de Compra",
                        "Registra el valor monetario devuelto al proveedor y ajusta IVA e inventario.",
                        "Registrar devolución",
                        "DEVOLUCION_COMPRA"
                )
        );


        mercaderia.add(
                crearTarjeta(
                        "Devolución de Venta",
                        "Registra el valor monetario devuelto por un cliente y reincorpora unidades al Kardex.",
                        "Registrar devolución",
                        "DEVOLUCION_VENTA"
                )
        );


        contenido.add(
                mercaderia
        );


        contenido.add(
                Box.createVerticalStrut(
                        30
                )
        );


        // =====================================================
        // CLIENTES / PROVEEDORES
        // =====================================================

        contenido.add(
                crearTituloSeccion(
                        "Clientes y Proveedores"
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        12
                )
        );


        JPanel clientes =
                crearGrid();


        clientes.add(
                crearTarjeta(
                        "Cobro a Cliente",
                        "Registra el cobro de una cuenta pendiente en efectivo o banco.",
                        "Registrar cobro",
                        "COBRO_CLIENTE"
                )
        );


        clientes.add(
                crearTarjeta(
                        "Pago a Proveedor",
                        "Registra el pago de una obligación pendiente con un proveedor.",
                        "Registrar pago",
                        "PAGO_PROVEEDOR"
                )
        );


        contenido.add(
                clientes
        );


        contenido.add(
                Box.createVerticalStrut(
                        30
                )
        );


        // =====================================================
        // GASTOS / ACTIVOS
        // =====================================================

        contenido.add(
                crearTituloSeccion(
                        "Gastos y Activos"
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        12
                )
        );


        JPanel gastos =
                crearGrid();


        gastos.add(
                crearTarjeta(
                        "Registrar Gasto",
                        "Gastos administrativos, de venta, financieros u otros, con manejo de IVA.",
                        "Registrar gasto",
                        "GASTO"
                )
        );


        gastos.add(
                crearTarjeta(
                        "Compra de Activo",
                        "Registra mobiliario, equipo de cómputo, transporte u otros activos.",
                        "Registrar activo",
                        "COMPRA_ACTIVO"
                )
        );


        contenido.add(
                gastos
        );


        contenido.add(
                Box.createVerticalStrut(
                        30
                )
        );


        // =====================================================
        // FINANCIAMIENTO
        // =====================================================

        contenido.add(
                crearTituloSeccion(
                        "Financiamiento y Patrimonio"
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        12
                )
        );


        JPanel financiamiento =
                crearGrid();


        financiamiento.add(
                crearTarjeta(
                        "Préstamo Bancario",
                        "Registra préstamos, comisión bancaria, IVA y monto neto recibido.",
                        "Registrar préstamo",
                        "PRESTAMO"
                )
        );


        financiamiento.add(
                crearTarjeta(
                        "Aporte de Capital",
                        "Registra nuevos aportes realizados por los propietarios después de la apertura.",
                        "Registrar aporte",
                        "APORTE_CAPITAL"
                )
        );


        contenido.add(
                financiamiento
        );


        contenido.add(
                Box.createVerticalStrut(
                        30
                )
        );


        // =====================================================
        // CONTABILIDAD GENERAL
        // =====================================================

        contenido.add(
                crearTituloSeccion(
                        "Contabilidad General"
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        12
                )
        );


        JPanel contabilidad =
                crearGrid();


        contabilidad.add(
                crearTarjeta(
                        "Asiento Manual",
                        "Registra ajustes contables seleccionando cuentas y valores en Debe y Haber.",
                        "Nuevo asiento manual",
                        "ASIENTO_MANUAL"
                )
        );


        contenido.add(
                contabilidad
        );


        contenido.add(
                Box.createVerticalStrut(
                        25
                )
        );


        JScrollPane scroll =
                new JScrollPane(
                        contenido
                );

        scroll.setBorder(
                null
        );

        scroll.setOpaque(
                false
        );

        scroll.getViewport()
                .setOpaque(
                        false
                );

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(
                        16
                );


        add(
                scroll,
                BorderLayout.CENTER
        );
    }


    // =========================================================
    // GRID
    // =========================================================

    private JPanel crearGrid() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                0,
                                3,
                                16,
                                16
                        )
                );

        panel.setOpaque(
                false
        );

        panel.setAlignmentX(
                LEFT_ALIGNMENT
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        500
                )
        );

        return panel;
    }


    // =========================================================
    // TITULO DE SECCION
    // =========================================================

    private JLabel crearTituloSeccion(
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
                        17
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


    // =========================================================
    // TARJETA
    // =========================================================

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
                                20,
                                20,
                                20,
                                20
                        )
                )
        );

        tarjeta.setPreferredSize(
                new Dimension(
                        280,
                        185
                )
        );


        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
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
                        9,
                        14,
                        9,
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
                        38
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


        boton.addActionListener(
                e -> abrirOperacion(
                        operacion
                )
        );


        tarjeta.add(
                lblTitulo
        );

        tarjeta.add(
                Box.createVerticalStrut(
                        10
                )
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


    // =========================================================
    // ABRIR OPERACION
    // =========================================================

    private void abrirOperacion(
            String operacion
    ) {

        Window ventana =
                SwingUtilities.getWindowAncestor(
                        this
                );


        // =====================================================
        // APERTURA
        // =====================================================

        if ("APERTURA".equals(
                operacion
        )) {

            DialogoAperturaPeriodo dialogo =
                    new DialogoAperturaPeriodo(
                            ventana
                    );

            dialogo.setVisible(
                    true
            );

            return;
        }


        // =====================================================
        // TRANSFERENCIA CAJA -> BANCO
        // =====================================================

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


        // =====================================================
        // COMPRA / VENTA
        // =====================================================

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


        // =====================================================
        // DEVOLUCIONES
        // =====================================================

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


        // =====================================================
        // COBROS / PAGOS
        // =====================================================

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


        // =====================================================
        // GASTOS / ACTIVOS
        // =====================================================

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


        // =====================================================
        // FINANCIAMIENTO
        // =====================================================

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


        // =====================================================
        // ASIENTO MANUAL
        // =====================================================

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