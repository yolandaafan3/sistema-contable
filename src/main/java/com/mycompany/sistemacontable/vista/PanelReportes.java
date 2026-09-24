package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.servicio.GeneradorReportesService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelReportes extends JPanel {

    private final GeneradorReportesService service =
            new GeneradorReportesService();

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

    private final Color ROJO =
            new Color(220, 38, 38);

    private final Color ROJO_HOVER =
            new Color(185, 28, 28);

    private final Color VERDE =
            new Color(22, 163, 74);

    private final Color VERDE_HOVER =
            new Color(21, 128, 61);

    public PanelReportes() {

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
                crearEncabezado(),
                BorderLayout.NORTH
        );

        add(
                crearCatalogoReportes(),
                BorderLayout.CENTER
        );
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
                        "Generador de reportes"
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
                        "Consulta y exporta los principales reportes contables de ContaProMax."
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

        JLabel etiqueta =
                new JLabel(
                        "PDF  ·  Excel  ·  Vista previa"
                );

        etiqueta.setOpaque(
                true
        );

        etiqueta.setBackground(
                new Color(
                        239,
                        246,
                        255
                )
        );

        etiqueta.setForeground(
                PRIMARIO
        );

        etiqueta.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        etiqueta.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        12,
                        8,
                        12
                )
        );

        panel.add(
                textos,
                BorderLayout.WEST
        );

        panel.add(
                etiqueta,
                BorderLayout.EAST
        );

        return panel;
    }

    private JScrollPane crearCatalogoReportes() {

        JPanel tarjetas =
                new JPanel(
                        new WrapLayout(
                                FlowLayout.LEFT,
                                18,
                                18
                        )
                );

        tarjetas.setBackground(
                FONDO
        );

        tarjetas.add(
                crearTarjetaReporte(
                        "Libro Diario",
                        "Movimientos contables registrados cronológicamente durante el período.",
                        "librodiario.png"
                )
        );

        tarjetas.add(
                crearTarjetaReporte(
                        "Libro Mayor",
                        "Movimientos agrupados por cuenta para revisar cargos, abonos y saldos.",
                        "libromayor.png"
                )
        );

        tarjetas.add(
                crearTarjetaReporte(
                        "Balance de Comprobación",
                        "Movimientos y saldos de las cuentas para comprobar el equilibrio contable.",
                        "balancedecomprobacion.png"
                )
        );

        tarjetas.add(
                crearTarjetaReporte(
                        "Balance General",
                        "Activos, pasivos y patrimonio correspondientes al período contable activo.",
                        "balancegeneral.png"
                )
        );

        tarjetas.add(
                crearTarjetaReporte(
                        "Estado de Resultados",
                        "Ventas, costos, gastos y utilidad o pérdida obtenida durante el período.",
                        "estadoderesultado.png"
                )
        );

        tarjetas.add(
                crearTarjetaReporte(
                        "IVA",
                        "Información relacionada con IVA Crédito Fiscal, IVA Débito Fiscal y saldos.",
                        "iva.png"
                )
        );

        tarjetas.add(
                crearTarjetaReporte(
                        "Auxiliares",
                        "Detalle auxiliar de cuentas y movimientos incluidos en los reportes del sistema.",
                        "auxiliar.png"
                )
        );

        tarjetas.add(
                crearTarjetaReporte(
                        "Kardex",
                        "Movimientos, existencias y valoración PEPS del inventario por producto.",
                        "kardex.png"
                )
        );

        JScrollPane scroll =
                new JScrollPane(
                        tarjetas
                );

        scroll.setBorder(
                null
        );

        scroll.setOpaque(
                false
        );

        scroll.getViewport()
                .setBackground(
                        FONDO
                );

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar()
                .setUnitIncrement(
                        18
                );

        return scroll;
    }

    private JPanel crearTarjetaReporte(
            String tipoReporte,
            String descripcion,
            String imagen
    ) {

        JPanel tarjeta =
                new JPanel(
                        new BorderLayout()
                );

        tarjeta.setBackground(
                Color.WHITE
        );

        tarjeta.setPreferredSize(
                new Dimension(
                        330,
                        355
                )
        );

        tarjeta.setMaximumSize(
                new Dimension(
                        330,
                        355
                )
        );

        Border bordeNormal =
                BorderFactory.createLineBorder(
                        BORDE
                );

        Border bordeHover =
                BorderFactory.createLineBorder(
                        new Color(
                                147,
                                197,
                                253
                        ),
                        2
                );

        tarjeta.setBorder(
                bordeNormal
        );

        JLabel imagenLabel =
                new JLabel(
                        cargarImagen(
                                imagen,
                                328,
                                155
                        )
                );

        imagenLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        imagenLabel.setPreferredSize(
                new Dimension(
                        328,
                        155
                )
        );

        JPanel centro =
                new JPanel();

        centro.setBackground(
                Color.WHITE
        );

        centro.setLayout(
                new BoxLayout(
                        centro,
                        BoxLayout.Y_AXIS
                )
        );

        centro.setBorder(
                BorderFactory.createEmptyBorder(
                        14,
                        16,
                        12,
                        16
                )
        );

        JLabel titulo =
                new JLabel(
                        tipoReporte
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        titulo.setForeground(
                TEXTO
        );

        titulo.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel desc =
                new JLabel(
                        "<html><div style='width:290px;'>"
                        + escaparHtml(
                                descripcion
                        )
                        + "</div></html>"
                );

        desc.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        desc.setForeground(
                SECUNDARIO
        );

        desc.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        centro.add(
                titulo
        );

        centro.add(
                Box.createVerticalStrut(
                        7
                )
        );

        centro.add(
                desc
        );

        centro.add(
                Box.createVerticalGlue()
        );

        JPanel acciones =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                7,
                                0
                        )
                );

        acciones.setOpaque(
                false
        );

        acciones.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JButton ver =
                crearBoton(
                        "Ver",
                        PRIMARIO,
                        PRIMARIO_HOVER
                );

        JButton pdf =
                crearBoton(
                        "PDF",
                        ROJO,
                        ROJO_HOVER
                );

        JButton excel =
                crearBoton(
                        "Excel",
                        VERDE,
                        VERDE_HOVER
                );

        ver.setToolTipText(
                "Ver una vista previa antes de exportar"
        );

        pdf.setToolTipText(
                "Exportar este reporte directamente a PDF"
        );

        excel.setToolTipText(
                "Exportar este reporte directamente a Excel"
        );

        ver.addActionListener(
                e -> verReporte(
                        tipoReporte
                )
        );

        pdf.addActionListener(
                e -> exportarReporte(
                        tipoReporte,
                        false
                )
        );

        excel.addActionListener(
                e -> exportarReporte(
                        tipoReporte,
                        true
                )
        );

        acciones.add(
                ver
        );

        acciones.add(
                pdf
        );

        acciones.add(
                excel
        );

        centro.add(
                Box.createVerticalStrut(
                        14
                )
        );

        centro.add(
                acciones
        );

        tarjeta.add(
                imagenLabel,
                BorderLayout.NORTH
        );

        tarjeta.add(
                centro,
                BorderLayout.CENTER
        );

        tarjeta.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        tarjeta.setBorder(
                                bordeHover
                        );

                        tarjeta.setCursor(
                                new Cursor(
                                        Cursor.HAND_CURSOR
                                )
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {

                        tarjeta.setBorder(
                                bordeNormal
                        );
                    }
                }
        );

        return tarjeta;
    }

    private JButton crearBoton(
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
                        9,
                        15,
                        9,
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
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent e
                    ) {

                        boton.setBackground(
                                hover
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent e
                    ) {

                        boton.setBackground(
                                fondo
                        );
                    }
                }
        );

        return boton;
    }

    private void verReporte(
            String tipoReporte
    ) {

        try {

            GeneradorReportesService.Reporte reporte =
                    generarReporte(
                            tipoReporte
                    );

            mostrarVistaPrevia(
                    reporte,
                    tipoReporte
            );

        } catch (
                Exception e
        ) {

            mostrarErrorReporte(
                    tipoReporte,
                    e
            );
        }
    }

    private void mostrarVistaPrevia(
            GeneradorReportesService.Reporte reporte,
            String tipoReporte
    ) {

        Window owner =
                SwingUtilities.getWindowAncestor(
                        this
                );

        JDialog dialogo =
                new JDialog(
                        owner,
                        "Vista previa - "
                        + reporte.titulo(),
                        JDialog.ModalityType.APPLICATION_MODAL
                );

        dialogo.setDefaultCloseOperation(
                JDialog.DISPOSE_ON_CLOSE
        );

        dialogo.setSize(
                1120,
                720
        );

        dialogo.setMinimumSize(
                new Dimension(
                        860,
                        580
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

        JPanel encabezado =
                new JPanel(
                        new BorderLayout(
                                15,
                                0
                        )
                );

        encabezado.setBackground(
                Color.WHITE
        );

        encabezado.setBorder(
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
                        reporte.titulo()
                );

        titulo.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        23
                )
        );

        titulo.setForeground(
                TEXTO
        );

        JLabel sub =
                new JLabel(
                        reporte.filas().size()
                        + " fila(s) disponibles para exportar"
                );

        sub.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        sub.setForeground(
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
                sub
        );

        encabezado.add(
                textos,
                BorderLayout.WEST
        );

        dialogo.add(
                encabezado,
                BorderLayout.NORTH
        );

        DefaultTableModel modelo =
                new DefaultTableModel(
                        reporte.columnas(),
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

        for (
                String[] fila
                : reporte.filas()
        ) {

            modelo.addRow(
                    fila
            );
        }

        JTable tabla =
                new JTable(
                        modelo
                );

        configurarTablaVistaPrevia(
                tabla
        );

        JScrollPane scroll =
                new JScrollPane(
                        tabla
                );

        scroll.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        22,
                        18,
                        22
                )
        );

        scroll.getViewport()
                .setBackground(
                        Color.WHITE
                );

        dialogo.add(
                scroll,
                BorderLayout.CENTER
        );

        JPanel pie =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                9,
                                14
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

        JButton cerrar =
                crearBotonSecundario(
                        "Cerrar"
                );

        JButton pdf =
                crearBoton(
                        "Exportar PDF",
                        ROJO,
                        ROJO_HOVER
                );

        JButton excel =
                crearBoton(
                        "Exportar Excel",
                        VERDE,
                        VERDE_HOVER
                );

        cerrar.addActionListener(
                e -> dialogo.dispose()
        );

        pdf.addActionListener(
                e -> exportarReporteGenerado(
                        reporte,
                        false
                )
        );

        excel.addActionListener(
                e -> exportarReporteGenerado(
                        reporte,
                        true
                )
        );

        pie.add(
                cerrar
        );

        pie.add(
                pdf
        );

        pie.add(
                excel
        );

        dialogo.add(
                pie,
                BorderLayout.SOUTH
        );

        dialogo.setVisible(
                true
        );
    }

    private void configurarTablaVistaPrevia(
            JTable tabla
    ) {

        tabla.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        tabla.setRowHeight(
                32
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

        tabla.setAutoCreateRowSorter(
                true
        );

        tabla.setSelectionBackground(
                new Color(
                        219,
                        234,
                        254
                )
        );

        tabla.setSelectionForeground(
                TEXTO
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

        DefaultTableCellRenderer render =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column
                    ) {

                        Component componente =
                                super.getTableCellRendererComponent(
                                        table,
                                        value,
                                        isSelected,
                                        hasFocus,
                                        row,
                                        column
                                );

                        setBorder(
                                BorderFactory.createEmptyBorder(
                                        0,
                                        7,
                                        0,
                                        7
                                )
                        );

                        if (
                                !isSelected
                        ) {

                            componente.setBackground(
                                    row % 2 == 0
                                            ? Color.WHITE
                                            : new Color(
                                                    248,
                                                    250,
                                                    252
                                            )
                            );

                            componente.setForeground(
                                    TEXTO
                            );
                        }

                        return componente;
                    }
                };

        for (
                int i = 0;
                i < tabla.getColumnCount();
                i++
        ) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(
                            render
                    );
        }
    }

    private void exportarReporte(
            String tipoReporte,
            boolean excel
    ) {

        try {

            GeneradorReportesService.Reporte reporte =
                    generarReporte(
                            tipoReporte
                    );

            exportarReporteGenerado(
                    reporte,
                    excel
            );

        } catch (
                Exception e
        ) {

            mostrarErrorReporte(
                    tipoReporte,
                    e
            );
        }
    }

    private GeneradorReportesService.Reporte generarReporte(
            String tipoReporte
    ) {

        return service.generar(
                tipoReporte
        );
    }

    private void exportarReporteGenerado(
            GeneradorReportesService.Reporte reporte,
            boolean excel
    ) {

        JFileChooser chooser =
                new JFileChooser();

        String extension =
                excel
                        ? ".xlsx"
                        : ".pdf";

        chooser.setSelectedFile(
                new File(
                        reporte.titulo()
                                .replace(
                                        ' ',
                                        '_'
                                )
                        + extension
                )
        );

        if (
                chooser.showSaveDialog(
                        this
                )
                != JFileChooser.APPROVE_OPTION
        ) {

            return;
        }

        File archivo =
                asegurarExtension(
                        chooser.getSelectedFile(),
                        extension
                );

        try {

            if (
                    excel
            ) {

                service.excel(
                        reporte,
                        archivo
                );

            } else {

                service.pdf(
                        reporte,
                        archivo
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Reporte exportado correctamente.\n\n"
                    + archivo.getAbsolutePath(),
                    "Exportación completada",
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
                    "No se pudo exportar el reporte",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private File asegurarExtension(
            File archivo,
            String extension
    ) {

        if (
                archivo.getName()
                        .toLowerCase()
                        .endsWith(
                                extension
                        )
        ) {

            return archivo;
        }

        return new File(
                archivo.getParentFile(),
                archivo.getName()
                + extension
        );
    }

    private void mostrarErrorReporte(
            String tipoReporte,
            Throwable error
    ) {

        String mensaje =
                obtenerMensajeError(
                        error
                );

        if (
                "Kardex".equals(
                        tipoReporte
                )
        ) {

            mensaje =
                    "La tarjeta de Kardex ya está preparada en la interfaz, "
                    + "pero GeneradorReportesService debe incluir el reporte "
                    + "\"Kardex\" para poder verlo y exportarlo.\n\n"
                    + "Detalle: "
                    + mensaje;
        }

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "No se pudo generar "
                + tipoReporte,
                JOptionPane.ERROR_MESSAGE
        );
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

        return boton;
    }

    private ImageIcon cargarImagen(
            String archivo,
            int ancho,
            int alto
    ) {

        try {

            URL recurso =
                    getClass().getResource(
                            "/branding/"
                            + archivo
                    );

            if (
                    recurso == null
            ) {

                return crearImagenFallback(
                        ancho,
                        alto
                );
            }

            BufferedImage original =
                    ImageIO.read(
                            recurso
                    );

            if (
                    original == null
            ) {

                return crearImagenFallback(
                        ancho,
                        alto
                );
            }

            BufferedImage salida =
                    new BufferedImage(
                            ancho,
                            alto,
                            BufferedImage.TYPE_INT_ARGB
                    );

            Graphics2D g2 =
                    salida.createGraphics();

            g2.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );

            g2.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            double escala =
                    Math.max(
                            (double) ancho
                            / original.getWidth(),
                            (double) alto
                            / original.getHeight()
                    );

            int nuevoAncho =
                    (int) Math.ceil(
                            original.getWidth()
                            * escala
                    );

            int nuevoAlto =
                    (int) Math.ceil(
                            original.getHeight()
                            * escala
                    );

            int x =
                    (ancho - nuevoAncho)
                    / 2;

            int y =
                    (alto - nuevoAlto)
                    / 2;

            Image redimensionada =
                    original.getScaledInstance(
                            nuevoAncho,
                            nuevoAlto,
                            Image.SCALE_SMOOTH
                    );

            g2.drawImage(
                    redimensionada,
                    x,
                    y,
                    null
            );

            g2.dispose();

            return new ImageIcon(
                    salida
            );

        } catch (
                Exception e
        ) {

            return crearImagenFallback(
                    ancho,
                    alto
            );
        }
    }

    private ImageIcon crearImagenFallback(
            int ancho,
            int alto
    ) {

        BufferedImage imagen =
                new BufferedImage(
                        ancho,
                        alto,
                        BufferedImage.TYPE_INT_ARGB
                );

        Graphics2D g2 =
                imagen.createGraphics();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        g2.setColor(
                new Color(
                        239,
                        246,
                        255
                )
        );

        g2.fillRect(
                0,
                0,
                ancho,
                alto
        );

        g2.setColor(
                PRIMARIO
        );

        int centroX =
                ancho / 2;

        int centroY =
                alto / 2;

        g2.fillRoundRect(
                centroX - 31,
                centroY - 38,
                62,
                76,
                8,
                8
        );

        g2.setColor(
                Color.WHITE
        );

        g2.fillRect(
                centroX - 19,
                centroY - 20,
                38,
                4
        );

        g2.fillRect(
                centroX - 19,
                centroY - 7,
                38,
                4
        );

        g2.fillRect(
                centroX - 19,
                centroY + 6,
                28,
                4
        );

        g2.dispose();

        return new ImageIcon(
                imagen
        );
    }

    private String escaparHtml(
            String texto
    ) {

        if (
                texto == null
        ) {

            return "";
        }

        return texto
                .replace(
                        "&",
                        "&amp;"
                )
                .replace(
                        "<",
                        "&lt;"
                )
                .replace(
                        ">",
                        "&gt;"
                );
    }

    private static class WrapLayout
            extends FlowLayout {

        public WrapLayout(
                int align,
                int hgap,
                int vgap
        ) {

            super(
                    align,
                    hgap,
                    vgap
            );
        }

        @Override
        public Dimension preferredLayoutSize(
                Container target
        ) {

            return layoutSize(
                    target,
                    true
            );
        }

        @Override
        public Dimension minimumLayoutSize(
                Container target
        ) {

            Dimension minimum =
                    layoutSize(
                            target,
                            false
                    );

            minimum.width -=
                    getHgap()
                    + 1;

            return minimum;
        }

        private Dimension layoutSize(
                Container target,
                boolean preferred
        ) {

            synchronized (
                    target.getTreeLock()
            ) {

                int anchoObjetivo =
                        target.getWidth();

                if (
                        anchoObjetivo
                        <= 0
                ) {

                    anchoObjetivo =
                            Integer.MAX_VALUE;
                }

                Insets insets =
                        target.getInsets();

                int horizontal =
                        insets.left
                        + insets.right
                        + getHgap()
                        * 2;

                int maxWidth =
                        anchoObjetivo
                        - horizontal;

                Dimension dim =
                        new Dimension(
                                0,
                                0
                        );

                int rowWidth =
                        0;

                int rowHeight =
                        0;

                int nmembers =
                        target.getComponentCount();

                for (
                        int i = 0;
                        i < nmembers;
                        i++
                ) {

                    Component componente =
                            target.getComponent(
                                    i
                            );

                    if (
                            !componente.isVisible()
                    ) {

                        continue;
                    }

                    Dimension d =
                            preferred
                                    ? componente.getPreferredSize()
                                    : componente.getMinimumSize();

                    if (
                            rowWidth + d.width
                            > maxWidth
                            &&
                            rowWidth > 0
                    ) {

                        agregarFila(
                                dim,
                                rowWidth,
                                rowHeight
                        );

                        rowWidth =
                                0;

                        rowHeight =
                                0;
                    }

                    if (
                            rowWidth != 0
                    ) {

                        rowWidth +=
                                getHgap();
                    }

                    rowWidth +=
                            d.width;

                    rowHeight =
                            Math.max(
                                    rowHeight,
                                    d.height
                            );
                }

                agregarFila(
                        dim,
                        rowWidth,
                        rowHeight
                );

                dim.width +=
                        horizontal;

                dim.height +=
                        insets.top
                        + insets.bottom
                        + getVgap()
                        * 2;

                Container scrollPane =
                        SwingUtilities.getAncestorOfClass(
                                JScrollPane.class,
                                target
                        );

                if (
                        scrollPane != null
                ) {

                    dim.width -=
                            getHgap()
                            + 1;
                }

                return dim;
            }
        }

        private void agregarFila(
                Dimension dim,
                int rowWidth,
                int rowHeight
        ) {

            dim.width =
                    Math.max(
                            dim.width,
                            rowWidth
                    );

            if (
                    dim.height > 0
            ) {

                dim.height +=
                        getVgap();
            }

            dim.height +=
                    rowHeight;
        }
    }
}
