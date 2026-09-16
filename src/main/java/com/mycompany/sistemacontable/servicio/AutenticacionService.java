package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.dao.UsuarioDAO;
import com.mycompany.sistemacontable.modelo.Usuario;

public class AutenticacionService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario iniciarSesion(String usuario, char[] password) {
        if (usuario == null || usuario.isBlank() || password == null || password.length == 0)
            throw new IllegalArgumentException("Ingresa usuario y contraseña.");
        Usuario encontrado = usuarioDAO.buscarPorUsuario(usuario.trim());
        if (encontrado == null || !encontrado.isActivo())
            throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        String hash = usuarioDAO.obtenerHash(usuario.trim());
        if (!PasswordUtil.verificar(new String(password), hash))
            throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        usuarioDAO.registrarAcceso(encontrado.getIdUsuario());
        SesionUsuario.iniciar(encontrado);
        return encontrado;
    }
}
