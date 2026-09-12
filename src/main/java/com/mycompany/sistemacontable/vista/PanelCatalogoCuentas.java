package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.modelo.Cuenta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

public class PanelCatalogoCuentas extends JPanel {

    private final CuentaDAO cuentaDAO;

    private JTable tablaCuentas;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField txtBuscar;
    private JLabel lblCantidad;

    private final Color COLOR_FONDO =
            new Color(245, 247, 250);

    private final Color COLOR_TEXTO =
            new Color(30, 41, 59);

    private final Color COLOR_SECUNDARIO =
            new Color(100, 116, 139);

    private final Color COLOR_BORDE =
            new Color(226, 232, 240);

    private final Color COLOR_AZUL =
            new Color(37, 99, 235);

    private final Color COLOR_AZUL_HOVER =
            new Color(29, 78, 216);

    public PanelCatalogoCuentas() {

        cuentaDAO =
                new CuentaDAO();

        configurarPanel();

        construirInterfaz();

        cargarCuentas();
    }

    private void configurarPanel() {

        setLayout(
                new BorderLayout(
                        0,
                        20
                )
        );

        setBackground(
                COLOR_FONDO
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        30,
                        30,
                        30,
                        30
                )
        );
    }

    private void construirInterfaz() {

        add(
                crearEncabezado(),
                BorderLayout.NORTH
        );

        add(
                crearContenido(),
                BorderLayout.CENTER
        );
    }

    private JPanel crearEncabezado() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
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
                new javax.swing.BoxLayout(
                        textos,
                        javax.swing.BoxLayout.Y_AXIS
                )
        );

        JLabel titulo =
                new JLabel(
                        "Catálogo de Cuentas"
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

        JLabel descripcion =
                new JLabel(
                        "Consulta las cuentas contables utilizadas por el sistema."
                );

        descripcion.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        descripcion.setForeground(
                COLOR_SECUNDARIO
        );

        textos.add(
                titulo
        );

        textos.add(
                javax.swing.Box.createVerticalStrut(
                        5
                )
        );

        textos.add(
                descripcion
        );

        JButton btnActualizar =
                crearBotonAzul(
                        "Actualizar"
                );

        btnActualizar.addActionListener(
                e -> cargarCuentas()
        );

        panel.add(
                textos,
                BorderLayout.WEST
        );

        panel.add(
                btnActualizar,
                BorderLayout.EAST
        );

        return panel;
    }

    private JPanel crearContenido() {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout(
                                0,
                                15
                        )
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                20,
                                20,
                                20,
                                20
                        )
                )
        );

        tarjeta.add(
                crearBarraBusqueda(),
                BorderLayout.NORTH
        );

        tarjeta.add(
                crearTabla(),
                BorderLayout.CENTER
        );

        return tarjeta;
    }

    private JPanel crearBarraBusqueda() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                15,
                                0
                        )
                );

        panel.setOpaque(
                false
        );

        JPanel busqueda =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                0,
                                0
                        )
                );

        busqueda.setOpaque(
                false
        );

        JLabel lblBuscar =
                new JLabel(
                        "Buscar: "
                );

        lblBuscar.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblBuscar.setForeground(
                COLOR_TEXTO
        );

        txtBuscar =
                new JTextField();

        txtBuscar.setPreferredSize(
                new Dimension(
                        300,
                        34
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
                "Buscar por código, nombre, tipo o clasificación"
        );

        busqueda.add(
                lblBuscar
        );

        busqueda.add(
                txtBuscar
        );

        lblCantidad =
                new JLabel(
                        "0 cuentas"
                );

        lblCantidad.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblCantidad.setForeground(
                COLOR_SECUNDARIO
        );

        panel.add(
                busqueda,
                BorderLayout.WEST
        );

        panel.add(
                lblCantidad,
                BorderLayout.EAST
        );

        agregarFiltroBusqueda();

        return panel;
    }

    private JScrollPane crearTabla() {

        modeloTabla =
                new DefaultTableModel(
                        new Object[]{
                            "Código",
                            "Nombre",
                            "Tipo",
                            "Clasificación",
                            "Naturaleza",
                            "Rol Reporte",
                            "Movimiento",
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

        tablaCuentas =
                new JTable(
                        modeloTabla
                );

        tablaCuentas.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        tablaCuentas.setRowHeight(
                30
        );

        tablaCuentas.setSelectionBackground(
                new Color(
                        219,
                        234,
                        254
                )
        );

        tablaCuentas.setSelectionForeground(
                COLOR_TEXTO
        );

        tablaCuentas.setGridColor(
                COLOR_BORDE
        );

        tablaCuentas.setShowVerticalLines(
                false
        );

        tablaCuentas.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        tablaCuentas.getTableHeader().setPreferredSize(
                new Dimension(
                        0,
                        36
                )
        );

        sorter =
                new TableRowSorter<>(
                        modeloTabla
                );

        tablaCuentas.setRowSorter(
                sorter
        );

        configurarAnchoColumnas();

        JScrollPane scroll =
                new JScrollPane(
                        tablaCuentas
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        COLOR_BORDE
                )
        );

        scroll.getViewport().setBackground(
                Color.WHITE
        );

        return scroll;
    }

    private void configurarAnchoColumnas() {

        TableColumnModel columnas =
                tablaCuentas.getColumnModel();

        columnas.getColumn(0)
                .setPreferredWidth(
                        90
                );

        columnas.getColumn(1)
                .setPreferredWidth(
                        210
                );

        columnas.getColumn(2)
                .setPreferredWidth(
                        100
                );

        columnas.getColumn(3)
                .setPreferredWidth(
                        120
                );

        columnas.getColumn(4)
                .setPreferredWidth(
                        100
                );

        columnas.getColumn(5)
                .setPreferredWidth(
                        170
                );

        columnas.getColumn(6)
                .setPreferredWidth(
                        90
                );

        columnas.getColumn(7)
                .setPreferredWidth(
                        80
                );
    }

    private void cargarCuentas() {

        try {

            List<Cuenta> cuentas =
                    cuentaDAO.listarTodas(
                            true
                    );

            modeloTabla.setRowCount(
                    0
            );

            for (Cuenta cuenta : cuentas) {

                modeloTabla.addRow(
                        new Object[]{
                            cuenta.getCodigo(),
                            cuenta.getNombre(),
                            formatearTexto(
                                    cuenta.getTipo()
                            ),
                            formatearTexto(
                                    cuenta.getClasificacion()
                            ),
                            formatearTexto(
                                    cuenta.getNaturaleza()
                            ),
                            formatearRol(
                                    cuenta.getRolReporte()
                            ),
                            cuenta.isPermiteMovimiento()
                                    ? "Sí"
                                    : "No",
                            cuenta.isActivo()
                                    ? "Activa"
                                    : "Inactiva"
                        }
                );
            }

            lblCantidad.setText(
                    cuentas.size()
                    + (
                        cuentas.size() == 1
                                ? " cuenta"
                                : " cuentas"
                    )
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(
                            e
                    ),
                    "Error al cargar catálogo",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void agregarFiltroBusqueda() {

        txtBuscar.getDocument()
                .addDocumentListener(
                        new DocumentListener() {

                            @Override
                            public void insertUpdate(
                                    DocumentEvent e
                            ) {

                                filtrar();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent e
                            ) {

                                filtrar();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent e
                            ) {

                                filtrar();
                            }
                        }
                );
    }

    private void filtrar() {

        String texto =
                txtBuscar.getText()
                        .trim();

        if (texto.isEmpty()) {

            sorter.setRowFilter(
                    null
            );

        } else {

            sorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)"
                            + java.util.regex.Pattern.quote(
                                    texto
                            )
                    )
            );
        }
    }

    private JButton crearBotonAzul(
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
                        13
                )
        );

        boton.setForeground(
                Color.WHITE
        );

        boton.setBackground(
                COLOR_AZUL
        );

        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        18,
                        10,
                        18
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
                                COLOR_AZUL_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            java.awt.event.MouseEvent e
                    ) {

                        boton.setBackground(
                                COLOR_AZUL
                        );
                    }
                }
        );

        return boton;
    }

    private String formatearTexto(
            String texto
    ) {

        if (texto == null
                ||
            texto.isBlank()) {

            return "-";
        }

        return texto.replace(
                "_",
                " "
        );
    }

    private String formatearRol(
            String rol
    ) {

        if (rol == null
                ||
            rol.isBlank()
                ||
            rol.equalsIgnoreCase(
                    "NINGUNO"
            )) {

            return "-";
        }

        return rol.replace(
                "_",
                " "
        );
    }

    private String obtenerMensajeError(
            Throwable error
    ) {

        Throwable actual =
                error;

        String mensaje =
                "Ocurrió un error desconocido.";

        while (actual != null) {

            if (actual.getMessage() != null
                    &&
                !actual.getMessage().isBlank()) {

                mensaje =
                        actual.getMessage();
            }

            actual =
                    actual.getCause();
        }

        return mensaje;
    }
}