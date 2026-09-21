package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.AlertasService;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class GraficaDonutPanel extends JPanel {

    private final String titulo;
    private final List<AlertasService.DatoGrafica> datos;

    private static final Color TEXTO = new Color(30, 41, 59);
    private static final Color SECUNDARIO = new Color(100, 116, 139);
    private static final Color BORDE = new Color(226, 232, 240);
    private static final Color[] PALETA = {
        new Color(37, 99, 235),
        new Color(14, 165, 233),
        new Color(5, 150, 105),
        new Color(245, 158, 11),
        new Color(139, 92, 246),
        new Color(239, 68, 68)
    };

    public GraficaDonutPanel(String titulo, List<AlertasService.DatoGrafica> datos) {
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

        double total = datos == null ? 0 : datos.stream()
                .mapToDouble(d -> Math.max(0, d.valor()))
                .sum();

        if (datos == null || datos.isEmpty() || total <= 0) {
            g2.setColor(SECUNDARIO);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("Sin datos en el período actual", 18, 76);
            g2.dispose();
            return;
        }

        int size = Math.min(132, Math.min(getWidth() / 3, getHeight() - 82));
        int x = 28;
        int y = 62;
        int inicio = 90;

        for (int i = 0; i < datos.size(); i++) {
            double valor = Math.max(0, datos.get(i).valor());
            int arco = i == datos.size() - 1
                    ? 360 - (90 - inicio)
                    : (int) Math.round((valor / total) * 360.0);
            g2.setColor(PALETA[i % PALETA.length]);
            g2.fillArc(x, y, size, size, inicio, -arco);
            inicio -= arco;
        }

        int hueco = (int) (size * 0.58);
        int hx = x + (size - hueco) / 2;
        int hy = y + (size - hueco) / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(hx, hy, hueco, hueco);

        g2.setColor(TEXTO);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String totalTxt = monedaCorta(total);
        int tw = g2.getFontMetrics().stringWidth(totalTxt);
        g2.drawString(totalTxt, x + size / 2 - tw / 2, y + size / 2 + 4);

        int lx = x + size + 26;
        int ly = 72;
        for (int i = 0; i < datos.size(); i++) {
            AlertasService.DatoGrafica dato = datos.get(i);
            g2.setColor(PALETA[i % PALETA.length]);
            g2.fillRoundRect(lx, ly - 9, 10, 10, 4, 4);

            g2.setColor(TEXTO);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            String etiqueta = abreviar(dato.etiqueta(), 16);
            g2.drawString(etiqueta, lx + 17, ly);

            g2.setColor(SECUNDARIO);
            String valor = monedaCorta(dato.valor());
            g2.drawString(valor, lx + 17, ly + 15);
            ly += 38;
            if (ly > getHeight() - 18) break;
        }

        g2.dispose();
    }

    private String abreviar(String texto, int max) {
        if (texto == null) return "";
        String limpio = texto.trim().replace('_', ' ');
        return limpio.length() <= max ? limpio : limpio.substring(0, max - 1) + "…";
    }

    private String monedaCorta(double valor) {
        double abs = Math.abs(valor);
        if (abs >= 1_000_000) return String.format("$%.1fM", valor / 1_000_000.0);
        if (abs >= 1_000) return String.format("$%.1fk", valor / 1_000.0);
        return String.format("$%.0f", valor);
    }
}
