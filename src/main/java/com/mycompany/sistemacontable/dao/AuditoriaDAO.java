package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.Usuario;
import com.mycompany.sistemacontable.servicio.SesionUsuario;
import java.sql.*;
import java.util.*;

public class AuditoriaDAO {
    public void asegurarTabla(Connection c) throws SQLException {
        try (Statement st=c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS auditoria (id_auditoria BIGINT AUTO_INCREMENT PRIMARY KEY,id_usuario INT NULL,entidad VARCHAR(60) NOT NULL,id_entidad BIGINT NOT NULL,accion ENUM('CREO','MODIFICO','ANULO','APROBO','ELIMINO') NOT NULL,datos_anteriores TEXT NULL,datos_nuevos TEXT NULL,fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,INDEX idx_auditoria_fecha(fecha),INDEX idx_auditoria_entidad(entidad,id_entidad),CONSTRAINT fk_auditoria_usuario FOREIGN KEY(id_usuario) REFERENCES usuarios(id_usuario)) ENGINE=InnoDB");
        }
    }

    public void registrarSiFalta(Connection c,String entidad,long id,String accion,String antes,String despues) throws SQLException {
        asegurarTabla(c);
        // Si existe un trigger de auditoría, éste ya insertó el registro dentro de la misma transacción.
        try (PreparedStatement q=c.prepareStatement("SELECT 1 FROM auditoria WHERE entidad=? AND id_entidad=? AND accion=? AND fecha>=DATE_SUB(NOW(),INTERVAL 3 SECOND) LIMIT 1")) {
            q.setString(1,entidad); q.setLong(2,id); q.setString(3,accion);
            try(ResultSet r=q.executeQuery()){ if(r.next()) return; }
        }
        Usuario u=SesionUsuario.getUsuarioActual();
        try(PreparedStatement p=c.prepareStatement("INSERT INTO auditoria(id_usuario,entidad,id_entidad,accion,datos_anteriores,datos_nuevos) VALUES(?,?,?,?,?,?)")){
            if(u==null)p.setNull(1,Types.INTEGER);else p.setInt(1,u.getIdUsuario());
            p.setString(2,entidad);p.setLong(3,id);p.setString(4,accion);p.setString(5,antes);p.setString(6,despues);p.executeUpdate();
        }
    }

    public List<Object[]> listar(){
        List<Object[]> x=new ArrayList<>();
        try(Connection c=Conexion.conectar()){
            asegurarTabla(c);
            String q="SELECT au.id_auditoria,au.fecha,COALESCE(u.nombre_completo,'Sistema'),au.entidad,au.id_entidad,au.accion,au.datos_anteriores,au.datos_nuevos FROM auditoria au LEFT JOIN usuarios u ON u.id_usuario=au.id_usuario ORDER BY au.id_auditoria DESC LIMIT 1000";
            try(PreparedStatement p=c.prepareStatement(q);ResultSet r=p.executeQuery()){
                while(r.next())x.add(new Object[]{r.getLong(1),r.getTimestamp(2),r.getString(3),r.getString(4),r.getLong(5),r.getString(6),r.getString(7),r.getString(8)});
            }
        }catch(SQLException e){throw new RuntimeException("No se pudo consultar la bitácora: "+e.getMessage(),e);}return x;
    }
}
