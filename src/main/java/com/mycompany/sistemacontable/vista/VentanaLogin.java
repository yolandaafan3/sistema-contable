package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.Usuario;
import com.mycompany.sistemacontable.servicio.AutenticacionService;
import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class VentanaLogin extends JFrame {
    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final AutenticacionService auth = new AutenticacionService();

    public VentanaLogin() {
        setTitle("Sistema Contable - Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(470, 470);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245,247,250));

        JPanel cab = new JPanel(); cab.setBackground(new Color(18,32,52));
        cab.setBorder(BorderFactory.createEmptyBorder(28,28,28,28));
        JLabel t = new JLabel("SISTEMA CONTABLE"); t.setForeground(Color.WHITE); t.setFont(new Font("Segoe UI",Font.BOLD,24)); cab.add(t);
        add(cab,BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(226,232,240)), BorderFactory.createEmptyBorder(28,34,28,34)));
        GridBagConstraints g=new GridBagConstraints(); g.gridx=0; g.gridy=0; g.weightx=1; g.fill=GridBagConstraints.HORIZONTAL; g.insets=new Insets(0,0,8,0);
        JLabel title=new JLabel("Iniciar sesión"); title.setFont(new Font("Segoe UI",Font.BOLD,24)); form.add(title,g);
        g.gridy++; g.insets=new Insets(0,0,22,0); JLabel sub=new JLabel("Ingresa tus credenciales para continuar."); sub.setForeground(new Color(100,116,139)); form.add(sub,g);
        g.gridy++; g.insets=new Insets(0,0,6,0); form.add(label("Usuario"),g);
        g.gridy++; g.insets=new Insets(0,0,16,0); txtUsuario.setPreferredSize(new Dimension(0,38)); form.add(txtUsuario,g);
        g.gridy++; g.insets=new Insets(0,0,6,0); form.add(label("Contraseña"),g);
        g.gridy++; g.insets=new Insets(0,0,22,0); txtPassword.setPreferredSize(new Dimension(0,38)); form.add(txtPassword,g);
        JButton btn=new JButton("Ingresar"); btn.setUI(new BasicButtonUI()); btn.setBackground(new Color(37,99,235)); btn.setForeground(Color.WHITE); btn.setFont(new Font("Segoe UI",Font.BOLD,14)); btn.setBorder(BorderFactory.createEmptyBorder(11,18,11,18)); btn.setFocusPainted(false); btn.addActionListener(e->login());
        g.gridy++; g.insets=new Insets(0,0,0,0); form.add(btn,g);
        getRootPane().setDefaultButton(btn);
        JPanel wrap=new JPanel(new GridBagLayout()); wrap.setBackground(new Color(245,247,250)); wrap.setBorder(BorderFactory.createEmptyBorder(25,45,25,45)); wrap.add(form); add(wrap,BorderLayout.CENTER);
    }

    private JLabel label(String s){ JLabel l=new JLabel(s); l.setFont(new Font("Segoe UI",Font.BOLD,13)); return l; }
    private void login(){
        try{
            Usuario u=auth.iniciarSesion(txtUsuario.getText(),txtPassword.getPassword());
            dispose(); new VentanaPrincipal(u).setVisible(true);
        }catch(Exception e){ JOptionPane.showMessageDialog(this,e.getMessage(),"No se pudo iniciar sesión",JOptionPane.ERROR_MESSAGE); txtPassword.setText(""); }
    }
}
