package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.AsientoDAO;
import com.mycompany.sistemacontable.dao.CuentaDAO;
import com.mycompany.sistemacontable.modelo.AsientoContable;
import com.mycompany.sistemacontable.modelo.Cuenta;
import com.mycompany.sistemacontable.modelo.DetalleAsiento;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.util.List;

public class GestionAsientoService {
    private final AsientoDAO asientoDAO = new AsientoDAO();
    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final PeriodoService periodoService = new PeriodoService();

    public AsientoContable obtenerAsiento(int idAsiento) { return asientoDAO.buscarPorId(idAsiento); }
    public List<DetalleAsiento> obtenerDetalles(int idAsiento) { return asientoDAO.listarDetalles(idAsiento); }

    public void actualizarAsientoManual(int idAsiento, java.time.LocalDate fecha, String concepto, List<DetalleAsiento> detalles) {
        if (!SesionUsuario.esAdministrador()) throw new SecurityException("Solo el Administrador puede modificar asientos.");
        AsientoContable asiento = asientoDAO.buscarPorId(idAsiento);
        if (asiento == null) throw new IllegalArgumentException("El asiento no existe.");
        if (!"MANUAL".equals(asiento.getTipoAsiento()) || asiento.getIdOperacion() != null)
            throw new IllegalArgumentException("Los asientos automáticos son de solo lectura. Corrige la operación que los originó.");
        if (fecha == null) throw new IllegalArgumentException("La fecha es obligatoria.");
        periodoService.validarFecha(fecha);
        if (concepto == null || concepto.isBlank()) throw new IllegalArgumentException("El concepto es obligatorio.");
        if (detalles == null || detalles.size() < 2) throw new IllegalArgumentException("Se requieren al menos dos líneas.");

        BigDecimal debe = BigDecimal.ZERO, haber = BigDecimal.ZERO;
        for (DetalleAsiento d : detalles) {
            Cuenta c = cuentaDAO.buscarPorId(d.getIdCuenta());
            if (c == null || !c.isActivo() || !c.isPermiteMovimiento()) throw new IllegalArgumentException("Cuenta inválida en el asiento.");
            if ("INVENTARIO".equals(c.getRolReporte()) || "1.1.03".equals(c.getCodigo()))
                throw new IllegalArgumentException("Inventario no se modifica desde un asiento manual; utiliza las operaciones de inventario.");
            BigDecimal dv = norm(d.getDebe()), hv = norm(d.getHaber());
            if (dv.signum() < 0 || hv.signum() < 0 || (dv.signum() > 0 && hv.signum() > 0) || (dv.signum()==0 && hv.signum()==0))
                throw new IllegalArgumentException("Cada línea debe tener un valor únicamente en Debe o Haber.");
            d.setDebe(dv); d.setHaber(hv); debe=debe.add(dv); haber=haber.add(hv);
        }
        debe=norm(debe); haber=norm(haber);
        if (debe.signum() <= 0 || debe.compareTo(haber)!=0) throw new IllegalArgumentException("El asiento no cumple la partida doble. Debe: $"+debe+" | Haber: $"+haber);

        Connection cn=null;
        try {
            cn=Conexion.conectar(); if(cn==null) throw new IllegalStateException("No se pudo conectar con MySQL.");
            cn.setAutoCommit(false);
            asientoDAO.actualizarCabecera(idAsiento, fecha, concepto.trim(), cn);
            asientoDAO.eliminarDetalles(idAsiento, cn);
            for (DetalleAsiento d: detalles) { d.setIdAsiento(idAsiento); asientoDAO.insertarDetalle(d, cn); }
            cn.commit();
        } catch(Exception e) {
            if(cn!=null) try{cn.rollback();}catch(Exception ignored){}
            throw new RuntimeException("No se pudo actualizar el asiento: "+e.getMessage(), e);
        } finally { if(cn!=null) try{cn.setAutoCommit(true); cn.close();}catch(Exception ignored){} }
    }

    private BigDecimal norm(BigDecimal v){ return (v==null?BigDecimal.ZERO:v).setScale(2, RoundingMode.HALF_UP); }
}
