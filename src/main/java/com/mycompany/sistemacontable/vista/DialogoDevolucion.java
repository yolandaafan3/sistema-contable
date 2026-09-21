package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.ProductoDAO;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.servicio.DevolucionCompraService;
import com.mycompany.sistemacontable.servicio.DevolucionVentaService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.*;
import java.util.Date;

public class DialogoDevolucion extends JDialog {
    private final String tipoOperacion;
    private final ProductoDAO productoDAO=new ProductoDAO();
    private final DevolucionCompraService compraService=new DevolucionCompraService();
    private final DevolucionVentaService ventaService=new DevolucionVentaService();

    private JSpinner spFecha;
    private JComboBox<Producto> cmbProducto;
    private JComboBox<OperacionOrigenItem> cmbOrigen;
    private JTextField txtMonto;
    private JLabel lblUnidades;
    private JComboBox<String> cmbForma;
    private JLabel lblFormaOrigen;
    private JTextArea txtConcepto;

    public DialogoDevolucion(Window owner,String tipoOperacion){
        super(owner,"DEVOLUCION_COMPRA".equals(tipoOperacion)?"Devolución sobre Compra":"Devolución sobre Venta",ModalityType.APPLICATION_MODAL);
        this.tipoOperacion=tipoOperacion;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(700,650); setMinimumSize(new Dimension(600,520)); setLocationRelativeTo(owner);
        construir(); cargarProductos();
    }

    private boolean esCompra(){return "DEVOLUCION_COMPRA".equals(tipoOperacion);}

    private void construir(){
        JPanel form=new JPanel(new GridBagLayout()); form.setBorder(BorderFactory.createEmptyBorder(24,28,24,28));
        GridBagConstraints g=new GridBagConstraints();g.gridx=0;g.gridy=0;g.weightx=1;g.fill=GridBagConstraints.HORIZONTAL;g.insets=new Insets(6,6,6,6);
        JLabel titulo=new JLabel(esCompra()?"Registrar devolución sobre compra":"Registrar devolución sobre venta");titulo.setFont(new Font("Segoe UI",Font.BOLD,24));addRow(form,g,"",titulo);
        JLabel tipoAviso = new JLabel(esCompra()
                ? "SALIDA DE INVENTARIO: reduce el lote de la compra original"
                : "ENTRADA A INVENTARIO: la mercancía devuelta regresa al Kardex PEPS");
        tipoAviso.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tipoAviso.setForeground(esCompra() ? new Color(180, 83, 9) : new Color(22, 101, 52));
        addRow(form,g,"",tipoAviso);
        spFecha=new JSpinner(DialogoUIUtils.crearModeloFechaPeriodoActivo());spFecha.setEditor(new JSpinner.DateEditor(spFecha,"dd/MM/yyyy"));addRow(form,g,"Fecha",spFecha);
        cmbProducto=new JComboBox<>();cmbProducto.addActionListener(e->cargarOrigenes());addRow(form,g,"Producto",cmbProducto);
        cmbOrigen=new JComboBox<>();cmbOrigen.addActionListener(e->{actualizarUnidades();actualizarFormaOrigen();});addRow(form,g,esCompra()?"Compra de origen":"Venta de origen",cmbOrigen);
        txtMonto=new JTextField();txtMonto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){public void insertUpdate(javax.swing.event.DocumentEvent e){actualizarUnidades();}public void removeUpdate(javax.swing.event.DocumentEvent e){actualizarUnidades();}public void changedUpdate(javax.swing.event.DocumentEvent e){actualizarUnidades();}});addRow(form,g,"Monto de la devolución ($)",txtMonto);
        lblUnidades=new JLabel("0");lblUnidades.setFont(new Font("Segoe UI",Font.BOLD,16));addRow(form,g,"Unidades calculadas",lblUnidades);
        cmbForma=new JComboBox<>(new String[]{"CREDITO","EFECTIVO","BANCO"});cmbForma.setEnabled(false);addRow(form,g,esCompra()?"Forma según compra original":"Forma según venta original",cmbForma);
        lblFormaOrigen=new JLabel("Se toma automáticamente de la operación original");lblFormaOrigen.setForeground(new Color(71,85,105));addRow(form,g,"",lblFormaOrigen);
        txtConcepto=new JTextArea(3,30);txtConcepto.setLineWrap(true);txtConcepto.setWrapStyleWord(true);addRow(form,g,"Concepto",new JScrollPane(txtConcepto));
        JLabel ayuda=new JLabel("<html><div style='width:520px'>La devolución queda vinculada a la operación original. Las unidades se calculan como monto ÷ costo/precio unitario de esa compra o venta. En compra se descuenta del lote original; en venta regresa al inventario al costo PEPS de la venta original.</div></html>");addRow(form,g,"",ayuda);

        JButton cancelar=new JButton("Cancelar");cancelar.addActionListener(e->dispose()); JButton guardar=new JButton("Registrar devolución");guardar.addActionListener(e->guardar());
        JPanel pie=new JPanel(new FlowLayout(FlowLayout.RIGHT));pie.add(cancelar);pie.add(guardar);
        setLayout(new BorderLayout());add(DialogoUIUtils.envolverEnScroll(form,new Color(245,247,250)),BorderLayout.CENTER);add(pie,BorderLayout.SOUTH);
    }

    private void addRow(JPanel p,GridBagConstraints g,String label,Component c){
        if(!label.isEmpty()){JLabel l=new JLabel(label);l.setFont(new Font("Segoe UI",Font.BOLD,12));p.add(l,g);g.gridy++;}
        p.add(c,g);g.gridy++;
    }

    private void cargarProductos(){
        cmbProducto.removeAllItems(); for(Producto p:productoDAO.listarProductosActivos())cmbProducto.addItem(p); if(cmbProducto.getItemCount()>0)cmbProducto.setSelectedIndex(0); cargarOrigenes();
    }

    private void cargarOrigenes(){
        cmbOrigen.removeAllItems(); Producto p=(Producto)cmbProducto.getSelectedItem(); if(p==null)return;
        String tipo=esCompra()?"COMPRA":"VENTA";
        String sql="SELECT id_operacion,fecha,precio_unitario,cantidad,total,concepto,forma_pago FROM operaciones WHERE id_producto=? AND tipo_operacion=? ORDER BY fecha DESC,id_operacion DESC";
        try(Connection c=Conexion.conectar();PreparedStatement ps=c.prepareStatement(sql)){ps.setInt(1,p.getIdProducto());ps.setString(2,tipo);try(ResultSet rs=ps.executeQuery()){while(rs.next())cmbOrigen.addItem(new OperacionOrigenItem(rs.getInt("id_operacion"),rs.getDate("fecha").toLocalDate(),rs.getBigDecimal("precio_unitario"),rs.getBigDecimal("cantidad"),rs.getBigDecimal("total"),rs.getString("concepto"),rs.getString("forma_pago")));}}
        catch(Exception e){JOptionPane.showMessageDialog(this,"No se pudieron cargar las operaciones de origen: "+e.getMessage(),"Devolución",JOptionPane.ERROR_MESSAGE);}
        actualizarUnidades(); actualizarFormaOrigen();
    }


    private void actualizarFormaOrigen(){
        if(cmbForma==null)return;
        OperacionOrigenItem o=(OperacionOrigenItem)cmbOrigen.getSelectedItem();
        if(o==null){cmbForma.setSelectedItem("EFECTIVO");return;}
        if("CREDITO".equals(o.forma)||"EFECTIVO".equals(o.forma)||"BANCO".equals(o.forma))cmbForma.setSelectedItem(o.forma);
    }

    private void actualizarUnidades(){
        if(lblUnidades==null)return; try{OperacionOrigenItem o=(OperacionOrigenItem)cmbOrigen.getSelectedItem();BigDecimal m=leerMonto(false);if(o==null||m==null||o.precio.signum()<=0){lblUnidades.setText("0");return;}lblUnidades.setText(m.divide(o.precio,6,RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());}catch(Exception e){lblUnidades.setText("0");}
    }

    private void guardar(){
        try{
            Producto p=(Producto)cmbProducto.getSelectedItem(); OperacionOrigenItem o=(OperacionOrigenItem)cmbOrigen.getSelectedItem(); if(p==null)throw new IllegalArgumentException("Selecciona un producto.");if(o==null)throw new IllegalArgumentException("Selecciona la operación original que se está devolviendo.");
            BigDecimal monto=leerMonto(true);LocalDate fecha=((Date)spFecha.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();String forma=o.forma;String concepto=txtConcepto.getText().trim();BigDecimal unidades=monto.divide(o.precio,6,RoundingMode.HALF_UP);
            String efecto = esCompra()
                    ? "EFECTO EN KARDEX: SALIDA de inventario"
                    : "EFECTO EN KARDEX: ENTRADA de inventario";
            int ok=JOptionPane.showConfirmDialog(this,"Producto: "+p.getNombre()+"\nOrigen: "+o+"\nMonto: $"+monto+"\nUnidades: "+unidades.stripTrailingZeros().toPlainString()+"\n"+efecto+"\n\n¿Registrar devolución?","Confirmar",JOptionPane.YES_NO_OPTION);if(ok!=JOptionPane.YES_OPTION)return;
            if(esCompra())compraService.registrar(fecha,p.getIdProducto(),o.id,monto,forma,concepto);else ventaService.registrar(fecha,p.getIdProducto(),o.id,monto,forma,concepto);
            JOptionPane.showMessageDialog(this,"Devolución registrada. Kardex PEPS, lote y existencia fueron recalculados.","Correcto",JOptionPane.INFORMATION_MESSAGE);dispose();
        }catch(Exception e){JOptionPane.showMessageDialog(this,mensaje(e),"No se pudo registrar",JOptionPane.ERROR_MESSAGE);}
    }

    private BigDecimal leerMonto(boolean obligatorio){String s=txtMonto.getText().trim().replace("$","").replace(" ","");if(s.isBlank()){if(obligatorio)throw new IllegalArgumentException("Ingresa el monto de la devolución.");return null;}s=s.replace(',','.');try{BigDecimal v=new BigDecimal(s).setScale(2,RoundingMode.HALF_UP);if(v.signum()<=0)throw new IllegalArgumentException("El monto debe ser mayor que cero.");return v;}catch(NumberFormatException e){throw new IllegalArgumentException("El monto no es válido.");}}
    private String mensaje(Throwable e){String m="Ocurrió un error.";for(Throwable x=e;x!=null;x=x.getCause())if(x.getMessage()!=null&&!x.getMessage().isBlank())m=x.getMessage();return m;}
    private static class OperacionOrigenItem{final int id;final LocalDate fecha;final BigDecimal precio,cantidad,total;final String concepto,forma;OperacionOrigenItem(int i,LocalDate f,BigDecimal p,BigDecimal c,BigDecimal t,String x,String forma){id=i;fecha=f;precio=p;cantidad=c;total=t;concepto=x;this.forma=forma;}public String toString(){return "#"+id+" - "+fecha+" - "+(concepto==null?"Operación":concepto)+" - "+forma+" - unit. $"+precio;}}
}
