package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.dao.UsuarioDAO;
import com.mycompany.sistemacontable.modelo.Usuario;
import com.mycompany.sistemacontable.servicio.PasswordUtil;
import com.mycompany.sistemacontable.servicio.SesionUsuario;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PanelUsuarios extends JPanel {
    private final UsuarioDAO dao=new UsuarioDAO();
    private final DefaultTableModel model=new DefaultTableModel(new Object[]{"ID","Usuario","Nombre","Rol","Estado"},0){public boolean isCellEditable(int r,int c){return false;}};
    private final JTable tabla=new JTable(model);

    public PanelUsuarios(){
        setLayout(new BorderLayout(0,18)); setBackground(new Color(245,247,250)); setBorder(BorderFactory.createEmptyBorder(28,30,30,30));
        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false); JLabel t=new JLabel("Usuarios y Roles"); t.setFont(new Font("Segoe UI",Font.BOLD,28)); top.add(t,BorderLayout.WEST);
        JPanel b=new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); b.setOpaque(false); JButton nuevo=new JButton("Nuevo usuario"); JButton estado=new JButton("Activar / Desactivar"); JButton act=new JButton("Actualizar");
        nuevo.addActionListener(e->nuevo()); estado.addActionListener(e->cambiarEstado()); act.addActionListener(e->cargar()); b.add(nuevo); b.add(estado); b.add(act); top.add(b,BorderLayout.EAST); add(top,BorderLayout.NORTH);
        tabla.setRowHeight(30); add(new JScrollPane(tabla),BorderLayout.CENTER); cargar();
    }
    public final void cargar(){ model.setRowCount(0); for(Usuario u:dao.listar()) model.addRow(new Object[]{u.getIdUsuario(),u.getUsuario(),u.getNombreCompleto(),u.getRolNombre(),u.isActivo()?"Activo":"Inactivo"}); }
    private void nuevo(){
        JTextField user=new JTextField(), nombre=new JTextField(); JPasswordField pass=new JPasswordField(); JComboBox<String> rol=new JComboBox<>(new String[]{"ADMINISTRADOR","CONTABLE","CONSULTA"});
        JPanel p=new JPanel(new GridLayout(0,1,5,5)); p.add(new JLabel("Usuario")); p.add(user); p.add(new JLabel("Nombre completo")); p.add(nombre); p.add(new JLabel("Contraseña")); p.add(pass); p.add(new JLabel("Rol")); p.add(rol);
        if(JOptionPane.showConfirmDialog(this,p,"Nuevo usuario",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){
            if(user.getText().isBlank()||nombre.getText().isBlank()||pass.getPassword().length<6){JOptionPane.showMessageDialog(this,"Completa los datos y usa una contraseña de al menos 6 caracteres.");return;}
            dao.crear(user.getText().trim(),nombre.getText().trim(),PasswordUtil.generarHash(new String(pass.getPassword())),rol.getSelectedItem().toString()); cargar();
        }
    }
    private void cambiarEstado(){
        int r=tabla.getSelectedRow(); if(r<0){JOptionPane.showMessageDialog(this,"Selecciona un usuario.");return;} int id=(Integer)tabla.getValueAt(r,0); boolean activo="Activo".equals(tabla.getValueAt(r,4));
        if(SesionUsuario.getUsuarioActual()!=null && id==SesionUsuario.getUsuarioActual().getIdUsuario()){JOptionPane.showMessageDialog(this,"No puedes desactivar tu propia sesión.");return;}
        dao.cambiarActivo(id,!activo); cargar();
    }
}
