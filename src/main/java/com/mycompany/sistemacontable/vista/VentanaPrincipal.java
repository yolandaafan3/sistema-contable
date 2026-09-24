package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.Usuario;
import com.mycompany.sistemacontable.servicio.SesionUsuario;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

public class VentanaPrincipal extends JFrame {

    private final Color COLOR_FONDO =
            new Color(245, 247, 250);

    private final Color COLOR_MENU =
            new Color(19, 65, 125);

    private final Color COLOR_MENU_HOVER =
            new Color(31, 86, 157);

    private final Color COLOR_ACTIVO =
            new Color(37, 99, 235);

    private final Color COLOR_TEXTO =
            new Color(30, 41, 59);

    private final Color COLOR_SECUNDARIO =
            new Color(100, 116, 139);

    private final Usuario usuarioActual;

    private final CardLayout cardLayout;
    private final JPanel panelContenido;
    private final Map<String, JButton> botonesMenu;

    private PanelDashboard panelDashboard;
    private PanelLibroDiario panelLibroDiario;
    private PanelMayorizacion panelMayorizacion;
    private PanelBalanceComprobacion panelBalanceComprobacion;
    private PanelEstadoResultados panelEstadoResultados;
    private PanelBalanceGeneral panelBalanceGeneral;
    private PanelKardexPeps panelKardexPeps;
    private PanelConfiguracion panelConfiguracion;
    private PanelUsuarios panelUsuarios;
    private PanelAuditoria panelAuditoria;
    private PanelReportes panelReportes;
    private PanelPeriodos panelPeriodos;

    public VentanaPrincipal(Usuario usuarioActual) {

        this.usuarioActual = usuarioActual;

        cardLayout =
                new CardLayout();

        panelContenido =
                new JPanel(
                        cardLayout
                );

        botonesMenu =
                new LinkedHashMap<>();

        configurarVentana();

        construirInterfaz();

        mostrarPanel(
                "DASHBOARD"
        );
    }

    private void configurarVentana() {

        setTitle(
                "ContaProMax - Sistema Contable"
        );

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setMinimumSize(
                new Dimension(
                        1100,
                        700
                )
        );

        setSize(
                1360,
                820
        );

        setLocationRelativeTo(
                null
        );

        // Abrir ContaProMax maximizado automáticamente después del inicio de sesión.
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(
                new BorderLayout()
        );

        getContentPane().setBackground(
                COLOR_FONDO
        );

        // Icono oficial de ContaProMax para ventana, barra de tareas y dock.
        aplicarIconoAplicacion();
    }

    private void aplicarIconoAplicacion() {

        URL recursoIcono =
                getClass().getResource(
                        "/branding/ContaProMaxIcon.png"
                );

        if (recursoIcono == null) {

            if (MarcaUI.iconoVentana() != null) {
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

        // Varias resoluciones ayudan a que Windows/Linux elijan
        // la más adecuada para título, barra de tareas y selector de ventanas.
        java.util.List<Image> iconos =
                new java.util.ArrayList<>();

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

        // En escritorios que permiten cambiar específicamente
        // el icono del dock/barra de tareas, también lo establecemos aquí.
        try {

            if (
                    java.awt.Taskbar.isTaskbarSupported()
            ) {

                java.awt.Taskbar barra =
                        java.awt.Taskbar.getTaskbar();

                if (
                        barra.isSupported(
                                java.awt.Taskbar.Feature.ICON_IMAGE
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
            // Algunos entornos de escritorio no permiten modificarlo en ejecución.
        }
    }

    private void construirInterfaz() {

        add(
                crearMenuLateral(),
                BorderLayout.WEST
        );

        add(
                crearAreaPrincipal(),
                BorderLayout.CENTER
        );
    }

    private JPanel crearMenuLateral() {

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint degradado = new GradientPaint(
                        0, 0, new Color(20, 91, 173),
                        0, getHeight(), new Color(8, 43, 96)
                );

                g2.setPaint(degradado);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        panel.setPreferredSize(new Dimension(270, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(22, 16, 18, 16));
        panel.setOpaque(false);

        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.X_AXIS));
        cabecera.setOpaque(false);
        cabecera.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JLabel logoImagen = new JLabel(cargarLogoSistema(54, 54));
        logoImagen.setPreferredSize(new Dimension(54, 54));
        logoImagen.setMinimumSize(new Dimension(54, 54));
        logoImagen.setMaximumSize(new Dimension(54, 54));

        JPanel textosLogo = new JPanel();
        textosLogo.setLayout(new BoxLayout(textosLogo, BoxLayout.Y_AXIS));
        textosLogo.setOpaque(false);

        JLabel nombreSistema = new JLabel("ContaProMax");
        nombreSistema.setForeground(Color.WHITE);
        nombreSistema.setFont(new Font("Segoe UI", Font.BOLD, 21));
        nombreSistema.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel(MarcaUI.LEMA);
        subtitulo.setForeground(new Color(205, 220, 240));
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        textosLogo.add(Box.createVerticalGlue());
        textosLogo.add(nombreSistema);
        textosLogo.add(Box.createVerticalStrut(2));
        textosLogo.add(subtitulo);
        textosLogo.add(Box.createVerticalGlue());

        cabecera.add(logoImagen);
        cabecera.add(Box.createHorizontalStrut(10));
        cabecera.add(textosLogo);

        panel.add(cabecera);
        panel.add(Box.createVerticalStrut(18));

        JPanel separador = new JPanel();
        separador.setBackground(new Color(255, 255, 255, 45));
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separador.setPreferredSize(new Dimension(0, 1));
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(separador);
        panel.add(Box.createVerticalStrut(14));

        agregarBotonMenu(panel, "DASHBOARD", "Dashboard");

        if (SesionUsuario.puedeRegistrarOperaciones()) {
            agregarBotonMenu(panel, "OPERACIONES", "Operaciones");
        }

        agregarBotonMenu(panel, "LIBRO_DIARIO", "Libro Diario");
        agregarBotonMenu(panel, "MAYORIZACION", "Mayorización");
        agregarBotonMenu(panel, "BALANCE_COMPROBACION", "Balance de Comprobación");
        agregarBotonMenu(panel, "ESTADO_RESULTADOS", "Estado de Resultados");
        agregarBotonMenu(panel, "BALANCE_GENERAL", "Balance General");
        agregarBotonMenu(panel, "KARDEX", "Kardex PEPS");
        agregarBotonMenu(panel, "REPORTES", "Generador de Reportes");

        if (SesionUsuario.esAdministrador()) {
            agregarBotonMenu(panel, "PERIODOS", "Períodos Contables");
            agregarBotonMenu(panel, "AUDITORIA", "Bitácora de Auditoría");
            agregarBotonMenu(panel, "CATALOGO", "Catálogo de Cuentas");
            agregarBotonMenu(panel, "CONFIGURACION", "Configuración");
            agregarBotonMenu(panel, "USUARIOS", "Usuarios y Roles");
        }

        panel.add(Box.createVerticalGlue());

        JPanel tarjetaUsuario = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 24));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        tarjetaUsuario.setOpaque(false);
        tarjetaUsuario.setLayout(new BorderLayout(10, 0));
        tarjetaUsuario.setBorder(BorderFactory.createEmptyBorder(10, 11, 10, 11));
        tarjetaUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
        tarjetaUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconoUsuario = new JLabel(crearIconoMenu("USUARIOS"));
        iconoUsuario.setHorizontalAlignment(SwingConstants.CENTER);
        iconoUsuario.setPreferredSize(new Dimension(28, 28));

        JLabel sesion = new JLabel(
                "<html>"
                + "<span style='color:white'><b>" + usuarioActual.getNombreCompleto() + "</b></span>"
                + "<br>"
                + "<span style='color:#C7D7EA'>" + usuarioActual.getRolNombre() + "</span>"
                + "</html>"
        );
        sesion.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        tarjetaUsuario.add(iconoUsuario, BorderLayout.WEST);
        tarjetaUsuario.add(sesion, BorderLayout.CENTER);

        panel.add(tarjetaUsuario);
        panel.add(Box.createVerticalStrut(8));

        JButton cerrarSesion = crearBotonMenu("Cerrar sesión", "SALIR");
        cerrarSesion.addActionListener(e -> cerrarSesion());
        panel.add(cerrarSesion);
        panel.add(Box.createVerticalStrut(7));

        JLabel version = new JLabel("ContaProMax v2.4-PERIODOS-CERO");
        version.setForeground(new Color(163, 190, 220));
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(version);

        return panel;
    }

    private void agregarBotonMenu(
            JPanel panel,
            String clave,
            String texto
    ) {

        JButton boton = crearBotonMenu(texto, clave);

        boton.addActionListener(
                e -> mostrarPanel(clave)
        );

        botonesMenu.put(clave, boton);

        panel.add(boton);
        panel.add(Box.createVerticalStrut(4));
    }

    private JButton crearBotonMenu(
            String texto,
            String clave
    ) {

        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);

                if (getBackground().equals(COLOR_ACTIVO)) {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(3, 9, 4, getHeight() - 18, 4, 4);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        boton.setUI(new BasicButtonUI());
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        boton.setPreferredSize(new Dimension(230, 40));
        boton.setMinimumSize(new Dimension(210, 40));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setForeground(new Color(235, 242, 252));
        boton.setBackground(COLOR_MENU);
        boton.setIcon(crearIconoMenu(clave));
        boton.setIconTextGap(13);
        boton.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(false);
        boton.setContentAreaFilled(false);

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        if (!boton.getBackground().equals(COLOR_ACTIVO)) {
                            boton.setBackground(COLOR_MENU_HOVER);
                            boton.repaint();
                        }
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (!boton.getBackground().equals(COLOR_ACTIVO)) {
                            boton.setBackground(COLOR_MENU);
                            boton.repaint();
                        }
                    }
                }
        );

        return boton;
    }

    private Icon crearIconoMenu(String clave) {

        int tamano = 20;
        BufferedImage imagen = new BufferedImage(tamano, tamano, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = imagen.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(
                1.8f,
                BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND
        ));

        switch (clave) {
            case "DASHBOARD":
                g2.fillRoundRect(2, 2, 7, 7, 2, 2);
                g2.fillRoundRect(11, 2, 7, 7, 2, 2);
                g2.fillRoundRect(2, 11, 7, 7, 2, 2);
                g2.fillRoundRect(11, 11, 7, 7, 2, 2);
                break;

            case "OPERACIONES":
                g2.drawRoundRect(3, 2, 14, 16, 2, 2);
                g2.drawLine(6, 7, 14, 7);
                g2.drawLine(6, 11, 14, 11);
                g2.drawLine(6, 15, 11, 15);
                break;

            case "LIBRO_DIARIO":
                g2.drawRoundRect(3, 2, 14, 16, 2, 2);
                g2.drawLine(7, 2, 7, 18);
                g2.drawLine(10, 7, 14, 7);
                g2.drawLine(10, 11, 14, 11);
                break;

            case "MAYORIZACION":
                g2.drawLine(3, 5, 17, 5);
                g2.drawLine(10, 5, 10, 17);
                g2.drawLine(4, 9, 8, 9);
                g2.drawLine(12, 9, 16, 9);
                g2.drawLine(4, 13, 8, 13);
                g2.drawLine(12, 13, 16, 13);
                break;

            case "BALANCE_COMPROBACION":
                g2.drawLine(10, 3, 10, 16);
                g2.drawLine(4, 6, 16, 6);
                g2.drawLine(6, 6, 3, 12);
                g2.drawLine(14, 6, 17, 12);
                g2.drawArc(1, 10, 6, 5, 180, 180);
                g2.drawArc(13, 10, 6, 5, 180, 180);
                g2.drawLine(6, 17, 14, 17);
                break;

            case "ESTADO_RESULTADOS":
                g2.drawLine(3, 17, 3, 3);
                g2.drawLine(3, 17, 17, 17);
                g2.drawLine(5, 14, 9, 10);
                g2.drawLine(9, 10, 12, 12);
                g2.drawLine(12, 12, 17, 5);
                g2.drawLine(14, 5, 17, 5);
                g2.drawLine(17, 5, 17, 8);
                break;

            case "BALANCE_GENERAL":
                g2.drawLine(2, 7, 10, 2);
                g2.drawLine(10, 2, 18, 7);
                g2.drawLine(3, 7, 17, 7);
                g2.drawLine(5, 8, 5, 16);
                g2.drawLine(10, 8, 10, 16);
                g2.drawLine(15, 8, 15, 16);
                g2.drawLine(3, 17, 17, 17);
                break;

            case "KARDEX":
                g2.drawRect(3, 4, 14, 13);
                g2.drawLine(3, 8, 17, 8);
                g2.drawLine(8, 4, 8, 17);
                g2.drawLine(12, 4, 12, 17);
                g2.drawLine(3, 12, 17, 12);
                break;

            case "REPORTES":
                g2.drawRoundRect(3, 2, 14, 16, 2, 2);
                g2.fillRect(6, 11, 2, 4);
                g2.fillRect(9, 8, 2, 7);
                g2.fillRect(12, 5, 2, 10);
                break;

            case "PERIODOS":
                g2.drawRoundRect(2, 4, 16, 14, 2, 2);
                g2.drawLine(2, 8, 18, 8);
                g2.drawLine(6, 2, 6, 6);
                g2.drawLine(14, 2, 14, 6);
                g2.fillOval(6, 11, 2, 2);
                g2.fillOval(10, 11, 2, 2);
                g2.fillOval(14, 11, 2, 2);
                break;

            case "AUDITORIA":
                g2.drawOval(3, 3, 10, 10);
                g2.drawLine(11, 11, 17, 17);
                g2.drawLine(6, 7, 10, 7);
                g2.drawLine(6, 10, 9, 10);
                break;

            case "CATALOGO":
                g2.fillOval(3, 4, 2, 2);
                g2.fillOval(3, 9, 2, 2);
                g2.fillOval(3, 14, 2, 2);
                g2.drawLine(8, 5, 17, 5);
                g2.drawLine(8, 10, 17, 10);
                g2.drawLine(8, 15, 17, 15);
                break;

            case "CONFIGURACION":
                g2.drawOval(5, 5, 10, 10);
                g2.drawOval(8, 8, 4, 4);
                g2.drawLine(10, 1, 10, 5);
                g2.drawLine(10, 15, 10, 19);
                g2.drawLine(1, 10, 5, 10);
                g2.drawLine(15, 10, 19, 10);
                g2.drawLine(4, 4, 6, 6);
                g2.drawLine(14, 14, 16, 16);
                g2.drawLine(16, 4, 14, 6);
                g2.drawLine(4, 16, 6, 14);
                break;

            case "USUARIOS":
                g2.drawOval(7, 2, 6, 6);
                g2.drawArc(4, 9, 12, 9, 0, 180);
                g2.drawOval(2, 5, 4, 4);
                g2.drawOval(14, 5, 4, 4);
                break;

            case "SALIR":
                g2.drawRect(3, 3, 8, 14);
                g2.drawLine(8, 10, 18, 10);
                g2.drawLine(15, 7, 18, 10);
                g2.drawLine(15, 13, 18, 10);
                break;

            default:
                g2.fillOval(6, 6, 8, 8);
                break;
        }

        g2.dispose();
        return new ImageIcon(imagen);
    }

    private Icon cargarLogoSistema(int ancho, int alto) {

        String[] rutas = {
            "/ContaProMaxLogo.png",
            "/branding/ContaProMaxLogo.png",
            "/img/ContaProMaxLogo.png",
            "/com/mycompany/sistemacontable/imagenes/ContaProMaxLogo.png"
                
        };

        for (String ruta : rutas) {
            URL recurso = getClass().getResource(ruta);

            if (recurso != null) {
                ImageIcon original = new ImageIcon(recurso);
                Image escalada = original.getImage().getScaledInstance(
                        ancho,
                        alto,
                        Image.SCALE_SMOOTH
                );
                return new ImageIcon(escalada);
            }
        }

        return MarcaUI.logo(ancho, alto);
    }

    private JPanel crearAreaPrincipal() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                COLOR_FONDO
        );

        panelContenido.setBackground(
                COLOR_FONDO
        );

        registrarPaneles();

        panel.add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        panel.add(
                panelContenido,
                BorderLayout.CENTER
        );

        return panel;
    }

    private JPanel crearEncabezado() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setPreferredSize(
                new Dimension(
                        0,
                        72
                )
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                new Color(
                                        226,
                                        232,
                                        240
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                0,
                                30,
                                0,
                                30
                        )
                )
        );

        JLabel titulo =
                new JLabel(
                        "Gestión Contable"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        titulo.setForeground(
                COLOR_TEXTO
        );

        JLabel empresa =
                new JLabel(
                        usuarioActual.getNombreCompleto() + " · " + usuarioActual.getRolNombre()
                );

        empresa.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        empresa.setForeground(
                COLOR_SECUNDARIO
        );

        panel.add(
                titulo,
                BorderLayout.WEST
        );

        panel.add(
                empresa,
                BorderLayout.EAST
        );

        return panel;
    }

    private void registrarPaneles() {

        panelDashboard =
                new PanelDashboard();

        panelContenido.add(
                panelDashboard,
                "DASHBOARD"
        );

        panelContenido.add(
                new PanelOperaciones(),
                "OPERACIONES"
        );

        panelLibroDiario =
                new PanelLibroDiario(SesionUsuario.puedeEditarAsientos());

        panelContenido.add(
                panelLibroDiario,
                "LIBRO_DIARIO"
        );

        panelMayorizacion =
                new PanelMayorizacion();

        panelContenido.add(
                panelMayorizacion,
                "MAYORIZACION"
        );

        panelBalanceComprobacion =
                new PanelBalanceComprobacion();

        panelContenido.add(
                panelBalanceComprobacion,
                "BALANCE_COMPROBACION"
        );

        panelEstadoResultados =
                new PanelEstadoResultados();

        panelContenido.add(
                panelEstadoResultados,
                "ESTADO_RESULTADOS"
        );

        panelBalanceGeneral =
                new PanelBalanceGeneral();

        panelContenido.add(
                panelBalanceGeneral,
                "BALANCE_GENERAL"
        );

        panelKardexPeps =
                new PanelKardexPeps();

        panelContenido.add(
                panelKardexPeps,
                "KARDEX"
        );

        panelReportes = new PanelReportes();
        panelContenido.add(panelReportes, "REPORTES");
        if (SesionUsuario.esAdministrador()) {
            panelPeriodos = new PanelPeriodos();
            panelContenido.add(panelPeriodos, "PERIODOS");
            panelAuditoria = new PanelAuditoria();
            panelContenido.add(panelAuditoria, "AUDITORIA");
        }

        panelContenido.add(
                new PanelCatalogoCuentas(),
                "CATALOGO"
        );

        panelConfiguracion =
                new PanelConfiguracion();

        panelContenido.add(
                panelConfiguracion,
                "CONFIGURACION"
        );

        if (SesionUsuario.esAdministrador()) {
            panelUsuarios = new PanelUsuarios();
            panelContenido.add(panelUsuarios, "USUARIOS");
        }
    }

    private JPanel crearDashboard() {

        JPanel fondo =
                new JPanel(
                        new BorderLayout()
                );

        fondo.setBackground(
                COLOR_FONDO
        );

        fondo.setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );

        JPanel contenido =
                new JPanel();

        contenido.setBackground(
                COLOR_FONDO
        );

        contenido.setLayout(
                new BoxLayout(
                        contenido,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Dashboard"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(
                COLOR_TEXTO
        );

        titulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel subtitulo =
                new JLabel(
                        "Resumen general del período contable"
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitulo.setForeground(
                COLOR_SECUNDARIO
        );

        subtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        contenido.add(
                titulo
        );

        contenido.add(
                Box.createVerticalStrut(
                        5
                )
        );

        contenido.add(
                subtitulo
        );

        contenido.add(
                Box.createVerticalStrut(
                        30
                )
        );

        JPanel tarjetas =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                18,
                                0
                        )
                );

        tarjetas.setOpaque(
                false
        );

        tarjetas.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        125
                )
        );

        tarjetas.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        tarjetas.add(
                crearTarjetaDashboard(
                        "Ventas",
                        "$0.00",
                        "Ventas netas"
                )
        );

        tarjetas.add(
                crearTarjetaDashboard(
                        "Inventario",
                        "$0.00",
                        "Saldo PEPS"
                )
        );

        tarjetas.add(
                crearTarjetaDashboard(
                        "Utilidad",
                        "$0.00",
                        "Resultado del período"
                )
        );

        tarjetas.add(
                crearTarjetaDashboard(
                        "Balance",
                        "Pendiente",
                        "Estado contable"
                )
        );

        contenido.add(
                tarjetas
        );

        contenido.add(
                Box.createVerticalStrut(
                        25
                )
        );

        JPanel bienvenida =
                new JPanel(
                        new BorderLayout()
                );

        bienvenida.setBackground(
                Color.WHITE
        );

        bienvenida.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        226,
                                        232,
                                        240
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                25,
                                25,
                                25,
                                25
                        )
                )
        );

        bienvenida.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        175
                )
        );

        bienvenida.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel mensaje =
                new JLabel(
                        "<html>"
                        + "<div style='width:700px'>"
                        + "<h2>Bienvenido a ContaProMax</h2>"
                        + "<p>Registra operaciones y consulta automáticamente "
                        + "el Libro Diario, Mayorización, Balance de Comprobación, "
                        + "Estado de Resultados, Balance General y Kardex PEPS.</p>"
                        + "</div>"
                        + "</html>"
                );

        mensaje.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        mensaje.setForeground(
                new Color(
                        55,
                        65,
                        81
                )
        );

        bienvenida.add(
                mensaje,
                BorderLayout.CENTER
        );

        contenido.add(
                bienvenida
        );

        JScrollPane scroll =
                new JScrollPane(
                        contenido
                );

        scroll.setBorder(
                null
        );

        scroll.getViewport().setBackground(
                COLOR_FONDO
        );

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        fondo.add(
                scroll,
                BorderLayout.CENTER
        );

        return fondo;
    }

    private JPanel crearTarjetaDashboard(
            String titulo,
            String valor,
            String descripcion
    ) {

        JPanel tarjeta =
                new JPanel();

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setLayout(
                new BoxLayout(
                        tarjeta,
                        BoxLayout.Y_AXIS
                )
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        226,
                                        232,
                                        240
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        lblTitulo.setForeground(
                COLOR_SECUNDARIO
        );

        JLabel lblValor =
                new JLabel(
                        valor
                );

        lblValor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        24
                )
        );

        lblValor.setForeground(
                COLOR_TEXTO
        );

        JLabel lblDescripcion =
                new JLabel(
                        descripcion
                );

        lblDescripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        lblDescripcion.setForeground(
                new Color(
                        140,
                        150,
                        165
                )
        );

        tarjeta.add(
                lblTitulo
        );

        tarjeta.add(
                Box.createVerticalStrut(
                        8
                )
        );

        tarjeta.add(
                lblValor
        );

        tarjeta.add(
                Box.createVerticalGlue()
        );

        tarjeta.add(
                lblDescripcion
        );

        return tarjeta;
    }

    private JPanel crearPanelTemporal(
            String titulo,
            String descripcion
    ) {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
                );

        panel.setBackground(
                COLOR_FONDO
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );

        JPanel tarjeta =
                new JPanel();

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setLayout(
                new BoxLayout(
                        tarjeta,
                        BoxLayout.Y_AXIS
                )
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        226,
                                        232,
                                        240
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                30,
                                30,
                                30,
                                30
                        )
                )
        );

        JLabel lblTitulo =
                new JLabel(
                        titulo
                );

        lblTitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        27
                )
        );

        lblTitulo.setForeground(
                COLOR_TEXTO
        );

        JLabel lblDescripcion =
                new JLabel(
                        descripcion
                );

        lblDescripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        lblDescripcion.setForeground(
                COLOR_SECUNDARIO
        );

        JLabel lblProximamente =
                new JLabel(
                        "Esta pantalla será conectada en los siguientes pasos."
                );

        lblProximamente.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        lblProximamente.setForeground(
                COLOR_ACTIVO
        );

        tarjeta.add(
                lblTitulo
        );

        tarjeta.add(
                Box.createVerticalStrut(
                        8
                )
        );

        tarjeta.add(
                lblDescripcion
        );

        tarjeta.add(
                Box.createVerticalStrut(
                        25
                )
        );

        tarjeta.add(
                lblProximamente
        );

        panel.add(
                tarjeta,
                BorderLayout.NORTH
        );

        return panel;
    }

    private void mostrarPanel(
            String nombre
    ) {

        if ("DASHBOARD".equals(nombre) && panelDashboard != null) {
            panelDashboard.cargarDashboard();
        } else if ("LIBRO_DIARIO".equals(nombre) && panelLibroDiario != null) {
            panelLibroDiario.cargarLibroDiario();
        } else if ("MAYORIZACION".equals(nombre) && panelMayorizacion != null) {
            panelMayorizacion.cargarMayorizacion();
        } else if ("BALANCE_COMPROBACION".equals(nombre) && panelBalanceComprobacion != null) {
            panelBalanceComprobacion.cargarBalance();
        } else if ("ESTADO_RESULTADOS".equals(nombre) && panelEstadoResultados != null) {
            panelEstadoResultados.cargarEstadoResultados();
        } else if ("BALANCE_GENERAL".equals(nombre) && panelBalanceGeneral != null) {
            panelBalanceGeneral.cargarBalanceGeneral();
        } else if ("KARDEX".equals(nombre) && panelKardexPeps != null) {
            panelKardexPeps.cargarKardex();
        } else if ("AUDITORIA".equals(nombre) && panelAuditoria != null) {
            panelAuditoria.cargar();
        } else if ("CONFIGURACION".equals(nombre) && panelConfiguracion != null) {
            panelConfiguracion.cargarConfiguracion();
        } else if ("USUARIOS".equals(nombre) && panelUsuarios != null) {
            panelUsuarios.cargar();
        }

        cardLayout.show(
                panelContenido,
                nombre
        );

        for (Map.Entry<String, JButton> entrada
                : botonesMenu.entrySet()) {

            JButton boton =
                    entrada.getValue();

            if (entrada.getKey()
                    .equals(
                            nombre
                    )) {

                boton.setBackground(
                        COLOR_ACTIVO
                );

                boton.setForeground(
                        Color.WHITE
                );

            } else {

                boton.setBackground(
                        COLOR_MENU
                );

                boton.setForeground(
                        new Color(
                                220,
                                226,
                                235
                        )
                );
            }
        }
    }


    private void cerrarSesion() {
        int op = javax.swing.JOptionPane.showConfirmDialog(
                this, "¿Deseas cerrar la sesión actual?", "Cerrar sesión",
                javax.swing.JOptionPane.YES_NO_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE);
        if (op == javax.swing.JOptionPane.YES_OPTION) {
            SesionUsuario.cerrar();
            dispose();
            new VentanaLogin().setVisible(true);
        }
    }
}