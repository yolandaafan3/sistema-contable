package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.BalanceGeneral;
import com.mycompany.sistemacontable.modelo.LineaBalanceGeneral;
import com.mycompany.sistemacontable.servicio.BalanceGeneralService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

public class PanelBalanceGeneral extends JPanel {

    private final BalanceGeneralService service =
            new BalanceGeneralService();

    private final JPanel detalle =
            new JPanel();

    private final JLabel lblEstado =
            new JLabel();

    private final JLabel lblTotalActivo =
            new JLabel("$0.00");

    private final JLabel lblTotalPasivo =
            new JLabel("$0.00");

    private final JLabel lblTotalPatrimonio =
            new JLabel("$0.00");

    private final JLabel lblTotalPasivoPatrimonio =
            new JLabel("$0.00");

    private final Color FONDO =
            new Color(245, 247, 250);

    private final Color TEXTO =
            new Color(30, 41, 59);

    private final Color SECUNDARIO =
            new Color(100, 116, 139);

    private final Color BORDE =
            new Color(226, 232, 240);

    private final Color PRIMARIO =
            new Color(37, 99, 235);

    private final Color PRIMARIO_HOVER =
            new Color(29, 78, 216);

    private final Color CELESTE =
            new Color(14, 165, 233);

    private final Color VERDE =
            new Color(22, 163, 74);

    private final Color VERDE_SUAVE =
            new Color(240, 253, 244);

    private final Color ROJO =
            new Color(220, 38, 38);

    private final Color ROJO_SUAVE =
            new Color(254, 242, 242);

    private final Color MORADO =
            new Color(126, 34, 206);

    public PanelBalanceGeneral() {

        setLayout(
                new BorderLayout(
                        0,
                        18
                )
        );

        setBackground(
                FONDO
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        26,
                        26,
                        26
                )
        );

        detalle.setOpaque(
                false
        );

        detalle.setLayout(
                new BoxLayout(
                        detalle,
                        BoxLayout.Y_AXIS
                )
        );

        add(
                crearZonaSuperior(),
                BorderLayout.NORTH
        );

        JScrollPane scroll =
                new JScrollPane(
                        detalle
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

        scroll.getViewport()
                .setBackground(
                        FONDO
                );

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(
                        18
                );

        add(
                scroll,
                BorderLayout.CENTER
        );

        add(
                crearPie(),
                BorderLayout.SOUTH
        );

        cargarBalanceGeneral();
    }

    private JPanel crearZonaSuperior() {

        JPanel zona =
                new JPanel();

        zona.setOpaque(
                false
        );

        zona.setLayout(
                new BoxLayout(
                        zona,
                        BoxLayout.Y_AXIS
                )
        );

        zona.add(
                crearEncabezado()
        );

        zona.add(
                Box.createVerticalStrut(
                        18
                )
        );

        zona.add(
                crearResumenPrincipal()
        );

        return zona;
    }

    private JPanel crearEncabezado() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(
                false
        );

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Balance General"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel subtitulo =
                new JLabel(
                        "Activos, pasivos y patrimonio del período activo."
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitulo.setForeground(
                SECUNDARIO
        );

        textos.add(
                titulo
        );

        textos.add(
                Box.createVerticalStrut(
                        5
                )
        );

        textos.add(
                subtitulo
        );

        JButton actualizar =
                crearBoton(
                        "↻  Actualizar"
                );

        actualizar.addActionListener(
                e -> cargarBalanceGeneral()
        );

        panel.add(
                textos,
                BorderLayout.WEST
        );

        panel.add(
                actualizar,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel crearResumenPrincipal() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                14,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        105
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Total Activo",
                        lblTotalActivo,
                        "A",
                        new Color(
                                219,
                                234,
                                254
                        ),
                        PRIMARIO
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Total Pasivo",
                        lblTotalPasivo,
                        "P",
                        new Color(
                                254,
                                226,
                                226
                        ),
                        ROJO
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Total Patrimonio",
                        lblTotalPatrimonio,
                        "◆",
                        new Color(
                                243,
                                232,
                                255
                        ),
                        MORADO
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Pasivo + Patrimonio",
                        lblTotalPasivoPatrimonio,
                        "✓",
                        VERDE_SUAVE,
                        VERDE
                )
        );

        return panel;
    }

    private JPanel crearTarjetaResumen(
            String titulo,
            JLabel valor,
            String iconoTexto,
            Color fondoIcono,
            Color colorIcono
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                14,
                                15,
                                14,
                                15
                        )
                )
        );

        JLabel icono =
                new JLabel(
                        iconoTexto,
                        SwingConstants.CENTER
                );

        icono.setOpaque(
                true
        );

        icono.setBackground(
                fondoIcono
        );

        icono.setForeground(
                colorIcono
        );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        18
                )
        );

        icono.setPreferredSize(
                new Dimension(
                        42,
                        42
                )
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(
                false
        );

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        lblTitulo.setForeground(
                SECUNDARIO
        );

        valor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        valor.setForeground(
                TEXTO
        );

        textos.add(
                lblTitulo
        );

        textos.add(
                Box.createVerticalStrut(
                        4
                )
        );

        textos.add(
                valor
        );

        tarjeta.add(
                icono,
                BorderLayout.WEST
        );

        tarjeta.add(
                textos,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    public final void cargarBalanceGeneral() {

        detalle.removeAll();

        lblEstado.setText(
                "Sin datos del período activo"
        );

        lblEstado.setForeground(
                SECUNDARIO
        );

        lblTotalActivo.setText(
                "$0.00"
        );

        lblTotalPasivo.setText(
                "$0.00"
        );

        lblTotalPatrimonio.setText(
                "$0.00"
        );

        lblTotalPasivoPatrimonio.setText(
                "$0.00"
        );

        try {

            BalanceGeneral balance =
                    service.generar();

            lblTotalActivo.setText(
                    dinero(
                            balance.getTotalActivo()
                    )
            );

            lblTotalPasivo.setText(
                    dinero(
                            balance.getTotalPasivo()
                    )
            );

            lblTotalPatrimonio.setText(
                    dinero(
                            balance.getTotalPatrimonio()
                    )
            );

            lblTotalPasivoPatrimonio.setText(
                    dinero(
                            balance.getTotalPasivoPatrimonio()
                    )
            );

            detalle.add(
                    crearTarjetaGrupo(
                            "ACTIVO",
                            "Recursos y bienes controlados por la empresa",
                            PRIMARIO,
                            crearBloqueCuentas(
                                    "Activo corriente",
                                    balance.getActivosCorrientes(),
                                    balance.getTotalActivoCorriente()
                            ),
                            crearBloqueCuentas(
                                    "Activo no corriente",
                                    balance.getActivosNoCorrientes(),
                                    balance.getTotalActivoNoCorriente()
                            ),
                            "TOTAL ACTIVO",
                            balance.getTotalActivo()
                    )
            );

            detalle.add(
                    Box.createVerticalStrut(
                            14
                    )
            );

            detalle.add(
                    crearTarjetaGrupo(
                            "PASIVO",
                            "Obligaciones y deudas pendientes de la empresa",
                            ROJO,
                            crearBloqueCuentas(
                                    "Pasivo corriente",
                                    balance.getPasivosCorrientes(),
                                    balance.getTotalPasivoCorriente()
                            ),
                            crearBloqueCuentas(
                                    "Pasivo no corriente",
                                    balance.getPasivosNoCorrientes(),
                                    balance.getTotalPasivoNoCorriente()
                            ),
                            "TOTAL PASIVO",
                            balance.getTotalPasivo()
                    )
            );

            detalle.add(
                    Box.createVerticalStrut(
                            14
                    )
            );

            detalle.add(
                    crearTarjetaPatrimonio(
                            balance.getPatrimonio(),
                            balance.getTotalPatrimonio(),
                            balance.getTotalPasivoPatrimonio()
                    )
            );

            detalle.add(
                    Box.createVerticalStrut(
                            6
                    )
            );

            lblEstado.setText(
                    (
                            balance.isCuadrado()
                                    ? "✓ Balance General cuadrado"
                                    : "⚠ Balance General no cuadrado"
                    )
                    + "   ·   Diferencia: "
                    + dinero(
                            balance.getDiferencia()
                    )
            );

            lblEstado.setForeground(
                    balance.isCuadrado()
                            ? VERDE
                            : ROJO
            );

            lblTotalPasivoPatrimonio.setForeground(
                    balance.isCuadrado()
                            ? VERDE
                            : ROJO
            );

            detalle.revalidate();
            detalle.repaint();

        } catch (
                Exception e
        ) {

            mostrarError(
                    e
            );
        }
    }

    private JPanel crearTarjetaGrupo(
            String titulo,
            String descripcion,
            Color acento,
            JPanel bloque1,
            JPanel bloque2,
            String tituloTotal,
            BigDecimal total
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout()
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );

        tarjeta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        tarjeta.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1000
                )
        );

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

        JPanel cabecera =
                new JPanel(
                        new BorderLayout()
                );

        cabecera.setOpaque(
                false
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(
                false
        );

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
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
                        15
                )
        );

        lblTitulo.setForeground(
                acento
        );

        JLabel lblDescripcion =
                new JLabel(
                        descripcion
                );

        lblDescripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        lblDescripcion.setForeground(
                SECUNDARIO
        );

        textos.add(
                lblTitulo
        );

        textos.add(
                Box.createVerticalStrut(
                        3
                )
        );

        textos.add(
                lblDescripcion
        );

        JLabel chip =
                new JLabel(
                        titulo,
                        SwingConstants.CENTER
                );

        chip.setOpaque(
                true
        );

        chip.setBackground(
                new Color(
                        248,
                        250,
                        252
                )
        );

        chip.setForeground(
                acento
        );

        chip.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        10
                )
        );

        chip.setBorder(
                BorderFactory.createEmptyBorder(
                        6,
                        10,
                        6,
                        10
                )
        );

        cabecera.add(
                textos,
                BorderLayout.WEST
        );

        cabecera.add(
                chip,
                BorderLayout.EAST
        );

        contenido.add(
                cabecera
        );

        contenido.add(
                Box.createVerticalStrut(
                        14
                )
        );

        contenido.add(
                bloque1
        );

        contenido.add(
                Box.createVerticalStrut(
                        10
                )
        );

        contenido.add(
                bloque2
        );

        contenido.add(
                Box.createVerticalStrut(
                        12
                )
        );

        contenido.add(
                crearFilaTotalGeneral(
                        tituloTotal,
                        total,
                        acento
                )
        );

        tarjeta.add(
                contenido,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearBloqueCuentas(
            String titulo,
            List<LineaBalanceGeneral> cuentas,
            BigDecimal total
    ) {

        JPanel bloque =
                new JPanel();

        bloque.setOpaque(
                false
        );

        bloque.setLayout(
                new BoxLayout(
                        bloque,
                        BoxLayout.Y_AXIS
                )
        );

        bloque.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        241,
                                        245,
                                        249
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                12,
                                14,
                                12,
                                14
                        )
                )
        );

        bloque.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        bloque.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1000
                )
        );

        JLabel lblTitulo =
                new JLabel(
                        titulo.toUpperCase()
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblTitulo.setForeground(
                PRIMARIO
        );

        bloque.add(
                lblTitulo
        );

        bloque.add(
                Box.createVerticalStrut(
                        8
                )
        );

        if (
                cuentas == null
                ||
                cuentas.isEmpty()
        ) {

            JLabel vacio =
                    new JLabel(
                            "Sin cuentas con saldo"
                    );

            vacio.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            12
                    )
            );

            vacio.setForeground(
                    SECUNDARIO
            );

            bloque.add(
                    vacio
            );

        } else {

            for (
                    LineaBalanceGeneral cuenta
                    : cuentas
            ) {

                bloque.add(
                        crearFilaCuenta(
                                cuenta.getCodigo()
                                + "  "
                                + cuenta.getNombre(),
                                cuenta.getSaldo()
                        )
                );
            }
        }

        bloque.add(
                Box.createVerticalStrut(
                        6
                )
        );

        bloque.add(
                crearFilaSubtotal(
                        "Total " + titulo,
                        total
                )
        );

        return bloque;
    }

    private JPanel crearTarjetaPatrimonio(
            List<LineaBalanceGeneral> patrimonio,
            BigDecimal totalPatrimonio,
            BigDecimal totalPasivoPatrimonio
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout()
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );

        tarjeta.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        tarjeta.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        1000
                )
        );

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

        JPanel cabecera =
                new JPanel(
                        new BorderLayout()
                );

        cabecera.setOpaque(
                false
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(
                false
        );

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "PATRIMONIO"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        titulo.setForeground(
                MORADO
        );

        JLabel descripcion =
                new JLabel(
                        "Aportes y resultados acumulados pertenecientes a los propietarios"
                );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        descripcion.setForeground(
                SECUNDARIO
        );

        textos.add(
                titulo
        );

        textos.add(
                Box.createVerticalStrut(
                        3
                )
        );

        textos.add(
                descripcion
        );

        cabecera.add(
                textos,
                BorderLayout.WEST
        );

        contenido.add(
                cabecera
        );

        contenido.add(
                Box.createVerticalStrut(
                        14
                )
        );

        if (
                patrimonio == null
                ||
                patrimonio.isEmpty()
        ) {

            JLabel vacio =
                    new JLabel(
                            "Sin cuentas patrimoniales con saldo"
                    );

            vacio.setForeground(
                    SECUNDARIO
            );

            vacio.setFont(
                    new Font(
                            "Segoe UI",
                            Font.PLAIN,
                            12
                    )
            );

            contenido.add(
                    vacio
            );

        } else {

            for (
                    LineaBalanceGeneral cuenta
                    : patrimonio
            ) {

                contenido.add(
                        crearFilaCuenta(
                                cuenta.getCodigo()
                                + "  "
                                + cuenta.getNombre(),
                                cuenta.getSaldo()
                        )
                );
            }
        }

        contenido.add(
                Box.createVerticalStrut(
                        8
                )
        );

        contenido.add(
                crearFilaSubtotal(
                        "TOTAL PATRIMONIO",
                        totalPatrimonio
                )
        );

        contenido.add(
                Box.createVerticalStrut(
                        10
                )
        );

        contenido.add(
                crearFilaTotalGeneral(
                        "TOTAL PASIVO + PATRIMONIO",
                        totalPasivoPatrimonio,
                        MORADO
                )
        );

        tarjeta.add(
                contenido,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearFilaCuenta(
            String texto,
            BigDecimal valor
    ) {

        JPanel fila =
                new JPanel(
                        new BorderLayout(
                                18,
                                0
                        )
                );

        fila.setOpaque(
                false
        );

        fila.setBorder(
                BorderFactory.createEmptyBorder(
                        6,
                        0,
                        6,
                        0
                )
        );

        fila.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        36
                )
        );

        JLabel nombre =
                new JLabel(
                        texto
                );

        nombre.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        nombre.setForeground(
                TEXTO
        );

        JLabel numero =
                new JLabel(
                        dinero(
                                valor
                        ),
                        SwingConstants.RIGHT
                );

        numero.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        numero.setForeground(
                TEXTO
        );

        fila.add(
                nombre,
                BorderLayout.WEST
        );

        fila.add(
                numero,
                BorderLayout.EAST
        );

        return fila;
    }

    private JPanel crearFilaSubtotal(
            String texto,
            BigDecimal valor
    ) {

        JPanel fila =
                crearFilaCuenta(
                        texto,
                        valor
                );

        fila.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                1,
                                0,
                                0,
                                0,
                                new Color(
                                        241,
                                        245,
                                        249
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                0,
                                3,
                                0
                        )
                )
        );

        for (
                Component componente
                : fila.getComponents()
        ) {

            if (
                    componente
                    instanceof JLabel
            ) {

                JLabel label =
                        (JLabel) componente;

                label.setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );
            }
        }

        return fila;
    }

    private JPanel crearFilaTotalGeneral(
            String texto,
            BigDecimal valor,
            Color acento
    ) {

        JPanel fila =
                new JPanel(
                        new BorderLayout(
                                18,
                                0
                        )
                );

        fila.setBackground(
                new Color(
                        248,
                        250,
                        252
                )
        );

        fila.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                11,
                                13,
                                11,
                                13
                        )
                )
        );

        JLabel nombre =
                new JLabel(
                        texto
                );

        nombre.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        nombre.setForeground(
                acento
        );

        JLabel numero =
                new JLabel(
                        dinero(
                                valor
                        ),
                        SwingConstants.RIGHT
                );

        numero.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        numero.setForeground(
                acento
        );

        fila.add(
                nombre,
                BorderLayout.WEST
        );

        fila.add(
                numero,
                BorderLayout.EAST
        );

        return fila;
    }

    private JPanel crearPie() {

        JPanel pie =
                new JPanel(
                        new BorderLayout()
                );

        pie.setBackground(
                Color.WHITE
        );

        pie.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                12,
                                16,
                                12,
                                16
                        )
                )
        );

        lblEstado.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        pie.add(
                lblEstado,
                BorderLayout.WEST
        );

        return pie;
    }

    private JButton crearBoton(
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
                PRIMARIO
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

                        boton.setBackground(
                                PRIMARIO_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                PRIMARIO
                        );
                    }
                }
        );

        return boton;
    }

    private String dinero(
            BigDecimal valor
    ) {

        if (
                valor == null
        ) {

            valor =
                    BigDecimal.ZERO;
        }

        return "$"
                + String.format(
                        "%,.2f",
                        valor.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
    }

    private void mostrarError(
            Throwable error
    ) {

        String mensaje =
                "Ocurrió un error desconocido.";

        for (
                Throwable actual = error;
                actual != null;
                actual = actual.getCause()
        ) {

            if (
                    actual.getMessage() != null
                    &&
                    !actual.getMessage().isBlank()
            ) {

                mensaje =
                        actual.getMessage();
            }
        }

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "No se pudo cargar el Balance General",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
