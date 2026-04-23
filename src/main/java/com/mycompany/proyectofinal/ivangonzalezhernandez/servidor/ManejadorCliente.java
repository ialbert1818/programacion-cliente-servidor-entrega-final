package com.mycompany.proyectofinal.ivangonzalezhernandez.servidor;

import com.mycompany.proyectofinal.ivangonzalezhernandez.comun.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Hilo del servidor que maneja cada cliente conectado.
 */
public class ManejadorCliente implements Runnable {

    private Socket socket;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;

    public ManejadorCliente(Socket socket) throws IOException {
        this.socket = socket;
        // Orden: primero salida, luego entrada (evita deadlock)
        this.salida = new ObjectOutputStream(socket.getOutputStream());
        this.entrada = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        System.out.println("[SERVER] Cliente conectado: " + socket.getInetAddress());
        try {
            while (!socket.isClosed()) {
                Solicitud solicitud = (Solicitud) entrada.readObject();
                Solicitud respuesta = procesarSolicitud(solicitud);
                salida.writeObject(respuesta);
                salida.flush();
                salida.reset();
            }
        } catch (EOFException | java.net.SocketException e) {
            System.out.println("[SERVER] Cliente desconectado: " + socket.getInetAddress());
        } catch (Exception e) {
            System.err.println("[SERVER] Error manejando cliente: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignore) {}
        }
    }

    private Solicitud procesarSolicitud(Solicitud req) {
        Solicitud resp = new Solicitud(req.getAccion(), null);
        try {
            switch (req.getAccion()) {

                case Solicitud.LOGIN: {
                    String[] creds = (String[]) req.getDatos();
                    UsuarioDAO dao = new UsuarioDAO();
                    Usuario u = dao.login(creds[0], creds[1]);
                    if (u != null) {
                        resp.setExito(true);
                        resp.setRespuesta(u);
                        resp.setMensaje("Login exitoso");
                    } else {
                        resp.setExito(false);
                        resp.setMensaje("Credenciales incorrectas");
                    }
                    break;
                }

                case Solicitud.LISTAR_CLIENTES: {
                    ClienteDAO dao = new ClienteDAO();
                    List<Cliente> lista = dao.listarClientes();
                    resp.setExito(true);
                    resp.setRespuesta((java.io.Serializable) lista);
                    break;
                }

                case Solicitud.AGREGAR_CLIENTE: {
                    Cliente c = (Cliente) req.getDatos();
                    ClienteDAO dao = new ClienteDAO();
                    dao.agregar(c);
                    resp.setExito(true);
                    resp.setRespuesta(c);
                    resp.setMensaje("Cliente agregado correctamente");
                    break;
                }

                case Solicitud.ELIMINAR_CLIENTE: {
                    int id = (Integer) req.getDatos();
                    ClienteDAO dao = new ClienteDAO();
                    boolean ok = dao.eliminar(id);
                    resp.setExito(ok);
                    resp.setMensaje(ok ? "Cliente eliminado" : "No se puede eliminar: tiene facturas asociadas");
                    break;
                }

                case Solicitud.LISTAR_PRODUCTOS: {
                    ProductoDAO dao = new ProductoDAO();
                    List<Producto> lista = dao.listarProductos();
                    resp.setExito(true);
                    resp.setRespuesta((java.io.Serializable) lista);
                    break;
                }

                case Solicitud.AGREGAR_PRODUCTO: {
                    Producto p = (Producto) req.getDatos();
                    ProductoDAO dao = new ProductoDAO();
                    dao.agregar(p);
                    resp.setExito(true);
                    resp.setRespuesta(p);
                    resp.setMensaje("Producto agregado correctamente");
                    break;
                }

                case Solicitud.ELIMINAR_PRODUCTO: {
                    int id = (Integer) req.getDatos();
                    ProductoDAO dao = new ProductoDAO();
                    boolean ok = dao.eliminar(id);
                    resp.setExito(ok);
                    resp.setMensaje(ok ? "Producto eliminado" : "Error al eliminar producto");
                    break;
                }

                case Solicitud.GENERAR_FACTURA: {
                    Factura f = (Factura) req.getDatos();
                    FacturaDAO dao = new FacturaDAO();
                    Factura guardada = dao.guardarFactura(f);
                    resp.setExito(true);
                    resp.setRespuesta(guardada);
                    resp.setMensaje("Factura #" + guardada.getNumeroFactura() + " generada");
                    break;
                }

                case Solicitud.LISTAR_FACTURAS: {
                    FacturaDAO dao = new FacturaDAO();
                    List<Factura> lista = dao.listarFacturas();
                    resp.setExito(true);
                    resp.setRespuesta((java.io.Serializable) lista);
                    break;
                }

                case Solicitud.OBTENER_FACTURA: {
                    int id = (Integer) req.getDatos();
                    FacturaDAO dao = new FacturaDAO();
                    Factura f = dao.obtenerFacturaCompleta(id);
                    resp.setExito(f != null);
                    resp.setRespuesta(f);
                    resp.setMensaje(f != null ? "OK" : "Factura no encontrada");
                    break;
                }

                default:
                    resp.setExito(false);
                    resp.setMensaje("Accion no reconocida: " + req.getAccion());
            }
        } catch (Exception e) {
            resp.setExito(false);
            resp.setMensaje("Error en servidor: " + e.getMessage());
            e.printStackTrace();
        }
        return resp;
    }
}
