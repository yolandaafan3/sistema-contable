package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.Usuario;
import com.mycompany.sistemacontable.servicio.AutenticacionService;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class VentanaLogin extends JFrame {

    private final JTextField txtUsuario =
            new JTextField();

    private final JPasswordField txtPassword =
            new JPasswordField();

    private final AutenticacionService auth =
            new AutenticacionService();

    public VentanaLogin() {

        setTitle(
                "ContaProMax - Iniciar sesión"
        );

        aplicarIconoAplicacion();

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setSize(
                850,
                550
        );

        setResizable(
                false
        );

        setLocationRelativeTo(
                null
        );

        JPanel panelPrincipal =
                new JPanel(
                        new GridLayout(
                                1,
                                2
                        )
                );

        JPanel panelIzquierdo =
                new JPanel();

        panelIzquierdo.setBackground(
                Color.WHITE
        );

        panelIzquierdo.setLayout(
                null
        );

        JLabel title =
                new JLabel(
                        "Iniciar sesión"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        26
                )
        );

        title.setBounds(
                50,
                70,
                300,
                35
        );

        panelIzquierdo.add(
                title
        );

        JLabel sub =
                new JLabel(
                        "Ingresa tus credenciales para continuar."
                );

        sub.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        sub.setForeground(
                new Color(
                        100,
                        116,
                        139
                )
        );

        sub.setBounds(
                50,
                110,
                300,
                20
        );

        panelIzquierdo.add(
                sub
        );

        JLabel lblUsuario =
                label(
                        "USUARIO"
                );

        lblUsuario.setForeground(
                new Color(
                        100,
                        100,
                        100
                )
        );

        lblUsuario.setBounds(
                50,
                160,
                300,
                20
        );

        panelIzquierdo.add(
                lblUsuario
        );

        txtUsuario.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        txtUsuario.setBorder(
                new MatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(
                                200,
                                200,
                                200
                        )
                )
        );

        txtUsuario.setBounds(
                50,
                185,
                300,
                35
        );

        panelIzquierdo.add(
                txtUsuario
        );

        JLabel lblPassword =
                label(
                        "CONTRASEÑA"
                );

        lblPassword.setForeground(
                new Color(
                        100,
                        100,
                        100
                )
        );

        lblPassword.setBounds(
                50,
                240,
                300,
                20
        );

        panelIzquierdo.add(
                lblPassword
        );

        txtPassword.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        txtPassword.setBorder(
                new MatteBorder(
                        0,
                        0,
                        1,
                        0,
                        new Color(
                                200,
                                200,
                                200
                        )
                )
        );

        txtPassword.setBounds(
                50,
                265,
                300,
                35
        );

        panelIzquierdo.add(
                txtPassword
        );

        JButton btn =
                new JButton(
                        "Ingresar"
                );

        btn.setUI(
                new BasicButtonUI()
        );

        btn.setBackground(
                new Color(
                        37,
                        99,
                        235
                )
        );

        btn.setForeground(
                Color.WHITE
        );

        btn.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        btn.setFocusPainted(
                false
        );

        btn.setBorder(
                BorderFactory.createEmptyBorder(
                        11,
                        18,
                        11,
                        18
                )
        );

        btn.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        btn.setBounds(
                50,
                340,
                300,
                45
        );

        btn.addActionListener(
                e -> login()
        );

        panelIzquierdo.add(
                btn
        );

        getRootPane()
                .setDefaultButton(
                        btn
                );

        JPanel panelDerecho =
                new JPanel() {

                    @Override
                    protected void paintComponent(
                            Graphics g
                    ) {

                        super.paintComponent(
                                g
                        );

                        URL bgURL =
                                getClass()
                                        .getResource(
                                                "/branding/azul.png"
                                        );

                        if (
                                bgURL != null
                        ) {

                            ImageIcon fondo =
                                    new ImageIcon(
                                            bgURL
                                    );

                            g.drawImage(
                                    fondo.getImage(),
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight(),
                                    this
                            );

                        } else {

                            g.setColor(
                                    new Color(
                                            18,
                                            32,
                                            52
                                    )
                            );

                            g.fillRect(
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight()
                            );
                        }
                    }
                };

        panelDerecho.setLayout(
                new GridBagLayout()
        );

        URL logoURL =
                getClass()
                        .getResource(
                                "/branding/ContaProMaxLogo.png"
                        );

        if (
                logoURL != null
        ) {

            ImageIcon logoIcon =
                    new ImageIcon(
                            logoURL
                    );

            Image imagenRedimensionada =
                    logoIcon.getImage()
                            .getScaledInstance(
                                    220,
                                    220,
                                    Image.SCALE_SMOOTH
                            );

            JLabel img =
                    new JLabel(
                            new ImageIcon(
                                    imagenRedimensionada
                            )
                    );

            panelDerecho.add(
                    img
            );

        } else {

            System.err.println(
                    "Advertencia: No se encontró la imagen en /branding/ContaProMaxLogo.png"
            );
        }

        panelPrincipal.add(
                panelIzquierdo
        );

        panelPrincipal.add(
                panelDerecho
        );

        add(
                panelPrincipal
        );
    }

    private void aplicarIconoAplicacion() {

        URL recursoIcono =
                getClass()
                        .getResource(
                                "/branding/ContaProMaxIcon.png"
                        );

        if (
                recursoIcono == null
        ) {

            if (
                    MarcaUI.iconoVentana() != null
            ) {

                setIconImage(
                        MarcaUI.iconoVentana()
                );
            }

            return;
        }

        Image imagenBase =
                new ImageIcon(
                        recursoIcono
                ).getImage();

        List<Image> iconos =
                new ArrayList<>();

        int[] tamanos = {
            16,
            20,
            24,
            32,
            40,
            48,
            64,
            128,
            256
        };

        for (
                int tamano
                : tamanos
        ) {

            iconos.add(
                    imagenBase.getScaledInstance(
                            tamano,
                            tamano,
                            Image.SCALE_SMOOTH
                    )
            );
        }

        setIconImages(
                iconos
        );

        try {

            if (
                    Taskbar.isTaskbarSupported()
            ) {

                Taskbar barra =
                        Taskbar.getTaskbar();

                if (
                        barra.isSupported(
                                Taskbar.Feature.ICON_IMAGE
                        )
                ) {

                    barra.setIconImage(
                            imagenBase
                    );
                }
            }

        } catch (
                UnsupportedOperationException
                | SecurityException ignored
        ) {
        }
    }

    private JLabel label(
            String s
    ) {

        JLabel l =
                new JLabel(
                        s
                );

        l.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        return l;
    }

    private void login() {

        try {

            Usuario u =
                    auth.iniciarSesion(
                            txtUsuario.getText(),
                            txtPassword.getPassword()
                    );

            dispose();

            new VentanaPrincipal(
                    u
            ).setVisible(
                    true
            );

        } catch (
                Exception e
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "No se pudo iniciar sesión",
                    JOptionPane.ERROR_MESSAGE
            );

            txtPassword.setText(
                    ""
            );
        }
    }
}
