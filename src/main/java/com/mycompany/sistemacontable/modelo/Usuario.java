package com.mycompany.sistemacontable.modelo;

public class Usuario {
    private int idUsuario;
    private String usuario;
    private String nombreCompleto;
    private String rolCodigo;
    private String rolNombre;
    private boolean activo;

    public Usuario() {}

    public Usuario(int idUsuario, String usuario, String nombreCompleto,
                   String rolCodigo, String rolNombre, boolean activo) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
        this.rolCodigo = rolCodigo;
        this.rolNombre = rolNombre;
        this.activo = activo;
    }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getRolCodigo() { return rolCodigo; }
    public void setRolCodigo(String rolCodigo) { this.rolCodigo = rolCodigo; }
    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean esAdministrador() { return "ADMINISTRADOR".equals(rolCodigo); }
    public boolean esContable() { return "CONTABLE".equals(rolCodigo); }
    public boolean esConsulta() { return "CONSULTA".equals(rolCodigo); }
}
