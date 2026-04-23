package com.mycompany.proyectofinal.ivangonzalezhernandez.cliente;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.Usuario;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Ventana principal con pestañas para Clientes, Productos, Facturación e Historial.
 */
public class MenuGUI extends JFrame {

    private Usuario usuarioActual;

    public MenuGUI(Usuario usuario) {
        this.usuarioActual = usuario;

        setTitle("Sistema de Facturación - [" + usuario.getUsername() + " | " + usuario.getRol() + "]");
        setSize(900, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Barra superior
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(34, 45, 65));
        topBar.setPreferredSize(new Dimension(900, 45));

        JLabel lblSistema = new JLabel("  Sistema de Facturación v2.0 - Derby DB");
        lblSistema.setFont(new Font("Arial", Font.BOLD, 14));
        lblSistema.setForeground(Color.WHITE);
        topBar.add(lblSistema, BorderLayout.WEST);

        JButton btnCerrar = new JButton("Cerrar Sesión");
        btnCerrar.setBackground(new Color(192, 57, 43));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.addActionListener(e -> cerrarSesion());
        topBar.add(btnCerrar, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 12));

        tabs.addTab("👥 Clientes",    new ClientesPanel());
        tabs.addTab("📦 Productos",   new ProductosPanel());
        tabs.addTab("🧾 Nueva Factura", new FacturaPanel());
        tabs.addTab("📋 Historial",   new HistorialPanel());

        // Si no es ADMIN, ocultar tab de clientes/productos (solo lectura)
        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

        private void cerrarSesion() {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea cerrar sesión?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ConexionServidor.getInstance().cerrar();
                } catch (IOException e) {
                    // Si ya estaba cerrada, ignorar
                }
                new LoginGUI();
                dispose();
            }
        }
}
