package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.CuentaDAO;

import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;

import com.mycompany.sistemacontable.servicio.AsientoManualService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class DialogoAsientoManual extends JDialog {

    private final AsientoManualService asientoManualService;
    private final CuentaDAO cuentaDAO;

    private JSpinner spFecha;

    private JTextArea txtConcepto;

    private JTable tabla;

    private DefaultTableModel modeloTabla;

    private JComboBox<Cuenta> cmbEditorCuenta;

    private JLabel lblTotalDebe;
    private JLabel lblTotalHaber;
    private JLabel lblDiferencia;

    private JButton btnAgregarLinea;
    private JButton btnEliminarLinea;
    private JButton btnGuardar;
    private JButton btnCancelar;


    private final Color COLOR_FONDO =
            new Color(245, 247, 250);

    private final Color COLOR_TEXTO =
            new Color(30, 41, 59);

    private final Color COLOR_SECUNDARIO =
            new Color(100, 116, 139);

    private final Color COLOR_PRIMARIO =
            new Color(37, 99, 235);

    private final Color COLOR_BORDE =
            new Color(226, 232, 240);

    private final Color COLOR_EXITO =
            new Color(22, 163, 74);

    private final Color COLOR_ERROR =
            new Color(220, 38, 38);


    public DialogoAsientoManual(
            Window propietario
    ) {

        super(
                propietario,
                "Asiento Manual",
                ModalityType.APPLICATION_MODAL
        );


        asientoManualService =
                new AsientoManualService();


        cuentaDAO =
                new CuentaDAO();


        configurarVentana();

        construirInterfaz();

        cargarCuentas();

        agregarLinea();

        agregarLinea();

        actualizarTotales();
    }


    // =========================================================
    // CONFIGURACION
    // =========================================================

    private void configurarVentana() {

        setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );


        setSize(
                950,
                720
        );


        setMinimumSize(
                new Dimension(
                        850,
                        650
                )
        );


        setLocationRelativeTo(
                getOwner()
        );


        setLayout(
                new BorderLayout()
        );


        getContentPane()
                .setBackground(
                        COLOR_FONDO
                );
    }


    // =========================================================
    // INTERFAZ
    // =========================================================

    private void construirInterfaz() {

        add(
                crearEncabezado(),
                BorderLayout.NORTH
        );


        add(
                crearContenido(),
                BorderLayout.CENTER
        );


        add(
                crearBotonesFinales(),
                BorderLayout.SOUTH
        );
    }


    // =========================================================
    // ENCABEZADO
    // =========================================================

    private JPanel crearEncabezado() {

        JPanel panel =
                new JPanel(
                        new BorderLayout()
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
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                22,
                                28,
                                22,
                                28
                        )
                )
        );


        JLabel titulo =
                new JLabel(
                        "<html>"
                        + "<span style='font-size:20px;'>"
                        + "Asiento Manual"
                        + "</span>"
                        + "<br>"
                        + "<span style='font-size:11px; font-weight:normal;'>"
                        + "Registra movimientos contables manuales "
                        + "manteniendo Debe y Haber cuadrados."
                        + "</span>"
                        + "</html>"
                );


        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );


        titulo.setForeground(
                COLOR_TEXTO
        );


        panel.add(
                titulo,
                BorderLayout.WEST
        );


        return panel;
    }


    // =========================================================
    // CONTENIDO
    // =========================================================

    private JPanel crearContenido() {

        JPanel fondo =
                new JPanel(
                        new BorderLayout(
                                0,
                                15
                        )
                );


        fondo.setBackground(
                COLOR_FONDO
        );


        fondo.setBorder(
                BorderFactory.createEmptyBorder(
                        20,
                        28,
                        20,
                        28
                )
        );


        fondo.add(
                crearDatosGenerales(),
                BorderLayout.NORTH
        );


        fondo.add(
                crearAreaTabla(),
                BorderLayout.CENTER
        );


        fondo.add(
                crearResumen(),
                BorderLayout.SOUTH
        );


        return fondo;
    }


    // =========================================================
    // DATOS GENERALES
    // =========================================================

    private JPanel crearDatosGenerales() {

        JPanel tarjeta =
                new JPanel(
                        new GridBagLayout()
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
                                16,
                                20,
                                16,
                                20
                        )
                )
        );


        GridBagConstraints gbc =
                new GridBagConstraints();


        gbc.gridx = 0;
        gbc.gridy = 0;

        gbc.weightx = 0.25;

        gbc.fill =
                GridBagConstraints.HORIZONTAL;


        gbc.insets =
                new Insets(
                        0,
                        0,
                        6,
                        15
                );


        tarjeta.add(
                crearEtiqueta(
                        "Fecha"
                ),
                gbc
        );


        gbc.gridx = 1;

        gbc.weightx = 0.75;


        tarjeta.add(
                crearEtiqueta(
                        "Concepto del asiento"
                ),
                gbc
        );


        gbc.gridy++;

        gbc.gridx = 0;

        gbc.weightx = 0.25;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        0,
                        15
                );


        spFecha =
                new JSpinner(
                        new SpinnerDateModel()
                );


        JSpinner.DateEditor editorFecha =
                new JSpinner.DateEditor(
                        spFecha,
                        "dd/MM/yyyy"
                );


        spFecha.setEditor(
                editorFecha
        );


        spFecha.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );


        spFecha.setPreferredSize(
                new Dimension(
                        0,
                        40
                )
        );


        tarjeta.add(
                spFecha,
                gbc
        );


        gbc.gridx = 1;

        gbc.weightx = 0.75;

        gbc.insets =
                new Insets(
                        0,
                        0,
                        0,
                        0
                );


        txtConcepto =
                new JTextArea();


        txtConcepto.setRows(
                2
        );


        txtConcepto.setLineWrap(
                true
        );


        txtConcepto.setWrapStyleWord(
                true
        );


        txtConcepto.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );


        txtConcepto.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        190,
                                        195,
                                        205
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                7,
                                8,
                                7,
                                8
                        )
                )
        );


        tarjeta.add(
                txtConcepto,
                gbc
        );


        return tarjeta;
    }


    // =========================================================
    // TABLA
    // =========================================================

    private JPanel crearAreaTabla() {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                0,
                                10
                        )
                );


        panel.setOpaque(
                false
        );


        JPanel encabezado =
                new JPanel(
                        new BorderLayout()
                );


        encabezado.setOpaque(
                false
        );


        JLabel titulo =
                new JLabel(
                        "Detalle del asiento"
                );


        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );


        titulo.setForeground(
                COLOR_TEXTO
        );


        encabezado.add(
                titulo,
                BorderLayout.WEST
        );


        encabezado.add(
                crearBotonesLineas(),
                BorderLayout.EAST
        );


        modeloTabla =
                new DefaultTableModel(
                        new Object[]{
                            "Cuenta",
                            "Debe",
                            "Haber"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {

                        return true;
                    }


                    @Override
                    public Class<?> getColumnClass(
                            int columnIndex
                    ) {

                        if (columnIndex == 0) {

                            return Cuenta.class;
                        }


                        return String.class;
                    }
                };


        tabla =
                new JTable(
                        modeloTabla
                );


        tabla.setRowHeight(
                34
        );


        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );


        tabla.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                13
                        )
                );


        tabla.setGridColor(
                COLOR_BORDE
        );


        tabla.setSelectionBackground(
                new Color(
                        219,
                        234,
                        254
                )
        );


        tabla.setSelectionForeground(
                COLOR_TEXTO
        );


        cmbEditorCuenta =
                new JComboBox<>();


        cmbEditorCuenta.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );


        TableColumn columnaCuenta =
                tabla.getColumnModel()
                        .getColumn(
                                0
                        );


        columnaCuenta.setCellEditor(
                new DefaultCellEditor(
                        cmbEditorCuenta
                )
        );


        columnaCuenta.setPreferredWidth(
                500
        );


        tabla.getColumnModel()
                .getColumn(
                        1
                )
                .setPreferredWidth(
                        140
                );


        tabla.getColumnModel()
                .getColumn(
                        2
                )
                .setPreferredWidth(
                        140
                );


        modeloTabla.addTableModelListener(
                e -> actualizarTotales()
        );


        JScrollPane scroll =
                new JScrollPane(
                        tabla
                );


        scroll.setBorder(
                BorderFactory.createLineBorder(
                        COLOR_BORDE
                )
        );


        scroll.getViewport()
                .setBackground(
                        Color.WHITE
                );


        panel.add(
                encabezado,
                BorderLayout.NORTH
        );


        panel.add(
                scroll,
                BorderLayout.CENTER
        );


        return panel;
    }


    // =========================================================
    // BOTONES DE LINEAS
    // =========================================================

    private JPanel crearBotonesLineas() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                8,
                                0
                        )
                );


        panel.setOpaque(
                false
        );


        btnAgregarLinea =
                new JButton(
                        "+ Agregar línea"
                );


        configurarBotonPrincipal(
                btnAgregarLinea
        );


        btnAgregarLinea.addActionListener(
                e -> agregarLinea()
        );


        btnEliminarLinea =
                new JButton(
                        "Eliminar línea"
                );


        configurarBotonSecundario(
                btnEliminarLinea
        );


        btnEliminarLinea.addActionListener(
                e -> eliminarLinea()
        );


        panel.add(
                btnAgregarLinea
        );


        panel.add(
                btnEliminarLinea
        );


        return panel;
    }


    // =========================================================
    // CARGAR CUENTAS
    // =========================================================

    private void cargarCuentas() {

        try {

            List<Cuenta> cuentas =
                    cuentaDAO.listarCuentasMovimiento();


            Cuenta inventario =
                    cuentaDAO.buscarPorRol(
                            "INVENTARIO"
                    );


            cmbEditorCuenta.removeAllItems();


            for (Cuenta cuenta : cuentas) {

                if (cuenta == null) {

                    continue;
                }


                if (!cuenta.isActivo()) {

                    continue;
                }


                if (!cuenta.isPermiteMovimiento()) {

                    continue;
                }


                boolean esInventario =
                        inventario != null
                        &&
                        inventario.getIdCuenta()
                        == cuenta.getIdCuenta();


                if (!esInventario
                        &&
                    "1.1.03".equals(
                            cuenta.getCodigo()
                    )) {

                    esInventario =
                            true;
                }


                if (esInventario) {

                    continue;
                }


                cmbEditorCuenta.addItem(
                        cuenta
                );
            }


            if (cmbEditorCuenta.getItemCount() == 0) {

                btnGuardar.setEnabled(
                        false
                );


                btnAgregarLinea.setEnabled(
                        false
                );


                JOptionPane.showMessageDialog(
                        this,
                        "No existen cuentas disponibles "
                        + "para crear un asiento manual.",
                        "Sin cuentas",
                        JOptionPane.WARNING_MESSAGE
                );
            }


        } catch (Exception e) {

            btnGuardar.setEnabled(
                    false
            );


            btnAgregarLinea.setEnabled(
                    false
            );


            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(
                            e
                    ),
                    "Error al cargar cuentas",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


    // =========================================================
    // AGREGAR LINEA
    // =========================================================

    private void agregarLinea() {

        if (cmbEditorCuenta == null
                ||
            cmbEditorCuenta.getItemCount() == 0) {

            return;
        }


        Cuenta cuenta =
                cmbEditorCuenta.getItemAt(
                        0
                );


        modeloTabla.addRow(
                new Object[]{
                    cuenta,
                    "0.00",
                    "0.00"
                }
        );


        actualizarTotales();
    }


    // =========================================================
    // ELIMINAR LINEA
    // =========================================================

    private void eliminarLinea() {

        int fila =
                tabla.getSelectedRow();


        if (fila < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona la línea que deseas eliminar.",
                    "Selecciona una línea",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (modeloTabla.getRowCount() <= 2) {

            JOptionPane.showMessageDialog(
                    this,
                    "El asiento debe conservar al menos dos líneas.",
                    "No se puede eliminar",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }


        if (tabla.isEditing()) {

            tabla.getCellEditor()
                    .stopCellEditing();
        }


        int filaModelo =
                tabla.convertRowIndexToModel(
                        fila
                );


        modeloTabla.removeRow(
                filaModelo
        );


        actualizarTotales();
    }


    // =========================================================
    // RESUMEN
    // =========================================================

    private JPanel crearResumen() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                30,
                                12
                        )
                );


        panel.setBackground(
                Color.WHITE
        );


        panel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                5,
                                15,
                                5,
                                15
                        )
                )
        );


        lblTotalDebe =
                crearValorResumen(
                        "Total Debe: $0.00"
                );


        lblTotalHaber =
                crearValorResumen(
                        "Total Haber: $0.00"
                );


        lblDiferencia =
                crearValorResumen(
                        "Diferencia: $0.00"
                );


        lblDiferencia.setForeground(
                COLOR_EXITO
        );


        panel.add(
                lblTotalDebe
        );


        panel.add(
                lblTotalHaber
        );


        panel.add(
                lblDiferencia
        );


        return panel;
    }


    // =========================================================
    // ACTUALIZAR TOTALES
    // =========================================================

    private void actualizarTotales() {

        if (modeloTabla == null
                ||
            lblTotalDebe == null
                ||
            lblTotalHaber == null
                ||
            lblDiferencia == null) {

            return;
        }


        BigDecimal totalDebe =
                BigDecimal.ZERO;


        BigDecimal totalHaber =
                BigDecimal.ZERO;


        for (
                int fila = 0;
                fila < modeloTabla.getRowCount();
                fila++
        ) {

            try {

                BigDecimal debe =
                        leerDecimal(
                                modeloTabla.getValueAt(
                                        fila,
                                        1
                                )
                        );


                BigDecimal haber =
                        leerDecimal(
                                modeloTabla.getValueAt(
                                        fila,
                                        2
                                )
                        );


                totalDebe =
                        totalDebe.add(
                                debe
                        );


                totalHaber =
                        totalHaber.add(
                                haber
                        );


            } catch (Exception e) {

                // Mientras el usuario escribe,
                // simplemente no contamos el valor inválido.
            }
        }


        totalDebe =
                totalDebe.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        totalHaber =
                totalHaber.setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        BigDecimal diferencia =
                totalDebe.subtract(
                        totalHaber
                )
                .abs()
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );


        lblTotalDebe.setText(
                "Total Debe: $"
                + totalDebe.toPlainString()
        );


        lblTotalHaber.setText(
                "Total Haber: $"
                + totalHaber.toPlainString()
        );


        lblDiferencia.setText(
                "Diferencia: $"
                + diferencia.toPlainString()
        );


        if (diferencia.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            lblDiferencia.setForeground(
                    COLOR_EXITO
            );

        } else {

            lblDiferencia.setForeground(
                    COLOR_ERROR
            );
        }
    }


    // =========================================================
    // BOTONES FINALES
    // =========================================================

    private JPanel crearBotonesFinales() {

        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                16
                        )
                );


        panel.setBackground(
                Color.WHITE
        );


        panel.setBorder(
                BorderFactory.createMatteBorder(
                        1,
                        0,
                        0,
                        0,
                        COLOR_BORDE
                )
        );


        btnCancelar =
                new JButton(
                        "Cancelar"
                );


        configurarBotonSecundario(
                btnCancelar
        );


        btnCancelar.addActionListener(
                e -> dispose()
        );


        btnGuardar =
                new JButton(
                        "Registrar asiento"
                );


        configurarBotonPrincipal(
                btnGuardar
        );


        btnGuardar.addActionListener(
                e -> guardar()
        );


        panel.add(
                btnCancelar
        );


        panel.add(
                btnGuardar
        );


        return panel;
    }


    // =========================================================
    // GUARDAR
    // =========================================================

    private void guardar() {

        try {

            if (tabla.isEditing()) {

                if (!tabla.getCellEditor()
                        .stopCellEditing()) {

                    throw new IllegalArgumentException(
                            "Termina de editar la línea actual."
                    );
                }
            }


            LocalDate fecha =
                    obtenerFecha();


            String concepto =
                    txtConcepto
                            .getText()
                            .trim();


            if (concepto.isBlank()) {

                throw new IllegalArgumentException(
                        "El concepto del asiento es obligatorio."
                );
            }


            if (modeloTabla.getRowCount() < 2) {

                throw new IllegalArgumentException(
                        "El asiento debe tener al menos dos líneas."
                );
            }


            List<DetalleAsiento> detalles =
                    crearDetalles(
                            concepto
                    );


            btnGuardar.setEnabled(
                    false
            );


            asientoManualService.registrar(
                    fecha,
                    concepto,
                    detalles
            );


            JOptionPane.showMessageDialog(
                    this,
                    "Asiento manual registrado correctamente.\n"
                    + "El Libro Diario, Mayorización y balances "
                    + "se actualizarán automáticamente.",
                    "Asiento registrado",
                    JOptionPane.INFORMATION_MESSAGE
            );


            dispose();


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Revisa los valores ingresados en Debe y Haber.",
                    "Dato incorrecto",
                    JOptionPane.WARNING_MESSAGE
            );


            btnGuardar.setEnabled(
                    true
            );


        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    obtenerMensajeError(
                            e
                    ),
                    "No se pudo registrar el asiento",
                    JOptionPane.ERROR_MESSAGE
            );


            btnGuardar.setEnabled(
                    true
            );
        }
    }


    // =========================================================
    // CREAR DETALLES
    // =========================================================

    private List<DetalleAsiento> crearDetalles(
            String concepto
    ) {

        List<DetalleAsiento> detalles =
                new ArrayList<>();


        for (
                int fila = 0;
                fila < modeloTabla.getRowCount();
                fila++
        ) {

            Object valorCuenta =
                    modeloTabla.getValueAt(
                            fila,
                            0
                    );


            if (!(valorCuenta instanceof Cuenta)) {

                throw new IllegalArgumentException(
                        "Debe seleccionar una cuenta "
                        + "en la línea "
                        + (fila + 1)
                        + "."
                );
            }


            Cuenta cuenta =
                    (Cuenta) valorCuenta;


            BigDecimal debe =
                    leerDecimal(
                            modeloTabla.getValueAt(
                                    fila,
                                    1
                            )
                    );


            BigDecimal haber =
                    leerDecimal(
                            modeloTabla.getValueAt(
                                    fila,
                                    2
                            )
                    );


            if (debe.compareTo(
                    BigDecimal.ZERO
            ) < 0
                    ||
                haber.compareTo(
                        BigDecimal.ZERO
                ) < 0) {

                throw new IllegalArgumentException(
                        "Debe y Haber no pueden contener valores negativos."
                );
            }


            if (debe.compareTo(
                    BigDecimal.ZERO
            ) > 0
                    &&
                haber.compareTo(
                        BigDecimal.ZERO
                ) > 0) {

                throw new IllegalArgumentException(
                        "La línea "
                        + (fila + 1)
                        + " no puede tener valores en Debe "
                        + "y Haber al mismo tiempo."
                );
            }


            if (debe.compareTo(
                    BigDecimal.ZERO
            ) == 0
                    &&
                haber.compareTo(
                        BigDecimal.ZERO
                ) == 0) {

                throw new IllegalArgumentException(
                        "La línea "
                        + (fila + 1)
                        + " debe contener un valor en Debe o Haber."
                );
            }


            DetalleAsiento detalle =
                    new DetalleAsiento();


            detalle.setIdCuenta(
                    cuenta.getIdCuenta()
            );


            detalle.setDescripcion(
                    concepto
            );


            detalle.setDebe(
                    debe
            );


            detalle.setHaber(
                    haber
            );


            detalles.add(
                    detalle
            );
        }


        return detalles;
    }


    // =========================================================
    // FECHA
    // =========================================================

    private LocalDate obtenerFecha() {

        Date fecha =
                (Date) spFecha.getValue();


        return Instant
                .ofEpochMilli(
                        fecha.getTime()
                )
                .atZone(
                        ZoneId.systemDefault()
                )
                .toLocalDate();
    }


    // =========================================================
    // LEER DECIMAL
    // =========================================================

    private BigDecimal leerDecimal(
            Object valor
    ) {

        if (valor == null) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }


        String texto =
                valor.toString()
                        .trim()
                        .replace(
                                ",",
                                "."
                        );


        if (texto.isBlank()) {

            return BigDecimal.ZERO.setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }


        return new BigDecimal(
                texto
        ).setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =========================================================
    // MENSAJE ERROR
    // =========================================================

    private String obtenerMensajeError(
            Throwable error
    ) {

        Throwable actual =
                error;


        String ultimoMensaje =
                "Ocurrió un error desconocido.";


        while (actual != null) {

            if (actual.getMessage() != null
                    &&
                !actual.getMessage().isBlank()) {

                ultimoMensaje =
                        actual.getMessage();
            }


            actual =
                    actual.getCause();
        }


        return ultimoMensaje;
    }


    // =========================================================
    // COMPONENTES
    // =========================================================

    private JLabel crearEtiqueta(
            String texto
    ) {

        JLabel label =
                new JLabel(
                        texto
                );


        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );


        label.setForeground(
                COLOR_TEXTO
        );


        return label;
    }


    private JLabel crearValorResumen(
            String texto
    ) {

        JLabel label =
                new JLabel(
                        texto
                );


        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );


        label.setForeground(
                COLOR_TEXTO
        );


        return label;
    }


    // =========================================================
    // BOTON PRINCIPAL
    // =========================================================

    private void configurarBotonPrincipal(
            JButton boton
    ) {

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
                COLOR_PRIMARIO
        );


        boton.setBorder(
                BorderFactory.createEmptyBorder(
                        9,
                        14,
                        9,
                        14
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
    }


    // =========================================================
    // BOTON SECUNDARIO
    // =========================================================

    private void configurarBotonSecundario(
            JButton boton
    ) {

        boton.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );


        boton.setForeground(
                COLOR_TEXTO
        );


        boton.setBackground(
                new Color(
                        241,
                        245,
                        249
                )
        );


        boton.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                COLOR_BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                8,
                                14,
                                8,
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
    }
}