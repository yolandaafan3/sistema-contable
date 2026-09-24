/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacontable.vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * JPanel con esquinas redondeadas y borde opcional, usado para tarjetas
 * (dashboard, formulario de login, contenedores del sidebar).
 * No agrega ninguna lógica, solo estética.
 */
public class RoundedPanel extends JPanel {

    private final int radio;
    private Color colorBorde;
    private int grosorBorde = 1;
    private Color colorFranjaSuperior;
    private int grosorFranja = 4;

    public RoundedPanel(int radio) {
        this.radio = radio;
        setOpaque(false);
        setBorder(new EmptyBorder(18, 20, 18, 20));
    }

    public RoundedPanel conBorde(Color color) {
        this.colorBorde = color;
        return this;
    }

    public RoundedPanel conBorde(Color color, int grosor) {
        this.colorBorde = color;
        this.grosorBorde = grosor;
        return this;
    }

    /** Franja de color decorativa en la parte superior de la tarjeta (acento). */
    public RoundedPanel conFranjaSuperior(Color color) {
        this.colorFranjaSuperior = color;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);

        if (colorFranjaSuperior != null) {
            g2.setColor(colorFranjaSuperior);
            g2.fillRoundRect(0, 0, getWidth() - 1, grosorFranja * 2, radio, radio);
            g2.fillRect(0, grosorFranja, getWidth() - 1, grosorFranja);
        }

        if (colorBorde != null) {
            g2.setColor(colorBorde);
            g2.setStroke(new java.awt.BasicStroke(grosorBorde));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
