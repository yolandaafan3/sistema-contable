package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.CapaPeps;
import com.mycompany.sistemacontable.modelo.MovimientoKardex;
import com.mycompany.sistemacontable.modelo.Producto;
import com.mycompany.sistemacontable.servicio.KardexService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class PanelKardexPeps extends JPanel {

    private final KardexService service = new KardexService();
    private final JTextField txtFiltro = new JTextField();
    private final JLabel lblResumen = new JLabel();
    private final JLabel lblResumenLotes = new JLabel();
    private DefaultTableModel modelo;
    private DefaultTableModel modeloLotes;

    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Color FONDO = new Color(245, 247, 250);
    private final Color TEXTO = new Color(30, 41, 59);
    private final Color SECUNDARIO = new Color(100, 116, 139);
    private final Color BORDE = new Color(226, 232, 240);
    private final Color PRIMARIO = new Color(37, 99, 235);

    public PanelKardexPeps() {
        setLayout(new BorderLayout(0, 18));
        setBackground(FONDO);
        setBorder(BorderFactory.createEmptyBorder(28, 30, 30, 30));
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearTabs(), BorderLayout.CENTER);
        cargarKardex();
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Kardex PEPS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titulo.setForeground(TEXTO);
        JLabel subtitulo = new JLabel("Movimientos y lotes PEPS por producto. Cada compra crea o alimenta una capa con su costo propio.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(SECUNDARIO);
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JPanel controles = new JPanel();
        controles.setOpaque(false);
        JLabel lblBuscar = new JLabel("Filtrar producto:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBuscar.setForeground(TEXTO);
        txtFiltro.setPreferredSize(new Dimension(240, 38));
        txtFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtFiltro.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { cargarKardex(); }
        });
        JButton actualizar = new JButton("Actualizar");
        actualizar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actualizar.setForeground(Color.WHITE);
        actualizar.setBackground(PRIMARIO);
        actualizar.setFocusPainted(false);
        actualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actualizar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        actualizar.addActionListener(e -> cargarKardex());
        controles.add(lblBuscar);
        controles.add(txtFiltro);
        controles.add(actualizar);
        panel.add(textos, BorderLayout.WEST);
        panel.add(controles, BorderLayout.EAST);
        return panel;
    }

    private JTabbedPane crearTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("Movimientos Kardex", crearPanelMovimientos());
        tabs.addTab("Lotes / Capas PEPS", crearPanelLotes());
        return tabs;
    }

    private JPanel crearPanelMovimientos() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);
        modelo = new DefaultTableModel(new String[]{
                "Producto","Fecha","Asiento","Concepto","Entrada","Salida","Existencia",
                "Costo Unit.","Costo PEPS","Saldo Deudor","Saldo Acreedor","Saldo"
        },0){ @Override public boolean isCellEditable(int r,int c){ return false; }};
        JTable tabla = tablaBase(modelo);
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i=4;i<=11;i++) tabla.getColumnModel().getColumn(i).setCellRenderer(derecha);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        p.add(scroll, BorderLayout.CENTER);
        p.add(crearPie(lblResumen), BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelLotes() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);
        modeloLotes = new DefaultTableModel(new String[]{
                "Producto","Lote","Fecha","Kardex entrada","Costo unitario","Cantidad original","Cantidad disponible","Consumido","Valor disponible","Estado"
        },0){ @Override public boolean isCellEditable(int r,int c){ return false; }};
        JTable tabla = tablaBase(modeloLotes);
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i=4;i<=8;i++) tabla.getColumnModel().getColumn(i).setCellRenderer(derecha);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        p.add(scroll, BorderLayout.CENTER);
        p.add(crearPie(lblResumenLotes), BorderLayout.SOUTH);
        return p;
    }

    private JTable tablaBase(DefaultTableModel m) {
        JTable tabla = new JTable(m);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(29);
        tabla.setGridColor(BORDE);
        tabla.setShowVerticalLines(false);
        tabla.setAutoCreateRowSorter(true);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 38));
        return tabla;
    }

    private JPanel crearPie(JLabel label) {
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(Color.WHITE);
        pie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXTO);
        pie.add(label, BorderLayout.WEST);
        return pie;
    }

    public final void cargarKardex() {
        if (modelo == null || modeloLotes == null) return;
        try {
            modelo.setRowCount(0);
            modeloLotes.setRowCount(0);
            List<Producto> productos = service.listarProductosActivos();
            String filtro = txtFiltro.getText() == null ? "" : txtFiltro.getText().trim().toLowerCase();
            List<FilaKardex> filas = new ArrayList<>();
            int productosVisibles = 0;
            int lotes = 0;
            BigDecimal saldoTotal = BigDecimal.ZERO;
            BigDecimal valorLotes = BigDecimal.ZERO;

            for (Producto producto : productos) {
                String codigo = producto.getCodigo() == null ? "" : producto.getCodigo().toLowerCase();
                String nombre = producto.getNombre() == null ? "" : producto.getNombre().toLowerCase();
                if (!filtro.isBlank() && !codigo.contains(filtro) && !nombre.contains(filtro)) continue;
                productosVisibles++;
                service.recalcular(producto.getIdProducto());
                for (MovimientoKardex mov : service.obtenerMovimientos(producto.getIdProducto())) filas.add(new FilaKardex(producto,mov));
                BigDecimal saldo = service.obtenerSaldoPeps(producto.getIdProducto());
                if (saldo != null) saldoTotal = saldoTotal.add(saldo);

                for (CapaPeps c : service.obtenerCapas(producto.getIdProducto())) {
                    lotes++;
                    BigDecimal original = nz(c.getCantidadOriginal());
                    BigDecimal disponible = nz(c.getCantidadDisponible());
                    BigDecimal consumido = original.subtract(disponible);
                    BigDecimal valor = disponible.multiply(nz(c.getCostoUnitario())).setScale(2,RoundingMode.HALF_UP);
                    valorLotes = valorLotes.add(valor);
                    modeloLotes.addRow(new Object[]{
                            producto.getCodigo()+" - "+producto.getNombre(),
                            "Lote #"+c.getIdCapa(),
                            c.getFecha()==null?"":c.getFecha().format(formatoFecha),
                            c.getIdKardexEntrada(),
                            dinero(c.getCostoUnitario()), numero(original), numero(disponible), numero(consumido), dinero(valor),
                            disponible.compareTo(BigDecimal.ZERO)==0 ? "AGOTADO" : (disponible.compareTo(original)<0 ? "PARCIAL" : "DISPONIBLE")
                    });
                }
            }

            filas.sort(Comparator.comparing((FilaKardex f)->f.movimiento.getFecha(), Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(f->f.producto.getCodigo()==null?"":f.producto.getCodigo()));
            for (FilaKardex fila: filas) {
                MovimientoKardex m=fila.movimiento; Producto p=fila.producto;
                modelo.addRow(new Object[]{
                        p.getCodigo()+" - "+p.getNombre(), m.getFecha()==null?"":m.getFecha().format(formatoFecha),
                        m.getNumeroAsiento()==null?"—":m.getNumeroAsiento(), m.getConcepto(), numero(m.getUnidadesEntrada()),
                        numero(m.getUnidadesSalida()), numero(m.getUnidadesExistencia()), dinero(m.getCostoUnitario()),
                        dinero(m.getCostoPeps()), dinero(m.getSaldoDeudor()), dinero(m.getSaldoAcreedor()), dinero(m.getSaldo())
                });
            }
            lblResumen.setText("Productos mostrados: "+productosVisibles+"   |   Movimientos: "+filas.size()+"   |   Valor total PEPS: "+dinero(saldoTotal));
            lblResumenLotes.setText("Lotes mostrados: "+lotes+"   |   Valor disponible en lotes: "+dinero(valorLotes)+"   |   Las ventas consumen primero los lotes más antiguos.");
        } catch (Exception e) {
            lblResumen.setText("No se pudo cargar el Kardex: "+obtenerMensajeError(e));
            lblResumenLotes.setText("No se pudieron cargar los lotes: "+obtenerMensajeError(e));
        }
    }

    private BigDecimal nz(BigDecimal v){ return v==null?BigDecimal.ZERO:v; }
    private String numero(BigDecimal valor){ return nz(valor).setScale(6,RoundingMode.HALF_UP).stripTrailingZeros().toPlainString(); }
    private String dinero(BigDecimal valor){ return "$"+nz(valor).setScale(2,RoundingMode.HALF_UP).toPlainString(); }
    private String obtenerMensajeError(Throwable error){ Throwable a=error; String m="Error desconocido."; while(a!=null){if(a.getMessage()!=null&&!a.getMessage().isBlank())m=a.getMessage();a=a.getCause();}return m; }
    private static class FilaKardex { final Producto producto; final MovimientoKardex movimiento; FilaKardex(Producto p,MovimientoKardex m){producto=p;movimiento=m;} }
}
