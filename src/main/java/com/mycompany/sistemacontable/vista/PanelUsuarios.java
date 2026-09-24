package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.UsuarioDAO;
import com.mycompany.sistemacontable.modelo.Usuario;
import com.mycompany.sistemacontable.servicio.PasswordUtil;
import com.mycompany.sistemacontable.servicio.SesionUsuario;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

public class PanelUsuarios extends JPanel {

    private final UsuarioDAO dao =
            new UsuarioDAO();

    private final DefaultTableModel model =
            new DefaultTableModel(
                    new Object[]{
                        "ID",
                        "Usuario",
                        "Nombre",
                        "Rol",
                        "Estado"
                    },
                    0
            ) {
                @Override
                public boolean isCellEditable(
                        int row,
                        int column
                ) {
                    return false;
                }
            };

    private final JTable tabla =
            new JTable(
                    model
            );

    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField txtBuscar;
    private JComboBox<String> cmbRol;
    private JComboBox<String> cmbEstado;

    private final JLabel lblTotal =
            new JLabel("0");

    private final JLabel lblActivos =
            new JLabel("0");

    private final JLabel lblAdministradores =
            new JLabel("0");

    private final JLabel lblRoles =
            new JLabel("0");

    private final JLabel lblVisible =
            new JLabel("0 usuarios visibles");

    private final Color FONDO =
            new Color(245, 247, 250);

    private final Color TEXTO =
            new Color(30, 41, 59);

    private final Color SECUNDARIO =
            new Color(100, 116, 139);

    private final Color BORDE =
            new Color(226, 232, 240);

    private final Color PRIMARIO =
            new Color(37, 99, 235);

    private final Color PRIMARIO_HOVER =
            new Color(29, 78, 216);

    private final Color VERDE =
            new Color(22, 163, 74);

    private final Color VERDE_HOVER =
            new Color(21, 128, 61);

    private final Color NARANJA =
            new Color(234, 88, 12);

    private final Color MORADO =
            new Color(126, 34, 206);

    private final Color ROJO =
            new Color(220, 38, 38);

    private final Color CELESTE_SUAVE =
            new Color(239, 246, 255);

    private final Color SELECCION =
            new Color(219, 234, 254);

    public PanelUsuarios() {

        setLayout(
                new BorderLayout(
                        0,
                        18
                )
        );

        setBackground(
                FONDO
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        24,
                        26,
                        26,
                        26
                )
        );

        add(
                crearZonaSuperior(),
                BorderLayout.NORTH
        );

        add(
                crearContenido(),
                BorderLayout.CENTER
        );

        cargar();
    }

    private JPanel crearZonaSuperior() {

        JPanel zona =
                new JPanel();

        zona.setOpaque(
                false
        );

        zona.setLayout(
                new BoxLayout(
                        zona,
                        BoxLayout.Y_AXIS
                )
        );

        zona.add(
                crearEncabezado()
        );

        zona.add(
                Box.createVerticalStrut(
                        18
                )
        );

        zona.add(
                crearResumen()
        );

        return zona;
    }

    private JPanel crearEncabezado() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                20,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(
                false
        );

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Usuarios y Roles"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        28
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel subtitulo =
                new JLabel(
                        "Administra los usuarios, roles y estados de acceso al sistema."
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        subtitulo.setForeground(
                SECUNDARIO
        );

        textos.add(
                titulo
        );

        textos.add(
                Box.createVerticalStrut(
                        5
                )
        );

        textos.add(
                subtitulo
        );

        JPanel acciones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );

        acciones.setOpaque(
                false
        );

        JButton nuevo =
                crearBotonPrincipal(
                        "＋  Nuevo usuario",
                        PRIMARIO,
                        PRIMARIO_HOVER
                );

        JButton estado =
                crearBotonSecundario(
                        "◉  Activar / Desactivar"
                );

        JButton actualizar =
                crearBotonSecundario(
                        "↻  Actualizar"
                );

        nuevo.addActionListener(
                e -> nuevo()
        );

        estado.addActionListener(
                e -> cambiarEstado()
        );

        actualizar.addActionListener(
                e -> cargar()
        );

        acciones.add(
                nuevo
        );

        acciones.add(
                estado
        );

        acciones.add(
                actualizar
        );

        panel.add(
                textos,
                BorderLayout.WEST
        );

        panel.add(
                acciones,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel crearResumen() {

        JPanel panel =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                14,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        panel.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        100
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Usuarios registrados",
                        lblTotal,
                        "●",
                        new Color(
                                219,
                                234,
                                254
                        ),
                        PRIMARIO
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Usuarios activos",
                        lblActivos,
                        "✓",
                        new Color(
                                220,
                                252,
                                231
                        ),
                        VERDE
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Administradores",
                        lblAdministradores,
                        "★",
                        new Color(
                                243,
                                232,
                                255
                        ),
                        MORADO
                )
        );

        panel.add(
                crearTarjetaResumen(
                        "Roles configurados",
                        lblRoles,
                        "▤",
                        new Color(
                                255,
                                247,
                                237
                        ),
                        NARANJA
                )
        );

        return panel;
    }

    private JPanel crearTarjetaResumen(
            String titulo,
            JLabel valor,
            String iconoTexto,
            Color fondoIcono,
            Color colorIcono
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                12,
                                0
                        )
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                13,
                                15,
                                13,
                                15
                        )
                )
        );

        JLabel icono =
                new JLabel(
                        iconoTexto,
                        SwingConstants.CENTER
                );

        icono.setOpaque(
                true
        );

        icono.setBackground(
                fondoIcono
        );

        icono.setForeground(
                colorIcono
        );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        18
                )
        );

        icono.setPreferredSize(
                new Dimension(
                        42,
                        42
                )
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(
                false
        );

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
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
                        12
                )
        );

        lblTitulo.setForeground(
                SECUNDARIO
        );

        valor.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        valor.setForeground(
                TEXTO
        );

        textos.add(
                lblTitulo
        );

        textos.add(
                Box.createVerticalStrut(
                        4
                )
        );

        textos.add(
                valor
        );

        tarjeta.add(
                icono,
                BorderLayout.WEST
        );

        tarjeta.add(
                textos,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearContenido() {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
                        )
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                16,
                                16,
                                16,
                                16
                        )
                )
        );

        tarjeta.add(
                crearBarraFiltros(),
                BorderLayout.NORTH
        );

        configurarTabla();

        JScrollPane scroll =
                new JScrollPane(
                        tabla
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );

        scroll.getViewport()
                .setBackground(
                        Color.WHITE
                );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(
                        18
                );

        tarjeta.add(
                scroll,
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearBarraFiltros() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                14,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        JPanel filtros =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                8,
                                0
                        )
                );

        filtros.setOpaque(
                false
        );

        JLabel iconoBuscar =
                new JLabel(
                        "⌕"
                );

        iconoBuscar.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        18
                )
        );

        iconoBuscar.setForeground(
                PRIMARIO
        );

        txtBuscar =
                new JTextField();

        txtBuscar.setPreferredSize(
                new Dimension(
                        250,
                        36
                )
        );

        txtBuscar.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        txtBuscar.setToolTipText(
                "Buscar por usuario, nombre o rol"
        );

        txtBuscar.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        203,
                                        213,
                                        225
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                7,
                                10,
                                7,
                                10
                        )
                )
        );

        cmbRol =
                new JComboBox<>(
                        new String[]{
                            "Todos los roles",
                            "Administrador",
                            "Contable",
                            "Consulta / Auditor"
                        }
                );

        cmbEstado =
                new JComboBox<>(
                        new String[]{
                            "Todos los estados",
                            "Activo",
                            "Inactivo"
                        }
                );

        configurarCombo(
                cmbRol,
                170
        );

        configurarCombo(
                cmbEstado,
                155
        );

        JButton limpiar =
                crearBotonSecundario(
                        "Limpiar filtros"
                );

        limpiar.addActionListener(
                e -> limpiarFiltros()
        );

        filtros.add(
                iconoBuscar
        );

        filtros.add(
                txtBuscar
        );

        filtros.add(
                cmbRol
        );

        filtros.add(
                cmbEstado
        );

        filtros.add(
                limpiar
        );

        lblVisible.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblVisible.setForeground(
                SECUNDARIO
        );

        panel.add(
                filtros,
                BorderLayout.WEST
        );

        panel.add(
                lblVisible,
                BorderLayout.EAST
        );

        return panel;
    }

    private void configurarCombo(
            JComboBox<String> combo,
            int ancho
    ) {

        combo.setPreferredSize(
                new Dimension(
                        ancho,
                        36
                )
        );

        combo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        combo.setBackground(
                Color.WHITE
        );
    }

    private void configurarTabla() {

        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        tabla.setRowHeight(
                36
        );

        tabla.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tabla.setSelectionBackground(
                SELECCION
        );

        tabla.setSelectionForeground(
                TEXTO
        );

        tabla.setGridColor(
                BORDE
        );

        tabla.setShowVerticalLines(
                false
        );

        tabla.setShowHorizontalLines(
                true
        );

        tabla.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        tabla.setFillsViewportHeight(
                true
        );

        JTableHeader header =
                tabla.getTableHeader();

        header.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        header.setPreferredSize(
                new Dimension(
                        0,
                        40
                )
        );

        header.setBackground(
                new Color(
                        241,
                        245,
                        249
                )
        );

        header.setForeground(
                TEXTO
        );

        header.setReorderingAllowed(
                false
        );

        sorter =
                new TableRowSorter<>(
                        model
                );

        tabla.setRowSorter(
                sorter
        );

        tabla.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(
                        70
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(
                        180
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(
                        260
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(
                        180
                );

        tabla.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(
                        120
                );

        tabla.getColumnModel()
                .getColumn(0)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.CENTER
                        )
                );

        tabla.getColumnModel()
                .getColumn(1)
                .setCellRenderer(
                        new RenderUsuario()
                );

        tabla.getColumnModel()
                .getColumn(2)
                .setCellRenderer(
                        new RenderTexto(
                                SwingConstants.LEFT
                        )
                );

        tabla.getColumnModel()
                .getColumn(3)
                .setCellRenderer(
                        new RenderRol()
                );

        tabla.getColumnModel()
                .getColumn(4)
                .setCellRenderer(
                        new RenderEstado()
                );

        txtBuscar.getDocument()
                .addDocumentListener(
                        new javax.swing.event.DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                aplicarFiltros();
                            }

                            @Override
                            public void removeUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                aplicarFiltros();
                            }

                            @Override
                            public void changedUpdate(
                                    javax.swing.event.DocumentEvent e
                            ) {
                                aplicarFiltros();
                            }
                        }
                );

        cmbRol.addActionListener(
                e -> aplicarFiltros()
        );

        cmbEstado.addActionListener(
                e -> aplicarFiltros()
        );
    }

    public final void cargar() {

        model.setRowCount(
                0
        );

        List<Usuario> usuarios =
                dao.listar();

        int activos =
                0;

        int administradores =
                0;

        java.util.Set<String> roles =
                new java.util.HashSet<>();

        for (
                Usuario usuario
                : usuarios
        ) {

            model.addRow(
                    new Object[]{
                        usuario.getIdUsuario(),
                        usuario.getUsuario(),
                        usuario.getNombreCompleto(),
                        usuario.getRolNombre(),
                        usuario.isActivo()
                                ? "Activo"
                                : "Inactivo"
                    }
            );

            if (
                    usuario.isActivo()
            ) {
                activos++;
            }

            if (
                    usuario.getRolNombre() != null
                    &&
                    usuario.getRolNombre()
                            .toUpperCase()
                            .contains(
                                    "ADMIN"
                            )
            ) {
                administradores++;
            }

            if (
                    usuario.getRolNombre() != null
                    &&
                    !usuario.getRolNombre()
                            .isBlank()
            ) {

                roles.add(
                        usuario.getRolNombre()
                );
            }
        }

        lblTotal.setText(
                String.valueOf(
                        usuarios.size()
                )
        );

        lblActivos.setText(
                String.valueOf(
                        activos
                )
        );

        lblAdministradores.setText(
                String.valueOf(
                        administradores
                )
        );

        lblRoles.setText(
                String.valueOf(
                        roles.size()
                )
        );

        aplicarFiltros();
    }

    private void aplicarFiltros() {

        if (
                sorter == null
        ) {
            return;
        }

        List<RowFilter<Object, Object>> filtros =
                new ArrayList<>();

        String texto =
                txtBuscar == null
                        ? ""
                        : txtBuscar.getText()
                                .trim();

        if (
                !texto.isEmpty()
        ) {

            filtros.add(
                    RowFilter.regexFilter(
                            "(?i)"
                            + Pattern.quote(
                                    texto
                            ),
                            1,
                            2,
                            3
                    )
            );
        }

        if (
                cmbRol != null
        ) {

            String rol =
                    String.valueOf(
                            cmbRol.getSelectedItem()
                    );

            if (
                    !"Todos los roles".equals(
                            rol
                    )
            ) {

                filtros.add(
                        RowFilter.regexFilter(
                                "^"
                                + Pattern.quote(
                                        rol
                                )
                                + "$",
                                3
                        )
                );
            }
        }

        if (
                cmbEstado != null
        ) {

            String estado =
                    String.valueOf(
                            cmbEstado.getSelectedItem()
                    );

            if (
                    !"Todos los estados".equals(
                            estado
                    )
            ) {

                filtros.add(
                        RowFilter.regexFilter(
                                "^"
                                + Pattern.quote(
                                        estado
                                )
                                + "$",
                                4
                        )
                );
            }
        }

        if (
                filtros.isEmpty()
        ) {

            sorter.setRowFilter(
                    null
            );

        } else {

            sorter.setRowFilter(
                    RowFilter.andFilter(
                            filtros
                    )
            );
        }

        int visibles =
                tabla.getRowCount();

        lblVisible.setText(
                visibles
                + (
                        visibles == 1
                                ? " usuario visible"
                                : " usuarios visibles"
                )
        );
    }

    private void limpiarFiltros() {

        txtBuscar.setText(
                ""
        );

        cmbRol.setSelectedIndex(
                0
        );

        cmbEstado.setSelectedIndex(
                0
        );

        aplicarFiltros();
    }

    private void nuevo() {

        Window owner =
                SwingUtilities.getWindowAncestor(
                        this
                );

        JDialog dialogo =
                new JDialog(
                        owner,
                        "Nuevo usuario",
                        JDialog.ModalityType.APPLICATION_MODAL
                );

        dialogo.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialogo.setSize(
                560,
                520
        );

        dialogo.setMinimumSize(
                new Dimension(
                        500,
                        470
                )
        );

        dialogo.setLocationRelativeTo(
                owner
        );

        dialogo.getContentPane()
                .setBackground(
                        FONDO
                );

        dialogo.setLayout(
                new BorderLayout()
        );

        dialogo.add(
                crearEncabezadoNuevoUsuario(),
                BorderLayout.NORTH
        );

        JTextField user =
                crearCampoTexto();

        JTextField nombre =
                crearCampoTexto();

        JPasswordField pass =
                new JPasswordField();

        configurarCampoPassword(
                pass
        );

        JComboBox<String> rol =
                new JComboBox<>(
                        new String[]{
                            "ADMINISTRADOR",
                            "CONTABLE",
                            "CONSULTA"
                        }
                );

        rol.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        rol.setBackground(
                Color.WHITE
        );

        rol.setPreferredSize(
                new Dimension(
                        300,
                        38
                )
        );

        JPanel formulario =
                new JPanel();

        formulario.setBackground(
                Color.WHITE
        );

        formulario.setLayout(
                new BoxLayout(
                        formulario,
                        BoxLayout.Y_AXIS
                )
        );

        formulario.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );

        formulario.add(
                crearGrupoCampo(
                        "Usuario",
                        user
                )
        );

        formulario.add(
                Box.createVerticalStrut(
                        14
                )
        );

        formulario.add(
                crearGrupoCampo(
                        "Nombre completo",
                        nombre
                )
        );

        formulario.add(
                Box.createVerticalStrut(
                        14
                )
        );

        formulario.add(
                crearGrupoCampo(
                        "Contraseña",
                        pass
                )
        );

        formulario.add(
                Box.createVerticalStrut(
                        14
                )
        );

        JPanel grupoRol =
                new JPanel();

        grupoRol.setOpaque(
                false
        );

        grupoRol.setLayout(
                new BoxLayout(
                        grupoRol,
                        BoxLayout.Y_AXIS
                )
        );

        grupoRol.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel lblRol =
                new JLabel(
                        "Rol"
                );

        lblRol.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        lblRol.setForeground(
                TEXTO
        );

        rol.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        rol.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        38
                )
        );

        grupoRol.add(
                lblRol
        );

        grupoRol.add(
                Box.createVerticalStrut(
                        6
                )
        );

        grupoRol.add(
                rol
        );

        formulario.add(
                grupoRol
        );

        JPanel envoltura =
                new JPanel(
                        new BorderLayout()
                );

        envoltura.setBackground(
                FONDO
        );

        envoltura.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        22,
                        18,
                        22
                )
        );

        envoltura.add(
                formulario,
                BorderLayout.CENTER
        );

        dialogo.add(
                envoltura,
                BorderLayout.CENTER
        );

        JPanel pie =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                13
                        )
                );

        pie.setBackground(
                Color.WHITE
        );

        pie.setBorder(
                BorderFactory.createMatteBorder(
                        1,
                        0,
                        0,
                        0,
                        BORDE
                )
        );

        JButton cancelar =
                crearBotonSecundario(
                        "Cancelar"
                );

        JButton guardar =
                crearBotonPrincipal(
                        "Guardar usuario",
                        PRIMARIO,
                        PRIMARIO_HOVER
                );

        cancelar.addActionListener(
                e -> dialogo.dispose()
        );

        guardar.addActionListener(
                e -> {

                    if (
                            user.getText().isBlank()
                            ||
                            nombre.getText().isBlank()
                            ||
                            pass.getPassword().length < 6
                    ) {

                        JOptionPane.showMessageDialog(
                                dialogo,
                                "Completa todos los datos y usa una contraseña de al menos 6 caracteres.",
                                "Verifica la información",
                                JOptionPane.WARNING_MESSAGE
                        );

                        return;
                    }

                    try {

                        dao.crear(
                                user.getText().trim(),
                                nombre.getText().trim(),
                                PasswordUtil.generarHash(
                                        new String(
                                                pass.getPassword()
                                        )
                                ),
                                rol.getSelectedItem()
                                        .toString()
                        );

                        cargar();

                        dialogo.dispose();

                        JOptionPane.showMessageDialog(
                                this,
                                "Usuario creado correctamente.",
                                "Usuarios y Roles",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                    } catch (
                            Exception ex
                    ) {

                        JOptionPane.showMessageDialog(
                                dialogo,
                                obtenerMensajeError(
                                        ex
                                ),
                                "No se pudo crear el usuario",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
        );

        pie.add(
                cancelar
        );

        pie.add(
                guardar
        );

        dialogo.add(
                pie,
                BorderLayout.SOUTH
        );

        dialogo.getRootPane()
                .setDefaultButton(
                        guardar
                );

        dialogo.setVisible(
                true
        );
    }

    private JPanel crearEncabezadoNuevoUsuario() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                14,
                                0
                        )
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                22,
                                18,
                                22
                        )
                )
        );

        JLabel icono =
                new JLabel(
                        "＋",
                        SwingConstants.CENTER
                );

        icono.setOpaque(
                true
        );

        icono.setBackground(
                CELESTE_SUAVE
        );

        icono.setForeground(
                PRIMARIO
        );

        icono.setFont(
                new Font(
                        "Segoe UI Symbol",
                        Font.BOLD,
                        22
                )
        );

        icono.setPreferredSize(
                new Dimension(
                        48,
                        48
                )
        );

        JPanel textos =
                new JPanel();

        textos.setOpaque(
                false
        );

        textos.setLayout(
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Crear nuevo usuario"
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        21
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel subtitulo =
                new JLabel(
                        "Registra las credenciales y asigna el rol de acceso correspondiente."
                );

        subtitulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        subtitulo.setForeground(
                SECUNDARIO
        );

        textos.add(
                titulo
        );

        textos.add(
                Box.createVerticalStrut(
                        4
                )
        );

        textos.add(
                subtitulo
        );

        panel.add(
                icono,
                BorderLayout.WEST
        );

        panel.add(
                textos,
                BorderLayout.CENTER
        );

        return panel;
    }

    private JTextField crearCampoTexto() {

        JTextField campo =
                new JTextField();

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        300,
                        38
                )
        );

        campo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        38
                )
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        203,
                                        213,
                                        225
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                7,
                                10,
                                7,
                                10
                        )
                )
        );

        return campo;
    }

    private void configurarCampoPassword(
            JPasswordField campo
    ) {

        campo.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        300,
                        38
                )
        );

        campo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        38
                )
        );

        campo.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        203,
                                        213,
                                        225
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                7,
                                10,
                                7,
                                10
                        )
                )
        );
    }

    private JPanel crearGrupoCampo(
            String titulo,
            Component campo
    ) {

        JPanel grupo =
                new JPanel();

        grupo.setOpaque(
                false
        );

        grupo.setLayout(
                new BoxLayout(
                        grupo,
                        BoxLayout.Y_AXIS
                )
        );

        grupo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel label =
                new JLabel(
                        titulo
                );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        label.setForeground(
                TEXTO
        );

        grupo.add(
                label
        );

        grupo.add(
                Box.createVerticalStrut(
                        6
                )
        );

        campo.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        38
                )
        );

        campo.setPreferredSize(
                new Dimension(
                        300,
                        38
                )
        );

        grupo.add(
                campo
        );

        return grupo;
    }

    private void cambiarEstado() {

        int filaVista =
                tabla.getSelectedRow();

        if (
                filaVista < 0
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un usuario.",
                    "Usuarios y Roles",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int fila =
                tabla.convertRowIndexToModel(
                        filaVista
                );

        int id =
                ((Number) model.getValueAt(
                        fila,
                        0
                )).intValue();

        String usuario =
                String.valueOf(
                        model.getValueAt(
                                fila,
                                1
                        )
                );

        boolean activo =
                "Activo".equals(
                        model.getValueAt(
                                fila,
                                4
                        )
                );

        if (
                SesionUsuario.getUsuarioActual() != null
                &&
                id
                == SesionUsuario.getUsuarioActual()
                        .getIdUsuario()
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "No puedes desactivar tu propia sesión.",
                    "Acción no permitida",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String accion =
                activo
                        ? "desactivar"
                        : "activar";

        int confirmar =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Deseas "
                        + accion
                        + " al usuario \""
                        + usuario
                        + "\"?",
                        "Confirmar cambio de estado",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (
                confirmar
                != JOptionPane.YES_OPTION
        ) {

            return;
        }

        try {

            dao.cambiarActivo(
                    id,
                    !activo
            );

            cargar();

            JOptionPane.showMessageDialog(
                    this,
                    "Estado del usuario actualizado correctamente.",
                    "Usuarios y Roles",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (
                Exception e
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(
                            e
                    ),
                    "No se pudo actualizar el usuario",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JButton crearBotonPrincipal(
            String texto,
            Color fondo,
            Color hover
    ) {

        JButton boton =
                new JButton(
                        texto
                );

        boton.setUI(
                new BasicButtonUI()
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setBackground(
                fondo
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        15,
                        10,
                        15
                )
        );

        boton.setFocusPainted(
                false
        );

        boton.setOpaque(
                true
        );

        boton.setContentAreaFilled(
                true
        );

        boton.setCursor(
                new Cursor(
                        Cursor.HAND_CURSOR
                )
        );

        boton.addMouseListener(
                new java.awt.event.MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                hover
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                fondo
                        );
                    }
                }
        );

        return boton;
    }

    private JButton crearBotonSecundario(
            String texto
    ) {

        JButton boton =
                new JButton(
                        texto
                );

        boton.setUI(
                new BasicButtonUI()
        );

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        boton.setForeground(
                TEXTO
        );

        boton.setBackground(
                Color.WHITE
        );

        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                9,
                                14,
                                9,
                                14
                        )
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

        return boton;
    }

    private String obtenerMensajeError(
            Throwable error
    ) {

        String mensaje =
                "Ocurrió un error desconocido.";

        for (
                Throwable actual = error;
                actual != null;
                actual = actual.getCause()
        ) {

            if (
                    actual.getMessage() != null
                    &&
                    !actual.getMessage()
                            .isBlank()
            ) {

                mensaje =
                        actual.getMessage();
            }
        }

        return mensaje;
    }

    private class RenderTexto
            extends DefaultTableCellRenderer {

        private final int alineacion;

        public RenderTexto(
                int alineacion
        ) {

            this.alineacion =
                    alineacion;

            setOpaque(
                    true
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            setHorizontalAlignment(
                    alineacion
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            8,
                            0,
                            8
                    )
            );

            if (
                    isSelected
            ) {

                setBackground(
                        SELECCION
                );

                setForeground(
                        TEXTO
                );

            } else {

                setBackground(
                        row % 2 == 0
                                ? Color.WHITE
                                : new Color(
                                        248,
                                        250,
                                        252
                                )
                );

                setForeground(
                        TEXTO
                );
            }

            return this;
        }
    }

    private class RenderUsuario
            extends DefaultTableCellRenderer {

        public RenderUsuario() {

            setOpaque(
                    true
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            setText(
                    "●  "
                    + (
                            value == null
                                    ? ""
                                    : value.toString()
                    )
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            8,
                            0,
                            8
                    )
            );

            if (
                    isSelected
            ) {

                setBackground(
                        SELECCION
                );

                setForeground(
                        TEXTO
                );

            } else {

                setBackground(
                        row % 2 == 0
                                ? Color.WHITE
                                : new Color(
                                        248,
                                        250,
                                        252
                                )
                );

                setForeground(
                        PRIMARIO
                );
            }

            return this;
        }
    }

    private class RenderRol
            extends DefaultTableCellRenderer {

        public RenderRol() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            setOpaque(
                    true
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            String rol =
                    value == null
                            ? ""
                            : value.toString();

            setText(
                    rol
            );

            setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            11
                    )
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            5,
                            8,
                            5,
                            8
                    )
            );

            if (
                    isSelected
            ) {

                setBackground(
                        SELECCION
                );

                setForeground(
                        TEXTO
                );

                return this;
            }

            String normalizado =
                    rol.toUpperCase();

            if (
                    normalizado.contains(
                            "ADMIN"
                    )
            ) {

                setBackground(
                        new Color(
                                243,
                                232,
                                255
                        )
                );

                setForeground(
                        MORADO
                );

            } else if (
                    normalizado.contains(
                            "CONTABLE"
                    )
            ) {

                setBackground(
                        new Color(
                                239,
                                246,
                                255
                        )
                );

                setForeground(
                        PRIMARIO
                );

            } else {

                setBackground(
                        new Color(
                                255,
                                247,
                                237
                        )
                );

                setForeground(
                        NARANJA
                );
            }

            return this;
        }
    }

    private class RenderEstado
            extends DefaultTableCellRenderer {

        public RenderEstado() {

            setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            setOpaque(
                    true
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            super.getTableCellRendererComponent(
                    table,
                    value,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            String estado =
                    value == null
                            ? ""
                            : value.toString();

            setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            11
                    )
            );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            5,
                            8,
                            5,
                            8
                    )
            );

            if (
                    isSelected
            ) {

                setText(
                        estado
                );

                setBackground(
                        SELECCION
                );

                setForeground(
                        TEXTO
                );

            } else if (
                    "Activo".equalsIgnoreCase(
                            estado
                    )
            ) {

                setText(
                        "●  Activo"
                );

                setBackground(
                        new Color(
                                240,
                                253,
                                244
                        )
                );

                setForeground(
                        VERDE
                );

            } else {

                setText(
                        "●  Inactivo"
                );

                setBackground(
                        new Color(
                                254,
                                242,
                                242
                        )
                );

                setForeground(
                        ROJO
                );
            }

            return this;
        }
    }
}
