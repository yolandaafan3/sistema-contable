package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.AlertasService;
import java.awt.*;
import javax.swing.*;

public class PanelDashboard extends JPanel {

    private final AlertasService service = new AlertasService();
    private final JPanel contenido = new JPanel();

    public PanelDashboard() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(28, 30, 30, 30));

        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(new Color(245, 247, 250));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        add(scroll, BorderLayout.CENTER);

        cargarDashboard();
    }

    public void cargarDashboard() {
        contenido.removeAll();

        JLabel titulo = new JLabel("Dashboard");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Resumen y análisis visual del período contable activo");
        sub.setForeground(new Color(100, 116, 139));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(4));
        contenido.add(sub);
        contenido.add(Box.createVerticalStrut(24));

        AlertasService.Resumen r = service.obtenerResumen();
        JPanel resumen = new JPanel(new GridLayout(1, 4, 16, 0));
        resumen.setOpaque(false);
        resumen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 125));
        resumen.setAlignmentX(Component.LEFT_ALIGNMENT);
        resumen.add(tarjeta("Ventas", r.ventas(), "Ventas netas del período"));
        resumen.add(tarjeta("Inventario", r.inventario(), "Saldo contable del período"));
        resumen.add(tarjeta("Utilidad", r.utilidad(), "Resultado del período"));
        resumen.add(tarjeta("Balance", r.balance(), "Estado contable actual"));
        contenido.add(resumen);
        contenido.add(Box.createVerticalStrut(28));

        JLabel gt = new JLabel("Gráficas del período");
        gt.setFont(new Font("Segoe UI", Font.BOLD, 21));
        gt.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(gt);
        contenido.add(Box.createVerticalStrut(12));

        JPanel graficas = new JPanel(new GridLayout(2, 2, 16, 16));
        graficas.setOpaque(false);
        graficas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        graficas.setPreferredSize(new Dimension(920, 500));
        graficas.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Se combinan distintos tipos de gráfica para evitar un dashboard repetitivo.
        graficas.add(new GraficaLineaPanel("Ventas por mes", service.ventasPorMes()));
        graficas.add(new GraficaBarrasPanel("Compras por mes", service.comprasPorMes()));
        graficas.add(new GraficaDonutPanel("Costos y gastos", service.gastosPorTipo()));
        graficas.add(new GraficaBarrasPanel("Estructura contable", service.estructuraContable()));

        contenido.add(graficas);
        contenido.revalidate();
        contenido.repaint();
    }

    private JPanel tarjeta(String titulo, String valor, String detalle) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", valor.isEmpty() ? Font.BOLD : Font.PLAIN,
                valor.isEmpty() ? 17 : 13));
        t.setForeground(new Color(30, 41, 59));
        p.add(t);

        if (!valor.isEmpty()) {
            p.add(Box.createVerticalStrut(8));
            JLabel v = new JLabel(valor);
            v.setFont(new Font("Segoe UI", Font.BOLD, 24));
            v.setForeground(new Color(30, 41, 59));
            p.add(v);
        }

        p.add(Box.createVerticalGlue());
        JLabel d = new JLabel(detalle);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        d.setForeground(new Color(100, 116, 139));
        p.add(d);
        return p;
    }
}
