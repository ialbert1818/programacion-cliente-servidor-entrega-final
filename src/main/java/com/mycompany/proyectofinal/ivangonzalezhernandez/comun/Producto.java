package com.mycompany.proyectofinal.ivangonzalezhernandez.comun;

import java.io.Serializable;

public abstract class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int idProducto;
    protected String nombre;
    protected double precio;
    protected int cantidad;
    protected String tipo; // "ALIMENTO" o "ELECTRONICO"

    public Producto() {}

    public Producto(int idProducto, String nombre, double precio, int cantidad, String tipo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.tipo = tipo;
    }

    public abstract double calcularPrecioFinal();

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int id) { this.idProducto = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public void reducirCantidad(int cant) { this.cantidad -= cant; }

    @Override
    public String toString() {
        return "[" + tipo + "] " + nombre + " - $" + String.format("%.2f", calcularPrecioFinal()) + " (Stock: " + cantidad + ")";
    }
}
