package com.mycompany.sistemacontable.vista;

import com.mycompany.sistemacontable.modelo.Usuario;
import com.mycompany.sistemacontable.servicio.AutenticacionService;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class VentanaLogin extends JFrame {
    private final JTextField txtUsuario = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final AutenticacionService auth = new AutenticacionService();

    public VentanaLogin() {
        setTitle("Sistema Contable - Iniciar sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 560);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Paleta.FONDO);

        add(crearPanelMarca(), BorderLayout.WEST);
        add(crearPanelFormulario(), BorderLayout.CENTER);
    }

    /** Panel izquierdo oscuro con la identidad de marca del sistema. */
    private JPanel crearPanelMarca() {
        JPanel marca = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, Paleta.AZUL_OSCURO, getWidth(), getHeight(), Paleta.AZUL_OSCURO_2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        marca.setOpaque(false);
        marca.setPreferredSize(new Dimension(360, 0));
        marca.setLayout(new BoxLayout(marca, BoxLayout.Y_AXIS));
        marca.setBorder(new EmptyBorder(48, 42, 42, 42));

        JLabel titulo = new JLabel("<html>SISTEMA<br>CONTABLE</html>");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font(Paleta.FUENTE, Font.BOLD, 30));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Gestión financiera y contable");
        subtitulo.setForeground(Paleta.TEXTO_SIDEBAR_SUAVE);
        subtitulo.setFont(new Font(Paleta.FUENTE, Font.PLAIN, 13));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        marca.add(titulo);
        marca.add(Box.createVerticalStrut(8));
        marca.add(subtitulo);
        marca.add(Box.createVerticalGlue());

        marca.add(crearPunto("Libro Diario y Mayorización automáticos"));
        marca.add(Box.createVerticalStrut(10));
        marca.add(crearPunto("Balance de Comprobación y Balance General"));
        marca.add(Box.createVerticalStrut(10));
        marca.add(crearPunto("Kardex PEPS y Estado de Resultados"));

        return marca;
    }

    private JPanel crearPunto(String texto) {
        JPanel fila = new JPanel();
        fila.setOpaque(false);
        fila.setLayout(new BoxLayout(fila, BoxLayout.X_AXIS));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(280, 40));

        JLabel bola = new JLabel("●");
        bola.setForeground(Paleta.ACENTO_CLARO);
        bola.setFont(new Font(Paleta.FUENTE, Font.PLAIN, 9));

        JLabel lbl = new JLabel("  " + texto);
        lbl.setForeground(Paleta.TEXTO_SIDEBAR);
        lbl.setFont(new Font(Paleta.FUENTE, Font.PLAIN, 12));

        fila.add(bola);
        fila.add(lbl);
        return fila;
    }

    /** Panel derecho blanco con el formulario de acceso. */
    private JPanel crearPanelFormulario() {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(Paleta.FONDO);

        RoundedPanel form = new RoundedPanel(18);
        form.setBackground(Paleta.BLANCO);
        form.setBorder(new EmptyBorder(40, 44, 40, 44));
        form.setLayout(new GridBagLayout());
        form.setPreferredSize(new Dimension(400, 420));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL; g.insets = new Insets(0, 0, 6, 0);

        JLabel title = new JLabel("Bienvenido de nuevo");
        title.setFont(new Font(Paleta.FUENTE, Font.BOLD, 24));
        title.setForeground(Paleta.TEXTO);
        form.add(title, g);

        g.gridy++; g.insets = new Insets(0, 0, 28, 0);
        JLabel sub = new JLabel("Ingresa tus credenciales para continuar.");
        sub.setForeground(Paleta.TEXTO_SUAVE);
        sub.setFont(new Font(Paleta.FUENTE, Font.PLAIN, 13));
        form.add(sub, g);

        g.gridy++; g.insets = new Insets(0, 0, 6, 0);
        form.add(label("USUARIO"), g);

        g.gridy++; g.insets = new Insets(0, 0, 18, 0);
        estilizarCampo(txtUsuario);
        form.add(txtUsuario, g);

        g.gridy++; g.insets = new Insets(0, 0, 6, 0);
        form.add(label("CONTRASEÑA"), g);

        g.gridy++; g.insets = new Insets(0, 0, 26, 0);
        estilizarCampo(txtPassword);
        form.add(txtPassword, g);

        RoundedButton btn = new RoundedButton("Ingresar", 10);
        btn.setColores(Paleta.ACENTO_OSCURO, Paleta.ACENTO);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(Paleta.FUENTE, Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(0, 44));
        btn.addActionListener(e -> login());

        g.gridy++; g.insets = new Insets(0, 0, 0, 0);
        form.add(btn, g);
        getRootPane().setDefaultButton(btn);

        wrap.add(form);
        return wrap;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBorder(new RoundedFieldBorder(Paleta.BORDE, 10));
        campo.setFont(new Font(Paleta.FUENTE, Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(0, 40));
    }

    private JLabel label(String s) {
        JLabel l = new JLabel(s);
        l.setFont(new Font(Paleta.FUENTE, Font.BOLD, 11));
        l.setForeground(Paleta.TEXTO_SUAVE);
        return l;
    }

    private void login() {
        try {
            Usuario u = auth.iniciarSesion(txtUsuario.getText(), txtPassword.getPassword());
            dispose();
            new VentanaPrincipal(u).setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo iniciar sesión", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }
}