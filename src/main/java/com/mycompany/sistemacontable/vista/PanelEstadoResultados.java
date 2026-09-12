package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.EstadoResultados;
import com.mycompany.sistemacontable.servicio.EstadoResultadosService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
import javax.swing.plaf.basic.BasicButtonUI;

public class PanelEstadoResultados extends JPanel {

    private final EstadoResultadosService service;
    private final JPanel detalle;

    private final Color FONDO = new Color(245, 247, 250);
    private final Color TEXTO = new Color(30, 41, 59);
    private final Color SECUNDARIO = new Color(100, 116, 139);
    private final Color BORDE = new Color(226, 232, 240);
    private final Color PRIMARIO = new Color(37, 99, 235);
    private final Color ERROR = new Color(220, 38, 38);

    public PanelEstadoResultados() {

        service = new EstadoResultadosService();
        detalle = new JPanel(new GridBagLayout());

        setLayout(new BorderLayout(0, 18));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(28, 30, 30, 30));

        add(crearEncabezado(), BorderLayout.NORTH);

        detalle.setBackground(Color.WHITE);
        detalle.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDE),
                        BorderFactory.createEmptyBorder(20, 28, 20, 28)
                )
        );

        JScrollPane scroll = new JScrollPane(detalle);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);

        cargarEstadoResultados();
    }

    private JPanel crearEncabezado() {

        JPanel panel = new JPanel(new BorderLayout());
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

        JButton actualizar = crearBoton("Actualizar");
        actualizar.addActionListener(e -> cargarEstadoResultados());

        panel.add(textos, BorderLayout.WEST);
        panel.add(actualizar, BorderLayout.EAST);

        return panel;
    }

    public final void cargarEstadoResultados() {

        try {

            EstadoResultados estado = service.generar();

            detalle.removeAll();
            int[] fila = {0};

            // =====================================================
            // VENTAS
            // =====================================================

            agregarSeccion("VENTAS", fila);

            agregarLinea(
                    "Ventas",
                    estado.getVentas(),
                    false,
                    fila
            );

            agregarLinea(
                    "(-) Devoluciones sobre ventas",
                    estado.getDevolucionVentas(),
                    false,
                    fila
            );

            agregarLinea(
                    "(-) Descuentos sobre ventas",
                    estado.getDescuentoVentas(),
                    false,
                    fila
            );

            agregarLinea(
                    "Ventas netas",
                    estado.getVentasNetas(),
                    true,
                    fila
            );


            // =====================================================
            // COSTO DE VENTAS
            // =====================================================
            // La linea Compras ya incorpora internamente:
            // Compras de mayorizacion + Inventario inicial
            // - Inventario final del Kardex PEPS.
            // No se muestran inventarios por separado.
            // =====================================================

            agregarSeccion("COSTO DE VENTAS", fila);

            agregarLinea(
                    "Compras",
                    estado.getCompras(),
                    false,
                    fila
            );

            agregarLinea(
                    "(+) Gastos sobre compras",
                    estado.getGastosSobreCompras(),
                    false,
                    fila
            );

            agregarLinea(
                    "Compras totales",
                    estado.getComprasTotales(),
                    true,
                    fila
            );

            agregarLinea(
                    "(-) Devoluciones sobre compras",
                    estado.getDevolucionCompras(),
                    false,
                    fila
            );

            agregarLinea(
                    "(-) Descuentos sobre compras",
                    estado.getDescuentoCompras(),
                    false,
                    fila
            );

            agregarLinea(
                    "Compras netas",
                    estado.getComprasNetas(),
                    true,
                    fila
            );

            agregarLinea(
                    "Costo de ventas",
                    estado.getCostoVentas(),
                    true,
                    fila
            );


            // =====================================================
            // RESULTADO
            // =====================================================

            agregarSeccion("RESULTADO", fila);

            agregarLinea(
                    "Utilidad bruta",
                    estado.getUtilidadBruta(),
                    true,
                    fila
            );

            agregarLinea(
                    "Gastos administrativos",
                    estado.getGastosAdministrativos(),
                    false,
                    fila
            );

            agregarLinea(
                    "Gastos de venta",
                    estado.getGastosVenta(),
                    false,
                    fila
            );

            agregarLinea(
                    "Gastos financieros",
                    estado.getGastosFinancieros(),
                    false,
                    fila
            );

            agregarLinea(
                    "Total gastos operacionales",
                    estado.getTotalGastosOperativos(),
                    true,
                    fila
            );

            agregarLinea(
                    "Utilidad operacional antes del impuesto",
                    estado.getUtilidadOperacionalAntesImpuesto(),
                    true,
                    fila
            );

            agregarLinea(
                    "(+) Otros productos / Productos financieros",
                    estado.getOtrosProductos(),
                    false,
                    fila
            );

            agregarLinea(
                    "UTILIDAD DEL EJERCICIO",
                    estado.getUtilidadEjercicio(),
                    true,
                    fila
            );

            detalle.revalidate();
            detalle.repaint();

        } catch (Exception e) {
            mostrarError(e);
        }
    }

    private void agregarSeccion(String texto, int[] fila) {

        GridBagConstraints gbc = base(fila[0]++);
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 0, 8, 0);

        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(PRIMARIO);

        detalle.add(label, gbc);
    }

    private void agregarLinea(
            String texto,
            BigDecimal valor,
            boolean fuerte,
            int[] fila
    ) {

        GridBagConstraints izquierda = base(fila[0]);
        izquierda.gridx = 0;
        izquierda.weightx = 1;
        izquierda.anchor = GridBagConstraints.WEST;

        JLabel label = new JLabel(texto);
        label.setFont(
                new Font(
                        "Segoe UI",
                        fuerte ? Font.BOLD : Font.PLAIN,
                        13
                )
        );
        label.setForeground(TEXTO);

        detalle.add(label, izquierda);

        GridBagConstraints derecha = base(fila[0]++);
        derecha.gridx = 1;
        derecha.weightx = 0;
        derecha.anchor = GridBagConstraints.EAST;

        JLabel numero = new JLabel(dinero(valor));
        numero.setFont(
                new Font(
                        "Segoe UI",
                        fuerte ? Font.BOLD : Font.PLAIN,
                        13
                )
        );
        numero.setForeground(
                fuerte
                        && valor != null
                        && valor.compareTo(BigDecimal.ZERO) < 0
                                ? ERROR
                                : TEXTO
        );

        detalle.add(numero, derecha);
    }

    private GridBagConstraints base(int y) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = y;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        return gbc;
    }

    private JButton crearBoton(String texto) {

        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(Color.WHITE);
        boton.setBackground(PRIMARIO);
        boton.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }

    private String dinero(BigDecimal valor) {

        if (valor == null) {
            valor = BigDecimal.ZERO;
        }

        return "$"
                + String.format(
                        "%,.2f",
                        valor.setScale(2, RoundingMode.HALF_UP)
                );
    }

    private void mostrarError(Throwable error) {

        String mensaje = "Ocurrió un error desconocido.";

        for (
                Throwable actual = error;
                actual != null;
                actual = actual.getCause()
        ) {
            if (actual.getMessage() != null
                    && !actual.getMessage().isBlank()) {
                mensaje = actual.getMessage();
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
