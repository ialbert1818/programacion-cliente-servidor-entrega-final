package com.mycompany.proyectofinal.ivangonzalezhernandez.comun;

import java.io.Serializable;

public class DetalleFactura implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idDetalle;
    private int idFactura;
    private Producto producto;
    private int cantidad;
    private double subtotal;

    public DetalleFactura() {}

    public DetalleFactura(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.calcularPrecioFinal() * cantidad;
    }

    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int id) { this.idDetalle = id; }
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int id) { this.idFactura = id; }
    public Producto getProducto() { return producto; }
    public void setProducto(Producto p) { this.producto = p; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int c) { this.cantidad = c; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double s) { this.subtotal = s; }

    @Override
    public String toString() {
        return producto.getNombre() + " x" + cantidad + " = $" + String.format("%.2f", subtotal);
    }
}
