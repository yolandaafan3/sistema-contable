package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.AperturaPeriodoService;
import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.Producto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.LocalDate;
import java.time.ZoneId;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JComboBox;
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
    private JComboBox<Producto> cmbProductoInventario;
    private JTextField txtCostoInicial;
    private JLabel lblUnidadesCalculadas;
    private final ProductoDAO productoDAO = new ProductoDAO();

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

        setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        setSize(
                1180,
                760
        );

        setMinimumSize(
                new Dimension(
                        920,
                        680
                )
        );

        setResizable(true);

        setLayout(
                new BorderLayout()
        );

        getContentPane().setBackground(
                COLOR_FONDO
        );

        if (MarcaUI.iconoVentana() != null) {
            setIconImage(MarcaUI.iconoVentana());
        }

        setLocationRelativeTo(
                getOwner()
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

        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setBackground(Color.WHITE);

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
                                20,
                                32,
                                20,
                                32
                        )
                )
        );

        JPanel icono = crearIconoCalendario();

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo = new JLabel("Apertura del Período");
        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );
        titulo.setForeground(COLOR_TEXTO);
        titulo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descripcion = new JLabel(
                "Registra los valores monetarios con los que inicia la empresa."
        );
        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );
        descripcion.setForeground(COLOR_SECUNDARIO);
        descripcion.setAlignmentX(LEFT_ALIGNMENT);

        textos.add(Box.createVerticalGlue());
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(descripcion);
        textos.add(Box.createVerticalGlue());

        panel.add(icono, BorderLayout.WEST);
        panel.add(textos, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearContenido() {

        JPanel fondo = new JPanel(new BorderLayout());
        fondo.setBackground(COLOR_FONDO);
        fondo.setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        32,
                        24,
                        32
                )
        );

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDE),
                        BorderFactory.createEmptyBorder(
                                24,
                                26,
                                24,
                                26
                        )
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 7, 0);

        tarjeta.add(
                crearEtiqueta("Fecha de apertura"),
                gbc
        );

        gbc.gridy++;
        spFecha = crearSelectorFecha();
        tarjeta.add(spFecha, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 7, 0);
        tarjeta.add(
                crearEtiqueta("Efectivo inicial ($)"),
                gbc
        );

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        txtEfectivo = crearCampoTexto();
        txtEfectivo.setText("");
        txtEfectivo.setToolTipText(
                "Ingresa el efectivo disponible al inicio del período."
        );
        tarjeta.add(txtEfectivo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 7, 0);
        tarjeta.add(
                crearEtiqueta("Valor del inventario inicial ($)"),
                gbc
        );

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        txtInventarioInicial = crearCampoTexto();
        txtInventarioInicial.setText("0.00");
        txtInventarioInicial.setEditable(true);
        txtInventarioInicial.setToolTipText(
                "Ingresa manualmente el valor monetario del inventario inicial. Se sumará al efectivo para calcular el Capital Social."
        );
        tarjeta.add(txtInventarioInicial, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 7, 0);
        tarjeta.add(
                crearEtiqueta("Producto del inventario inicial"),
                gbc
        );

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        cmbProductoInventario = new JComboBox<>();
        cmbProductoInventario.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );
        cmbProductoInventario.setPreferredSize(new Dimension(200, 42));
        cmbProductoInventario.setBackground(Color.WHITE);
        try {
            List<Producto> productos = productoDAO.listarProductosActivos();
            for (Producto p : productos) {
                cmbProductoInventario.addItem(p);
            }
        } catch (Exception ignored) {
        }
        tarjeta.add(cmbProductoInventario, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 7, 0);
        tarjeta.add(
                crearEtiqueta("Costo unitario inicial ($)"),
                gbc
        );

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        txtCostoInicial = crearCampoTexto();
        txtCostoInicial.setToolTipText(
                "Costo por unidad del inventario inicial. Este valor no lleva IVA."
        );
        tarjeta.add(txtCostoInicial, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 0, 0);
        lblUnidadesCalculadas = crearIndicadorUnidades();
        tarjeta.add(lblUnidadesCalculadas, gbc);

        Runnable actualizarCostoYUnidades = () -> {
            Producto seleccionado = (Producto) cmbProductoInventario.getSelectedItem();
            if (seleccionado != null
                    && (txtCostoInicial.getText() == null
                    || txtCostoInicial.getText().isBlank())) {

                BigDecimal costo = seleccionado.getCostoInicial();

                if (costo != null
                        && costo.compareTo(BigDecimal.ZERO) > 0) {
                    txtCostoInicial.setText(
                            costo.setScale(2, RoundingMode.HALF_UP).toPlainString()
                    );
                }
            }

            actualizarUnidadesCalculadas();
        };

        cmbProductoInventario.addActionListener(e -> {
            Producto seleccionado = (Producto) cmbProductoInventario.getSelectedItem();

            if (seleccionado != null) {
                BigDecimal costo = seleccionado.getCostoInicial();
                txtCostoInicial.setText(
                        costo != null
                        && costo.compareTo(BigDecimal.ZERO) > 0
                                ? costo.setScale(2, RoundingMode.HALF_UP).toPlainString()
                                : ""
                );
            }

            actualizarUnidadesCalculadas();
        });

        txtInventarioInicial.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    @Override
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        actualizarUnidadesCalculadas();
                    }

                    @Override
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        actualizarUnidadesCalculadas();
                    }

                    @Override
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        actualizarUnidadesCalculadas();
                    }
                }
        );

        txtCostoInicial.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    @Override
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        actualizarUnidadesCalculadas();
                    }

                    @Override
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        actualizarUnidadesCalculadas();
                    }

                    @Override
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        actualizarUnidadesCalculadas();
                    }
                }
        );

        actualizarCostoYUnidades.run();

        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 0, 0);
        tarjeta.add(crearTarjetaInformativa(), gbc);

        fondo.add(tarjeta, BorderLayout.NORTH);

        return fondo;
    }

    private JPanel crearPie() {

        JPanel panel = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        12,
                        14
                )
        );

        panel.setBackground(Color.WHITE);
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                0,
                                0,
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                0,
                                20,
                                0,
                                20
                        )
                )
        );

        JButton btnCancelar = crearBotonSecundario("Cancelar");
        JButton btnRegistrar = crearBotonPrincipal("Registrar apertura");

        btnCancelar.addActionListener(e -> dispose());
        btnRegistrar.addActionListener(e -> registrarApertura());

        panel.add(btnCancelar);
        panel.add(btnRegistrar);

        return panel;
    }

    private JLabel crearEtiqueta(
            String texto
    ) {

        JLabel label = new JLabel(texto);
        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );
        label.setForeground(COLOR_TEXTO);

        return label;
    }

    private JTextField crearCampoTexto() {

        JTextField campo = new JTextField();
        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );
        campo.setPreferredSize(new Dimension(0, 42));
        campo.setBackground(Color.WHITE);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_AZUL);
        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(203, 213, 225)
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                10,
                                8,
                                10
                        )
                )
        );

        return campo;
    }

    private JSpinner crearSelectorFecha() {

        Calendar calendario = Calendar.getInstance();

        calendario.set(Calendar.YEAR, 2026);
        calendario.set(Calendar.MONTH, Calendar.JANUARY);
        calendario.set(Calendar.DAY_OF_MONTH, 1);
        calendario.set(Calendar.HOUR_OF_DAY, 12);
        calendario.set(Calendar.MINUTE, 0);
        calendario.set(Calendar.SECOND, 0);
        calendario.set(Calendar.MILLISECOND, 0);

        SpinnerDateModel modelo = new SpinnerDateModel(
                calendario.getTime(),
                null,
                null,
                Calendar.DAY_OF_MONTH
        );

        JSpinner spinner = new JSpinner(modelo);
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
        spinner.setPreferredSize(new Dimension(0, 42));
        spinner.setBackground(Color.WHITE);
        spinner.setBorder(
                BorderFactory.createLineBorder(
                        new Color(203, 213, 225)
                )
        );

        return spinner;
    }

    private JButton crearBotonPrincipal(
            String texto
    ) {

        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );
        boton.setForeground(Color.WHITE);
        boton.setBackground(COLOR_AZUL);
        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        11,
                        22,
                        11,
                        22
                )
        );
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        boton.setBackground(COLOR_AZUL_HOVER);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        boton.setBackground(COLOR_AZUL);
                    }
                }
        );

        return boton;
    }

    private JButton crearBotonSecundario(
            String texto
    ) {

        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );
        boton.setForeground(COLOR_TEXTO);
        boton.setBackground(Color.WHITE);
        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_BORDE),
                        BorderFactory.createEmptyBorder(
                                10,
                                20,
                                10,
                                20
                        )
                )
        );
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        boton.setBackground(new Color(248, 250, 252));
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        boton.setBackground(Color.WHITE);
                    }
                }
        );

        return boton;
    }

    private JLabel crearIndicadorUnidades() {

        JLabel label = new JLabel("Unidades iniciales calculadas: 0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(COLOR_AZUL);
        label.setOpaque(true);
        label.setBackground(new Color(239, 246, 255));
        label.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(191, 219, 254)
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                12,
                                8,
                                12
                        )
                )
        );

        return label;
    }

    private JPanel crearTarjetaInformativa() {

        JPanel panel = new JPanel(new BorderLayout(14, 0));
        panel.setBackground(new Color(239, 246, 255));
        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(191, 219, 254)
                        ),
                        BorderFactory.createEmptyBorder(
                                14,
                                16,
                                14,
                                16
                        )
                )
        );

        JPanel icono = crearIconoInformacion();

        JLabel ayuda = new JLabel(
                "<html>"
                + "<div style='width:760px;'>"
                + "<b>Registro de apertura</b><br><br>"
                + "La apertura registra Caja e Inventario al Debe y Capital Social al Haber. "
                + "Capital Social = Efectivo inicial + Inventario inicial. "
                + "Si existe inventario inicial, selecciona el producto y su costo unitario. "
                + "El sistema calculará las unidades como Valor del inventario ÷ Costo unitario, "
                + "creará el movimiento y la capa PEPS inicial sin aplicar IVA."
                + "</div>"
                + "</html>"
        );
        ayuda.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );
        ayuda.setForeground(COLOR_SECUNDARIO);

        panel.add(icono, BorderLayout.WEST);
        panel.add(ayuda, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearIconoCalendario() {

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(new Color(219, 234, 254));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);

                int x = 15;
                int y = 15;
                int w = 28;
                int h = 26;

                g2.setColor(COLOR_AZUL);
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.drawRoundRect(x, y + 3, w, h - 3, 5, 5);
                g2.drawLine(x, y + 11, x + w, y + 11);
                g2.drawLine(x + 7, y, x + 7, y + 7);
                g2.drawLine(x + w - 7, y, x + w - 7, y + 7);
                g2.fillOval(x + 7, y + 16, 4, 4);
                g2.fillOval(x + 16, y + 16, 4, 4);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(58, 58));
        panel.setMinimumSize(new Dimension(58, 58));
        panel.setMaximumSize(new Dimension(58, 58));

        return panel;
    }

    private JPanel crearIconoInformacion() {

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(new Color(219, 234, 254));
                g2.fillOval(2, 2, 32, 32);

                g2.setColor(COLOR_AZUL);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                g2.drawString("i", 16, 25);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(36, 36));
        panel.setMinimumSize(new Dimension(36, 36));
        panel.setMaximumSize(new Dimension(36, 36));

        return panel;
    }

    private void actualizarUnidadesCalculadas() {
        if (lblUnidadesCalculadas == null) return;
        try {
            String invTxt = txtInventarioInicial == null ? "" : txtInventarioInicial.getText().trim().replace(',', '.');
            String costoTxt = txtCostoInicial == null ? "" : txtCostoInicial.getText().trim().replace(',', '.');
            if (invTxt.isBlank() || costoTxt.isBlank()) {
                lblUnidadesCalculadas.setText("Unidades iniciales calculadas: 0");
                return;
            }
            BigDecimal inventario = new BigDecimal(invTxt);
            BigDecimal costo = new BigDecimal(costoTxt);
            if (inventario.compareTo(BigDecimal.ZERO) <= 0 || costo.compareTo(BigDecimal.ZERO) <= 0) {
                lblUnidadesCalculadas.setText("Unidades iniciales calculadas: 0");
                return;
            }
            BigDecimal unidades = inventario.divide(costo, 0, RoundingMode.HALF_UP);
            lblUnidadesCalculadas.setText("Unidades iniciales calculadas: "
                    + unidades.stripTrailingZeros().toPlainString());
        } catch (Exception e) {
            lblUnidadesCalculadas.setText("Unidades iniciales calculadas: —");
        }
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
                            "inventario inicial"
                    );

            if (inventario.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("El inventario inicial no puede ser negativo.");
            }

            Producto productoInventario = (Producto) cmbProductoInventario.getSelectedItem();
            BigDecimal costoInicial = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal unidadesIniciales = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
            if (inventario.compareTo(BigDecimal.ZERO) > 0) {
                if (productoInventario == null) {
                    throw new IllegalArgumentException("Selecciona el producto del inventario inicial.");
                }
                costoInicial = convertirDecimal(txtCostoInicial.getText(), "costo unitario inicial");
                if (costoInicial.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("El costo unitario inicial debe ser mayor que cero.");
                }
                unidadesIniciales = inventario.divide(costoInicial, 0, RoundingMode.HALF_UP);
            }

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
                            Producto: %s
                            Costo unitario inicial: $%,.2f
                            Unidades iniciales: %s

                            Esto generará el Asiento N.º 1 y, si hay inventario, su capa PEPS inicial.

                            ¿Deseas continuar?
                            """.formatted(
                                    efectivo,
                                    inventario,
                                    capital,
                                    productoInventario == null ? "—" : productoInventario.toString(),
                                    costoInicial,
                                    unidadesIniciales.stripTrailingZeros().toPlainString()
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
                            inventario,
                            productoInventario == null ? null : productoInventario.getIdProducto(),
                            costoInicial
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