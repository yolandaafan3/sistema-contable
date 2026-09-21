package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.AlertasService;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class GraficaBarrasPanel extends JPanel {

    private final String titulo;
    private final List<AlertasService.DatoGrafica> datos;

    private static final Color TEXTO = new Color(30, 41, 59);
    private static final Color SECUNDARIO = new Color(100, 116, 139);
    private static final Color BORDE = new Color(226, 232, 240);
    private static final Color PRIMARIO = new Color(37, 99, 235);
    private static final Color REJILLA = new Color(241, 245, 249);

    public GraficaBarrasPanel(String titulo, List<AlertasService.DatoGrafica> datos) {
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

        dibujarTitulo(g2);

        if (datos == null || datos.isEmpty()) {
            dibujarSinDatos(g2);
            g2.dispose();
            return;
        }

        double max = datos.stream()
                .mapToDouble(AlertasService.DatoGrafica::valor)
                .max()
                .orElse(0);

        if (max <= 0) {
            dibujarSinDatos(g2);
            g2.dispose();
            return;
        }

        int left = 48;
        int right = getWidth() - 22;
        int top = 58;
        int bottom = getHeight() - 42;
        int chartHeight = Math.max(1, bottom - top);
        int chartWidth = Math.max(1, right - left);

        g2.setColor(REJILLA);
        for (int i = 0; i <= 4; i++) {
            int y = top + (chartHeight * i / 4);
            g2.drawLine(left, y, right, y);
        }

        int cantidad = datos.size();
        int espacioPorDato = Math.max(1, chartWidth / cantidad);
        int anchoBarra = Math.min(44, Math.max(18, (int) (espacioPorDato * 0.48)));

        for (int i = 0; i < cantidad; i++) {
            AlertasService.DatoGrafica dato = datos.get(i);
            int centroX = left + espacioPorDato * i + espacioPorDato / 2;
            int x = centroX - anchoBarra / 2;
            int alto = (int) Math.round(chartHeight * (dato.valor() / max));
            int y = bottom - alto;

            g2.setColor(PRIMARIO);
            g2.fillRoundRect(x, y, anchoBarra, alto, 10, 10);

            g2.setColor(TEXTO);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String valor = monedaCorta(dato.valor());
            int valorW = g2.getFontMetrics().stringWidth(valor);
            g2.drawString(valor, centroX - valorW / 2, Math.max(top + 11, y - 6));

            g2.setColor(SECUNDARIO);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String etiqueta = abreviar(dato.etiqueta(), 10);
            int etiquetaW = g2.getFontMetrics().stringWidth(etiqueta);
            g2.drawString(etiqueta, centroX - etiquetaW / 2, bottom + 18);
        }

        g2.dispose();
    }

    private void dibujarTitulo(Graphics2D g2) {
        g2.setColor(TEXTO);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.drawString(titulo, 18, 28);
    }

    private void dibujarSinDatos(Graphics2D g2) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(SECUNDARIO);
        g2.drawString("Sin datos en el período actual", 18, 76);
    }

    private String abreviar(String texto, int max) {
        if (texto == null) return "";
        String limpio = texto.trim();
        return limpio.length() <= max ? limpio : limpio.substring(0, max - 1) + "…";
    }

    private String monedaCorta(double valor) {
        double abs = Math.abs(valor);
        if (abs >= 1_000_000) return String.format("$%.1fM", valor / 1_000_000.0);
        if (abs >= 1_000) return String.format("$%.1fk", valor / 1_000.0);
        return String.format("$%.0f", valor);
    }
}
