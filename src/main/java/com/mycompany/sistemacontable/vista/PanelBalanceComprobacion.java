package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.BalanceComprobacion;
import com.mycompany.sistemacontable.modelo.LineaBalanceComprobacion;
import com.mycompany.sistemacontable.servicio.BalanceComprobacionService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

public class PanelBalanceComprobacion extends JPanel {

    private final BalanceComprobacionService service = new BalanceComprobacionService();
    private final DefaultTableModel modelo;
    private final JLabel lblTotales = new JLabel();
    private final JLabel lblEstado = new JLabel();

    private final Color FONDO = new Color(245, 247, 250);
    private final Color TEXTO = new Color(30, 41, 59);
    private final Color SECUNDARIO = new Color(100, 116, 139);
    private final Color BORDE = new Color(226, 232, 240);
    private final Color PRIMARIO = new Color(37, 99, 235);
    private final Color EXITO = new Color(22, 163, 74);
    private final Color ERROR = new Color(220, 38, 38);

    public PanelBalanceComprobacion() {
        setLayout(new BorderLayout(0, 18));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(28, 30, 30, 30));

        add(crearEncabezado(), BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{
            "Código", "Cuenta", "Movimiento Debe", "Movimiento Haber", "Saldo Deudor", "Saldo Acreedor"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(30);
        tabla.setGridColor(BORDE);
        tabla.setShowVerticalLines(false);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 38));
        tabla.setAutoCreateRowSorter(true);

        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 2; i <= 5; i++) tabla.getColumnModel().getColumn(i).setCellRenderer(derecha);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        add(scroll, BorderLayout.CENTER);

        add(crearPie(), BorderLayout.SOUTH);
        cargarBalance();
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setOpaque(false);
        JPanel textos = new JPanel(); textos.setOpaque(false); textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Balance de Comprobación"); titulo.setFont(new Font("Segoe UI", Font.BOLD, 28)); titulo.setForeground(TEXTO);
        JLabel sub = new JLabel("Movimientos y saldos de todas las cuentas con actividad."); sub.setFont(new Font("Segoe UI", Font.PLAIN, 14)); sub.setForeground(SECUNDARIO);
        textos.add(titulo); textos.add(Box.createVerticalStrut(5)); textos.add(sub);
        JButton b = crearBoton("Actualizar"); b.addActionListener(e -> cargarBalance());
        panel.add(textos, BorderLayout.WEST); panel.add(b, BorderLayout.EAST); return panel;
    }

    private JPanel crearPie() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE), BorderFactory.createEmptyBorder(14, 18, 14, 18)));
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotales.setFont(new Font("Segoe UI", Font.BOLD, 12)); lblTotales.setForeground(TEXTO);
        panel.add(lblEstado, BorderLayout.WEST); panel.add(lblTotales, BorderLayout.EAST); return panel;
    }

    public final void cargarBalance() {
        try {
            BalanceComprobacion b = service.generarBalance();
            modelo.setRowCount(0);
            for (LineaBalanceComprobacion l : b.getLineas()) {
                modelo.addRow(new Object[]{l.getCodigo(), l.getNombre(), dinero(l.getMovimientoDebe()), dinero(l.getMovimientoHaber()), dinero(l.getSaldoDeudor()), dinero(l.getSaldoAcreedor())});
            }
            lblTotales.setText("Mov. Debe: " + dinero(b.getTotalMovimientoDebe()) + "   Mov. Haber: " + dinero(b.getTotalMovimientoHaber()) + "   |   Saldo Deudor: " + dinero(b.getTotalSaldoDeudor()) + "   Saldo Acreedor: " + dinero(b.getTotalSaldoAcreedor()));
            boolean ok = b.isMovimientosCuadrados() && b.isSaldosCuadrados();
            lblEstado.setText(ok ? "✓ Balance cuadrado" : "⚠ Revisar balance");
            lblEstado.setForeground(ok ? EXITO : ERROR);
        } catch (Exception e) { mostrarError(e); }
    }

    private JButton crearBoton(String t) { JButton b=new JButton(t); b.setUI(new BasicButtonUI()); b.setFont(new Font("Segoe UI",Font.BOLD,13)); b.setForeground(Color.WHITE); b.setBackground(PRIMARIO); b.setBorder(BorderFactory.createEmptyBorder(10,18,10,18)); b.setFocusPainted(false); b.setOpaque(true); b.setContentAreaFilled(true); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b; }
    private String dinero(BigDecimal v) { if(v==null)v=BigDecimal.ZERO; return "$"+String.format("%,.2f",v.setScale(2,RoundingMode.HALF_UP)); }
    private void mostrarError(Throwable e) { String m="Ocurrió un error desconocido."; for(Throwable a=e;a!=null;a=a.getCause()) if(a.getMessage()!=null&&!a.getMessage().isBlank())m=a.getMessage(); JOptionPane.showMessageDialog(this,m,"No se pudo cargar el Balance de Comprobación",JOptionPane.ERROR_MESSAGE); }
}
