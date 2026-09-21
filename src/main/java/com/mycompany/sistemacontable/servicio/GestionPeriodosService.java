package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.dao.AuditoriaDAO;
import com.mycompany.sistemacontable.dao.PeriodoContableDAO;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class GestionPeriodosService {
    private final PeriodoContableDAO dao = new PeriodoContableDAO();
    private final AuditoriaDAO auditoria = new AuditoriaDAO();

    public List<PeriodoContable> listar(){ return dao.listarTodos(); }
    public PeriodoContable abierto(){ return dao.obtenerPeriodoAbierto(); }

    public void cerrarActual(){
        if(!SesionUsuario.esAdministrador()) throw new SecurityException("Solo el administrador puede cerrar períodos contables.");
        Connection c=null;
        try{
            c=Conexion.conectar(); c.setAutoCommit(false);
            PeriodoContable x=dao.obtenerPeriodoAbierto(c);
            if(x==null) throw new IllegalStateException("No existe un período abierto.");
            validarCierre(x.getIdPeriodo(), c);
            dao.cerrar(x.getIdPeriodo(),c);
            auditoria.registrarSiFalta(c,"PERIODO_CONTABLE",x.getIdPeriodo(),"MODIFICO","Estado: ABIERTO","Estado: CERRADO | "+x.getNombre());
            c.commit();
        }catch(Exception e){
            if(c!=null) try{c.rollback();}catch(SQLException ignored){}
            throw new RuntimeException(mensaje(e),e);
        }finally{ if(c!=null) try{c.close();}catch(SQLException ignored){} }
    }

    public int iniciar(String nombre,LocalDate inicio,LocalDate fin){
        if(!SesionUsuario.esAdministrador()) throw new SecurityException("Solo el administrador puede iniciar períodos contables.");
        validarDatosNuevoPeriodo(nombre,inicio,fin);
        Connection c=null;
        try{
            c=Conexion.conectar(); c.setAutoCommit(false);
            if(dao.obtenerPeriodoAbierto(c)!=null) throw new IllegalStateException("Primero debes cerrar el período actualmente abierto.");
            int empresa=obtenerEmpresa(c);
            validarTraslape(inicio,fin,c);
            validarCatalogoContable(c);
            // Todo período iniciado sin otro período abierto comienza en cero.
            reiniciarEstadoOperativoNuevoPeriodo(c);
            int id=dao.crear(empresa,nombre.trim(),inicio,fin,c);
            auditoria.registrarSiFalta(c,"PERIODO_CONTABLE",id,"CREO",null,
                    "Período iniciado desde cero: "+nombre+" | "+inicio+" a "+fin+" | sin arrastre de saldos ni inventario");
            c.commit(); return id;
        }catch(Exception e){
            if(c!=null) try{c.rollback();}catch(SQLException ignored){}
            throw new RuntimeException(mensaje(e),e);
        }finally{ if(c!=null) try{c.close();}catch(SQLException ignored){} }
    }

    /**
     * Cierra el período activo y crea el siguiente completamente en cero.
     *
     * Regla funcional de ContaProMax:
     * - El período cerrado conserva intactos todos sus asientos, saldos y Kardex
     *   para consulta y exportación histórica.
     * - El nuevo período NO recibe asiento de apertura ni arrastre automático
     *   de Activo, Pasivo, Patrimonio, IVA, inventario o resultados acumulados.
     * - El estado operativo de productos se reinicia a cero para que el Kardex
     *   del nuevo período también comience sin existencias.
     *
     * Esta es una decisión funcional del sistema: se simula que las cuentas
     * fueron liquidadas al cierre, aunque no se generen asientos de liquidación.
     */
    public int cerrarYCrearNuevo(String nombre, LocalDate inicio, LocalDate fin){
        if(!SesionUsuario.esAdministrador()) {
            throw new SecurityException("Solo el administrador puede cerrar y crear períodos contables.");
        }
        validarDatosNuevoPeriodo(nombre,inicio,fin);

        Connection c=null;
        try{
            c=Conexion.conectar();
            c.setAutoCommit(false);

            PeriodoContable anterior=dao.obtenerPeriodoAbierto(c);
            if(anterior==null) {
                throw new IllegalStateException("No existe un período abierto para cerrar.");
            }

            if(!inicio.isAfter(anterior.getFechaFin())){
                throw new IllegalArgumentException(
                        "El nuevo período debe comenzar después del cierre del período actual ("
                        + anterior.getFechaFin() + ")."
                );
            }

            validarCierre(anterior.getIdPeriodo(),c);
            validarTraslape(inicio,fin,c);
            validarCatalogoContable(c);

            // 1) Cerrar el período anterior sin alterar ninguno de sus registros.
            dao.cerrar(anterior.getIdPeriodo(),c);
            auditoria.registrarSiFalta(
                    c,
                    "PERIODO_CONTABLE",
                    anterior.getIdPeriodo(),
                    "MODIFICO",
                    "Estado: ABIERTO",
                    "Estado: CERRADO | " + anterior.getNombre()
            );

            // 2) Reiniciar únicamente el estado operativo que pertenece al
            //    período nuevo. Los movimientos históricos permanecen intactos.
            reiniciarEstadoOperativoNuevoPeriodo(c);

            // 3) Crear el nuevo período vacío. No se crea asiento de apertura.
            int idNuevo=dao.crear(
                    anterior.getIdEmpresa(),
                    nombre.trim(),
                    inicio,
                    fin,
                    c
            );

            auditoria.registrarSiFalta(
                    c,
                    "PERIODO_CONTABLE",
                    idNuevo,
                    "CREO",
                    null,
                    "Período creado desde cero: " + nombre
                    + " | " + inicio + " a " + fin
                    + " | período anterior cerrado=" + anterior.getIdPeriodo()
                    + " | sin arrastre de saldos ni inventario"
            );

            c.commit();
            return idNuevo;

        }catch(Exception e){
            if(c!=null) {
                try{ c.rollback(); }catch(SQLException ignored){}
            }
            throw new RuntimeException(mensaje(e),e);
        }finally{
            if(c!=null) {
                try{ c.close(); }catch(SQLException ignored){}
            }
        }
    }

    /**
     * Deja el estado operativo listo para un período nuevo vacío.
     * No elimina asientos, operaciones, Kardex ni capas PEPS históricas.
     */
    private void reiniciarEstadoOperativoNuevoPeriodo(Connection c) throws SQLException {
        try(PreparedStatement ps=c.prepareStatement(
                "UPDATE productos "
                + "SET existencia_inicial=0, existencia_actual=0, "
                + "costo_inicial=0, valor_inventario_inicial=0"
        )){
            ps.executeUpdate();
        }
    }

    private int obtenerEmpresa(Connection c)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("SELECT id_empresa FROM empresa ORDER BY id_empresa LIMIT 1");ResultSet rs=ps.executeQuery()){
            if(!rs.next()) throw new SQLException("No existe una empresa configurada."); return rs.getInt(1);
        }
    }

    private void validarDatosNuevoPeriodo(String nombre,LocalDate inicio,LocalDate fin){
        if(nombre==null||nombre.isBlank()) throw new IllegalArgumentException("Escribe el nombre del período.");
        if(inicio==null||fin==null||fin.isBefore(inicio)) throw new IllegalArgumentException("El rango de fechas del período no es válido.");
    }

    private void validarTraslape(LocalDate inicio,LocalDate fin,Connection c)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM periodos_contables WHERE NOT(fecha_fin<? OR fecha_inicio>?)")){
            ps.setDate(1,Date.valueOf(inicio)); ps.setDate(2,Date.valueOf(fin));
            try(ResultSet rs=ps.executeQuery()){rs.next(); if(rs.getInt(1)>0) throw new IllegalArgumentException("Las fechas se traslapan con otro período contable.");}
        }
    }

    private void validarCierre(int idPeriodo, Connection c) throws SQLException {
        try (PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM asientos_contables WHERE id_periodo=? AND estado='BORRADOR'")) {
            ps.setInt(1,idPeriodo); try(ResultSet rs=ps.executeQuery()){rs.next(); if(rs.getInt(1)>0) throw new IllegalStateException("No se puede cerrar el período: existen asientos en BORRADOR.");}
        }
        String q="SELECT COUNT(*) FROM (SELECT a.id_asiento FROM asientos_contables a JOIN detalle_asientos d ON d.id_asiento=a.id_asiento WHERE a.id_periodo=? AND a.estado='CONTABILIZADO' GROUP BY a.id_asiento HAVING ABS(SUM(d.debe)-SUM(d.haber))>0.009) x";
        try (PreparedStatement ps=c.prepareStatement(q)) {
            ps.setInt(1,idPeriodo); try(ResultSet rs=ps.executeQuery()){rs.next(); if(rs.getInt(1)>0) throw new IllegalStateException("No se puede cerrar el período: existen asientos descuadrados.");}
        }
    }

    private void validarCatalogoContable(Connection c) throws SQLException {
        String[] codigos={"1.1.01.01","1.1.01.02","1.1.02.01","1.1.03","1.1.04","2.1.01.01","2.1.01.02","3.1.01","4.1.01","4.1.02","5.1.01","5.1.02"};
        for(String codigo:codigos){
            try(PreparedStatement ps=c.prepareStatement("SELECT COUNT(*) FROM catalogo_cuentas WHERE codigo=? AND activo=TRUE AND permite_movimiento=TRUE")){
                ps.setString(1,codigo); try(ResultSet rs=ps.executeQuery()){rs.next(); if(rs.getInt(1)!=1) throw new IllegalStateException("Catálogo incompleto: falta o está inactiva la cuenta operativa "+codigo+".");}
            }
        }
    }

    private String mensaje(Throwable e){
        Throwable x=e; String m="Ocurrió un error al gestionar el período.";
        while(x!=null){ if(x.getMessage()!=null&&!x.getMessage().isBlank()) m=x.getMessage(); x=x.getCause(); }
        return m;
    }

}
