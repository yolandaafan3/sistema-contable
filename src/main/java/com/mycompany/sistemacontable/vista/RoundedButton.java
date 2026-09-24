/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sistemacontable.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * JButton con esquinas redondeadas, sin cambiar el comportamiento estándar
 * de JButton (setText, addActionListener, etc. funcionan igual).
 */
public class RoundedButton extends JButton {

    private final int radio;
    private Color colorNormal;
    private Color colorHover;
    private boolean sobreBoton = false;

    public RoundedButton(String texto, int radio) {
        super(texto);
        this.radio = radio;
        setUI(new BasicButtonUI());
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setHorizontalAlignment(SwingConstants.CENTER);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { sobreBoton = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { sobreBoton = false; repaint(); }
        });
    }

    public void setColores(Color normal, Color hover) {
        this.colorNormal = normal;
        this.colorHover = hover;
        setBackground(normal);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fondo = getBackground();
        if (colorNormal != null) {
            fondo = (sobreBoton && colorHover != null) ? colorHover : colorNormal;
        }
        g2.setColor(fondo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
        g2.dispose();
        super.paintComponent(g);
    }
}
