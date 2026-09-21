package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.Conexion;
import java.sql.*;
import java.time.LocalDate;

public class ReinicioSistemaService {

    public String reiniciarDatosPrueba() {
        if(!SesionUsuario.esAdministrador()) throw new SecurityException("Solo el administrador puede reiniciar los datos de prueba.");
        Connection c=null;
        try{
            c=Conexion.conectar();
            if(c==null) throw new SQLException("No se pudo conectar con la base de datos.");
            c.setAutoCommit(false);

            asegurarTablaApertura(c);

            Integer periodoConservar=obtenerPeriodoConservar(c);
            if(periodoConservar==null){
                periodoConservar=crearPeriodoBase(c);
            }

            int consumos=ejecutar(c,"DELETE FROM detalle_consumo_peps");
            int capas=ejecutar(c,"DELETE FROM capas_peps");
            int kardex=ejecutar(c,"DELETE FROM kardex");
            int detalles=ejecutar(c,"DELETE FROM detalle_asientos");
            int asientos=ejecutar(c,"DELETE FROM asientos_contables");
            int operaciones=ejecutar(c,"DELETE FROM operaciones");
            ejecutar(c,"DELETE FROM inventario_apertura_lotes");

            // El reinicio de pruebas deja un único período listo para reutilizar.
            try(PreparedStatement ps=c.prepareStatement("DELETE FROM periodos_contables WHERE id_periodo<>?")){
                ps.setInt(1,periodoConservar); ps.executeUpdate();
            }
            try(PreparedStatement ps=c.prepareStatement("UPDATE periodos_contables SET estado='ABIERTO' WHERE id_periodo=?")){
                ps.setInt(1,periodoConservar); ps.executeUpdate();
            }

            ejecutar(c,"UPDATE productos SET costo_inicial=0, valor_inventario_inicial=0, existencia_inicial=0, existencia_actual=0");

            // Se eliminan eventos de movimientos ya inexistentes; se conservan login/usuarios y configuración.
            try(PreparedStatement ps=c.prepareStatement("DELETE FROM auditoria WHERE entidad IN ('ASIENTO','OPERACION','PERIODO_CONTABLE')")){ ps.executeUpdate(); }

            c.commit();
            return """
                    Sistema de prueba reiniciado correctamente.

                    Operaciones eliminadas: %d
                    Asientos eliminados: %d
                    Detalles eliminados: %d
                    Movimientos Kardex eliminados: %d
                    Capas PEPS eliminadas: %d
                    Consumos PEPS eliminados: %d

                    Se conservaron empresa, usuarios, roles, catálogo, configuración y productos.
                    Se dejó un único período ABIERTO y el inventario inicial/actual quedó en cero.
                    """.formatted(operaciones,asientos,detalles,kardex,capas,consumos);
        }catch(Exception e){
            if(c!=null) try{c.rollback();}catch(SQLException ignored){}
            throw new RuntimeException("No se pudo limpiar el sistema: "+mensaje(e),e);
        }finally{
            if(c!=null) try{c.setAutoCommit(true);c.close();}catch(SQLException ignored){}
        }
    }

    private Integer obtenerPeriodoConservar(Connection c)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement("SELECT id_periodo FROM periodos_contables ORDER BY (estado='ABIERTO') DESC,id_periodo DESC LIMIT 1");ResultSet rs=ps.executeQuery()){
            return rs.next()?rs.getInt(1):null;
        }
    }

    private int crearPeriodoBase(Connection c)throws SQLException{
        int empresa;
        try(PreparedStatement ps=c.prepareStatement("SELECT id_empresa FROM empresa ORDER BY id_empresa LIMIT 1");ResultSet rs=ps.executeQuery()){
            if(!rs.next()) throw new SQLException("No existe una empresa configurada."); empresa=rs.getInt(1);
        }
        int y=LocalDate.now().getYear();
        try(PreparedStatement ps=c.prepareStatement("INSERT INTO periodos_contables(id_empresa,nombre,fecha_inicio,fecha_fin,estado) VALUES(?,?,?,?, 'ABIERTO')",Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,empresa); ps.setString(2,"Periodo Contable "+y); ps.setDate(3,Date.valueOf(LocalDate.of(y,1,1))); ps.setDate(4,Date.valueOf(LocalDate.of(y,12,31))); ps.executeUpdate();
            try(ResultSet rs=ps.getGeneratedKeys()){ if(rs.next()) return rs.getInt(1); }
        }
        throw new SQLException("No se pudo crear el período base.");
    }

    private void asegurarTablaApertura(Connection c)throws SQLException{
        try(Statement st=c.createStatement()){
            st.executeUpdate("""
                CREATE TABLE IF NOT EXISTS inventario_apertura_lotes (
                    id_apertura_lote BIGINT AUTO_INCREMENT PRIMARY KEY,
                    id_periodo INT NOT NULL,
                    id_producto INT NOT NULL,
                    fecha_origen DATE NOT NULL,
                    cantidad DECIMAL(18,6) NOT NULL,
                    costo_unitario DECIMAL(18,6) NOT NULL,
                    orden_lote INT NOT NULL,
                    INDEX idx_apertura_lotes_periodo_producto(id_periodo,id_producto,orden_lote),
                    CONSTRAINT fk_apertura_lotes_periodo FOREIGN KEY(id_periodo) REFERENCES periodos_contables(id_periodo) ON DELETE CASCADE,
                    CONSTRAINT fk_apertura_lotes_producto FOREIGN KEY(id_producto) REFERENCES productos(id_producto)
                ) ENGINE=InnoDB
                """);
        }
    }

    private int ejecutar(Connection c,String sql)throws SQLException{
        try(PreparedStatement ps=c.prepareStatement(sql)){ return ps.executeUpdate(); }
    }

    private String mensaje(Throwable e){ Throwable x=e; String m="Error desconocido"; while(x!=null){if(x.getMessage()!=null&&!x.getMessage().isBlank())m=x.getMessage();x=x.getCause();}return m; }
}
