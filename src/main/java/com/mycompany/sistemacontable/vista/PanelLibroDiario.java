package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.LineaLibroDiario;
import com.mycompany.sistemacontable.servicio.LibroDiarioService;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.*;

public class PanelLibroDiario extends JPanel {
    private final LibroDiarioService service=new LibroDiarioService();
    private final boolean puedeEditar;
    private final DefaultTableModel model=new DefaultTableModel(new Object[]{"Asiento","Fecha","Código","Cuenta","Concepto","Descripción","Debe","Haber"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final JTable tabla=new JTable(model);
    private final java.util.List<Integer> idsPorFila=new ArrayList<>();
    private final JLabel totalDebe=new JLabel("$0.00"), totalHaber=new JLabel("$0.00"), estado=new JLabel("Sin movimientos");
    private final DateTimeFormatter fechaFmt=DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PanelLibroDiario(){ this(false); }
    public PanelLibroDiario(boolean puedeEditar){ this.puedeEditar=puedeEditar; construir(); cargarLibroDiario(); }

    private void construir(){
        setLayout(new BorderLayout(0,18)); setBackground(new Color(245,247,250)); setBorder(BorderFactory.createEmptyBorder(28,30,30,30));
        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false);
        JPanel textos=new JPanel(); textos.setOpaque(false); textos.setLayout(new BoxLayout(textos,BoxLayout.Y_AXIS));
        JLabel t=new JLabel("Libro Diario"); t.setFont(new Font("Segoe UI",Font.BOLD,28)); JLabel s=new JLabel("Asientos contables registrados durante el período activo."); s.setForeground(new Color(100,116,139)); textos.add(t); textos.add(Box.createVerticalStrut(5)); textos.add(s); top.add(textos,BorderLayout.WEST);
        JPanel botones=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); botones.setOpaque(false);
        JButton ver=boton("Ver asiento"), act=boton("Actualizar"); ver.addActionListener(e->verAsiento()); act.addActionListener(e->cargarLibroDiario()); botones.add(ver); botones.add(act); top.add(botones,BorderLayout.EAST); add(top,BorderLayout.NORTH);
        tabla.setRowHeight(30); tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); tabla.setAutoCreateRowSorter(true); tabla.setShowVerticalLines(false); tabla.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,13));
        DefaultTableCellRenderer der=new DefaultTableCellRenderer(); der.setHorizontalAlignment(SwingConstants.RIGHT); tabla.getColumnModel().getColumn(6).setCellRenderer(der); tabla.getColumnModel().getColumn(7).setCellRenderer(der);
        tabla.addMouseListener(new java.awt.event.MouseAdapter(){ public void mouseClicked(java.awt.event.MouseEvent e){ if(e.getClickCount()==2) verAsiento(); }});
        add(new JScrollPane(tabla),BorderLayout.CENTER);
        JPanel pie=new JPanel(new BorderLayout()); pie.setBackground(Color.WHITE); pie.setBorder(BorderFactory.createEmptyBorder(14,18,14,18)); estado.setFont(new Font("Segoe UI",Font.BOLD,13)); pie.add(estado,BorderLayout.WEST);
        JPanel tot=new JPanel(new FlowLayout(FlowLayout.RIGHT,20,0)); tot.setOpaque(false); tot.add(new JLabel("Total Debe:")); tot.add(totalDebe); tot.add(new JLabel("Total Haber:")); tot.add(totalHaber); totalDebe.setFont(new Font("Segoe UI",Font.BOLD,15)); totalHaber.setFont(new Font("Segoe UI",Font.BOLD,15)); pie.add(tot,BorderLayout.EAST); add(pie,BorderLayout.SOUTH);
    }

    public final void cargarLibroDiario(){
        try{
            model.setRowCount(0); idsPorFila.clear(); java.util.List<LineaLibroDiario> lineas=service.obtenerLibroDiario();
            for(LineaLibroDiario l:lineas){ idsPorFila.add(l.getIdAsiento()); model.addRow(new Object[]{l.getNumeroAsiento(),l.getFecha()==null?"":l.getFecha().format(fechaFmt),l.getCodigoCuenta(),l.getNombreCuenta(),l.getConcepto(),l.getDescripcion(),money(l.getDebe()),money(l.getHaber())}); }
            BigDecimal d=service.obtenerTotalDebe(), h=service.obtenerTotalHaber(); totalDebe.setText(money(d)); totalHaber.setText(money(h));
            if(lineas.isEmpty()){estado.setText("Sin movimientos registrados"); estado.setForeground(new Color(100,116,139));} else if(d.compareTo(h)==0){estado.setText("✓ Libro Diario cuadrado"); estado.setForeground(new Color(22,163,74));} else {estado.setText("⚠ Libro Diario descuadrado"); estado.setForeground(new Color(220,38,38));}
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"No se pudo cargar el Libro Diario",JOptionPane.ERROR_MESSAGE);}
    }

    private void verAsiento(){
        int vr=tabla.getSelectedRow(); if(vr<0){JOptionPane.showMessageDialog(this,"Selecciona una línea del asiento que deseas ver.");return;} int mr=tabla.convertRowIndexToModel(vr); int id=idsPorFila.get(mr);
        Window w=SwingUtilities.getWindowAncestor(this); DialogoAsientoDetalle d=new DialogoAsientoDetalle(w,id,puedeEditar,()->cargarLibroDiario()); d.setVisible(true);
    }
    private JButton boton(String x){JButton b=new JButton(x); b.setUI(new BasicButtonUI()); b.setBackground(new Color(37,99,235)); b.setForeground(Color.WHITE); b.setFont(new Font("Segoe UI",Font.BOLD,12)); b.setBorder(BorderFactory.createEmptyBorder(9,14,9,14)); b.setFocusPainted(false); return b;}
    private String money(BigDecimal v){return "$"+String.format("%,.2f",(v==null?BigDecimal.ZERO:v).setScale(2,RoundingMode.HALF_UP));}
}
