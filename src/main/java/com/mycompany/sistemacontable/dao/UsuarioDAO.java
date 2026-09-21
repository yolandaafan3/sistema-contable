package com.mycompany.sistemacontable.dao;

import com.mycompany.sistemacontable.Conexion;
import com.mycompany.sistemacontable.modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario buscarPorUsuario(String usuario) {
        String sql = """
            SELECT u.id_usuario, u.usuario, u.nombre_completo, u.password_hash,
                   u.activo, r.codigo AS rol_codigo, r.nombre AS rol_nombre
            FROM usuarios u
            INNER JOIN roles r ON r.id_rol = u.id_rol
            WHERE LOWER(u.usuario) = LOWER(?)
            LIMIT 1
            """;
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return construir(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar el usuario: " + e.getMessage(), e);
        }
        return null;
    }

    public String obtenerHash(String usuario) {
        String sql = "SELECT password_hash FROM usuarios WHERE LOWER(usuario)=LOWER(?) LIMIT 1";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString(1); }
        } catch (SQLException e) { throw new RuntimeException("No se pudo validar la contraseña.", e); }
        return null;
    }

    public void registrarAcceso(int idUsuario) {
        String sql = "UPDATE usuarios SET ultimo_acceso=CURRENT_TIMESTAMP WHERE id_usuario=?";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario); ps.executeUpdate();
        } catch (SQLException e) { /* no bloquea el login */ }
    }

    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = """
            SELECT u.id_usuario, u.usuario, u.nombre_completo, u.activo,
                   r.codigo AS rol_codigo, r.nombre AS rol_nombre
            FROM usuarios u INNER JOIN roles r ON r.id_rol=u.id_rol
            ORDER BY u.usuario
            """;
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(construir(rs));
        } catch (SQLException e) { throw new RuntimeException("No se pudieron listar usuarios: " + e.getMessage(), e); }
        return lista;
    }

    public void crear(String usuario, String nombre, String hash, String rolCodigo) {
        String sql = """
            INSERT INTO usuarios(usuario,nombre_completo,password_hash,id_rol,activo)
            SELECT ?,?,?,id_rol,TRUE FROM roles WHERE codigo=?
            """;
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario); ps.setString(2, nombre); ps.setString(3, hash); ps.setString(4, rolCodigo);
            if (ps.executeUpdate() != 1) throw new SQLException("Rol no encontrado.");
        } catch (SQLException e) { throw new RuntimeException("No se pudo crear el usuario: " + e.getMessage(), e); }
    }

    public void cambiarActivo(int idUsuario, boolean activo) {
        String sql = "UPDATE usuarios SET activo=? WHERE id_usuario=?";
        try (Connection cn = Conexion.conectar(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, activo); ps.setInt(2, idUsuario); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("No se pudo actualizar el usuario: " + e.getMessage(), e); }
    }

    private Usuario construir(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id_usuario"), rs.getString("usuario"), rs.getString("nombre_completo"),
                rs.getString("rol_codigo"), rs.getString("rol_nombre"), rs.getBoolean("activo"));
    }
}
