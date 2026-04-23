package com.mycompany.proyectofinal.ivangonzalezhernandez.cliente;

import javax.swing.*;

/**
 * Punto de entrada del cliente.
 * Lanza la ventana de Login en el Event Dispatch Thread.
 */
public class ClienteApp {

    public static void main(String[] args) {
        // Look & Feel nativo del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignore) {}

        SwingUtilities.invokeLater(() -> {
            System.out.println("Iniciando cliente de Facturación...");
            new LoginGUI();
        });
    }
}
