package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.Usuario;
import com.mycompany.sistemacontable.servicio.SesionUsuario;

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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class VentanaPrincipal extends JFrame {

    private final Color COLOR_FONDO =
            Paleta.FONDO;

    private final Color COLOR_MENU =
            Paleta.AZUL_OSCURO;

    private final Color COLOR_MENU_HOVER =
            Paleta.AZUL_HOVER;

    private final Color COLOR_ACTIVO =
            Paleta.ACENTO_OSCURO;

    private final Color COLOR_TEXTO =
            Paleta.TEXTO;

    private final Color COLOR_SECUNDARIO =
            Paleta.TEXTO_SUAVE;

    /** Iconos (unicode) asociados al texto de cada botón del menú lateral. */
    private String iconoPara(String texto) {
        switch (texto) {
            case "Dashboard": return "\u2302";
            case "Operaciones": return "\u21C4";
            case "Libro Diario": return "\u2630";
            case "Mayorización": return "\u2261";
            case "Balance de Comprobación": return "\u2696";
            case "Estado de Resultados": return "\u25B2";
            case "Balance General": return "\u25A4";
            case "Kardex PEPS": return "\u25A6";
            case "Catálogo de Cuentas": return "\u2637";
            case "Configuración": return "\u2699";
            case "Usuarios y Roles": return "\u263A";
            case "Cerrar sesión": return "\u23FB";
            default: return "\u2022";
        }
    }

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
                "Sistema Contable"
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

        setLayout(
                new BorderLayout()
        );

        getContentPane().setBackground(
                COLOR_FONDO
        );
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
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, Paleta.AZUL_OSCURO, 0, getHeight(), Paleta.AZUL_OSCURO_2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };

        panel.setOpaque(false);

        panel.setPreferredSize(
                new Dimension(
                        260,
                        0
                )
        );

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        25,
                        18,
                        25,
                        18
                )
        );

        JLabel iso = new JLabel("\u25C6 SISTEMA CONTABLE");

        iso.setForeground(
                Color.WHITE
        );

        iso.setFont(
                new Font(
                        Paleta.FUENTE,
                        Font.BOLD,
                        18
                )
        );

        iso.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel subtitulo =
                new JLabel(
                        "Gestión financiera"
                );

        subtitulo.setForeground(
                Paleta.TEXTO_SIDEBAR_SUAVE
        );

        subtitulo.setFont(
                new Font(
                        Paleta.FUENTE,
                        Font.PLAIN,
                        12
                )
        );

        subtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(
                iso
        );

        panel.add(
                Box.createVerticalStrut(
                        3
                )
        );

        panel.add(
                subtitulo
        );

        JSeparator separadorLogo = new JSeparator();
        separadorLogo.setForeground(new Color(255, 255, 255, 24));
        separadorLogo.setBackground(new Color(255, 255, 255, 0));
        separadorLogo.setAlignmentX(Component.LEFT_ALIGNMENT);
        separadorLogo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        panel.add(
                Box.createVerticalStrut(
                        22
                )
        );
        panel.add(separadorLogo);
        panel.add(
                Box.createVerticalStrut(
                        22
                )
        );

        agregarBotonMenu(
                panel,
                "DASHBOARD",
                "Dashboard"
        );

        if (SesionUsuario.puedeRegistrarOperaciones()) {
            agregarBotonMenu(
                    panel,
                    "OPERACIONES",
                    "Operaciones"
            );
        }

        agregarBotonMenu(
                panel,
                "LIBRO_DIARIO",
                "Libro Diario"
        );

        agregarBotonMenu(
                panel,
                "MAYORIZACION",
                "Mayorización"
        );

        agregarBotonMenu(
                panel,
                "BALANCE_COMPROBACION",
                "Balance de Comprobación"
        );

        agregarBotonMenu(
                panel,
                "ESTADO_RESULTADOS",
                "Estado de Resultados"
        );

        agregarBotonMenu(
                panel,
                "BALANCE_GENERAL",
                "Balance General"
        );

        agregarBotonMenu(
                panel,
                "KARDEX",
                "Kardex PEPS"
        );

        if (SesionUsuario.esAdministrador()) {
            agregarBotonMenu(
                    panel,
                    "CATALOGO",
                    "Catálogo de Cuentas"
            );

            agregarBotonMenu(
                    panel,
                    "CONFIGURACION",
                    "Configuración"
            );

            agregarBotonMenu(
                    panel,
                    "USUARIOS",
                    "Usuarios y Roles"
            );
        }

        panel.add(
                Box.createVerticalGlue()
        );

        RoundedPanel tarjetaSesion = new RoundedPanel(12);
        tarjetaSesion.setBackground(new Color(255, 255, 255, 18));
        tarjetaSesion.setLayout(new BoxLayout(tarjetaSesion, BoxLayout.Y_AXIS));
        tarjetaSesion.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        tarjetaSesion.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaSesion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel sesion = new JLabel(
                "<html><b>" + usuarioActual.getNombreCompleto() + "</b><br>"
                + usuarioActual.getRolNombre() + "</html>"
        );
        sesion.setForeground(Paleta.TEXTO_SIDEBAR);
        sesion.setFont(new Font(Paleta.FUENTE, Font.PLAIN, 12));
        sesion.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjetaSesion.add(sesion);
        panel.add(tarjetaSesion);
        panel.add(Box.createVerticalStrut(10));

        JButton cerrarSesion = crearBotonMenu("Cerrar sesión");
        cerrarSesion.addActionListener(e -> cerrarSesion());
        panel.add(cerrarSesion);
        panel.add(Box.createVerticalStrut(14));

        JLabel version =
                new JLabel(
                        "Sistema Contable v1.0"
                );

        version.setForeground(
                Paleta.TEXTO_SIDEBAR_SUAVE
        );

        version.setFont(
                new Font(
                        Paleta.FUENTE,
                        Font.PLAIN,
                        11
                )
        );

        version.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(
                version
        );

        return panel;
    }

    private void agregarBotonMenu(
            JPanel panel,
            String clave,
            String texto
    ) {

        JButton boton =
                crearBotonMenu(
                        texto
                );

        boton.addActionListener(
                e -> mostrarPanel(
                        clave
                )
        );

        botonesMenu.put(
                clave,
                boton
        );

        panel.add(
                boton
        );

        panel.add(
                Box.createVerticalStrut(
                        7
                )
        );
    }

    private JButton crearBotonMenu(
            String texto
    ) {

        RoundedButton boton = new RoundedButton(
                "  " + texto,
                10
        );

        boton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        44
                )
        );

        boton.setPreferredSize(
                new Dimension(
                        210,
                        44
                )
        );

        boton.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        boton.setFont(
                new Font(
                        Paleta.FUENTE,
                        Font.BOLD,
                        13
                )
        );

        boton.setForeground(
                Paleta.TEXTO_SIDEBAR
        );

        boton.setBackground(COLOR_MENU);

        boton.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        if (!boton.getBackground().equals(COLOR_ACTIVO)) {
                            boton.setBackground(COLOR_MENU_HOVER);
                        }
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        if (!boton.getBackground().equals(COLOR_ACTIVO)) {
                            boton.setBackground(COLOR_MENU);
                        }
                    }
                }
        );

        return boton;
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
                        Paleta.FUENTE,
                        Font.BOLD,
                        20
                )
        );

        titulo.setForeground(
                COLOR_TEXTO
        );

        JPanel bloqueUsuario = new JPanel();
        bloqueUsuario.setOpaque(false);
        bloqueUsuario.setLayout(new BoxLayout(bloqueUsuario, BoxLayout.X_AXIS));

        RoundedPanel avatar = new RoundedPanel(20);
        avatar.setBackground(Paleta.ACENTO_SUAVE);
        avatar.setLayout(new BorderLayout());
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setMaximumSize(new Dimension(36, 36));
        avatar.setBorder(BorderFactory.createEmptyBorder());
        String inicial = usuarioActual.getNombreCompleto() != null && !usuarioActual.getNombreCompleto().isBlank()
                ? usuarioActual.getNombreCompleto().substring(0, 1).toUpperCase() : "U";
        JLabel lblInicial = new JLabel(inicial, SwingConstants.CENTER);
        lblInicial.setForeground(Paleta.ACENTO_OSCURO);
        lblInicial.setFont(new Font(Paleta.FUENTE, Font.BOLD, 14));
        avatar.add(lblInicial, BorderLayout.CENTER);

        JPanel textoUsuario = new JPanel();
        textoUsuario.setOpaque(false);
        textoUsuario.setLayout(new BoxLayout(textoUsuario, BoxLayout.Y_AXIS));

        JLabel empresa =
                new JLabel(
                        usuarioActual.getNombreCompleto()
                );

        empresa.setFont(
                new Font(
                        Paleta.FUENTE,
                        Font.BOLD,
                        13
                )
        );

        empresa.setForeground(
                COLOR_TEXTO
        );

        JLabel rol = new JLabel(usuarioActual.getRolNombre());
        rol.setFont(new Font(Paleta.FUENTE, Font.PLAIN, 11));
        rol.setForeground(COLOR_SECUNDARIO);

        textoUsuario.add(empresa);
        textoUsuario.add(rol);

        bloqueUsuario.add(textoUsuario);
        bloqueUsuario.add(Box.createHorizontalStrut(12));
        bloqueUsuario.add(avatar);

        panel.add(
                titulo,
                BorderLayout.WEST
        );

        panel.add(
                bloqueUsuario,
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
                        + "<h2>Bienvenido al Sistema Contable</h2>"
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