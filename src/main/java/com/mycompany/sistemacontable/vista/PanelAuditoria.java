package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.AuditoriaDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelAuditoria extends JPanel {

    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"ID", "Fecha", "Usuario", "Entidad", "Registro", "Acción", "Antes", "Después"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };

    private final JTable tabla = new JTable(modelo);

    public PanelAuditoria() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(25, 30, 30, 30));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Bitácora de auditoría");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitulo = new JLabel("Consulta los cambios realizados y revisa el detalle completo de cada registro.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(100, 116, 139));

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);

        JButton verDetalles = new JButton("Ver detalles");
        verDetalles.setBackground(new Color(37, 99, 235));
        verDetalles.setForeground(Color.WHITE);
        verDetalles.setFocusPainted(false);
        verDetalles.setFont(new Font("Segoe UI", Font.BOLD, 13));
        verDetalles.addActionListener(e -> mostrarDetalleSeleccionado());

        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(verDetalles, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(165);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(180);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(105);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(300);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(300);

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0) {
                    mostrarDetalleSeleccionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        cargar();
    }

    public void cargar() {
        modelo.setRowCount(0);
        for (Object[] fila : new AuditoriaDAO().listar()) {
            modelo.addRow(fila);
        }
    }

    private void mostrarDetalleSeleccionado() {
        int vista = tabla.getSelectedRow();
        if (vista < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona un registro de la bitácora.",
                    "Bitácora de auditoría",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int fila = tabla.convertRowIndexToModel(vista);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.setPreferredSize(new Dimension(680, 430));

        JPanel datos = new JPanel(new GridLayout(0, 2, 8, 6));
        datos.add(new JLabel("ID:"));
        datos.add(new JLabel(valor(0, fila)));
        datos.add(new JLabel("Fecha:"));
        datos.add(new JLabel(valor(1, fila)));
        datos.add(new JLabel("Usuario:"));
        datos.add(new JLabel(valor(2, fila)));
        datos.add(new JLabel("Entidad:"));
        datos.add(new JLabel(valor(3, fila)));
        datos.add(new JLabel("Registro:"));
        datos.add(new JLabel(valor(4, fila)));
        datos.add(new JLabel("Acción:"));
        datos.add(new JLabel(valor(5, fila)));
        contenido.add(datos, BorderLayout.NORTH);

        JPanel cambios = new JPanel(new GridLayout(2, 1, 0, 10));
        cambios.add(crearBloqueDetalle("Antes", valor(6, fila)));
        cambios.add(crearBloqueDetalle("Después", valor(7, fila)));
        contenido.add(cambios, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(
                this,
                contenido,
                "Detalle de auditoría #" + valor(0, fila),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private JPanel crearBloqueDetalle(String titulo, String texto) {
        JPanel p = new JPanel(new BorderLayout(0, 5));
        JLabel l = new JLabel(titulo);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JTextArea area = new JTextArea(texto == null || texto.isBlank() ? "(Sin datos)" : texto);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        p.add(l, BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    private String valor(int columna, int fila) {
        Object v = modelo.getValueAt(fila, columna);
        return v == null ? "" : String.valueOf(v);
    }
}
