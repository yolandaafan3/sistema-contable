package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.MovimientoKardex;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.servicio.KardexService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.time.format.DateTimeFormatter;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PanelKardexPeps extends JPanel {

    private final KardexService service;

    private final DefaultTableModel modelo;

    private final JLabel lblProducto;
    private final JLabel lblResumen;

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy"
            );

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


    public PanelKardexPeps() {

        service =
                new KardexService();

        lblProducto =
                new JLabel();

        lblResumen =
                new JLabel();


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
                        28,
                        30,
                        30,
                        30
                )
        );


        add(
                crearEncabezado(),
                BorderLayout.NORTH
        );


        modelo =
                new DefaultTableModel(
                        new String[]{
                            "Fecha",
                            "Asiento",
                            "Concepto",
                            "Entrada",
                            "Salida",
                            "Existencia",
                            "Costo Unit.",
                            "Costo PEPS",
                            "Saldo Deudor",
                            "Saldo Acreedor",
                            "Saldo"
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


        JTable tabla =
                new JTable(
                        modelo
                );

        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        tabla.setRowHeight(
                29
        );

        tabla.setGridColor(
                BORDE
        );

        tabla.setShowVerticalLines(
                false
        );

        tabla.getTableHeader()
                .setFont(
                        new Font(
                                "Segoe UI",
                                Font.BOLD,
                                12
                        )
                );

        tabla.getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                38
                        )
                );

        tabla.setAutoCreateRowSorter(
                true
        );


        DefaultTableCellRenderer rendererDerecha =
                new DefaultTableCellRenderer();

        rendererDerecha.setHorizontalAlignment(
                SwingConstants.RIGHT
        );


        for (int i = 3; i <= 10; i++) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(
                            rendererDerecha
                    );
        }


        JScrollPane scroll =
                new JScrollPane(
                        tabla
                );

        scroll.setBorder(
                BorderFactory.createLineBorder(
                        BORDE
                )
        );

        add(
                scroll,
                BorderLayout.CENTER
        );


        JPanel pie =
                new JPanel(
                        new BorderLayout()
                );

        pie.setBackground(
                Color.WHITE
        );

        pie.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                BORDE
                        ),
                        BorderFactory.createEmptyBorder(
                                12,
                                18,
                                12,
                                18
                        )
                )
        );


        lblResumen.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        lblResumen.setForeground(
                TEXTO
        );

        pie.add(
                lblResumen,
                BorderLayout.EAST
        );

        add(
                pie,
                BorderLayout.SOUTH
        );


        cargarKardex();
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
                new BoxLayout(
                        textos,
                        BoxLayout.Y_AXIS
                )
        );


        JLabel titulo =
                new JLabel(
                        "Kardex PEPS"
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


        lblProducto.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        lblProducto.setForeground(
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
                lblProducto
        );


        JButton actualizar =
                crearBoton(
                        "Actualizar"
                );

        actualizar.addActionListener(
                e -> cargarKardex()
        );


        panel.add(
                textos,
                BorderLayout.WEST
        );

        panel.add(
                actualizar,
                BorderLayout.EAST
        );


        return panel;
    }


    public final void cargarKardex() {

        try {

            /*
             * Reconstruimos el Kardex antes de mostrarlo para que
             * siempre use el costo unitario redondeado a centavos.
             */
            service.recalcular();


            Producto producto =
                    service.obtenerProductoActivo();


            modelo.setRowCount(
                    0
            );


            if (producto == null) {

                lblProducto.setText(
                        "No hay producto activo."
                );

                lblResumen.setText(
                        "Saldo PEPS: $0.00"
                );

                return;
            }


            lblProducto.setText(
                    producto.getCodigo()
                    + " · "
                    + producto.getNombre()
                    + " · Costos redondeados a 2 decimales"
            );


            List<MovimientoKardex> movimientos =
                    service.obtenerMovimientos();


            for (MovimientoKardex movimiento
                    : movimientos) {

                modelo.addRow(
                        new Object[]{
                            movimiento.getFecha() == null
                                    ? ""
                                    : movimiento.getFecha()
                                            .format(
                                                    formatoFecha
                                            ),
                            movimiento.getNumeroAsiento() == null
                                    ? "—"
                                    : movimiento.getNumeroAsiento(),
                            movimiento.getConcepto(),
                            numero(
                                    movimiento.getUnidadesEntrada()
                            ),
                            numero(
                                    movimiento.getUnidadesSalida()
                            ),
                            numero(
                                    movimiento.getUnidadesExistencia()
                            ),
                            dinero(
                                    movimiento.getCostoUnitario()
                            ),
                            dinero(
                                    movimiento.getCostoPeps()
                            ),
                            dinero(
                                    movimiento.getSaldoDeudor()
                            ),
                            dinero(
                                    movimiento.getSaldoAcreedor()
                            ),
                            dinero(
                                    movimiento.getSaldo()
                            )
                        }
                );
            }


            Producto productoActualizado =
                    service.obtenerProductoActivo();


            lblResumen.setText(
                    "Existencia actual: "
                    + numero(
                            productoActualizado.getExistenciaActual()
                    )
                    + "   |   Saldo final PEPS: "
                    + dinero(
                            service.obtenerSaldoPeps()
                    )
            );


        } catch (Exception e) {

            mostrarError(
                    e
            );
        }
    }


    private JButton crearBoton(
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
                PRIMARIO
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


        return boton;
    }


    private String numero(
            BigDecimal valor
    ) {

        if (valor == null) {

            valor =
                    BigDecimal.ZERO;
        }


        return String.format(
                "%,.2f",
                valor.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );
    }


    private String dinero(
            BigDecimal valor
    ) {

        if (valor == null) {

            valor =
                    BigDecimal.ZERO;
        }


        return "$"
                + String.format(
                        "%,.2f",
                        valor.setScale(
                                2,
                                RoundingMode.HALF_UP
                        )
                );
    }


    private void mostrarError(
            Throwable error
    ) {

        String mensaje =
                "Ocurrió un error desconocido.";


        for (
                Throwable actual = error;
                actual != null;
                actual = actual.getCause()
        ) {

            if (actual.getMessage() != null
                    &&
                !actual.getMessage().isBlank()) {

                mensaje =
                        actual.getMessage();
            }
        }


        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "No se pudo cargar el Kardex PEPS",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
