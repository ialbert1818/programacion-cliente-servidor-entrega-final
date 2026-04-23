package com.mycompany.proyectofinal.ivangonzalezhernandez.cliente;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.Solicitud;
import java.io.*;
import java.net.Socket;

/**
 * Gestiona la conexion del cliente con el servidor.
 * Singleton thread-safe.
 */
public class ConexionServidor {

    private static final String HOST = "localhost";
    private static final int    PUERTO = 9090;

    private static ConexionServidor instance;
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream  entrada;

    private ConexionServidor() throws IOException {
        socket = new Socket(HOST, PUERTO);
        salida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
    }

    public static synchronized ConexionServidor getInstance() throws IOException {
        if (instance == null || instance.socket.isClosed()) {
            instance = new ConexionServidor();
        }
        return instance;
    }

    public synchronized Solicitud enviar(Solicitud solicitud) throws IOException, ClassNotFoundException {
        salida.writeObject(solicitud);
        salida.flush();
        salida.reset();
        return (Solicitud) entrada.readObject();
    }

    public void cerrar() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignore) {}
    }
}
