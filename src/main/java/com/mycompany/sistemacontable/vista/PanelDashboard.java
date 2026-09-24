package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.AlertasService;
import java.awt.*;
import javax.swing.*;

public class PanelDashboard extends JPanel {

    private static final Color COLOR_FONDO = new Color(245, 247, 250);
    private static final Color COLOR_TEXTO = new Color(30, 41, 59);
    private static final Color COLOR_SECUNDARIO = new Color(100, 116, 139);
    private static final Color COLOR_BORDE = new Color(226, 232, 240);
    private static final Color COLOR_AZUL = new Color(37, 99, 235);
    private static final Color COLOR_CELESTE = new Color(14, 165, 233);
    private static final Color COLOR_VERDE = new Color(16, 185, 129);
    private static final Color COLOR_MORADO = new Color(139, 92, 246);

    private final AlertasService service = new AlertasService();
    private final JPanel contenido = new JPanel();

    public PanelDashboard() {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        add(scroll, BorderLayout.CENTER);

        cargarDashboard();
    }

    public void cargarDashboard() {
        contenido.removeAll();

        AlertasService.Resumen r = service.obtenerResumen();

        contenido.add(crearEncabezadoPrincipal());
        contenido.add(Box.createVerticalStrut(22));

        JPanel resumen = new JPanel(new GridLayout(1, 4, 16, 0));
        resumen.setOpaque(false);
        resumen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        resumen.setAlignmentX(Component.LEFT_ALIGNMENT);

        resumen.add(tarjeta(
                "Ventas",
                r.ventas(),
                "Ventas netas del período",
                "$",
                new Color(219, 234, 254),
                COLOR_AZUL
        ));

        resumen.add(tarjeta(
                "Inventario",
                r.inventario(),
                "Saldo contable del período",
                "▣",
                new Color(224, 242, 254),
                COLOR_CELESTE
        ));

        resumen.add(tarjeta(
                "Utilidad",
                r.utilidad(),
                "Resultado del período",
                "↗",
                new Color(220, 252, 231),
                COLOR_VERDE
        ));

        resumen.add(tarjeta(
                "Balance",
                r.balance(),
                "Estado contable actual",
                "✓",
                new Color(243, 232, 255),
                COLOR_MORADO
        ));

        contenido.add(resumen);
        contenido.add(Box.createVerticalStrut(26));

        contenido.add(crearTituloSeccion(
                "Gráficas del período",
                "Comportamiento visual de los principales indicadores contables"
        ));
        contenido.add(Box.createVerticalStrut(12));

        JPanel graficas = new JPanel(new GridLayout(2, 2, 16, 16));
        graficas.setOpaque(false);
        graficas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 560));
        graficas.setPreferredSize(new Dimension(920, 560));
        graficas.setAlignmentX(Component.LEFT_ALIGNMENT);

        graficas.add(new GraficaLineaPanel("Ventas por mes", service.ventasPorMes()));
        graficas.add(new GraficaBarrasPanel("Compras por mes", service.comprasPorMes()));
        graficas.add(new GraficaDonutPanel("Costos y gastos", service.gastosPorTipo()));
        graficas.add(new GraficaBarrasPanel("Estructura contable", service.estructuraContable()));

        contenido.add(graficas);
        contenido.add(Box.createVerticalStrut(18));

        JPanel zonaInferior = new JPanel(new GridLayout(1, 2, 16, 0));
        zonaInferior.setOpaque(false);
        zonaInferior.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        zonaInferior.setPreferredSize(new Dimension(920, 190));
        zonaInferior.setAlignmentX(Component.LEFT_ALIGNMENT);

        zonaInferior.add(crearTarjetaInformativa(
                "Lectura rápida del período",
                "Resumen visual de los indicadores principales",
                COLOR_AZUL,
                "Ventas registradas: " + r.ventas(),
                "Inventario actual: " + r.inventario(),
                "Utilidad del período: " + r.utilidad(),
                "Balance contable: " + r.balance()
        ));

        zonaInferior.add(crearTarjetaInformativa(
                "Análisis del dashboard",
                "Guía rápida para revisar la información mostrada",
                COLOR_CELESTE,
                "Compara ventas y compras para ver el comportamiento mensual.",
                "Revisa la gráfica de costos y gastos para detectar la mayor concentración.",
                "Consulta la estructura contable para ver la distribución actual.",
                "Si una gráfica no muestra datos, registra operaciones para alimentar el panel."
        ));

        contenido.add(zonaInferior);
        contenido.add(Box.createVerticalStrut(6));

        contenido.revalidate();
        contenido.repaint();
    }

    private JPanel crearEncabezadoPrincipal() {
        PanelRedondeado panel = new PanelRedondeado(24, Color.WHITE);
        panel.setLayout(new BorderLayout(16, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Dashboard");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 31));
        titulo.setForeground(COLOR_TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Resumen y análisis visual del período contable activo");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(COLOR_SECUNDARIO);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(sub);

        JPanel estado = new JPanel();
        estado.setOpaque(false);
        estado.setLayout(new BoxLayout(estado, BoxLayout.Y_AXIS));

        JLabel chip = new JLabel("Período activo");
        chip.setOpaque(true);
        chip.setBackground(new Color(219, 234, 254));
        chip.setForeground(COLOR_AZUL);
        chip.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chip.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        chip.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel leyenda = new JLabel("Panel contable");
        leyenda.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leyenda.setForeground(COLOR_SECUNDARIO);
        leyenda.setAlignmentX(Component.RIGHT_ALIGNMENT);

        estado.add(chip);
        estado.add(Box.createVerticalStrut(8));
        estado.add(leyenda);

        panel.add(textos, BorderLayout.WEST);
        panel.add(estado, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearTituloSeccion(String titulo, String subtitulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 22));
        t.setForeground(COLOR_TEXTO);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s = new JLabel(subtitulo);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(COLOR_SECUNDARIO);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        textos.add(t);
        textos.add(Box.createVerticalStrut(2));
        textos.add(s);

        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    private JPanel tarjeta(String titulo, String valor, String detalle, String icono, Color fondoIcono, Color acento) {
        PanelRedondeado p = new PanelRedondeado(22, Color.WHITE);
        p.setLayout(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(16, 18, 14, 18)
        ));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(COLOR_TEXTO);

        cabecera.add(t, BorderLayout.WEST);
        cabecera.add(crearBurbujaIcono(icono, fondoIcono, acento), BorderLayout.EAST);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(COLOR_TEXTO);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel d = new JLabel(detalle);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        d.setForeground(COLOR_SECUNDARIO);
        d.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linea = new JPanel();
        linea.setBackground(acento);
        linea.setPreferredSize(new Dimension(44, 4));
        linea.setMaximumSize(new Dimension(44, 4));
        linea.setOpaque(true);
        linea.setAlignmentX(Component.LEFT_ALIGNMENT);

        centro.add(cabecera);
        centro.add(Box.createVerticalStrut(16));
        centro.add(v);
        centro.add(Box.createVerticalStrut(10));
        centro.add(d);
        centro.add(Box.createVerticalStrut(12));
        centro.add(linea);

        p.add(centro, BorderLayout.CENTER);
        return p;
    }

    private JLabel crearBurbujaIcono(String texto, Color fondo, Color colorTexto) {
        JLabel icono = new JLabel(texto, SwingConstants.CENTER);
        icono.setOpaque(true);
        icono.setBackground(fondo);
        icono.setForeground(colorTexto);
        icono.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        icono.setPreferredSize(new Dimension(38, 38));
        icono.setMinimumSize(new Dimension(38, 38));
        icono.setMaximumSize(new Dimension(38, 38));
        icono.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        return icono;
    }

    private JPanel crearTarjetaInformativa(String titulo, String subtitulo, Color acento, String... lineas) {
        PanelRedondeado panel = new PanelRedondeado(22, Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setForeground(COLOR_TEXTO);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s = new JLabel(subtitulo);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(COLOR_SECUNDARIO);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel barra = new JPanel();
        barra.setBackground(acento);
        barra.setPreferredSize(new Dimension(62, 4));
        barra.setMaximumSize(new Dimension(62, 4));
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);

        superior.add(t);
        superior.add(Box.createVerticalStrut(4));
        superior.add(s);
        superior.add(Box.createVerticalStrut(10));
        superior.add(barra);

        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));

        for (String linea : lineas) {
            JLabel item = new JLabel("• " + linea);
            item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            item.setForeground(new Color(55, 65, 81));
            item.setAlignmentX(Component.LEFT_ALIGNMENT);
            lista.add(item);
            lista.add(Box.createVerticalStrut(8));
        }

        panel.add(superior, BorderLayout.NORTH);
        panel.add(lista, BorderLayout.CENTER);

        return panel;
    }

    private static class PanelRedondeado extends JPanel {

        private final int radio;
        private final Color colorFondo;

        public PanelRedondeado(int radio, Color colorFondo) {
            this.radio = radio;
            this.colorFondo = colorFondo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}