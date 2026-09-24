/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacontable.vista;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

/**
 * Borde redondeado para JTextField / JPasswordField, con un color
 * de acento que puede resaltarse al tener el foco.
 */
public class RoundedFieldBorder extends AbstractBorder {

    private final Color colorBorde;
    private final int radio;

    public RoundedFieldBorder(Color colorBorde, int radio) {
        this.colorBorde = colorBorde;
        this.radio = radio;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(colorBorde);
        g2.setStroke(new java.awt.BasicStroke(1.4f));
        g2.drawRoundRect(x, y, width - 1, height - 1, radio, radio);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(8, 14, 8, 14);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(8, 14, 8, 14);
        return insets;
    }
}
