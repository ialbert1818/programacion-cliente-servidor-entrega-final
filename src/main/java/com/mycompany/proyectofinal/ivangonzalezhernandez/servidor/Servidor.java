package com.mycompany.proyectofinal.ivangonzalezhernandez.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Servidor de Facturacion.
 * Escucha en el puerto 9090 y crea un hilo por cada cliente conectado.
 */
public class Servidor {

    public static final int PUERTO = 9090;

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  SERVIDOR DE FACTURACION - Puerto " + PUERTO);
        System.out.println("==============================================");

        // Inicializar Derby
        try {
            DatabaseManager.getInstance();
            System.out.println("[SERVER] Base de datos Derby inicializada.");
        } catch (Exception e) {
            System.err.println("[SERVER] Error al inicializar Derby: " + e.getMessage());
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("[SERVER] Esperando conexiones...");

            // Shutdown hook para cerrar Derby limpiamente
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("[SERVER] Cerrando servidor...");
                try {
                    DatabaseManager.getInstance().cerrar();
                } catch (SQLException e) {
                    System.err.println("[SERVER] Error al cerrar DB: " + e.getMessage());
                }
            }));

            while (true) {
                Socket clienteSocket = serverSocket.accept();
                try {
                    ManejadorCliente handler = new ManejadorCliente(clienteSocket);
                    Thread hilo = new Thread(handler);
                    hilo.setDaemon(true);
                    hilo.start();
                } catch (IOException e) {
                    System.err.println("[SERVER] Error al crear hilo para cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Error en servidor: " + e.getMessage());
        }
    }
}
