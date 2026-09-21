package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.PeriodoContable;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class PeriodoContableDAO {
    public PeriodoContable obtenerPeriodoAbierto() {
        try(Connection c=Conexion.conectar()) { return obtenerPeriodoAbierto(c); }
        catch(SQLException e){ throw new RuntimeException("No se pudo consultar el período abierto: "+e.getMessage(),e); }
    }

    public PeriodoContable obtenerPeriodoAbierto(Connection c) throws SQLException {
        String sql="SELECT id_periodo,id_empresa,nombre,fecha_inicio,fecha_fin,estado FROM periodos_contables WHERE estado='ABIERTO' ORDER BY id_periodo DESC LIMIT 1";
        try(PreparedStatement ps=c.prepareStatement(sql);ResultSet rs=ps.executeQuery()){ return rs.next()?map(rs):null; }
    }

    public List<PeriodoContable> listarTodos(){
        List<PeriodoContable> lista=new ArrayList<>();
        String sql="SELECT id_periodo,id_empresa,nombre,fecha_inicio,fecha_fin,estado FROM periodos_contables ORDER BY fecha_inicio DESC,id_periodo DESC";
        try(Connection c=Conexion.conectar();PreparedStatement ps=c.prepareStatement(sql);ResultSet rs=ps.executeQuery()){
            while(rs.next()) lista.add(map(rs));
        }catch(SQLException e){throw new RuntimeException("No se pudieron consultar los períodos: "+e.getMessage(),e);} return lista;
    }

    public int crear(int idEmpresa,String nombre,LocalDate inicio,LocalDate fin,Connection c)throws SQLException{
        String sql="INSERT INTO periodos_contables(id_empresa,nombre,fecha_inicio,fecha_fin,estado) VALUES(?,?,?,?,'ABIERTO')";
        try(PreparedStatement ps=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,idEmpresa);ps.setString(2,nombre);ps.setDate(3,java.sql.Date.valueOf(inicio));ps.setDate(4,java.sql.Date.valueOf(fin));ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()){if(rs.next())return rs.getInt(1);} throw new SQLException("No se obtuvo el ID del nuevo período.");
        }
    }

    public void cerrar(int idPeriodo,Connection c)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("UPDATE periodos_contables SET estado='CERRADO' WHERE id_periodo=? AND estado='ABIERTO'")){
            ps.setInt(1,idPeriodo); if(ps.executeUpdate()!=1)throw new SQLException("El período ya no está abierto.");
        }
    }

    private PeriodoContable map(ResultSet rs)throws SQLException{
        PeriodoContable x=new PeriodoContable();x.setIdPeriodo(rs.getInt("id_periodo"));x.setIdEmpresa(rs.getInt("id_empresa"));x.setNombre(rs.getString("nombre"));
        x.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());x.setFechaFin(rs.getDate("fecha_fin").toLocalDate());x.setEstado(rs.getString("estado"));return x;
    }
}
