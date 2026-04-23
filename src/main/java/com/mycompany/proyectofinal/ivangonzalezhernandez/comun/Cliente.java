package com.mycompany.proyectofinal.ivangonzalezhernandez.comun;

import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idCliente;
    private String nombre;
    private String correo;

    public Cliente() {}

    public Cliente(int idCliente, String nombre, String correo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.correo = correo;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int id) { this.idCliente = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    @Override
    public String toString() {
        return nombre + " (" + correo + ")";
    }
}
