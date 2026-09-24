package com.mycompany.sistemacontable.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
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
            new Color(218, 226, 238);

    private final Color COLOR_CELESTE_SUAVE =
            new Color(239, 246, 255);

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

        JPanel encabezado = crearEncabezado();
        contenido.add(encabezado);

        contenido.add(
                Box.createVerticalStrut(26)
        );

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
                        "Selecciona el tipo de operación que deseas realizar."
                )
        );

        contenido.add(
                Box.createVerticalStrut(16)
        );

        JPanel asientos = crearGrid();

        asientos.add(
                crearTarjeta(
                        "Nuevo Asiento Contable",
                        "Describe la operación, obtén cuentas sugeridas "
                        + "y revisa los valores en Debe y Haber antes "
                        + "de registrarla.",
                        "Registrar asiento",
                        "ASIENTO_MANUAL",
                        "/branding/contabilidad.png",
                        "CONTABILIDAD"
                )
        );

        asientos.add(
                crearTarjeta(
                        "Productos",
                        "Administra el catálogo de productos, sus costos y precios "
                        + "de referencia.",
                        "Administrar productos",
                        "PRODUCTOS",
                        "/branding/inventario.png",
                        "INVENTARIO"
                )
        );

        asientos.add(
                crearTarjeta(
                        "Apertura del Período",
                        "Registra la apertura del período contable y los valores "
                        + "iniciales necesarios para comenzar a trabajar.",
                        "Abrir período",
                        "APERTURA_PERIODO",
                        "/branding/periodo.png",
                        "PERÍODO CONTABLE"
                )
        );

        contenido.add(asientos);

        contenido.add(
                Box.createVerticalStrut(24)
        );

        JPanel ayuda = crearPanelAyuda();
        contenido.add(ayuda);

        contenido.add(
                Box.createVerticalStrut(10)
        );

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

        scroll.getVerticalScrollBar().setPreferredSize(
                new Dimension(8, 0)
        );

        SwingUtilities.invokeLater(
                () -> scroll.getVerticalScrollBar()
                        .setValue(0)
        );

        add(
                scroll,
                BorderLayout.CENTER
        );
    }

    private JPanel crearEncabezado() {

        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setAlignmentX(
                LEFT_ALIGNMENT
        );

        JLabel titulo = new JLabel(
                "Operaciones Contables"
        );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        30
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

        JPanel linea = new JPanel();
        linea.setBackground(COLOR_PRIMARIO);
        linea.setPreferredSize(new Dimension(58, 4));
        linea.setMaximumSize(new Dimension(58, 4));
        linea.setAlignmentX(LEFT_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitulo);
        panel.add(Box.createVerticalStrut(12));
        panel.add(linea);

        return panel;
    }

    private JPanel crearGrid() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                1,
                                3,
                                18,
                                0
                        )
                );

        panel.setOpaque(false);

        panel.setAlignmentX(
                LEFT_ALIGNMENT
        );

        panel.setPreferredSize(
                new Dimension(
                        960,
                        395
                )
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        395
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
                        20
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
            String operacion,
            String rutaImagen,
            String categoria
    ) {

        TarjetaPanel tarjeta =
                new TarjetaPanel();

        tarjeta.setLayout(
                new BorderLayout()
        );

        tarjeta.setPreferredSize(
                new Dimension(
                        290,
                        395
                )
        );

        ImagenTarjetaPanel imagen =
                new ImagenTarjetaPanel(
                        rutaImagen
                );

        imagen.setPreferredSize(
                new Dimension(
                        0,
                        178
                )
        );

        tarjeta.add(
                imagen,
                BorderLayout.NORTH
        );

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(
                new BoxLayout(
                        cuerpo,
                        BoxLayout.Y_AXIS
                )
        );

        cuerpo.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        18,
                        18,
                        18
                )
        );

        JLabel etiqueta = new JLabel(
                categoria
        );

        etiqueta.setOpaque(true);
        etiqueta.setBackground(COLOR_CELESTE_SUAVE);
        etiqueta.setForeground(COLOR_PRIMARIO);
        etiqueta.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );
        etiqueta.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        8,
                        5,
                        8
                )
        );
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTitulo =
                new JLabel(titulo);

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

        lblTitulo.setAlignmentX(
                LEFT_ALIGNMENT
        );

        JLabel lblDescripcion =
                new JLabel(
                        "<html><div style='width:260px;'>"
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

        cuerpo.add(etiqueta);
        cuerpo.add(Box.createVerticalStrut(10));
        cuerpo.add(lblTitulo);
        cuerpo.add(Box.createVerticalStrut(7));
        cuerpo.add(lblDescripcion);
        cuerpo.add(Box.createVerticalGlue());
        cuerpo.add(Box.createVerticalStrut(12));
        cuerpo.add(boton);

        tarjeta.add(
                cuerpo,
                BorderLayout.CENTER
        );

        tarjeta.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {
                        tarjeta.setHover(true);
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {
                        tarjeta.setHover(false);
                    }
                }
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
                        10,
                        14,
                        10,
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
    }

    private JPanel crearPanelAyuda() {

        JPanel panel = new JPanel(
                new BorderLayout(
                        16,
                        0
                )
        );

        panel.setBackground(
                new Color(239, 246, 255)
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(191, 219, 254)
                        ),
                        BorderFactory.createEmptyBorder(
                                14,
                                18,
                                14,
                                18
                        )
                )
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        72
                )
        );

        panel.setAlignmentX(
                LEFT_ALIGNMENT
        );

        JLabel icono = new JLabel(
                "i",
                SwingConstants.CENTER
        );
        icono.setOpaque(true);
        icono.setBackground(new Color(219, 234, 254));
        icono.setForeground(COLOR_PRIMARIO);
        icono.setFont(new Font("Segoe UI", Font.BOLD, 16));
        icono.setPreferredSize(new Dimension(34, 34));

        JLabel texto = new JLabel(
                "<html><b>Accesos principales</b><br>"
                + "Desde estas tarjetas puedes registrar asientos, administrar productos "
                + "o realizar la apertura del período contable.</html>"
        );
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        texto.setForeground(COLOR_TEXTO);

        panel.add(icono, BorderLayout.WEST);
        panel.add(texto, BorderLayout.CENTER);

        return panel;
    }

    private BufferedImage cargarImagen(
            String ruta
    ) {

        try (
                InputStream entrada =
                        getClass().getResourceAsStream(
                                ruta
                        )
        ) {

            if (entrada != null) {
                return ImageIO.read(entrada);
            }

        } catch (IOException e) {
            System.err.println(
                    "No se pudo cargar la imagen: "
                    + ruta
            );
        }

        return null;
    }

    private class ImagenTarjetaPanel extends JPanel {

        private final BufferedImage imagen;

        ImagenTarjetaPanel(
                String ruta
        ) {
            imagen = cargarImagen(ruta);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );

            g2.setClip(
                    new RoundRectangle2D.Double(
                            0,
                            0,
                            getWidth(),
                            getHeight() + 20,
                            18,
                            18
                    )
            );

            g2.setColor(
                    new Color(226, 232, 240)
            );

            g2.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

            if (imagen != null) {

                double escala = Math.max(
                        (double) getWidth() / imagen.getWidth(),
                        (double) getHeight() / imagen.getHeight()
                );

                int ancho =
                        (int) Math.ceil(
                                imagen.getWidth() * escala
                        );

                int alto =
                        (int) Math.ceil(
                                imagen.getHeight() * escala
                        );

                int x =
                        (getWidth() - ancho) / 2;

                int y =
                        (getHeight() - alto) / 2;

                Image escalada =
                        imagen.getScaledInstance(
                                ancho,
                                alto,
                                Image.SCALE_SMOOTH
                        );

                g2.drawImage(
                        escalada,
                        x,
                        y,
                        null
                );

            } else {

                g2.setColor(COLOR_PRIMARIO);
                g2.setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );

                String texto = "Imagen no encontrada";

                int anchoTexto =
                        g2.getFontMetrics()
                                .stringWidth(texto);

                g2.drawString(
                        texto,
                        Math.max(
                                12,
                                (getWidth() - anchoTexto) / 2
                        ),
                        getHeight() / 2
                );
            }

            g2.dispose();
        }
    }

    private class TarjetaPanel extends JPanel {

        private boolean hover = false;

        TarjetaPanel() {
            setOpaque(false);
            setBorder(
                    BorderFactory.createEmptyBorder(
                            1,
                            1,
                            1,
                            1
                    )
            );
        }

        void setHover(
                boolean hover
        ) {
            this.hover = hover;
            repaint();
        }

        @Override
        protected void paintComponent(
                Graphics g
        ) {

            Graphics2D g2 =
                    (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (hover) {
                g2.setColor(
                        new Color(220, 230, 244)
                );

                g2.fillRoundRect(
                        3,
                        4,
                        getWidth() - 6,
                        getHeight() - 5,
                        20,
                        20
                );
            }

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(
                    0,
                    0,
                    getWidth() - 1,
                    getHeight() - 2,
                    18,
                    18
            );

            g2.setColor(
                    hover
                    ? new Color(147, 197, 253)
                    : COLOR_BORDE
            );

            g2.drawRoundRect(
                    0,
                    0,
                    getWidth() - 2,
                    getHeight() - 3,
                    18,
                    18
            );

            g2.dispose();

            super.paintComponent(g);
        }
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

        if ("APERTURA_PERIODO".equals(
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
