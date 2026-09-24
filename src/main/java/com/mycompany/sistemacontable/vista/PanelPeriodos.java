package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.servicio.GeneradorReportesService;
import com.mycompany.sistemacontable.servicio.GestionPeriodosService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class PanelPeriodos extends JPanel {

    private final GestionPeriodosService service = new GestionPeriodosService();
    private final GeneradorReportesService reportes = new GeneradorReportesService();

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Periodo", "Inicio", "Fin", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modelo);
    private final JLabel actual = new JLabel();
    private final JLabel lblPeriodoActivo = new JLabel("Sin período activo");
    private final JLabel lblRangoActivo = new JLabel("—");
    private final JLabel lblHistorico = new JLabel("0 períodos");
    private final JLabel lblEstadoActivo = new JLabel("SIN PERÍODO");
    private final JLabel lblPie = new JLabel("Sin información disponible");
    private final JButton btnGestionarPeriodo;

    private final Color FONDO = new Color(245, 247, 250);
    private final Color TEXTO = new Color(30, 41, 59);
    private final Color SECUNDARIO = new Color(100, 116, 139);
    private final Color BORDE = new Color(226, 232, 240);
    private final Color PRIMARIO = new Color(37, 99, 235);
    private final Color PRIMARIO_HOVER = new Color(29, 78, 216);
    private final Color VERDE = new Color(5, 150, 105);
    private final Color VERDE_HOVER = new Color(4, 120, 87);
    private final Color SELECCION = new Color(219, 234, 254);

    public PanelPeriodos() {
        setLayout(new BorderLayout(0, 18));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 26, 26, 26));

        btnGestionarPeriodo = crearBotonPrincipal(
                "▣  Cerrar y crear nuevo", VERDE, VERDE_HOVER);

        add(crearZonaSuperior(), BorderLayout.NORTH);
        add(crearPanelTabla(), BorderLayout.CENTER);
        add(crearPie(), BorderLayout.SOUTH);

        btnGestionarPeriodo.addActionListener(e -> gestionarPeriodo());
        cargar();
    }

    private JPanel crearZonaSuperior() {
        JPanel zona = new JPanel();
        zona.setOpaque(false);
        zona.setLayout(new BoxLayout(zona, BoxLayout.Y_AXIS));
        zona.add(crearEncabezado());
        zona.add(Box.createVerticalStrut(18));
        zona.add(crearResumen());
        return zona;
    }

    private JPanel crearEncabezado() {
        JPanel top = new JPanel(new BorderLayout(20, 0));
        top.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Periodos Contables");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXTO);

        JLabel subtitulo = new JLabel(
                "Administra el período activo y consulta el historial contable de la empresa.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(SECUNDARIO);

        actual.setFont(new Font("Segoe UI", Font.BOLD, 12));
        actual.setForeground(PRIMARIO);

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(actual);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);

        JButton exportar = crearBotonPrincipal(
                "⇩  Exportar periodo", PRIMARIO, PRIMARIO_HOVER);
        exportar.addActionListener(e -> exportar());

        acciones.add(btnGestionarPeriodo);
        acciones.add(exportar);

        top.add(textos, BorderLayout.WEST);
        top.add(acciones, BorderLayout.EAST);
        return top;
    }

    private JPanel crearResumen() {
        JPanel resumen = new JPanel(new GridLayout(1, 4, 14, 0));
        resumen.setOpaque(false);
        resumen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        resumen.add(crearTarjetaResumen(
                "Período activo", lblPeriodoActivo, "▣",
                new Color(219, 234, 254), PRIMARIO));

        resumen.add(crearTarjetaResumen(
                "Rango actual", lblRangoActivo, "◷",
                new Color(224, 242, 254), new Color(14, 165, 233)));

        resumen.add(crearTarjetaResumen(
                "Histórico", lblHistorico, "≡",
                new Color(243, 232, 255), new Color(126, 34, 206)));

        resumen.add(crearTarjetaResumen(
                "Estado", lblEstadoActivo, "✓",
                new Color(220, 252, 231), VERDE));

        return resumen;
    }

    private JPanel crearTarjetaResumen(
            String titulo,
            JLabel valor,
            String iconoTexto,
            Color fondoIcono,
            Color colorIcono
    ) {
        JPanel tarjeta = new JPanel(new BorderLayout(12, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(13, 15, 13, 15)
        ));

        JLabel icono = new JLabel(iconoTexto, SwingConstants.CENTER);
        icono.setOpaque(true);
        icono.setBackground(fondoIcono);
        icono.setForeground(colorIcono);
        icono.setFont(new Font("Segoe UI Symbol", Font.BOLD, 18));
        icono.setPreferredSize(new Dimension(42, 42));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitulo.setForeground(SECUNDARIO);

        valor.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valor.setForeground(TEXTO);

        textos.add(lblTitulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(valor);

        tarjeta.add(icono, BorderLayout.WEST);
        tarjeta.add(textos, BorderLayout.CENTER);
        return tarjeta;
    }

    private JPanel crearPanelTabla() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel cabecera = new JPanel();
        cabecera.setOpaque(false);
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Historial de períodos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(TEXTO);

        JLabel descripcion = new JLabel(
                "Selecciona un período para exportar su información contable completa.");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcion.setForeground(SECUNDARIO);

        cabecera.add(titulo);
        cabecera.add(Box.createVerticalStrut(3));
        cabecera.add(descripcion);

        configurarTabla();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        tarjeta.add(cabecera, BorderLayout.NORTH);
        tarjeta.add(scroll, BorderLayout.CENTER);
        return tarjeta;
    }

    private void configurarTabla() {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(36);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(TEXTO);
        tabla.setGridColor(BORDE);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);
        tabla.setAutoCreateRowSorter(true);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXTO);
        header.setReorderingAllowed(false);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(160);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(130);

        tabla.getColumnModel().getColumn(0)
                .setCellRenderer(new RenderTabla(SwingConstants.CENTER));
        tabla.getColumnModel().getColumn(1)
                .setCellRenderer(new RenderTabla(SwingConstants.LEFT));
        tabla.getColumnModel().getColumn(2)
                .setCellRenderer(new RenderTabla(SwingConstants.CENTER));
        tabla.getColumnModel().getColumn(3)
                .setCellRenderer(new RenderTabla(SwingConstants.CENTER));
        tabla.getColumnModel().getColumn(4)
                .setCellRenderer(new RenderEstado());
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(11, 15, 11, 15)
        ));

        lblPie.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPie.setForeground(SECUNDARIO);
        pie.add(lblPie, BorderLayout.WEST);

        JLabel ayuda = new JLabel(
                "El histórico permanece disponible aunque el período esté cerrado.");
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ayuda.setForeground(SECUNDARIO);
        pie.add(ayuda, BorderLayout.EAST);

        return pie;
    }

    private JButton crearBotonPrincipal(String texto, Color fondo, Color hover) {
        JButton boton = new JButton(texto);
        boton.setUI(new BasicButtonUI());
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        boton.setFocusPainted(false);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                boton.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                boton.setBackground(fondo);
            }
        });

        return boton;
    }

    public final void cargar() {
        modelo.setRowCount(0);

        PeriodoContable abierto = service.abierto();
        List<PeriodoContable> periodos = service.listar();

        actual.setText(
                abierto == null
                        ? "No hay un periodo abierto. Puedes crear uno nuevo."
                        : "Periodo actual: " + abierto.getNombre()
                        + " · " + abierto.getFechaInicio()
                        + " a " + abierto.getFechaFin()
        );

        btnGestionarPeriodo.setText(
                abierto == null
                        ? "＋  Crear nuevo periodo"
                        : "▣  Cerrar y crear nuevo"
        );

        int cerrados = 0;

        for (PeriodoContable periodo : periodos) {
            modelo.addRow(new Object[]{
                periodo.getIdPeriodo(),
                periodo.getNombre(),
                periodo.getFechaInicio(),
                periodo.getFechaFin(),
                periodo.getEstado()
            });

            if (periodo.getEstado() != null
                    && !"ABIERTO".equalsIgnoreCase(periodo.getEstado())) {
                cerrados++;
            }
        }

        if (abierto == null) {
            lblPeriodoActivo.setText("Sin período activo");
            lblRangoActivo.setText("—");
            lblEstadoActivo.setText("SIN PERÍODO");
            lblEstadoActivo.setForeground(SECUNDARIO);
        } else {
            lblPeriodoActivo.setText(abierto.getNombre());
            lblRangoActivo.setText(
                    abierto.getFechaInicio() + "  →  " + abierto.getFechaFin());
            lblEstadoActivo.setText("ABIERTO");
            lblEstadoActivo.setForeground(VERDE);
        }

        lblHistorico.setText(
                periodos.size()
                + (periodos.size() == 1 ? " período" : " períodos")
        );

        lblPie.setText(
                periodos.size() + " período(s) registrado(s)"
                + "   ·   " + cerrados + " cerrado(s)"
        );
    }

    private void gestionarPeriodo() {
        if (service.abierto() == null) {
            crearNuevoSinPeriodoAbierto();
        } else {
            cerrarYCrearNuevo();
        }
    }

    private void crearNuevoSinPeriodoAbierto() {
        List<PeriodoContable> periodos = service.listar();

        LocalDate inicioSugerido;
        if (!periodos.isEmpty()) {
            PeriodoContable ultimo = periodos.get(0);
            inicioSugerido = ultimo.getFechaFin().plusDays(1);
        } else {
            int y = LocalDate.now().getYear();
            inicioSugerido = LocalDate.of(y, 1, 1);
        }

        LocalDate finSugerido = inicioSugerido.plusYears(1).minusDays(1);

        JTextField nombre = new JTextField(String.valueOf(inicioSugerido.getYear()), 16);
        JTextField inicio = new JTextField(inicioSugerido.toString(), 12);
        JTextField fin = new JTextField(finSugerido.toString(), 12);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nuevo nombre:"));
        form.add(nombre);
        form.add(new JLabel("Fecha inicio (AAAA-MM-DD):"));
        form.add(inicio);
        form.add(new JLabel("Fecha fin (AAAA-MM-DD):"));
        form.add(fin);

        JPanel cont = new JPanel(new BorderLayout(0, 10));
        cont.add(new JLabel(
                "<html>No existe un período abierto. Se creará uno nuevo con el rango indicado.<br>"
                + "Los períodos cerrados permanecerán intactos como histórico.</html>"
        ), BorderLayout.NORTH);
        cont.add(form, BorderLayout.CENTER);

        int op = JOptionPane.showConfirmDialog(
                this,
                cont,
                "Crear nuevo periodo",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (op != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int id = service.iniciar(
                    nombre.getText(),
                    LocalDate.parse(inicio.getText().trim()),
                    LocalDate.parse(fin.getText().trim())
            );

            cargar();
            mensaje(
                    "Nuevo periodo creado correctamente (ID " + id + ").\n"
                    + "Los módulos mostrarán únicamente la información de este período.",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            mensaje(e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrarYCrearNuevo() {
        PeriodoContable actualPeriodo = service.abierto();

        if (actualPeriodo == null) {
            crearNuevoSinPeriodoAbierto();
            return;
        }

        LocalDate inicioSugerido = actualPeriodo.getFechaFin().plusDays(1);
        LocalDate finSugerido = inicioSugerido.plusYears(1).minusDays(1);

        JTextField nombre = new JTextField(String.valueOf(inicioSugerido.getYear()), 16);
        JTextField inicio = new JTextField(inicioSugerido.toString(), 12);
        JTextField fin = new JTextField(finSugerido.toString(), 12);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Periodo actual:"));
        form.add(new JLabel(actualPeriodo.getNombre()));
        form.add(new JLabel("Nuevo nombre:"));
        form.add(nombre);
        form.add(new JLabel("Fecha inicio (AAAA-MM-DD):"));
        form.add(inicio);
        form.add(new JLabel("Fecha fin (AAAA-MM-DD):"));
        form.add(fin);

        JPanel cont = new JPanel(new BorderLayout(0, 10));
        cont.add(new JLabel(
                "<html>Se cerrará el periodo actual y se creará el siguiente.<br>"
                + "El nuevo período comenzará completamente en cero, sin arrastre de saldos ni inventario.<br>"
                + "Toda la información del período cerrado permanecerá intacta y disponible para exportación histórica.</html>"
        ), BorderLayout.NORTH);
        cont.add(form, BorderLayout.CENTER);

        int op = JOptionPane.showConfirmDialog(
                this,
                cont,
                "Cerrar y crear nuevo periodo",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (op != JOptionPane.OK_OPTION) {
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "¿Confirmas el cierre de " + actualPeriodo.getNombre()
                + " y la creación del nuevo periodo?\n"
                + "El nuevo período comenzará en cero.\n"
                + "El historial anterior NO se borrará.",
                "Confirmar cambio de periodo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int id = service.cerrarYCrearNuevo(
                    nombre.getText(),
                    LocalDate.parse(inicio.getText().trim()),
                    LocalDate.parse(fin.getText().trim())
            );

            cargar();
            mensaje(
                    "Nuevo periodo creado correctamente (ID " + id + ").\n"
                    + "El período comienza en cero y los datos anteriores permanecen únicamente en el histórico.",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            mensaje(e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportar() {
        int filaVista = tabla.getSelectedRow();

        if (filaVista < 0) {
            mensaje("Selecciona un periodo de la tabla.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        int id = ((Number) modelo.getValueAt(filaModelo, 0)).intValue();
        String nombre = Objects.toString(modelo.getValueAt(filaModelo, 1));

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(
                "ContaProMax_Periodo_"
                + nombre.replaceAll("[^a-zA-Z0-9_-]", "_")
                + ".xlsx"
        ));

        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = fc.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".xlsx")) {
            archivo = new File(archivo.getAbsolutePath() + ".xlsx");
        }

        try {
            reportes.excelPeriodoCompleto(id, nombre, archivo);
            mensaje(
                    "Periodo exportado correctamente.\n"
                    + "El Excel contiene una hoja por cada reporte.",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            mensaje(e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mensaje(String texto, int tipo) {
        JOptionPane.showMessageDialog(this, texto, "ContaProMax", tipo);
    }

    private class RenderTabla extends DefaultTableCellRenderer {

        private final int alineacion;

        public RenderTabla(int alineacion) {
            this.alineacion = alineacion;
            setOpaque(true);
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
            Component componente = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            setHorizontalAlignment(alineacion);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            if (isSelected) {
                componente.setBackground(SELECCION);
                componente.setForeground(TEXTO);
            } else {
                componente.setBackground(
                        row % 2 == 0
                                ? Color.WHITE
                                : new Color(248, 250, 252)
                );
                componente.setForeground(TEXTO);
            }

            return componente;
        }
    }

    private class RenderEstado extends DefaultTableCellRenderer {

        public RenderEstado() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
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
            Component componente = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

            if (isSelected) {
                componente.setBackground(SELECCION);
                componente.setForeground(TEXTO);
                return componente;
            }

            String estado = value == null ? "" : value.toString();

            if ("ABIERTO".equalsIgnoreCase(estado)) {
                componente.setBackground(new Color(220, 252, 231));
                componente.setForeground(VERDE);
            } else {
                componente.setBackground(new Color(241, 245, 249));
                componente.setForeground(SECUNDARIO);
            }

            return componente;
        }
    }
}
