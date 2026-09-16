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
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

public class VentanaPrincipal extends JFrame {

    private final Color COLOR_FONDO =
            new Color(245, 247, 250);

    private final Color COLOR_MENU =
            new Color(18, 32, 52);

    private final Color COLOR_MENU_HOVER =
            new Color(31, 49, 73);

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

        JPanel panel =
                new JPanel();

        panel.setBackground(
                COLOR_MENU
        );

        panel.setPreferredSize(
                new Dimension(
                        255,
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

        JLabel logo =
                new JLabel(
                        "SISTEMA CONTABLE"
                );

        logo.setForeground(
                Color.WHITE
        );

        logo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        20
                )
        );

        logo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel subtitulo =
                new JLabel(
                        "Gestión financiera"
                );

        subtitulo.setForeground(
                new Color(
                        160,
                        174,
                        192
                )
        );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        subtitulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        panel.add(
                logo
        );

        panel.add(
                Box.createVerticalStrut(
                        3
                )
        );

        panel.add(
                subtitulo
        );

        panel.add(
                Box.createVerticalStrut(
                        32
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

        JLabel sesion = new JLabel(
                "<html><b>" + usuarioActual.getNombreCompleto() + "</b><br>"
                + usuarioActual.getRolNombre() + "</html>"
        );
        sesion.setForeground(new Color(190, 200, 214));
        sesion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sesion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sesion);
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
                new Color(
                        120,
                        135,
                        155
                )
        );

        version.setFont(
                new Font(
                        "Segoe UI",
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

        JButton boton =
                new JButton(
                        texto
                );

        boton.setUI(
                new BasicButtonUI()
        );

        boton.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        46
                )
        );

        boton.setPreferredSize(
                new Dimension(
                        210,
                        46
                )
        );

        boton.setHorizontalAlignment(
                SwingConstants.LEFT
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        boton.setForeground(
                new Color(
                        220,
                        226,
                        235
                )
        );

        boton.setBackground(
                COLOR_MENU
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        0,
                        15,
                        0,
                        10
                )
        );

        boton.setFocusPainted(
                false
        );

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.setOpaque(
                true
        );

        boton.setContentAreaFilled(
                true
        );

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent evt
                    ) {

                        if (!boton.getBackground()
                                .equals(
                                        COLOR_ACTIVO
                                )) {

                            boton.setBackground(
                                    COLOR_MENU_HOVER
                            );
                        }
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent evt
                    ) {

                        if (!boton.getBackground()
                                .equals(
                                        COLOR_ACTIVO
                                )) {

                            boton.setBackground(
                                    COLOR_MENU
                            );
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