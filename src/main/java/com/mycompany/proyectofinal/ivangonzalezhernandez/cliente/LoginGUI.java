package com.mycompany.proyectofinal.ivangonzalezhernandez.cliente;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana de Login. Se comunica con el servidor para autenticar.
 */
public class LoginGUI extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginGUI() {
        setTitle("Sistema de Facturación - Login");
        setSize(420, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(34, 45, 65));
        setContentPane(panel);

        // Logo / Titulo
        JLabel lblTitulo = new JLabel("Sistema de Facturación", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(30, 20, 360, 35);
        panel.add(lblTitulo);

        JLabel lblSub = new JLabel("Ingrese sus credenciales", SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSub.setForeground(new Color(160, 175, 200));
        lblSub.setBounds(30, 55, 360, 20);
        panel.add(lblSub);

        // Campos
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setForeground(Color.WHITE);
        lblUser.setBounds(60, 95, 100, 25);
        panel.add(lblUser);

        txtUser = new JTextField();
        txtUser.setBounds(160, 95, 200, 28);
        panel.add(txtUser);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(60, 135, 100, 25);
        panel.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setBounds(160, 135, 200, 28);
        panel.add(txtPass);

        btnLogin = new JButton("INICIAR SESIÓN");
        btnLogin.setBounds(110, 185, 200, 35);
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.add(btnLogin);

        btnLogin.addActionListener(e -> login());
        txtPass.addActionListener(e -> login());

        setVisible(true);
    }

    private void login() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Conectando...");

        new Thread(() -> {
            try {
                Solicitud req = new Solicitud(Solicitud.LOGIN, new String[]{user, pass});
                Solicitud resp = ConexionServidor.getInstance().enviar(req);

                SwingUtilities.invokeLater(() -> {
                    if (resp.isExito()) {
                        Usuario u = (Usuario) resp.getRespuesta();
                        JOptionPane.showMessageDialog(this, "Bienvenido, " + u.getUsername() + " [" + u.getRol() + "]");
                        new MenuGUI(u);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, resp.getMensaje(), "Error", JOptionPane.ERROR_MESSAGE);
                        btnLogin.setEnabled(true);
                        btnLogin.setText("INICIAR SESIÓN");
                    }
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "No se pudo conectar al servidor.\nVerifique que el servidor esté en ejecución.",
                        "Error de Conexión", JOptionPane.ERROR_MESSAGE);
                    btnLogin.setEnabled(true);
                    btnLogin.setText("INICIAR SESIÓN");
                });
            }
        }).start();
    }
}
