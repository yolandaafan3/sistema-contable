package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.modelo.*;
import com.mycompany.sistemacontable.servicio.GestionAsientoService;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class DialogoAsientoDetalle extends JDialog {
    private final int idAsiento; private final boolean puedeEditar; private final Runnable alGuardar;
    private final GestionAsientoService service=new GestionAsientoService(); private final CuentaDAO cuentaDAO=new CuentaDAO();
    public DialogoAsientoDetalle(Window owner,int idAsiento,boolean puedeEditar,Runnable alGuardar){super(owner,"Detalle del asiento",ModalityType.APPLICATION_MODAL);this.idAsiento=idAsiento;this.puedeEditar=puedeEditar;this.alGuardar=alGuardar; setSize(820,560);setLocationRelativeTo(owner);construir();}
    private void construir(){
        AsientoContable a=service.obtenerAsiento(idAsiento); if(a==null){dispose();return;} setLayout(new BorderLayout(0,12)); getRootPane().setBorder(BorderFactory.createEmptyBorder(20,22,20,22));
        JLabel h=new JLabel("<html><b style='font-size:18px'>Asiento N.º "+a.getNumeroAsiento()+"</b><br>Fecha: "+a.getFecha()+" &nbsp;&nbsp; Tipo: "+a.getTipoAsiento()+" &nbsp;&nbsp; Estado: "+a.getEstado()+"<br>Concepto: "+esc(a.getConcepto())+"</html>"); add(h,BorderLayout.NORTH);
        DefaultTableModel m=new DefaultTableModel(new Object[]{"Código","Cuenta","Descripción","Debe","Haber"},0){public boolean isCellEditable(int r,int c){return false;}}; JTable t=new JTable(m);t.setRowHeight(28);
        for(DetalleAsiento d:service.obtenerDetalles(idAsiento)){Cuenta c=cuentaDAO.buscarPorId(d.getIdCuenta());m.addRow(new Object[]{c==null?"":c.getCodigo(),c==null?"":c.getNombre(),d.getDescripcion(),money(d.getDebe()),money(d.getHaber())});} add(new JScrollPane(t),BorderLayout.CENTER);
        JPanel b=new JPanel(new FlowLayout(FlowLayout.RIGHT)); JButton cerrar=new JButton("Cerrar"); cerrar.addActionListener(e->dispose()); b.add(cerrar);
        if(puedeEditar){JButton editar=new JButton("Editar asiento"); boolean manual="MANUAL".equals(a.getTipoAsiento())&&a.getIdOperacion()==null; editar.setEnabled(manual); editar.setToolTipText(manual?"Modificar este asiento manual":"Los asientos automáticos se corrigen desde la operación que los originó"); editar.addActionListener(e->{new DialogoEditarAsientoManual(this,idAsiento,()->{if(alGuardar!=null)alGuardar.run();dispose();}).setVisible(true);}); b.add(editar);} add(b,BorderLayout.SOUTH);
    }
    private String money(BigDecimal x){return "$"+String.format("%,.2f",x==null?BigDecimal.ZERO:x);} private String esc(String s){return s==null?"":s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");}
}
