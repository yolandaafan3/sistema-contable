package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.AlertasService;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import javax.swing.*;

public class GraficaLineaPanel extends JPanel {

    private final String titulo;
    private final List<AlertasService.DatoGrafica> datos;

    private static final Color TEXTO = new Color(30, 41, 59);
    private static final Color SECUNDARIO = new Color(100, 116, 139);
    private static final Color BORDE = new Color(226, 232, 240);
    private static final Color PRIMARIO = new Color(37, 99, 235);
    private static final Color AREA = new Color(219, 234, 254);
    private static final Color REJILLA = new Color(241, 245, 249);

    public GraficaLineaPanel(String titulo, List<AlertasService.DatoGrafica> datos) {
        this.titulo = titulo;
        this.datos = datos;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(420, 230));
        setBorder(BorderFactory.createLineBorder(BORDE));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(TEXTO);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.drawString(titulo, 18, 28);

        if (datos == null || datos.isEmpty()) {
            sinDatos(g2);
            g2.dispose();
            return;
        }

        double max = datos.stream().mapToDouble(AlertasService.DatoGrafica::valor).max().orElse(0);
        if (max <= 0) {
            sinDatos(g2);
            g2.dispose();
            return;
        }

        int left = 48;
        int right = getWidth() - 24;
        int top = 58;
        int bottom = getHeight() - 42;
        int width = Math.max(1, right - left);
        int height = Math.max(1, bottom - top);

        g2.setColor(REJILLA);
        for (int i = 0; i <= 4; i++) {
            int y = top + height * i / 4;
            g2.drawLine(left, y, right, y);
        }

        int n = datos.size();
        int[] xs = new int[n];
        int[] ys = new int[n];
        for (int i = 0; i < n; i++) {
            xs[i] = n == 1 ? left + width / 2 : left + (width * i / (n - 1));
            ys[i] = bottom - (int) Math.round(height * (datos.get(i).valor() / max));
        }

        Path2D area = new Path2D.Double();
        area.moveTo(xs[0], bottom);
        area.lineTo(xs[0], ys[0]);
        for (int i = 1; i < n; i++) area.lineTo(xs[i], ys[i]);
        area.lineTo(xs[n - 1], bottom);
        area.closePath();
        g2.setColor(AREA);
        g2.fill(area);

        g2.setColor(PRIMARIO);
        g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < n - 1; i++) g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);

        for (int i = 0; i < n; i++) {
            g2.setColor(Color.WHITE);
            g2.fillOval(xs[i] - 5, ys[i] - 5, 10, 10);
            g2.setColor(PRIMARIO);
            g2.drawOval(xs[i] - 5, ys[i] - 5, 10, 10);

            g2.setColor(TEXTO);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String valor = monedaCorta(datos.get(i).valor());
            int valorW = g2.getFontMetrics().stringWidth(valor);
            g2.drawString(valor, xs[i] - valorW / 2, Math.max(top + 10, ys[i] - 9));

            g2.setColor(SECUNDARIO);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String etiqueta = datos.get(i).etiqueta() == null ? "" : datos.get(i).etiqueta();
            int etiquetaW = g2.getFontMetrics().stringWidth(etiqueta);
            g2.drawString(etiqueta, xs[i] - etiquetaW / 2, bottom + 18);
        }

        g2.dispose();
    }

    private void sinDatos(Graphics2D g2) {
        g2.setColor(SECUNDARIO);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.drawString("Sin datos en el período actual", 18, 76);
    }

    private String monedaCorta(double valor) {
        double abs = Math.abs(valor);
        if (abs >= 1_000_000) return String.format("$%.1fM", valor / 1_000_000.0);
        if (abs >= 1_000) return String.format("$%.1fk", valor / 1_000.0);
        return String.format("$%.0f", valor);
    }
}
