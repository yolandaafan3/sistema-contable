package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.BalanceGeneral;
import com.mycompany.sistemacontable.modelo.LineaBalanceGeneral;
import com.mycompany.sistemacontable.servicio.BalanceGeneralService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.plaf.basic.BasicButtonUI;

public class PanelBalanceGeneral extends JPanel {

    private final BalanceGeneralService service=new BalanceGeneralService();
    private final JPanel detalle=new JPanel(new GridBagLayout());
    private final JLabel lblEstado=new JLabel();
    private final Color FONDO=new Color(245,247,250),TEXTO=new Color(30,41,59),SECUNDARIO=new Color(100,116,139),BORDE=new Color(226,232,240),PRIMARIO=new Color(37,99,235),EXITO=new Color(22,163,74),ERROR=new Color(220,38,38);

    public PanelBalanceGeneral(){setLayout(new BorderLayout(0,18));setBackground(FONDO);setBorder(BorderFactory.createEmptyBorder(28,30,30,30));add(crearEncabezado(),BorderLayout.NORTH);detalle.setBackground(Color.WHITE);detalle.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE),BorderFactory.createEmptyBorder(20,28,20,28)));JScrollPane sc=new JScrollPane(detalle);sc.setBorder(null);sc.getViewport().setBackground(FONDO);sc.getVerticalScrollBar().setUnitIncrement(16);add(sc,BorderLayout.CENTER);lblEstado.setFont(new Font("Segoe UI",Font.BOLD,13));JPanel pie=new JPanel(new BorderLayout());pie.setBackground(Color.WHITE);pie.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE),BorderFactory.createEmptyBorder(12,18,12,18)));pie.add(lblEstado,BorderLayout.WEST);add(pie,BorderLayout.SOUTH);cargarBalanceGeneral();}
    private JPanel crearEncabezado(){JPanel p=new JPanel(new BorderLayout());p.setOpaque(false);JPanel t=new JPanel();t.setOpaque(false);t.setLayout(new BoxLayout(t,BoxLayout.Y_AXIS));JLabel a=new JLabel("Balance General");a.setFont(new Font("Segoe UI",Font.BOLD,28));a.setForeground(TEXTO);JLabel s=new JLabel("Activos, pasivos y patrimonio del período activo.");s.setFont(new Font("Segoe UI",Font.PLAIN,14));s.setForeground(SECUNDARIO);t.add(a);t.add(Box.createVerticalStrut(5));t.add(s);JButton b=crearBoton("Actualizar");b.addActionListener(e->cargarBalanceGeneral());p.add(t,BorderLayout.WEST);p.add(b,BorderLayout.EAST);return p;}
    public final void cargarBalanceGeneral(){detalle.removeAll();lblEstado.setText("Sin datos del período activo");lblEstado.setForeground(SECUNDARIO);detalle.revalidate();detalle.repaint();try{BalanceGeneral b=service.generar();int[] f={0};agregarSeccion("ACTIVO CORRIENTE",f);agregarCuentas(b.getActivosCorrientes(),f);agregarTotal("Total Activo Corriente",b.getTotalActivoCorriente(),f);agregarSeccion("ACTIVO NO CORRIENTE",f);agregarCuentas(b.getActivosNoCorrientes(),f);agregarTotal("Total Activo No Corriente",b.getTotalActivoNoCorriente(),f);agregarTotal("TOTAL ACTIVO",b.getTotalActivo(),f);agregarSeccion("PASIVO CORRIENTE",f);agregarCuentas(b.getPasivosCorrientes(),f);agregarTotal("Total Pasivo Corriente",b.getTotalPasivoCorriente(),f);agregarSeccion("PASIVO NO CORRIENTE",f);agregarCuentas(b.getPasivosNoCorrientes(),f);agregarTotal("Total Pasivo No Corriente",b.getTotalPasivoNoCorriente(),f);agregarTotal("TOTAL PASIVO",b.getTotalPasivo(),f);agregarSeccion("PATRIMONIO",f);agregarCuentas(b.getPatrimonio(),f);agregarTotal("TOTAL PATRIMONIO",b.getTotalPatrimonio(),f);agregarTotal("TOTAL PASIVO + PATRIMONIO",b.getTotalPasivoPatrimonio(),f);lblEstado.setText((b.isCuadrado()?"✓ Balance General cuadrado":"⚠ Balance General no cuadrado")+"   |   Diferencia: "+dinero(b.getDiferencia()));lblEstado.setForeground(b.isCuadrado()?EXITO:ERROR);detalle.revalidate();detalle.repaint();}catch(Exception e){mostrarError(e);}}
    private void agregarCuentas(List<LineaBalanceGeneral> cuentas,int[]f){for(LineaBalanceGeneral c:cuentas)agregarLinea(c.getCodigo()+"  "+c.getNombre(),c.getSaldo(),false,f);}
    private void agregarSeccion(String t,int[]f){GridBagConstraints g=base(f[0]++);g.gridwidth=2;g.insets=new Insets(14,0,6,0);JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",Font.BOLD,14));l.setForeground(PRIMARIO);detalle.add(l,g);}
    private void agregarTotal(String t,BigDecimal v,int[]f){agregarLinea(t,v,true,f);}
    private void agregarLinea(String t,BigDecimal v,boolean fuerte,int[]f){GridBagConstraints a=base(f[0]);a.gridx=0;a.weightx=1;JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",fuerte?Font.BOLD:Font.PLAIN,13));l.setForeground(TEXTO);detalle.add(l,a);GridBagConstraints b=base(f[0]++);b.gridx=1;b.weightx=0;b.anchor=GridBagConstraints.EAST;JLabel n=new JLabel(dinero(v));n.setFont(new Font("Segoe UI",fuerte?Font.BOLD:Font.PLAIN,13));n.setForeground(TEXTO);detalle.add(n,b);}
    private GridBagConstraints base(int y){GridBagConstraints g=new GridBagConstraints();g.gridy=y;g.fill=GridBagConstraints.HORIZONTAL;g.anchor=GridBagConstraints.WEST;g.insets=new Insets(4,0,4,0);return g;}
    private JButton crearBoton(String t){JButton b=new JButton(t);b.setUI(new BasicButtonUI());b.setFont(new Font("Segoe UI",Font.BOLD,13));b.setForeground(Color.WHITE);b.setBackground(PRIMARIO);b.setBorder(BorderFactory.createEmptyBorder(10,18,10,18));b.setFocusPainted(false);b.setOpaque(true);b.setContentAreaFilled(true);b.setCursor(new Cursor(Cursor.HAND_CURSOR));return b;}
    private String dinero(BigDecimal v){if(v==null)v=BigDecimal.ZERO;return "$"+String.format("%,.2f",v.setScale(2,RoundingMode.HALF_UP));}
    private void mostrarError(Throwable e){String m="Ocurrió un error desconocido.";for(Throwable a=e;a!=null;a=a.getCause())if(a.getMessage()!=null&&!a.getMessage().isBlank())m=a.getMessage();JOptionPane.showMessageDialog(this,m,"No se pudo cargar el Balance General",JOptionPane.ERROR_MESSAGE);}
}
