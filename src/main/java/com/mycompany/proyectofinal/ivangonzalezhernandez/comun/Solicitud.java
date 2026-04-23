package com.mycompany.proyectofinal.ivangonzalezhernandez.comun;

import java.io.Serializable;

/**
 * Clase de protocolo de comunicacion entre Cliente y Servidor.
 * Encapsula accion, datos enviados y respuesta recibida.
 */
public class Solicitud implements Serializable {
    private static final long serialVersionUID = 1L;

    // Acciones disponibles
    public static final String LOGIN              = "LOGIN";
    public static final String LISTAR_CLIENTES    = "LISTAR_CLIENTES";
    public static final String AGREGAR_CLIENTE    = "AGREGAR_CLIENTE";
    public static final String ELIMINAR_CLIENTE   = "ELIMINAR_CLIENTE";
    public static final String LISTAR_PRODUCTOS   = "LISTAR_PRODUCTOS";
    public static final String AGREGAR_PRODUCTO   = "AGREGAR_PRODUCTO";
    public static final String ELIMINAR_PRODUCTO  = "ELIMINAR_PRODUCTO";
    public static final String GENERAR_FACTURA    = "GENERAR_FACTURA";
    public static final String LISTAR_FACTURAS    = "LISTAR_FACTURAS";
    public static final String OBTENER_FACTURA    = "OBTENER_FACTURA";

    private String accion;
    private Object datos;
    private boolean exito;
    private String mensaje;
    private Object respuesta;

    public Solicitud() {}

    public Solicitud(String accion, Object datos) {
        this.accion = accion;
        this.datos = datos;
    }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
    public Object getDatos() { return datos; }
    public void setDatos(Object datos) { this.datos = datos; }
    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Object getRespuesta() { return respuesta; }
    public void setRespuesta(Object respuesta) { this.respuesta = respuesta; }
}
