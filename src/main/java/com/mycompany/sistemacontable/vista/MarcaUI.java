package com.mycompany.sistemacontable.vista;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public final class MarcaUI {
    public static final String NOMBRE = "ContaProMax";
    public static final String SUBTITULO = "Sistema Contable";
    public static final String LEMA = "Tu contabilidad, más simple, más inteligente";
    private MarcaUI() {}

    public static ImageIcon logo(int ancho, int alto) {
        URL url = MarcaUI.class.getResource("/branding/contapromax-logo.png");
        if (url == null) return null;
        ImageIcon original = new ImageIcon(url);
        Image img = original.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    public static Image iconoVentana() {
        ImageIcon icon = logo(64, 64);
        return icon == null ? null : icon.getImage();
    }
}
