package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.BalanceGeneral;
import com.mycompany.sistemacontable.modelo.EstadoResultados;
import com.mycompany.sistemacontable.servicio.BalanceGeneralService;
import com.mycompany.sistemacontable.servicio.EstadoResultadosService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class PanelDashboard extends JPanel {

    private final EstadoResultadosService estadoService = new EstadoResultadosService();
    private final BalanceGeneralService balanceService = new BalanceGeneralService();

    private final JLabel lblVentas = new JLabel("$0.00");
    private final JLabel lblInventario = new JLabel("$0.00");
    private final JLabel lblUtilidad = new JLabel("$0.00");
    private final JLabel lblBalance = new JLabel("Pendiente");

    private final Color FONDO=new Color(245,247,250), TEXTO=new Color(30,41,59), SECUNDARIO=new Color(100,116,139), BORDE=new Color(226,232,240), EXITO=new Color(22,163,74), ERROR=new Color(220,38,38);

    public PanelDashboard(){setLayout(new BorderLayout());setBackground(FONDO);setBorder(BorderFactory.createEmptyBorder(30,30,30,30));JPanel contenido=new JPanel();contenido.setOpaque(false);contenido.setLayout(new BoxLayout(contenido,BoxLayout.Y_AXIS));JLabel titulo=new JLabel("Dashboard");titulo.setFont(new Font("Segoe UI",Font.BOLD,28));titulo.setForeground(TEXTO);JLabel sub=new JLabel("Resumen general del período contable");sub.setFont(new Font("Segoe UI",Font.PLAIN,14));sub.setForeground(SECUNDARIO);contenido.add(titulo);contenido.add(Box.createVerticalStrut(5));contenido.add(sub);contenido.add(Box.createVerticalStrut(30));JPanel tarjetas=new JPanel(new GridLayout(1,4,18,0));tarjetas.setOpaque(false);tarjetas.setMaximumSize(new Dimension(Integer.MAX_VALUE,125));tarjetas.add(tarjeta("Ventas netas",lblVentas,"Estado de Resultados"));tarjetas.add(tarjeta("Inventario",lblInventario,"Saldo final PEPS"));tarjetas.add(tarjeta("Utilidad",lblUtilidad,"Resultado del período"));tarjetas.add(tarjeta("Balance",lblBalance,"Activo = Pasivo + Patrimonio"));contenido.add(tarjetas);contenido.add(Box.createVerticalStrut(25));JPanel info=new JPanel(new BorderLayout());info.setBackground(Color.WHITE);info.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE),BorderFactory.createEmptyBorder(24,24,24,24)));JLabel texto=new JLabel("<html><div style='width:720px'><b>Resumen automático</b><br><br>Los valores se calculan con los asientos contabilizados, el Estado de Resultados y el Kardex PEPS. Entra a cada módulo para ver el detalle completo.</div></html>");texto.setFont(new Font("Segoe UI",Font.PLAIN,13));texto.setForeground(SECUNDARIO);info.add(texto);contenido.add(info);add(contenido,BorderLayout.NORTH);cargarDashboard();}
    private JPanel tarjeta(String t,JLabel valor,String d){JPanel p=new JPanel();p.setBackground(Color.WHITE);p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE),BorderFactory.createEmptyBorder(18,20,18,20)));JLabel a=new JLabel(t);a.setFont(new Font("Segoe UI",Font.PLAIN,13));a.setForeground(SECUNDARIO);valor.setFont(new Font("Segoe UI",Font.BOLD,22));valor.setForeground(TEXTO);JLabel z=new JLabel(d);z.setFont(new Font("Segoe UI",Font.PLAIN,11));z.setForeground(SECUNDARIO);p.add(a);p.add(Box.createVerticalStrut(8));p.add(valor);p.add(Box.createVerticalGlue());p.add(z);return p;}
    public final void cargarDashboard(){try{EstadoResultados e=estadoService.generar();BalanceGeneral b=balanceService.generar();lblVentas.setText(dinero(e.getVentasNetas()));lblInventario.setText(dinero(e.getInventarioFinal()));lblUtilidad.setText(dinero(e.getUtilidadEjercicio()));lblUtilidad.setForeground(e.getUtilidadEjercicio().compareTo(BigDecimal.ZERO)>=0?EXITO:ERROR);lblBalance.setText(b.isCuadrado()?"Cuadrado":"Revisar");lblBalance.setForeground(b.isCuadrado()?EXITO:ERROR);}catch(Exception e){String m="No se pudo actualizar el Dashboard.";for(Throwable a=e;a!=null;a=a.getCause())if(a.getMessage()!=null&&!a.getMessage().isBlank())m=a.getMessage();JOptionPane.showMessageDialog(this,m,"Dashboard",JOptionPane.ERROR_MESSAGE);}}
    private String dinero(BigDecimal v){if(v==null)v=BigDecimal.ZERO;return "$"+String.format("%,.2f",v.setScale(2,RoundingMode.HALF_UP));}
}
