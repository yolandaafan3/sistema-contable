package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.EstadoResultados;
import com.mycompany.sistemacontable.servicio.EstadoResultadosService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;

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

public class PanelEstadoResultados extends JPanel {

    private final EstadoResultadosService service;

    private final JPanel detalle;

    private final JLabel lblVentasNetas = new JLabel("$0.00");
    private final JLabel lblCostoVentas = new JLabel("$0.00");
    private final JLabel lblUtilidadBruta = new JLabel("$0.00");
    private final JLabel lblUtilidadEjercicio = new JLabel("$0.00");

    private final JLabel lblEstadoResultado = new JLabel("Sin datos del período");

    private final Color FONDO = new Color(245, 247, 250);
    private final Color TEXTO = new Color(30, 41, 59);
    private final Color SECUNDARIO = new Color(100, 116, 139);
    private final Color BORDE = new Color(226, 232, 240);
    private final Color PRIMARIO = new Color(37, 99, 235);
    private final Color PRIMARIO_HOVER = new Color(29, 78, 216);
    private final Color CELESTE = new Color(14, 165, 233);
    private final Color VERDE = new Color(22, 163, 74);
    private final Color VERDE_SUAVE = new Color(240, 253, 244);
    private final Color ROJO = new Color(220, 38, 38);
    private final Color ROJO_SUAVE = new Color(254, 242, 242);
    private final Color MORADO = new Color(126, 34, 206);

    public PanelEstadoResultados() {

        service = new EstadoResultadosService();

        detalle = new JPanel();
        detalle.setOpaque(false);
        detalle.setLayout(new BoxLayout(detalle, BoxLayout.Y_AXIS));

        setLayout(new BorderLayout(0, 18));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 26, 26, 26));

        add(crearZonaSuperior(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(detalle);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(FONDO);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        add(scroll, BorderLayout.CENTER);

        cargarEstadoResultados();
    }

    private JPanel crearZonaSuperior() {

        JPanel zona = new JPanel();
        zona.setOpaque(false);
        zona.setLayout(new BoxLayout(zona, BoxLayout.Y_AXIS));

        zona.add(crearEncabezado());
        zona.add(Box.createVerticalStrut(18));
        zona.add(crearResumenPrincipal());

        return zona;
    }

    private JPanel crearEncabezado() {

        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Estado de Resultados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Ventas netas, compras ajustadas, costo de ventas, gastos y utilidad del ejercicio."
        );
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JButton actualizar = crearBoton("↻  Actualizar");
        actualizar.addActionListener(e -> cargarEstadoResultados());

        panel.add(textos, BorderLayout.WEST);
        panel.add(actualizar, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearResumenPrincipal() {

        JPanel panel = new JPanel(new GridLayout(1, 4, 14, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        panel.add(crearTarjetaResumen(
                "Ventas netas",
                lblVentasNetas,
                "$",
                new Color(219, 234, 254),
                PRIMARIO
        ));

        panel.add(crearTarjetaResumen(
                "Costo de ventas",
                lblCostoVentas,
                "C",
                new Color(224, 242, 254),
                CELESTE
        ));

        panel.add(crearTarjetaResumen(
                "Utilidad bruta",
                lblUtilidadBruta,
                "↗",
                new Color(243, 232, 255),
                MORADO
        ));

        panel.add(crearTarjetaResumen(
                "Utilidad del ejercicio",
                lblUtilidadEjercicio,
                "✓",
                VERDE_SUAVE,
                VERDE
        ));

        return panel;
    }

    private JPanel crearTarjetaResumen(
            String titulo,
            JLabel valor,
            String iconoTexto,
            Color fondoIcono,
            Color colorIcono
    ) {

        JPanel tarjeta = new JPanel(new BorderLayout(12, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(14, 15, 14, 15)
        ));

        JLabel icono = new JLabel(iconoTexto, SwingConstants.CENTER);
        icono.setOpaque(true);
        icono.setBackground(fondoIcono);
        icono.setForeground(colorIcono);
        icono.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        icono.setPreferredSize(new Dimension(42, 42));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(SECUNDARIO);

        valor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valor.setForeground(TEXTO);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(valor);

        tarjeta.add(icono, BorderLayout.WEST);
        tarjeta.add(textos, BorderLayout.CENTER);

        return tarjeta;
    }

    public final void cargarEstadoResultados() {

        detalle.removeAll();

        try {

            EstadoResultados estado = service.generar();

            lblVentasNetas.setText(dinero(estado.getVentasNetas()));
            lblCostoVentas.setText(dinero(estado.getCostoVentas()));
            lblUtilidadBruta.setText(dinero(estado.getUtilidadBruta()));
            lblUtilidadEjercicio.setText(dinero(estado.getUtilidadEjercicio()));

            actualizarTarjetaUtilidad(estado.getUtilidadEjercicio());

            detalle.add(crearTarjetaSeccion(
                    "VENTAS",
                    "Ingresos netos generados durante el período",
                    new Object[][]{
                        {"Ventas", estado.getVentas(), false},
                        {"(-) Devoluciones sobre ventas", estado.getDevolucionVentas(), false},
                        {"(-) Descuentos sobre ventas", estado.getDescuentoVentas(), false},
                        {"Ventas netas", estado.getVentasNetas(), true}
                    }
            ));

            detalle.add(Box.createVerticalStrut(14));

            detalle.add(crearTarjetaSeccion(
                    "COSTO DE VENTAS",
                    "Compras y ajustes utilizados para determinar el costo del período",
                    new Object[][]{
                        {"Compras", estado.getCompras(), false},
                        {"(+) Gastos sobre compras", estado.getGastosSobreCompras(), false},
                        {"Compras totales", estado.getComprasTotales(), true},
                        {"(-) Devoluciones sobre compras", estado.getDevolucionCompras(), false},
                        {"(-) Descuentos sobre compras", estado.getDescuentoCompras(), false},
                        {"Compras netas", estado.getComprasNetas(), true},
                        {"Costo de ventas", estado.getCostoVentas(), true}
                    }
            ));

            detalle.add(Box.createVerticalStrut(14));

            detalle.add(crearTarjetaResultado(
                    estado
            ));

            detalle.add(Box.createVerticalStrut(12));

            detalle.add(crearBarraEstado(
                    estado.getUtilidadEjercicio()
            ));

            detalle.add(Box.createVerticalStrut(6));

            detalle.revalidate();
            detalle.repaint();

        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private JPanel crearTarjetaSeccion(
            String titulo,
            String descripcion,
            Object[][] lineas
    ) {

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitulo.setForeground(PRIMARIO);

        JLabel lblDescripcion = new JLabel(descripcion);
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDescripcion.setForeground(SECUNDARIO);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(lblDescripcion);

        JLabel chip = new JLabel("DETALLE", SwingConstants.CENTER);
        chip.setOpaque(true);
        chip.setBackground(new Color(239, 246, 255));
        chip.setForeground(PRIMARIO);
        chip.setFont(new Font("Segoe UI", Font.BOLD, 10));
        chip.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        cabecera.add(textos, BorderLayout.WEST);
        cabecera.add(chip, BorderLayout.EAST);

        contenido.add(cabecera);
        contenido.add(Box.createVerticalStrut(14));

        for (Object[] linea : lineas) {
            contenido.add(crearFilaResultado(
                    (String) linea[0],
                    (BigDecimal) linea[1],
                    (Boolean) linea[2]
            ));
        }

        tarjeta.add(contenido, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearTarjetaResultado(
            EstadoResultados estado
    ) {

        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1000));

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("RESULTADO");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(PRIMARIO);

        JLabel descripcion = new JLabel(
                "Resultado operacional y utilidad final obtenida durante el período"
        );
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcion.setForeground(SECUNDARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(3));
        textos.add(descripcion);

        cabecera.add(textos, BorderLayout.WEST);

        contenido.add(cabecera);
        contenido.add(Box.createVerticalStrut(14));

        contenido.add(crearFilaResultado(
                "Utilidad bruta",
                estado.getUtilidadBruta(),
                true
        ));

        contenido.add(crearFilaResultado(
                "Gastos administrativos",
                estado.getGastosAdministrativos(),
                false
        ));

        contenido.add(crearFilaResultado(
                "Gastos de venta",
                estado.getGastosVenta(),
                false
        ));

        contenido.add(crearFilaResultado(
                "Gastos financieros",
                estado.getGastosFinancieros(),
                false
        ));

        contenido.add(crearFilaResultado(
                "Total gastos operacionales",
                estado.getTotalGastosOperativos(),
                true
        ));

        contenido.add(crearFilaResultado(
                "Utilidad operacional antes del impuesto",
                estado.getUtilidadOperacionalAntesImpuesto(),
                true
        ));

        contenido.add(crearFilaResultado(
                "(+) Otros productos / Productos financieros",
                estado.getOtrosProductos(),
                false
        ));

        contenido.add(Box.createVerticalStrut(8));
        contenido.add(crearFilaUtilidadFinal(
                estado.getUtilidadEjercicio()
        ));

        tarjeta.add(contenido, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearFilaResultado(
            String texto,
            BigDecimal valor,
            boolean fuerte
    ) {

        JPanel fila = new JPanel(new BorderLayout(18, 0));
        fila.setOpaque(false);
        fila.setBorder(BorderFactory.createEmptyBorder(7, 0, 7, 0));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel nombre = new JLabel(texto);
        nombre.setFont(new Font(
                "Segoe UI",
                fuerte ? Font.BOLD : Font.PLAIN,
                13
        ));
        nombre.setForeground(TEXTO);

        JLabel numero = new JLabel(dinero(valor), SwingConstants.RIGHT);
        numero.setFont(new Font(
                "Segoe UI",
                fuerte ? Font.BOLD : Font.PLAIN,
                13
        ));

        if (valor != null && valor.compareTo(BigDecimal.ZERO) < 0) {
            numero.setForeground(ROJO);
        } else {
            numero.setForeground(TEXTO);
        }

        fila.add(nombre, BorderLayout.WEST);
        fila.add(numero, BorderLayout.EAST);

        if (fuerte) {
            fila.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                            1,
                            0,
                            0,
                            0,
                            new Color(241, 245, 249)
                    ),
                    BorderFactory.createEmptyBorder(9, 0, 7, 0)
            ));
        }

        return fila;
    }

    private JPanel crearFilaUtilidadFinal(
            BigDecimal utilidad
    ) {

        boolean positiva =
                utilidad != null
                && utilidad.compareTo(BigDecimal.ZERO) >= 0;

        JPanel fila = new JPanel(new BorderLayout(18, 0));

        fila.setBackground(
                positiva
                        ? VERDE_SUAVE
                        : ROJO_SUAVE
        );

        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        positiva
                                ? new Color(187, 247, 208)
                                : new Color(254, 202, 202)
                ),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel nombre = new JLabel(
                positiva
                        ? "✓  UTILIDAD DEL EJERCICIO"
                        : "⚠  PÉRDIDA DEL EJERCICIO"
        );

        nombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nombre.setForeground(
                positiva
                        ? VERDE
                        : ROJO
        );

        JLabel numero = new JLabel(
                dinero(utilidad),
                SwingConstants.RIGHT
        );

        numero.setFont(new Font("Segoe UI", Font.BOLD, 17));
        numero.setForeground(
                positiva
                        ? VERDE
                        : ROJO
        );

        fila.add(nombre, BorderLayout.WEST);
        fila.add(numero, BorderLayout.EAST);

        return fila;
    }

    private JPanel crearBarraEstado(
            BigDecimal utilidad
    ) {

        boolean positiva =
                utilidad != null
                && utilidad.compareTo(BigDecimal.ZERO) >= 0;

        JPanel panel = new JPanel(
                new BorderLayout()
        );

        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        lblEstadoResultado.setText(
                positiva
                        ? "✓ El período presenta utilidad"
                        : "⚠ El período presenta pérdida"
        );

        lblEstadoResultado.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblEstadoResultado.setForeground(
                positiva
                        ? VERDE
                        : ROJO
        );

        JLabel valor = new JLabel(
                "Resultado final: " + dinero(utilidad)
        );

        valor.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valor.setForeground(TEXTO);

        panel.add(lblEstadoResultado, BorderLayout.WEST);
        panel.add(valor, BorderLayout.EAST);

        return panel;
    }

    private void actualizarTarjetaUtilidad(
            BigDecimal utilidad
    ) {

        boolean positiva =
                utilidad != null
                && utilidad.compareTo(BigDecimal.ZERO) >= 0;

        lblUtilidadEjercicio.setForeground(
                positiva
                        ? VERDE
                        : ROJO
        );
    }

    private JButton crearBoton(
            String texto
    ) {

        JButton boton = new JButton(texto);

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

        if (valor == null) {
            valor = BigDecimal.ZERO;
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
                "No se pudo cargar el Estado de Resultados",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
