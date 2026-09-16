package com.mycompany.sistemacontable.servicio;

import com.mycompany.sistemacontable.modelo.Usuario;

public final class SesionUsuario {
    private static Usuario usuarioActual;

    private SesionUsuario() {}

    public static void iniciar(Usuario usuario) { usuarioActual = usuario; }
    public static void cerrar() { usuarioActual = null; }
    public static Usuario getUsuarioActual() { return usuarioActual; }
    public static boolean haySesion() { return usuarioActual != null; }
    public static boolean esAdministrador() { return haySesion() && usuarioActual.esAdministrador(); }
    public static boolean esContable() { return haySesion() && usuarioActual.esContable(); }
    public static boolean esConsulta() { return haySesion() && usuarioActual.esConsulta(); }
    public static boolean puedeRegistrarOperaciones() { return esAdministrador() || esContable(); }
    public static boolean puedeAdministrarSistema() { return esAdministrador(); }
    public static boolean puedeEditarAsientos() { return esAdministrador(); }
}
