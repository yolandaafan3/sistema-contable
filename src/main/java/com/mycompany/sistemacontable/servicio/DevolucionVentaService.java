package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.*;
import com.mycompany.sistemacontable.modelo.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DevolucionVentaService {
    private final ProductoDAO productoDAO=new ProductoDAO(); private final CuentaDAO cuentaDAO=new CuentaDAO();
    private final OperacionDAO operacionDAO=new OperacionDAO(); private final AsientoDAO asientoDAO=new AsientoDAO(); private final KardexDAO kardexDAO=new KardexDAO();
    private final PeriodoService periodoService=new PeriodoService(); private final CalculoIVAService ivaService=new CalculoIVAService(); private final RecalculoKardexService recalculo=new RecalculoKardexService();

    public ResultadoDevolucionVenta registrar(LocalDate fecha,int idProducto,int idOperacionOrigen,BigDecimal montoDevolucion,String formaDevolucion,String concepto){
        validar(fecha,idProducto,idOperacionOrigen,montoDevolucion,formaDevolucion); periodoService.validarFecha(fecha); PeriodoContable periodo=periodoService.obtenerPeriodoActivo(); Connection c=null;
        try{
            c=Conexion.conectar(); c.setAutoCommit(false); Origen o=cargarOrigen(c,idOperacionOrigen,"VENTA",idProducto);
            validarOrigenMismoPeriodo(c,idOperacionOrigen,periodo.getIdPeriodo());
            if(fecha.isBefore(o.fecha)) throw new IllegalArgumentException("La devolución no puede ser anterior a la venta de origen.");
            BigDecimal monto=montoDevolucion.setScale(2,RoundingMode.HALF_UP); BigDecimal cantidad=monto.divide(o.precioUnitario,6,RoundingMode.HALF_UP);
            ResultadoIVA r=ivaService.calcular(monto); BigDecimal subtotal=r.getSubtotal().setScale(2,RoundingMode.HALF_UP); BigDecimal iva=r.getIva().setScale(2,RoundingMode.HALF_UP); BigDecimal total=r.getTotal().setScale(2,RoundingMode.HALF_UP);
            validarSaldoDevolvible(c,idOperacionOrigen,o.totalOriginal,total,"VENTA");

            Cuenta dev=cuentaDAO.buscarPorRol("DEVOLUCION_VENTAS"); Cuenta ivaDeb=cuentaDAO.buscarPorRol("IVA_DEBITO");
            validarCuenta(dev,"Devolución sobre Ventas"); if(iva.signum()>0)validarCuenta(ivaDeb,"IVA Débito Fiscal");

            List<ParteAplicada> partes=distribuir(total,o.contrapartidas);
            for(ParteAplicada p:partes){validarCuenta(cuentaDAO.buscarPorCodigo(p.codigo),"contrapartida "+p.codigo);}

            Operacion op=new Operacion();op.setIdPeriodo(periodo.getIdPeriodo());op.setFecha(fecha);op.setTipoOperacion("DEVOLUCION_VENTA");op.setConcepto(concepto(concepto,"Devolución sobre venta"));op.setIdProducto(idProducto);op.setIdOperacionOrigen(idOperacionOrigen);op.setCantidad(cantidad);op.setPrecioUnitario(o.precioUnitario);op.setSubtotal(subtotal);op.setIva(iva);op.setTotal(total);op.setFormaPago(o.formaPago);int idOp=operacionDAO.insertar(op,c);
            int n=asientoDAO.obtenerSiguienteNumero(periodo.getIdPeriodo(),c);AsientoContable a=new AsientoContable();a.setIdPeriodo(periodo.getIdPeriodo());a.setIdOperacion(idOp);a.setNumeroAsiento(n);a.setFecha(fecha);a.setConcepto(op.getConcepto());a.setTipoAsiento("AUTOMATICO");a.setEstado("CONTABILIZADO");int idA=asientoDAO.insertarAsiento(a,c);
            detalle(idA,dev,"Devolución sobre ventas",subtotal,BigDecimal.ZERO,c);if(iva.signum()>0)detalle(idA,ivaDeb,"Disminución IVA Débito Fiscal",iva,BigDecimal.ZERO,c);
            for(ParteAplicada p:partes){Cuenta contra=cuentaDAO.buscarPorCodigo(p.codigo);detalle(idA,contra,descripcionContra(p.codigo),BigDecimal.ZERO,p.monto,c);}

            recalculo.recalcularProducto(idProducto,c);
            MovimientoKardex mov=kardexDAO.buscarMovimientoPorAsiento(idA,c);
            if (mov == null || mov.getUnidadesEntrada() == null || mov.getUnidadesEntrada().compareTo(cantidad) != 0 || (mov.getUnidadesSalida() != null && mov.getUnidadesSalida().compareTo(BigDecimal.ZERO) != 0)) {
                throw new IllegalStateException("La devolución sobre venta no fue registrada como entrada de inventario. La operación fue cancelada para proteger el Kardex.");
            }
            Producto pa=productoDAO.buscarPorId(idProducto,c);c.commit();
            return new ResultadoDevolucionVenta(idOp,idA,n,subtotal,iva,total,mov.getSaldoDeudor().setScale(2,RoundingMode.HALF_UP),pa.getExistenciaActual().setScale(2,RoundingMode.HALF_UP),mov.getSaldo().setScale(2,RoundingMode.HALF_UP));
        }catch(Exception e){if(c!=null)try{c.rollback();}catch(Exception ignored){}throw new RuntimeException("No se pudo registrar la devolución sobre venta: "+mensaje(e),e);}finally{if(c!=null)try{c.setAutoCommit(true);c.close();}catch(Exception ignored){}}
    }

    private void validarOrigenMismoPeriodo(Connection c,int idOperacion,int idPeriodo)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("SELECT id_periodo FROM operaciones WHERE id_operacion=?")){ps.setInt(1,idOperacion);try(ResultSet rs=ps.executeQuery()){if(!rs.next())throw new IllegalArgumentException("No existe la operación de origen seleccionada.");if(rs.getInt(1)!=idPeriodo)throw new IllegalArgumentException("La devolución debe corresponder a una operación del mismo período contable. Los períodos cerrados son históricos y no pueden modificarse desde el período actual.");}}
    }

    private Origen cargarOrigen(Connection c,int id,String tipo,int producto)throws SQLException{
        LocalDate fecha; BigDecimal precio,totalOriginal; String formaGuardada;
        try(PreparedStatement ps=c.prepareStatement("SELECT fecha,tipo_operacion,id_producto,precio_unitario,total,forma_pago FROM operaciones WHERE id_operacion=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){
                if(!rs.next())throw new IllegalArgumentException("No existe la operación de origen seleccionada.");
                if(!tipo.equals(rs.getString("tipo_operacion"))||rs.getInt("id_producto")!=producto)throw new IllegalArgumentException("La operación de origen no corresponde al producto y tipo de devolución.");
                precio=rs.getBigDecimal("precio_unitario");if(precio==null||precio.signum()<=0)throw new IllegalStateException("La operación de origen no tiene precio unitario válido.");
                fecha=rs.getDate("fecha").toLocalDate();totalOriginal=rs.getBigDecimal("total");formaGuardada=rs.getString("forma_pago");
            }
        }
        List<ParteOrigen> partes=new ArrayList<>();
        String sql="SELECT cc.codigo,SUM(d.debe) monto FROM asientos_contables a JOIN detalle_asientos d ON d.id_asiento=a.id_asiento JOIN catalogo_cuentas cc ON cc.id_cuenta=d.id_cuenta WHERE a.id_operacion=? AND d.debe>0 AND cc.codigo IN ('1.1.01.01','1.1.01.02','1.1.02.01') GROUP BY cc.codigo ORDER BY cc.codigo";
        try(PreparedStatement ps=c.prepareStatement(sql)){ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){while(rs.next()){BigDecimal m=rs.getBigDecimal("monto");if(m!=null&&m.signum()>0)partes.add(new ParteOrigen(rs.getString("codigo"),m.setScale(2,RoundingMode.HALF_UP)));}}}
        if(partes.isEmpty()){
            validarFormaOrigen(formaGuardada);String codigo=codigoDesdeForma(formaGuardada);if(codigo==null)throw new IllegalStateException("No se pudo determinar la contrapartida de la venta original.");partes.add(new ParteOrigen(codigo,totalOriginal.setScale(2,RoundingMode.HALF_UP)));
        }
        String forma=partes.size()>1?"MIXTO":formaDesdeCodigo(partes.get(0).codigo);
        if(forma==null)forma=formaGuardada;
        return new Origen(fecha,precio.setScale(2,RoundingMode.HALF_UP),totalOriginal.setScale(2,RoundingMode.HALF_UP),forma,partes);
    }

    private void validarSaldoDevolvible(Connection c,int origen,BigDecimal totalOriginal,BigDecimal nueva,String tipo)throws SQLException{
        String tipoDev="VENTA".equals(tipo)?"DEVOLUCION_VENTA":"DEVOLUCION_COMPRA";
        BigDecimal devuelto=BigDecimal.ZERO;
        try(PreparedStatement ps=c.prepareStatement("SELECT COALESCE(SUM(total),0) FROM operaciones WHERE id_operacion_origen=? AND tipo_operacion=?")){ps.setInt(1,origen);ps.setString(2,tipoDev);try(ResultSet rs=ps.executeQuery()){if(rs.next())devuelto=rs.getBigDecimal(1);}}
        BigDecimal disponible=totalOriginal.subtract(devuelto==null?BigDecimal.ZERO:devuelto).setScale(2,RoundingMode.HALF_UP);
        if(nueva.compareTo(disponible)>0)throw new IllegalArgumentException("La devolución supera el saldo disponible de la venta original. Disponible para devolver: $"+disponible.toPlainString()+".");
    }

    private List<ParteAplicada> distribuir(BigDecimal total,List<ParteOrigen> origen){
        BigDecimal base=origen.stream().map(ParteOrigen::monto).reduce(BigDecimal.ZERO,BigDecimal::add);if(base.signum()<=0)throw new IllegalStateException("La venta original no tiene distribución de cobro válida.");
        List<ParteAplicada> out=new ArrayList<>();BigDecimal acumulado=BigDecimal.ZERO;
        for(int i=0;i<origen.size();i++){ParteOrigen p=origen.get(i);BigDecimal monto=(i==origen.size()-1)?total.subtract(acumulado):total.multiply(p.monto).divide(base,10,RoundingMode.HALF_UP).setScale(2,RoundingMode.HALF_UP);if(monto.signum()>0){out.add(new ParteAplicada(p.codigo,monto));acumulado=acumulado.add(monto);}}
        return out;
    }

    private record ParteOrigen(String codigo,BigDecimal monto){}
    private record ParteAplicada(String codigo,BigDecimal monto){}
    private record Origen(LocalDate fecha,BigDecimal precioUnitario,BigDecimal totalOriginal,String formaPago,List<ParteOrigen> contrapartidas){}
    private String formaDesdeCodigo(String codigo){if("1.1.01.01".equals(codigo))return "EFECTIVO";if("1.1.01.02".equals(codigo))return "BANCO";if("1.1.02.01".equals(codigo))return "CREDITO";return null;}
    private String codigoDesdeForma(String forma){return switch(forma){case "EFECTIVO"->"1.1.01.01";case "BANCO"->"1.1.01.02";case "CREDITO"->"1.1.02.01";default->null;};}
    private void validarFormaOrigen(String forma){if(!"CREDITO".equals(forma)&&!"EFECTIVO".equals(forma)&&!"BANCO".equals(forma)&&!"MIXTO".equals(forma))throw new IllegalStateException("La venta original usa una forma de cobro no compatible con devolución automática: "+forma+".");}
    private void validar(LocalDate f,int p,int o,BigDecimal m,String forma){if(f==null||p<=0||o<=0||m==null||m.signum()<=0)throw new IllegalArgumentException("Completa producto, venta de origen, fecha y monto de devolución.");if(forma!=null&&!forma.isBlank()&&!forma.equals("CREDITO")&&!forma.equals("EFECTIVO")&&!forma.equals("BANCO")&&!forma.equals("MIXTO"))throw new IllegalArgumentException("Forma de devolución no válida.");}
    private String descripcionContra(String codigo){return switch(codigo){case "1.1.02.01"->"Disminución de cuenta por cobrar al cliente";case "1.1.01.01"->"Reintegro al cliente en efectivo";case "1.1.01.02"->"Reintegro al cliente por banco";default->"Devolución sobre venta";};}
    private void validarCuenta(Cuenta c,String n){if(c==null||!c.isActivo()||!c.isPermiteMovimiento())throw new IllegalStateException("La cuenta "+n+" no está disponible para movimientos.");}
    private void detalle(int a,Cuenta cuenta,String desc,BigDecimal debe,BigDecimal haber,Connection c)throws SQLException{DetalleAsiento d=new DetalleAsiento();d.setIdAsiento(a);d.setIdCuenta(cuenta.getIdCuenta());d.setDescripcion(desc);d.setDebe(debe.setScale(2,RoundingMode.HALF_UP));d.setHaber(haber.setScale(2,RoundingMode.HALF_UP));asientoDAO.insertarDetalle(d,c);}
    private String concepto(String s,String def){return s==null||s.isBlank()?def:s.trim();}private String mensaje(Throwable e){String m="Error";for(Throwable x=e;x!=null;x=x.getCause())if(x.getMessage()!=null&&!x.getMessage().isBlank())m=x.getMessage();return m;}
}
