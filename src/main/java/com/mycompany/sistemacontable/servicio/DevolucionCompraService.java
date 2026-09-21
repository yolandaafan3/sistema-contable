package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.*;
import com.mycompany.sistemacontable.modelo.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;

public class DevolucionCompraService {
    private final ProductoDAO productoDAO=new ProductoDAO();
    private final CuentaDAO cuentaDAO=new CuentaDAO();
    private final OperacionDAO operacionDAO=new OperacionDAO();
    private final AsientoDAO asientoDAO=new AsientoDAO();
    private final KardexDAO kardexDAO=new KardexDAO();
    private final PeriodoService periodoService=new PeriodoService();
    private final CalculoIVAService ivaService=new CalculoIVAService();
    private final RecalculoKardexService recalculo=new RecalculoKardexService();

    public ResultadoDevolucionCompra registrar(LocalDate fecha,int idProducto,int idOperacionOrigen,
            BigDecimal montoDevolucion,String formaReintegro,String concepto){
        validar(fecha,idProducto,idOperacionOrigen,montoDevolucion,formaReintegro);
        periodoService.validarFecha(fecha);
        PeriodoContable periodo=periodoService.obtenerPeriodoActivo();
        Connection c=null;
        try{
            c=Conexion.conectar(); c.setAutoCommit(false);
            Origen o=cargarOrigen(c,idOperacionOrigen,"COMPRA",idProducto);
            validarOrigenMismoPeriodo(c,idOperacionOrigen,periodo.getIdPeriodo());
            String formaAplicada = o.formaPago;
            if(fecha.isBefore(o.fecha)) throw new IllegalArgumentException("La devolución no puede ser anterior a la compra de origen.");
            BigDecimal monto=montoDevolucion.setScale(2,RoundingMode.HALF_UP);
            BigDecimal cantidad=monto.divide(o.precioUnitario,6,RoundingMode.HALF_UP);
            Producto productoActual=productoDAO.buscarPorIdParaActualizar(idProducto,c);
            BigDecimal existenciaDisponible=(productoActual==null||productoActual.getExistenciaActual()==null)
                    ? BigDecimal.ZERO : productoActual.getExistenciaActual();
            if(cantidad.compareTo(existenciaDisponible)>0)
                throw new IllegalArgumentException("No se puede devolver "
                        +cantidad.stripTrailingZeros().toPlainString()+" unidades al proveedor porque solo hay "
                        +existenciaDisponible.stripTrailingZeros().toPlainString()+" unidades en existencia.");
            ResultadoIVA riva=ivaService.calcular(monto);
            BigDecimal subtotal=riva.getSubtotal().setScale(2,RoundingMode.HALF_UP);
            BigDecimal iva=riva.getIva().setScale(2,RoundingMode.HALF_UP);
            BigDecimal total=riva.getTotal().setScale(2,RoundingMode.HALF_UP);

            Cuenta dev=cuentaDAO.buscarPorRol("DEVOLUCION_COMPRAS");
            Cuenta ivaCred=cuentaDAO.buscarPorRol("IVA_CREDITO");
            Cuenta contra=cuentaDAO.buscarPorCodigo(o.codigoContrapartida);
            validarCuenta(dev,"Devolución sobre Compras"); validarCuenta(contra,"contrapartida");
            if(iva.signum()>0) validarCuenta(ivaCred,"IVA Crédito Fiscal");

            Operacion op=new Operacion(); op.setIdPeriodo(periodo.getIdPeriodo()); op.setFecha(fecha);
            op.setTipoOperacion("DEVOLUCION_COMPRA"); op.setConcepto(concepto(concepto,"Devolución sobre compra"));
            op.setIdProducto(idProducto); op.setIdOperacionOrigen(idOperacionOrigen); op.setCantidad(cantidad);
            op.setPrecioUnitario(o.precioUnitario); op.setSubtotal(subtotal); op.setIva(iva); op.setTotal(total); op.setFormaPago(formaAplicada);
            int idOp=operacionDAO.insertar(op,c);

            int n=asientoDAO.obtenerSiguienteNumero(periodo.getIdPeriodo(),c);
            AsientoContable a=new AsientoContable(); a.setIdPeriodo(periodo.getIdPeriodo()); a.setIdOperacion(idOp); a.setNumeroAsiento(n);
            a.setFecha(fecha); a.setConcepto(op.getConcepto()); a.setTipoAsiento("AUTOMATICO"); a.setEstado("CONTABILIZADO");
            int idA=asientoDAO.insertarAsiento(a,c);
            detalle(idA,contra,descripcionContra(formaAplicada),total,BigDecimal.ZERO,c);
            detalle(idA,dev,"Devolución sobre compras",BigDecimal.ZERO,subtotal,c);
            if(iva.signum()>0) detalle(idA,ivaCred,"Disminución IVA Crédito Fiscal",BigDecimal.ZERO,iva,c);

            recalculo.recalcularProducto(idProducto,c);
            MovimientoKardex mov=kardexDAO.buscarMovimientoPorAsiento(idA,c);
            Producto pa=productoDAO.buscarPorId(idProducto,c);
            c.commit();
            return new ResultadoDevolucionCompra(idOp,idA,n,subtotal,iva,total,
                    mov.getSaldoAcreedor().setScale(2,RoundingMode.HALF_UP),
                    pa.getExistenciaActual().setScale(2,RoundingMode.HALF_UP),mov.getSaldo().setScale(2,RoundingMode.HALF_UP));
        }catch(Exception e){ if(c!=null)try{c.rollback();}catch(Exception ignored){} throw new RuntimeException("No se pudo registrar la devolución sobre compra: "+mensaje(e),e); }
        finally{ if(c!=null)try{c.setAutoCommit(true);c.close();}catch(Exception ignored){} }
    }

    private void validarOrigenMismoPeriodo(Connection c,int idOperacion,int idPeriodo)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("SELECT id_periodo FROM operaciones WHERE id_operacion=?")){
            ps.setInt(1,idOperacion);try(ResultSet rs=ps.executeQuery()){
                if(!rs.next())throw new IllegalArgumentException("No existe la operación de origen seleccionada.");
                if(rs.getInt(1)!=idPeriodo)throw new IllegalArgumentException("La devolución debe corresponder a una operación del mismo período contable. Los períodos cerrados son históricos y no pueden modificarse desde el período actual.");
            }
        }
    }
    private Origen cargarOrigen(Connection c,int id,String tipo,int producto)throws SQLException{
        LocalDate fecha; BigDecimal precio; String formaGuardada;
        try(PreparedStatement ps=c.prepareStatement("SELECT fecha,tipo_operacion,id_producto,precio_unitario,forma_pago FROM operaciones WHERE id_operacion=?")){
            ps.setInt(1,id); try(ResultSet rs=ps.executeQuery()){
                if(!rs.next()) throw new IllegalArgumentException("No existe la operación de origen seleccionada.");
                if(!tipo.equals(rs.getString("tipo_operacion")) || rs.getInt("id_producto")!=producto) throw new IllegalArgumentException("La operación de origen no corresponde al producto y tipo de devolución.");
                precio=rs.getBigDecimal("precio_unitario"); if(precio==null||precio.signum()<=0) throw new IllegalStateException("La operación de origen no tiene costo unitario válido.");
                fecha=rs.getDate("fecha").toLocalDate(); formaGuardada=rs.getString("forma_pago");
            }
        }
        String codigoContra=null;
        String sql="SELECT cc.codigo FROM asientos_contables a JOIN detalle_asientos d ON d.id_asiento=a.id_asiento JOIN catalogo_cuentas cc ON cc.id_cuenta=d.id_cuenta WHERE a.id_operacion=? AND d.haber>0 AND cc.codigo IN ('1.1.01.01','1.1.01.02','2.1.01.01') ORDER BY d.id_detalle LIMIT 1";
        try(PreparedStatement ps=c.prepareStatement(sql)){ ps.setInt(1,id); try(ResultSet rs=ps.executeQuery()){if(rs.next())codigoContra=rs.getString(1);} }
        String forma=formaDesdeCodigo(codigoContra);
        if(forma==null){ validarFormaOrigen(formaGuardada); forma=formaGuardada; codigoContra=codigoDesdeForma(forma); }
        return new Origen(fecha,precio.setScale(2,RoundingMode.HALF_UP),forma,codigoContra);
    }
    private record Origen(LocalDate fecha,BigDecimal precioUnitario,String formaPago,String codigoContrapartida){}
    private String formaDesdeCodigo(String codigo){ if("1.1.01.01".equals(codigo))return "EFECTIVO"; if("1.1.01.02".equals(codigo))return "BANCO"; if("2.1.01.01".equals(codigo))return "CREDITO"; return null; }
    private String codigoDesdeForma(String forma){ return switch(forma){case "EFECTIVO"->"1.1.01.01";case "BANCO"->"1.1.01.02";case "CREDITO"->"2.1.01.01";default->null;}; }
    private void validarFormaOrigen(String forma){
        if(!"CREDITO".equals(forma) && !"EFECTIVO".equals(forma) && !"BANCO".equals(forma))
            throw new IllegalStateException("La compra original usa una forma de pago no compatible con devolución automática: "+forma+".");
    }
    private void validar(LocalDate f,int p,int o,BigDecimal m,String forma){ if(f==null||p<=0||o<=0||m==null||m.signum()<=0)throw new IllegalArgumentException("Completa producto, compra de origen, fecha y monto de devolución."); if(!forma.equals("CREDITO")&&!forma.equals("EFECTIVO")&&!forma.equals("BANCO"))throw new IllegalArgumentException("Forma de reintegro no válida."); }
    private Cuenta contrapartida(String f){ return switch(f){case "CREDITO"->cuentaDAO.buscarPorCodigo("2.1.01.01");case "EFECTIVO"->cuentaDAO.buscarPorCodigo("1.1.01.01");case "BANCO"->cuentaDAO.buscarPorCodigo("1.1.01.02");default->null;}; }
    private String descripcionContra(String f){ return switch(f){case "CREDITO"->"Disminución de cuenta con proveedor";case "EFECTIVO"->"Reintegro recibido en efectivo";case "BANCO"->"Reintegro recibido en banco";default->"Devolución sobre compra";}; }
    private void validarCuenta(Cuenta c,String n){ if(c==null||!c.isActivo()||!c.isPermiteMovimiento())throw new IllegalStateException("La cuenta "+n+" no está disponible para movimientos."); }
    private void detalle(int a,Cuenta cuenta,String desc,BigDecimal debe,BigDecimal haber,Connection c)throws SQLException{ DetalleAsiento d=new DetalleAsiento();d.setIdAsiento(a);d.setIdCuenta(cuenta.getIdCuenta());d.setDescripcion(desc);d.setDebe(debe.setScale(2,RoundingMode.HALF_UP));d.setHaber(haber.setScale(2,RoundingMode.HALF_UP));asientoDAO.insertarDetalle(d,c); }
    private String concepto(String s,String def){return s==null||s.isBlank()?def:s.trim();}
    private String mensaje(Throwable e){String m="Error";for(Throwable x=e;x!=null;x=x.getCause())if(x.getMessage()!=null&&!x.getMessage().isBlank())m=x.getMessage();return m;}
}
