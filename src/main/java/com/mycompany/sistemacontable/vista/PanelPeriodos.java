package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.PeriodoContable;
import com.mycompany.sistemacontable.servicio.GeneradorReportesService;
import com.mycompany.sistemacontable.servicio.GestionPeriodosService;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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
    private final JButton btnGestionarPeriodo;

    public PanelPeriodos() {
        setLayout(new BorderLayout(0, 18));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(28, 30, 30, 30));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Periodos Contables");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        actual.setForeground(new Color(100, 116, 139));

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(actual);
        top.add(textos, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);

        btnGestionarPeriodo = boton("Cerrar y crear nuevo");
        btnGestionarPeriodo.setBackground(new Color(5, 150, 105));

        JButton exportar = boton("Exportar periodo");

        acciones.add(btnGestionarPeriodo);
        acciones.add(exportar);
        top.add(acciones, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        tabla.setRowHeight(30);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        btnGestionarPeriodo.addActionListener(e -> gestionarPeriodo());
        exportar.addActionListener(e -> exportar());

        cargar();
    }

    private JButton boton(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(new Color(37, 99, 235));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return b;
    }

    public void cargar() {
        modelo.setRowCount(0);

        PeriodoContable abierto = service.abierto();
        actual.setText(
                abierto == null
                        ? "No hay un periodo abierto. Puedes crear uno nuevo."
                        : "Periodo actual: " + abierto.getNombre()
                        + " · " + abierto.getFechaInicio()
                        + " a " + abierto.getFechaFin()
        );

        // Se conserva un único botón de gestión. Si existe período abierto,
        // lo cierra y crea el siguiente; si no existe, permite crear el inicial.
        btnGestionarPeriodo.setText(
                abierto == null ? "Crear nuevo periodo" : "Cerrar y crear nuevo"
        );

        for (PeriodoContable x : service.listar()) {
            modelo.addRow(new Object[]{
                x.getIdPeriodo(),
                x.getNombre(),
                x.getFechaInicio(),
                x.getFechaFin(),
                x.getEstado()
            });
        }
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
        int r = tabla.getSelectedRow();
        if (r < 0) {
            mensaje("Selecciona un periodo de la tabla.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = ((Number) modelo.getValueAt(r, 0)).intValue();
        String nombre = Objects.toString(modelo.getValueAt(r, 1));

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
}
