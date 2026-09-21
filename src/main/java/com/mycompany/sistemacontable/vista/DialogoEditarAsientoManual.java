package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.modelo.*;
import com.mycompany.sistemacontable.servicio.GestionAsientoService;
import java.awt.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class DialogoEditarAsientoManual extends JDialog {
    private final int id; private final Runnable ok; private final GestionAsientoService service=new GestionAsientoService(); private final CuentaDAO cuentaDAO=new CuentaDAO();
    private final JSpinner fecha=new JSpinner(DialogoUIUtils.crearModeloFechaPeriodoActivo()); private final JTextField concepto=new JTextField();
    private final DefaultTableModel model=new DefaultTableModel(new Object[]{"Cuenta","Descripción","Debe","Haber"},0); private final JTable tabla=new JTable(model);
    private java.util.List<Cuenta> cuentas;
    public DialogoEditarAsientoManual(Window owner,int id,Runnable ok){super(owner,"Editar asiento manual",ModalityType.APPLICATION_MODAL);this.id=id;this.ok=ok;setSize(900,620);setLocationRelativeTo(owner);construir();cargar();}
    private void construir(){setLayout(new BorderLayout(0,12)); getRootPane().setBorder(BorderFactory.createEmptyBorder(18,20,18,20)); JPanel top=new JPanel(new GridLayout(2,2,8,8));top.add(new JLabel("Fecha"));top.add(fecha);top.add(new JLabel("Concepto"));top.add(concepto);add(top,BorderLayout.NORTH); cuentas=cuentaDAO.listarCuentasMovimiento(); JComboBox<Cuenta> combo=new JComboBox<>(cuentas.toArray(new Cuenta[0]));tabla.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(combo));tabla.setRowHeight(30);add(new JScrollPane(tabla),BorderLayout.CENTER);JPanel b=new JPanel(new FlowLayout(FlowLayout.RIGHT));JButton add=new JButton("Agregar línea"),del=new JButton("Quitar línea"),save=new JButton("Guardar cambios"),cancel=new JButton("Cancelar");add.addActionListener(e->model.addRow(new Object[]{cuentas.isEmpty()?null:cuentas.get(0),"",BigDecimal.ZERO,BigDecimal.ZERO}));del.addActionListener(e->{int r=tabla.getSelectedRow();if(r>=0)model.removeRow(r);});cancel.addActionListener(e->dispose());save.addActionListener(e->guardar());b.add(add);b.add(del);b.add(cancel);b.add(save);add(b,BorderLayout.SOUTH);}
    private void cargar(){AsientoContable a=service.obtenerAsiento(id);fecha.setValue(java.util.Date.from(a.getFecha().atStartOfDay(ZoneId.systemDefault()).toInstant()));concepto.setText(a.getConcepto());for(DetalleAsiento d:service.obtenerDetalles(id))model.addRow(new Object[]{cuentaDAO.buscarPorId(d.getIdCuenta()),d.getDescripcion(),d.getDebe(),d.getHaber()});}
    private void guardar(){try{java.util.Date f=(java.util.Date)fecha.getValue();LocalDate ld=f.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();java.util.List<DetalleAsiento> ds=new ArrayList<>();for(int r=0;r<model.getRowCount();r++){Cuenta c=(Cuenta)model.getValueAt(r,0);if(c==null)throw new IllegalArgumentException("Selecciona una cuenta en cada línea.");DetalleAsiento d=new DetalleAsiento();d.setIdCuenta(c.getIdCuenta());d.setDescripcion(String.valueOf(model.getValueAt(r,1)));d.setDebe(dec(model.getValueAt(r,2)));d.setHaber(dec(model.getValueAt(r,3)));ds.add(d);}service.actualizarAsientoManual(id,ld,concepto.getText(),ds);JOptionPane.showMessageDialog(this,"Asiento actualizado correctamente.");if(ok!=null)ok.run();dispose();}catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"No se pudo guardar",JOptionPane.ERROR_MESSAGE);}}
    private BigDecimal dec(Object o){if(o==null||o.toString().isBlank())return BigDecimal.ZERO;return new BigDecimal(o.toString().replace(",","."));}
}
